package com.example.bulletinboard.service;

import com.example.bulletinboard.entity.Role;
import com.example.bulletinboard.entity.User;
import com.example.bulletinboard.exceptions.UserCreationException;
import com.example.bulletinboard.mapper.UserMapper;
import com.example.bulletinboard.repository.RoleRepository;
import com.example.bulletinboard.repository.UserRepository;
import com.example.bulletinboard.request.LoginRequest;
import com.example.bulletinboard.request.UserAuthUpdateRequest;
import com.example.bulletinboard.request.UserInfoUpdateRequest;
import com.example.bulletinboard.request.UserRequest;
import com.example.bulletinboard.response.AuthResponse;
import com.example.bulletinboard.response.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {


    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private KeycloakService keycloakService;
    @Mock
    private PlatformTransactionManager transactionManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private Authentication authentication;
    @Mock
    private JwtAuthenticationToken jwtAuthenticationToken;
    @Mock
    private KeycloakAuthenticationToken keycloakAuthenticationToken;
    @Mock
    private KeycloakPrincipal<?> keycloakPrincipal;
    @Mock
    private AccessToken accessToken;
    @Mock
    private AccessToken.Access realmAccess;
    @Mock
    private Map<String, AccessToken.Access> resourceAccess;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserRequest userRequest;
    private LoginRequest loginRequest;
    private UserAuthUpdateRequest authUpdateRequest;
    private UserInfoUpdateRequest infoUpdateRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("test-id");
        testUser.setUsername("testuser");
        testUser.setPassword("encoded-password");
        testUser.setEmail("test@example.com");
        testUser.setPhoneNumber("1234567890");

        Role userRole = new Role();
        userRole.setId(1);
        testUser.setRole(userRole);

        userRequest = new UserRequest();
        userRequest.setUsername("newuser");
        userRequest.setPassword("password");
        userRequest.setEmail("new@example.com");
        userRequest.setPhoneNumber("0987654321");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        authUpdateRequest = new UserAuthUpdateRequest();
        authUpdateRequest.setPassword("newpassword");

        infoUpdateRequest = new UserInfoUpdateRequest();
        infoUpdateRequest.setEmail("updated@example.com");
        infoUpdateRequest.setPhoneNumber("1111111111");
        infoUpdateRequest.setName("Updated");
        infoUpdateRequest.setSurname("User");

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void findUserById_ShouldReturnUserResponse() {
        // Arrange
        when(userRepository.findById("test-id")).thenReturn(Optional.of(testUser));
        when(userMapper.toUserResponse(testUser)).thenReturn(new UserResponse());

        // Act
        UserResponse result = userService.findUserById("test-id");

        // Assert
        assertNotNull(result);
        verify(userRepository).findById("test-id");
    }

    @Test
    void createUser_ShouldCreateUserAndReturnAuthResponse() {
        // Arrange
        TransactionStatus status = mock(TransactionStatus.class);
        when(transactionManager.getTransaction(any())).thenReturn(status);

        AuthResponse authResponse = new AuthResponse("token", "refresh", "new-id");
        when(keycloakService.createUser(userRequest)).thenReturn(authResponse);
        when(userMapper.toUser(userRequest)).thenReturn(testUser);
        when(passwordEncoder.encode(userRequest.getPassword())).thenReturn("encoded-password");
        when(roleRepository.findById(1)).thenReturn(Optional.of(new Role()));

        // Act
        AuthResponse result = userService.createUser(userRequest);

        // Assert
        assertEquals(authResponse, result);
        verify(userRepository).save(testUser);
        verify(transactionManager).commit(status);
    }

    @Test
    void createUser_ShouldRollbackWhenExceptionOccurs() {
        // Arrange
        TransactionStatus status = mock(TransactionStatus.class);
        when(transactionManager.getTransaction(any())).thenReturn(status);

        AuthResponse authResponse = new AuthResponse("token", "refresh", "new-id");
        when(keycloakService.createUser(userRequest)).thenReturn(authResponse);
        when(userMapper.toUser(userRequest)).thenReturn(testUser);
        when(userRepository.save(testUser)).thenThrow(new RuntimeException("DB error"));

        // Act & Assert
        assertThrows(UserCreationException.class, () -> userService.createUser(userRequest));
        verify(transactionManager).rollback(status);
        verify(keycloakService).deleteUserById("new-id");
    }

    @Test
    void loginUser_ShouldReturnAuthResponse() {
        // Arrange
        Map<String, String> tokens = Map.of(
                "access_token", "access-token",
                "refresh_token", "refresh-token"
        );
        when(keycloakService.loginUser("testuser", "password")).thenReturn(tokens);
        when(keycloakService.getUserIdByUsername("testuser")).thenReturn("user-id");

        // Act
        AuthResponse result = userService.loginUser(loginRequest);

        // Assert
        assertEquals("access-token", result.getAccessToken());
        assertEquals("refresh-token", result.getRefreshToken());
        assertEquals("user-id", result.getUserId());
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        // Act
        userService.deleteUser("test-id");

        // Assert
        verify(keycloakService).deleteUserById("test-id");
        verify(userRepository).deleteById("test-id");
    }

    @Test
    void getCurrentUsername_ShouldReturnUsernameFromJwt() {
        // Arrange
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("preferred_username")).thenReturn("testuser");

        JwtAuthenticationToken jwtAuth = mock(JwtAuthenticationToken.class);
        when(jwtAuth.getToken()).thenReturn(jwt);

        mockSecurityContext(jwtAuth);

        // Act
        String result = userService.getCurrentUsername();

        // Assert
        assertEquals("testuser", result);
    }

    @Test
    void getCurrentUserId_ShouldReturnUserIdFromJwt() {
        // Arrange
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("sub")).thenReturn("user-id");

        JwtAuthenticationToken jwtAuth = mock(JwtAuthenticationToken.class);
        when(jwtAuth.getToken()).thenReturn(jwt);

        mockSecurityContext(jwtAuth);

        // Act
        String result = userService.getCurrentUserId();

        // Assert
        assertEquals("user-id", result);
    }

    @Test
    void hasRole_ShouldReturnTrueForAdminRole() {
        // Arrange
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsStringList("roles")).thenReturn(List.of("ADMIN"));

        JwtAuthenticationToken jwtAuth = mock(JwtAuthenticationToken.class);
        when(jwtAuth.getToken()).thenReturn(jwt);

        mockSecurityContext(jwtAuth);

        // Act
        boolean result = userService.hasRole("ADMIN");

        // Assert
        assertTrue(result);
    }

    @Test
    void updateUserAuth_ShouldUpdatePassword() {
        // Arrange
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("sub")).thenReturn("test-id");

        JwtAuthenticationToken jwtAuth = mock(JwtAuthenticationToken.class);
        when(jwtAuth.getToken()).thenReturn(jwt);

        mockSecurityContext(jwtAuth);

        when(userRepository.findById("test-id")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("newpassword", "encoded-password")).thenReturn(false);
        when(passwordEncoder.encode("newpassword")).thenReturn("new-encoded-password");

        // Act
        userService.updateUserAuth(authUpdateRequest);

        // Assert
        assertEquals("new-encoded-password", testUser.getPassword());
        verify(keycloakService).setUserPassword("test-id", "newpassword");
    }

    @Test
    void updateUserInfo_ShouldUpdateUserInfo() {
        // Arrange
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("sub")).thenReturn("test-id");

        JwtAuthenticationToken jwtAuth = mock(JwtAuthenticationToken.class);
        when(jwtAuth.getToken()).thenReturn(jwt);

        mockSecurityContext(jwtAuth);

        when(userRepository.findById("test-id")).thenReturn(Optional.of(testUser));
        when(userMapper.toUserResponse(testUser)).thenReturn(new UserResponse());

        // Act
        UserResponse result = userService.updateUserInfo(infoUpdateRequest);

        // Assert
        assertNotNull(result);
        assertEquals("updated@example.com", testUser.getEmail());
        assertEquals("1111111111", testUser.getPhoneNumber());
        verify(keycloakService).updateUserInfo(eq("test-id"), any(UserInfoUpdateRequest.class));
    }

    @Test
    void isAdminOrModerator_ShouldReturnTrueForAdmin() {
        // Arrange
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsStringList("roles")).thenReturn(List.of("ADMIN"));

        JwtAuthenticationToken jwtAuth = mock(JwtAuthenticationToken.class);
        when(jwtAuth.getToken()).thenReturn(jwt);

        mockSecurityContext(jwtAuth);

        // Act
        boolean result = userService.isAdminOrModerator();

        // Assert
        assertTrue(result);
    }

    @Test
    void getCurrentUser_ShouldReturnCurrentUser() {
        // Arrange
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("sub")).thenReturn("test-id");

        JwtAuthenticationToken jwtAuth = mock(JwtAuthenticationToken.class);
        when(jwtAuth.getToken()).thenReturn(jwt);

        mockSecurityContext(jwtAuth);

        when(userRepository.findById("test-id")).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getCurrentUser();

        // Assert
        assertEquals(testUser, result);
    }



    private void mockSecurityContext(Authentication authentication) {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
