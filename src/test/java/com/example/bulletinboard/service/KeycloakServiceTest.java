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
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.management.Query.eq;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeycloakServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private KeycloakService keycloakService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(keycloakService, "authServerUrl", "http://localhost:8080/auth");
        ReflectionTestUtils.setField(keycloakService, "realm", "test-realm");
        ReflectionTestUtils.setField(keycloakService, "clientId", "test-client");
        ReflectionTestUtils.setField(keycloakService, "clientSecret", "test-secret");
    }

    @Test
    void createUser_ShouldReturnAuthResponse() {
        // Arrange
        UserRequest userRequest = new UserRequest(
                "testuser", "password", "test@example.com",
                "+79788546363", "User", "Surname");

        // 1. Mock admin token request
        when(restTemplate.postForEntity(
                ArgumentMatchers.eq("http://localhost:8080/auth/realms/test-realm/protocol/openid-connect/token"),
                any(HttpEntity.class),
                ArgumentMatchers.eq(Map.class)
        )).thenReturn(new ResponseEntity<>(
                Map.of("access_token", "admin-token"),
                HttpStatus.OK
        ));

        // 2. Mock user creation
        when(restTemplate.postForEntity(
                ArgumentMatchers.eq("http://localhost:8080/auth/admin/realms/test-realm/users"),
                any(HttpEntity.class),
                ArgumentMatchers.eq(Void.class)
        )).thenReturn(ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/users/test-user-id")
                .build());

        // 3. Mock set password
        doNothing().when(restTemplate).put(
                ArgumentMatchers.eq("http://localhost:8080/auth/admin/realms/test-realm/users/test-user-id/reset-password"),
                any(HttpEntity.class));

        // 4. Mock get client UUID
        when(restTemplate.exchange(
                ArgumentMatchers.eq("http://localhost:8080/auth/admin/realms/test-realm/clients?clientId=test-client"),
                ArgumentMatchers.eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(
                List.of(Map.of("id", "client-uuid")),
                HttpStatus.OK
        ));

        // 5. Mock role assignment
        when(restTemplate.exchange(
                ArgumentMatchers.eq("http://localhost:8080/auth/admin/realms/test-realm/users/test-user-id/role-mappings/clients/client-uuid"),
                ArgumentMatchers.eq(HttpMethod.POST),
                any(HttpEntity.class),
                ArgumentMatchers.eq(Void.class)
        )).thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        // 6. Mock login request
        when(restTemplate.postForEntity(
                ArgumentMatchers.eq("http://localhost:8080/auth/realms/test-realm/protocol/openid-connect/token"),
                any(HttpEntity.class),
                ArgumentMatchers.eq(Map.class)
        )).thenReturn(new ResponseEntity<>(
                Map.of(
                        "access_token", "test-access-token",
                        "refresh_token", "test-refresh-token"
                ),
                HttpStatus.OK
        ));

        // 7. Mock delete user (for rollback case)
        when(restTemplate.exchange(
                ArgumentMatchers.eq("http://localhost:8080/auth/admin/realms/test-realm/users/test-user-id"),
                ArgumentMatchers.eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                ArgumentMatchers.eq(Void.class)
        )).thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        // Act
        AuthResponse result = keycloakService.createUser(userRequest);

        // Assert
        assertNotNull(result);
        assertEquals("test-access-token", result.getAccessToken());
        assertEquals("test-refresh-token", result.getRefreshToken());
    }
}