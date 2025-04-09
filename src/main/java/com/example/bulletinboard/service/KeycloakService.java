package com.example.bulletinboard.service;

import com.example.bulletinboard.request.UserRequest;
import com.example.bulletinboard.response.AuthResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


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
        // 1. Получаем токен админа
        String adminToken = getAdminToken();
        // 2. Создаем пользователя в Keycloak
        String userId = createKeycloakUser(userRequest, adminToken);
        // 3. Устанавливаем пароль
        setUserPassword(userId, userRequest.getPassword(), adminToken);

        Map<String,String> tokens = loginUser(userRequest.getUsername(),userRequest.getPassword());
        //getRole(userId);
        return new AuthResponse(tokens.get("access_token"),tokens.get("refresh_token"),userId);
    }

    public String getAdminToken() {
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

    public String getRole(String userId){
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


    private void setUserPassword(String userId, String password, String adminToken) {
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

    public Map<String,String> loginUser(String username, String password){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String,String> body = new LinkedMultiValueMap<>();
        body.add("client_id",clientId);
        body.add("client_secret",clientSecret);
        body.add("username",username);
        body.add("password",password);
        body.add("grant_type","password");

        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(body,headers);

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
                new ParameterizedTypeReference<List<Map>>() {}
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




    @Data
    @AllArgsConstructor
    private static class KeycloakUserRequest {
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private boolean enabled;
        private boolean emailVerified;
    }

}