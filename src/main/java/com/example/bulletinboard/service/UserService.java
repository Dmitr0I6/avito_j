package com.example.bulletinboard.service;

import com.example.bulletinboard.entity.Role;
import com.example.bulletinboard.entity.User;
import com.example.bulletinboard.exceptions.UserCreationException;
import com.example.bulletinboard.mapper.UserMapper;
import com.example.bulletinboard.repository.RoleRepository;
import com.example.bulletinboard.repository.UserRepository;
import com.example.bulletinboard.request.UserRequest;
import com.example.bulletinboard.response.UserResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


import java.util.List;
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

    public UserResponse findUserById(long id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.map(userMapper::toUserResponse).orElse(null);
    }

    public User createUser(UserRequest userRequest) {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        String keycloakUserId = keycloakService.createUser(userRequest);
        try {
            User user = userMapper.toUser(userRequest);
            user.setId(keycloakUserId);
            roleRepository.findById(1).ifPresent(user::setRole);
            User savedUser = userRepository.save(user);

            transactionManager.commit(status);
            return savedUser;
        } catch (Exception e){
            transactionManager.rollback(status);
            if(keycloakUserId != null){
                try{
                    keycloakService.deleteUserById(keycloakUserId);
                }catch (Exception ex){
                    log.error("Failed to rollback eycloak user creation",ex);
                }
            }
            throw new UserCreationException("Failed to create user" + e.getMessage(),e);
        }
    }

    public void updateUser(UserRequest userRequest) {}

    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    public List<UserResponse> findAllUsers() {
        return userMapper.toUserResponses(userRepository.findAll());
    }
}
