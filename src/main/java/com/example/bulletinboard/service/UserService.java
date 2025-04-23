package com.example.bulletinboard.service;

import com.example.bulletinboard.entity.Role;
import com.example.bulletinboard.entity.User;
import com.example.bulletinboard.exceptions.ResourceNotFoundException;
import com.example.bulletinboard.exceptions.UserCreationException;
import com.example.bulletinboard.mapper.UserMapper;
import com.example.bulletinboard.repository.RoleRepository;
import com.example.bulletinboard.repository.UserRepository;
import com.example.bulletinboard.request.*;
import com.example.bulletinboard.response.AuthResponse;
import com.example.bulletinboard.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.sql.ast.tree.from.TableReference;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
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
    private final JwtDecoder jwtDecoder; 

    public UserResponse findUserById(String id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.map(userMapper::toUserResponse).orElse(null);
    }

    public AuthResponse createUser(UserRequest userRequest) {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        AuthResponse keycloakUser = null;

        try {
            keycloakUser = keycloakService.createUser(userRequest);
            User user = userMapper.toUser(userRequest);
            user.setId(keycloakUser.getUserId());
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            roleRepository.findById(1).ifPresent(user::setRole);

            userRepository.save(user);

            transactionManager.commit(status);
            return keycloakUser;
        } catch (Exception e) {
            transactionManager.rollback(status);
            if (keycloakUser != null && keycloakUser.getUserId() != null) {
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



    public void deleteUser(String id) {
        try {

            keycloakService.deleteUserById(id);
            userRepository.deleteById(id);
            log.debug("пользователь полностью удален");
        } catch (Exception e){
            log.error("Ошибка удаления пользователя\n" );
            throw new RuntimeException(e.getMessage());
        }
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

    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
            return jwtAuth.getToken().getClaimAsString("sub"); // Или другой claim
        }

        throw new IllegalStateException("Unsupported authentication type");
    }
    public String getUserIdFromToken(String token) {

        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getClaim("sub");
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

    @Transactional(rollbackFor = Exception.class)  // Откатывает транзакцию при любом исключении
    public void updateUserAuth(UserAuthUpdateRequest userAuthUpdateRequest) {

        boolean passwordUpdated = false;
        User user = getCurrentUser();

        try {


            // Обновление пароля (если изменился)
            if (userAuthUpdateRequest.getPassword() != null &&
                    !passwordEncoder.matches(userAuthUpdateRequest.getPassword(), user.getPassword())) {

                String newPassword = passwordEncoder.encode(userAuthUpdateRequest.getPassword());
                user.setPassword(newPassword);
                keycloakService.setUserPassword(user.getId(), userAuthUpdateRequest.getPassword());
                passwordUpdated = true;
            }

            // Сохраняем изменения в БД (если были обновления)
            if (passwordUpdated) {
                userRepository.save(user);
            }

        } catch (Exception e) {
            // Логируем ошибку
            log.error("Failed to update user auth data: {}", e.getMessage());

            // Откатываем изменения в Keycloak (если они были)
            try {
                if (passwordUpdated) {
                    keycloakService.setUserPassword(user.getId(), "old_password_not_stored"); // Проблема: старый пароль не хранится
                }
            } catch (Exception ex) {
                log.error("Failed to rollback Keycloak changes: {}", ex.getMessage());
            }

            // Пробрасываем исключение, чтобы Spring откатил транзакцию БД
            throw new RuntimeException("Failed to update user password", e);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public UserResponse updateUserInfo(UserInfoUpdateRequest userInfoUpdateRequest) {
        User user = getCurrentUser();
        String originalEmail = user.getEmail(); // Сохраняем оригинальный email для отката

        try {
            // Обновляем данные в базе
            if (userInfoUpdateRequest.getEmail() != null && !userInfoUpdateRequest.getEmail().isEmpty()) {
                user.setEmail(userInfoUpdateRequest.getEmail());
            }
            if (userInfoUpdateRequest.getPhoneNumber() != null && !userInfoUpdateRequest.getPhoneNumber().isEmpty()) {
                user.setPhoneNumber(userInfoUpdateRequest.getPhoneNumber());
            }
            if (userInfoUpdateRequest.getName() != null && !userInfoUpdateRequest.getName().isEmpty()) {
                user.setName(userInfoUpdateRequest.getName());
            }
            if (userInfoUpdateRequest.getSurname() != null && !userInfoUpdateRequest.getSurname().isEmpty()) {
                user.setSurname(userInfoUpdateRequest.getSurname());
            }

            // Сохраняем в БД
            User updatedUser = userRepository.save(user);

            // Обновляем данные в Keycloak
            keycloakService.updateUserInfo(
                    user.getId(), // ID пользователя в Keycloak
                    userInfoUpdateRequest
            );

            return userMapper.toUserResponse(updatedUser);

        } catch (Exception e) {
            // Откатываем изменения email в случае ошибки
            user.setEmail(originalEmail);
            userRepository.save(user);

            log.error("Failed to update user info in Keycloak: {}", e.getMessage());
            throw new RuntimeException("Failed to update user information", e);
        }
    }

    public AuthResponse refreshAccessToken(RefreshRequest refreshRequest){
        Map<String, String> tokens = keycloakService.refreshToken(refreshRequest.getRefreshToken());
        return new AuthResponse(tokens.get("access_token"),tokens.get("refresh_token"),getUserIdFromToken(tokens.get("access_token")));
    }

    public boolean isAdminOrModerator() {
        return hasRole("ADMIN") || hasRole("MODERATOR");
    }


    public User getCurrentUser(){
        return userRepository.findById(getCurrentUserId())
                .orElseThrow(()-> new ResourceNotFoundException("User not found")
                );
    }

    public User getUserById(String id){
        return userRepository.findById(id).orElseThrow(()->{return new ResourceNotFoundException("User not found");});
    }

    public AuthResponse createModerator(UserRequest userRequest){
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        AuthResponse keycloakUser = null;

        try {
            keycloakUser = keycloakService.createModerator(userRequest);
            User user = userMapper.toUser(userRequest);
            user.setId(keycloakUser.getUserId());
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            roleRepository.findById(2).ifPresent(user::setRole);

            userRepository.save(user);

            transactionManager.commit(status);
            return keycloakUser;
        } catch (Exception e) {
            transactionManager.rollback(status);
            if (keycloakUser != null && keycloakUser.getUserId() != null) {
                try {
                    keycloakService.deleteUserById(keycloakUser.getUserId());
                } catch (Exception ex) {
                    log.error("Failed to rollback keycloak user creation", ex);
                }
            }
            throw new UserCreationException("Failed to create user" + e.getMessage(), e);
        }
    }

}
