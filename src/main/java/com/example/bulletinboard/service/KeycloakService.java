package com.example.bulletinboard.service;

import com.example.bulletinboard.enums.ERole;
import com.example.bulletinboard.request.UserInfoUpdateRequest;
import com.example.bulletinboard.request.UserRequest;
import com.example.bulletinboard.response.AuthResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.account.UserRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;

    public AuthResponse createUser(UserRequest userRequest) {
        String adminToken = null;
        String userId = null;

        try {
            adminToken = getAdminToken();
            userId = createKeycloakUser(userRequest, adminToken);

            try {
                setUserPassword(userId, userRequest.getPassword(), adminToken);

                assignClientRoleToUser(userId, ERole.ROLE_USER);

                Map<String, String> tokens = loginUser(userRequest.getUsername(), userRequest.getPassword());

                return new AuthResponse(tokens.get("access_token"), tokens.get("refresh_token"), userId);

            } catch (Exception e) {
                // Удаляем пользователя при ошибке после создания
                deleteUserById(userId);
                throw new RuntimeException("User setup failed: " + e.getMessage(), e);
            }

        } catch (Exception e) {
            throw new RuntimeException("User creation failed: " + e.getMessage(), e);
        }
    }

    public AuthResponse createModerator(UserRequest userRequest) {
        String adminToken = null;
        String userId = null;

        try {
            adminToken = getAdminToken();
            userId = createKeycloakUser(userRequest, adminToken);

            try {
                setUserPassword(userId, userRequest.getPassword(), adminToken);

                assignClientRoleToUser(userId, ERole.ROLE_MODERATOR);

                Map<String, String> tokens = loginUser(userRequest.getUsername(), userRequest.getPassword());

                return new AuthResponse(tokens.get("access_token"), tokens.get("refresh_token"), userId);

            } catch (Exception e) {
                // Удаляем пользователя при ошибке после создания
                deleteUserById(userId);
                throw new RuntimeException("User setup failed: " + e.getMessage(), e);
            }

        } catch (Exception e) {
            throw new RuntimeException("User creation failed: " + e.getMessage(), e);
        }
    }

    private String getAdminToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token",
                request,
                Map.class);

        return (String) response.getBody().get("access_token");
    }

    public String getRole(String userId) {
        String adminToken = getAdminToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
        HttpEntity<String> rolesRequest = new HttpEntity<>(headers);
        ResponseEntity<RoleRepresentation[]> rolesResponse = restTemplate.exchange(
                authServerUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/clients/" + clientId,
                HttpMethod.GET,
                rolesRequest,
                RoleRepresentation[].class);

        RoleRepresentation[] userRoles = rolesResponse.getBody();
        System.out.println(userRoles.toString());
        return userRoles.toString();
    }

    public Map<String, String> refreshToken(String refreshToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);
        body.add("grant_type", "refresh_token");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token",
                request,
                Map.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to login user");
        }

        return Map.of(
                "access_token", (String) response.getBody().get("access_token"),
                "refresh_token", (String) response.getBody().get("refresh_token")
        );
    }

    private String createKeycloakUser(UserRequest userRequest, String adminToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        KeycloakUserRequest keycloakUser = new KeycloakUserRequest(
                userRequest.getUsername(),
                userRequest.getEmail(),
                userRequest.getName(),
                userRequest.getSurname(),
                true,
                true
        );

        HttpEntity<KeycloakUserRequest> request = new HttpEntity<>(keycloakUser, headers);
        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(
                    authServerUrl + "/admin/realms/" + realm + "/users",
                    request,
                    Void.class);


            if (response.getStatusCode() != HttpStatus.CREATED) {
                throw new RuntimeException("Failed to create user, status: " + response.getStatusCode());
            }

            String location = response.getHeaders().getFirst("Location");
            if (location == null) {
                throw new RuntimeException("No Location header in response");
            }

            return location.substring(location.lastIndexOf('/') + 1);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to create Keycloak user", e);
        }
    }


    public void setUserPassword(String userId, String password, String adminToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        Map<String, Object> passwordCredential = Map.of(
                "type", "password",
                "value", password,
                "temporary", false
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(passwordCredential, headers);
        restTemplate.put(
                authServerUrl + "/admin/realms/" + realm + "/users/" + userId + "/reset-password",
                request);
    }

    public void setUserPassword(String userId, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getAdminToken());

        Map<String, Object> passwordCredential = Map.of(
                "type", "password",
                "value", password,
                "temporary", false
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(passwordCredential, headers);
        restTemplate.put(
                authServerUrl + "/admin/realms/" + realm + "/users/" + userId + "/reset-password",
                request);
    }

    private boolean isUsernameExists(String username, String adminToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        String searchUrl = authServerUrl + "/admin/realms/" + realm + "/users?username=" + username;

        try {
            ResponseEntity<UserRepresentation[]> response = restTemplate.exchange(
                    searchUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    UserRepresentation[].class);

            return response.getBody() != null && response.getBody().length > 0;
        } catch (Exception e) {
            throw new RuntimeException("Failed to check username existence", e);
        }
    }


    private UserRepresentation getUserById(String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAdminToken());

        ResponseEntity<UserRepresentation> response = restTemplate.exchange(
                authServerUrl + "/admin/realms/" + realm + "/users/" + userId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserRepresentation.class);

        return response.getBody();
    }

    public void updateUserInfo(String userId, UserInfoUpdateRequest userInfoUpdateRequest) {
        // 1. Get user by ID endpoint
        String userUrl = authServerUrl + "/admin/realms/" + realm + "/users/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAdminToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        try {
            // 2. Prepare update payload
            UserRepresentation userUpdate = new UserRepresentation();

            // Only update non-null fields
            if (userInfoUpdateRequest.getEmail() != null) {
                userUpdate.setEmail(userInfoUpdateRequest.getEmail());
            }
            if (userInfoUpdateRequest.getName() != null) {
                userUpdate.setFirstName(userInfoUpdateRequest.getName());
            }
            if (userInfoUpdateRequest.getSurname() != null) {
                userUpdate.setLastName(userInfoUpdateRequest.getSurname());
            }
            userUpdate.setEmailVerified(true);

            // 3. Execute PUT request
            restTemplate.exchange(
                    userUrl,
                    HttpMethod.PUT,
                    new HttpEntity<>(userUpdate, headers),
                    Void.class);

        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("User not found with id: " + userId, e);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to update user in Keycloak", e);
        }
    }

    public Map<String, String> loginUser(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("username", username);
        body.add("password", password);
        body.add("grant_type", "password");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token",
                request,
                Map.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to login user");
        }

        return Map.of(
                "access_token", (String) response.getBody().get("access_token"),
                "refresh_token", (String) response.getBody().get("refresh_token")
        );

    }

    public String getUserIdByUsername(String username) {
        String adminToken = getAdminToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        String url = authServerUrl + "/admin/realms/" + realm + "/users?username=" + username;
        ResponseEntity<List<Map>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Map>>() {
                }
        );

        if (response.getBody() == null || response.getBody().isEmpty()) {
            throw new RuntimeException("User not found");
        }

        return (String) response.getBody().get(0).get("id");
    }

    public void deleteUserById(String userId) {
        String url = String.format("%s/admin/realms/%s/users/%s",
                authServerUrl, realm, userId);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAdminToken());

        ResponseEntity<Void> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class);

        if (response.getStatusCode() != HttpStatus.NO_CONTENT) {
            throw new RuntimeException("Failed to delete user");
        }
    }


    public void assignClientRoleToUser(String userId, ERole role) {
        // 1. Получаем токен администратора
        String adminToken = getAdminToken();

        // 2. Получаем UUID клиента spring-bulletin-board
        String clientUuid = getClientUuid(adminToken);

        // 3. Получаем ID роли
        String roleId = getClientRoleId(clientUuid, role.name().substring(5), adminToken);

        // 4. Назначаем роль пользователю
        assignRoleToUser(userId, clientUuid, roleId, adminToken, role);
    }

    private String getClientUuid(String adminToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(adminToken);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            String url = authServerUrl + "/admin/realms/" + realm + "/clients?clientId=" + clientId;
            System.out.println("Request URL: " + url);

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {
                    }
            );

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null || response.getBody().isEmpty()) {
                throw new RuntimeException("Client 'spring-bulletin-board' not found or access denied");
            }

            Map<String, Object> client = response.getBody().get(0);
            if (!client.containsKey("id")) {
                throw new RuntimeException("Client response doesn't contain ID field");
            }

            return (String) client.get("id");
        } catch (Exception e) {
            throw new RuntimeException("Failed to get client UUID: " + e.getMessage(), e);
        }
    }

    private String getClientRoleId(String clientUuid, String roleName, String adminToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        String url = authServerUrl + "/admin/realms/" + realm +
                "/clients/" + clientUuid + "/roles/" + roleName;

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );

        return (String) response.getBody().get("id");
    }

    private void assignRoleToUser(String userId, String clientUuid, String roleId, String adminToken, ERole roleName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Создаем представление роли
        Map<String, String> roleRepresentation = new HashMap<>();
        roleRepresentation.put("id", roleId);
        roleRepresentation.put("name", roleName.name().substring(5)); // Убираем "ROLE_"

        String url = authServerUrl + "/admin/realms/" + realm +
                "/users/" + userId + "/role-mappings/clients/" + clientUuid;

        HttpEntity<List<Map<String, String>>> request =
                new HttpEntity<>(Collections.singletonList(roleRepresentation), headers);

        restTemplate.postForEntity(url, request, Void.class);
    }


    @Data
    @AllArgsConstructor
    static class KeycloakUserRequest {
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private boolean enabled;
        private boolean emailVerified;
    }

}