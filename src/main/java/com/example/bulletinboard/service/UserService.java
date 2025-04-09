package com.example.bulletinboard.service;

import com.example.bulletinboard.entity.Role;
import com.example.bulletinboard.entity.User;
import com.example.bulletinboard.exceptions.ResourceNotFoundException;
import com.example.bulletinboard.exceptions.UserCreationException;
import com.example.bulletinboard.mapper.UserMapper;
import com.example.bulletinboard.repository.RoleRepository;
import com.example.bulletinboard.repository.UserRepository;
import com.example.bulletinboard.request.LoginRequest;
import com.example.bulletinboard.request.UserRequest;
import com.example.bulletinboard.response.AuthResponse;
import com.example.bulletinboard.response.UserResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final KeycloakService keycloakService;
    private final PlatformTransactionManager transactionManager;
    private final PasswordEncoder passwordEncoder;

    public UserResponse findUserById(long id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.map(userMapper::toUserResponse).orElse(null);
    }

    public AuthResponse createUser(UserRequest userRequest) {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        AuthResponse keycloakUser = keycloakService.createUser(userRequest);

        try {
            User user = userMapper.toUser(userRequest);
            user.setId(keycloakUser.getUserId());
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            roleRepository.findById(1).ifPresent(user::setRole);

            userRepository.save(user);

            transactionManager.commit(status);
            return keycloakUser;
        } catch (Exception e) {
            transactionManager.rollback(status);
            if (keycloakUser.getUserId() != null) {
                try {
                    keycloakService.deleteUserById(keycloakUser.getUserId());
                } catch (Exception ex) {
                    log.error("Failed to rollback keycloak user creation", ex);
                }
            }
            throw new UserCreationException("Failed to create user" + e.getMessage(), e);
        }
    }

    public AuthResponse loginUser(LoginRequest loginRequest) {
        Map<String, String> tokens = keycloakService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());

        return new AuthResponse(tokens.get("access_token"), tokens.get("refresh_token"),
                keycloakService.getUserIdByUsername(loginRequest.getUsername()));
    }

    public void updateUser(UserRequest userRequest) {
    }

    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    public List<UserResponse> findAllUsers() {
        return userMapper.toUserResponses(userRepository.findAll());
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).
                orElseThrow(() -> new RuntimeException("User with username" + username + "not found"));
    }


    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
            return jwtAuth.getToken().getClaimAsString("preferred_username"); // Или другой claim
        }

        throw new IllegalStateException("Unsupported authentication type");
    }

    public AccessToken getCurrentUserToken() {
        KeycloakAuthenticationToken authentication =
                (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        return ((KeycloakPrincipal<?>) authentication.getPrincipal())
                .getKeycloakSecurityContext()
                .getToken();
    }

    public boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
            List<String> roles = jwtAuth.getToken().getClaimAsStringList("roles");
            return roles != null && roles.contains(roleName);
        } else if (authentication instanceof KeycloakAuthenticationToken) {
            AccessToken token = getCurrentUserToken();
            return token.getRealmAccess().getRoles().contains(roleName) ||
                    token.getResourceAccess().values().stream()
                            .anyMatch(access -> access.getRoles().contains(roleName));
        }

        return false;
    }


    public boolean isAdminOrModerator() {
        return hasRole("ADMIN") || hasRole("MODERATOR");
    }


    public String getCurrentUserId(){
        return userRepository.findByUsername(getCurrentUsername())
                .orElseThrow(()-> new ResourceNotFoundException("User not found")
                ).getId();
    }

    public User getCurrentUser(){
        return userRepository.findByUsername(getCurrentUsername())
                .orElseThrow(()-> new ResourceNotFoundException("User not found")
                );
    }

}
