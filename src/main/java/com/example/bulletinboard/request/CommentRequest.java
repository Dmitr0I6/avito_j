package com.example.bulletinboard.request;

import com.example.bulletinboard.entity.Advertisement;
import com.example.bulletinboard.entity.User;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequest {

    @NotBlank
    private Long advertisement;

    @NotBlank
    @Max(value = 400, message = "Комментарий слишком длинный")
    private String text;
}
