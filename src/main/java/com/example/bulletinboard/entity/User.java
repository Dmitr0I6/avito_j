package com.example.bulletinboard.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_id", nullable = false)
    private long id;
    @Column(name = "username", nullable = false, length = 50)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "email", nullable = false, length = 50)
    private String email;
    @Column(name = "phone_num", nullable = false, length = 20)
    private String phoneNumber;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}
