package com.example.bulletinboard.service;

import com.example.bulletinboard.entity.User;
import com.example.bulletinboard.mapper.UserMapper;
import com.example.bulletinboard.repository.RoleRepository;
import com.example.bulletinboard.repository.UserRepository;
import com.example.bulletinboard.request.UserRequest;
import com.example.bulletinboard.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    public UserResponse findUserById(long id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.map(userMapper::toUserResponse).orElse(null);
    }

    public void createUser(UserRequest userRequest) {
        User user = userMapper.toUser(userRequest);
        user.setRole(roleRepository.findByName("ROLE_USER"));
        userRepository.save(user);
    }

    public void updateUser(UserRequest userRequest) {}

    public void deleteUser(long id) {}

    public List<UserResponse> findAllUsers() {
        return userMapper.toUserResponses(userRepository.findAll());
    }
}
