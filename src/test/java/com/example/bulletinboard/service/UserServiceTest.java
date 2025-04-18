package com.example.bulletinboard.service;

import com.example.bulletinboard.mapper.UserMapper;
import com.example.bulletinboard.repository.RoleRepository;
import com.example.bulletinboard.repository.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private  UserRepository userRepository;
    @Mock
    private  UserMapper userMapper;
    @Mock
    private  RoleRepository roleRepository;
    @Mock
    private  KeycloakService keycloakService;
    @Mock
    private  PlatformTransactionManager transactionManager;
    @Mock
    private  PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;


}
