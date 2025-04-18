package com.example.bulletinboard.service;

import com.example.bulletinboard.enums.ERole;
import com.example.bulletinboard.request.UserInfoUpdateRequest;
import com.example.bulletinboard.request.UserRequest;
import com.example.bulletinboard.response.AuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static javax.management.Query.eq;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeycloakServiceTest {

//    @Mock
//    private RestTemplate restTemplate;
//
//    @InjectMocks
//    private KeycloakService keycloakService;
//
//    private static final String TEST_USER_ID = "test-user-id";
//    private static final String TEST_ACCESS_TOKEN = "test-access-token";
//    private static final String TEST_REFRESH_TOKEN = "test-refresh-token";
//
//    @Test
//    void createUser_ShouldReturnAuthResponse() {
//        // Arrange
//        UserRequest userRequest = createTestUserRequest();
//
//        // Mock admin token request
//        mockTokenRequest("client_credentials", "test-client",
//                Map.of("access_token", "admin-token"));
//
//        // Mock user creation
//        mockUserCreationResponse();
//
//        // Mock login
//        mockTokenRequest("password", userRequest.getUsername(),
//                Map.of(
//                        "access_token", TEST_ACCESS_TOKEN,
//                        "refresh_token", TEST_REFRESH_TOKEN
//                ));
//
//        // Act
//        AuthResponse result = keycloakService.createUser(userRequest);
//    }
//
//    private void mockTokenRequest(String grantType, String usernameOrClient, Map<String, String> response) {
//        when(restTemplate.postForEntity(
//                contains("/protocol/openid-connect/token"),
//                argThat(request -> {
//                    HttpEntity<?> entity = (HttpEntity<?>) request;
//                    if (entity.getBody() instanceof MultiValueMap) {
//                        MultiValueMap<String, String> body = (MultiValueMap<String, String>) entity.getBody();
//                        return grantType.equals(body.getFirst("grant_type")) &&
//                                (grantType.equals("password") ?
//                                        usernameOrClient.equals(body.getFirst("username")) :
//                                        usernameOrClient.equals(body.getFirst("client_id")));
//                    }
//                    return false;
//                }),
//                eq(Map.class)
//        )).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
//    }
//
//    private UserRequest createTestUserRequest() {
//        return new UserRequest(
//                "testuser",
//                "password",
//                "test@example.com",
//                "+79788546363",
//                "User",
//                "Surname"
//        );
//    }
//
//    private void mockUserCreationResponse() {
//        ResponseEntity<Void> response = ResponseEntity.status(HttpStatus.CREATED)
//                .header("Location", "/users/" + TEST_USER_ID)
//                .build();
//
//        when(restTemplate.postForEntity(
//                contains("/users"),
//                any(HttpEntity.class),
//                eq(Void.class)
//        )).thenReturn(response);
//    }
//
//    private boolean verifyUserUpdateRequest(HttpEntity<?> request) {
//        if (!(request.getBody() instanceof UserRepresentation)) {
//            return false;
//        }
//        UserRepresentation user = (UserRepresentation) request.getBody();
//        return "new@example.com".equals(user.getEmail()) &&
//                "NewName".equals(user.getFirstName()) &&
//                "NewSurname".equals(user.getLastName());
//    }
//
//






//    @BeforeEach
//    void setUp() {
//        // Настройка тестовых значений вместо @Value полей
//        keycloakService.authServerUrl = "http://localhost:8080/auth";
//        keycloakService.realm = "test-realm";
//        keycloakService.clientId = "test-client";
//        keycloakService.clientSecret = "test-secret";
//    }
//
//
//
//
//    private void mockRoleLookup() {
//        when(restTemplate.exchange(
//                contains("/roles/USER"),
//                eq(HttpMethod.GET),
//                any(HttpEntity.class),
//                eq(Map.class)
//        )).thenReturn(new ResponseEntity<>(
//                Map.of("id", "role-uuid", "name", "USER"),
//                HttpStatus.OK
//        ));
//    }
//    @Test
//    void getAdminToken_ShouldReturnToken() {
//        // Arrange
//        Map<String, String> responseBody = new HashMap<>();
//        responseBody.put("access_token", testAdminToken);
//
//        ResponseEntity<Map<String, String>> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
//
//        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
//                .thenReturn(responseEntity);
//
//        // Act
//        String result = keycloakService.getAdminToken();
//
//        // Assert
//        assertEquals(testAdminToken, result);
//        verify(restTemplate).postForEntity(
//                eq("http://localhost:8080/auth/realms/test-realm/protocol/openid-connect/token"),
//                any(HttpEntity.class),
//                eq(Map.class));
//    }
//
//    @Test
//    void isUsernameExists_ShouldReturnTrueWhenUserExists() {
//        // Arrange
//        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
//                .thenReturn(new ResponseEntity<>(Map.of("access_token", testAdminToken), HttpStatus.OK));
//
//        UserRepresentation[] users = {new UserRepresentation()};
//        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
//                eq(UserRepresentation[].class)))
//                .thenReturn(new ResponseEntity<>(users, HttpStatus.OK));
//
//        // Act
//        boolean result = keycloakService.isUsernameExists("testuser", testAdminToken);
//
//        // Assert
//        assertTrue(result);
//    }




}