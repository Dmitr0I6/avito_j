package com.example.bulletinboard.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthUpdateRequest {

    @NotBlank
    @Size(min = 8, max = 20, message = "Длина пароля от 8 до 20 символов")
    private String password;

}
