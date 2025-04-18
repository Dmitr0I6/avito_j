package com.example.bulletinboard.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    @NotBlank
    @Size(min = 5, max = 25, message = "Длина имени пользователя от 5 до 25 символов")
    private String username;

    @NotBlank
    @Size(min = 8, max = 20, message = "Длина пароля от 8 до 20 символов")
    private String password;

    @NotBlank
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
    private String phoneNumber;

    @NotBlank
    private String name;

    @NotBlank
    private String surname;
}
