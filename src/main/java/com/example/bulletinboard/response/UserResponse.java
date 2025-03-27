package com.example.bulletinboard.response;

import com.example.bulletinboard.entity.Rating;
import com.example.bulletinboard.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;

    private String username;

    private String email;

    private String phoneNumber;

    private Role role;

    private List<Rating> ratings;
}
