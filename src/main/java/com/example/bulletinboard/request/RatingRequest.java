package com.example.bulletinboard.request;

import com.example.bulletinboard.entity.User;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingRequest {
    @NotBlank
    private User toUser;

    @NotBlank(message = "Текст отзыва не может быть пустым")
    @Size(max = 1000, message = "Отзыв слишком длинный")
    private String text;

    @Min(value = 1, message = "Рейтинг не может быть меньше 1")
    @Max(value = 5, message = "Рейтинг не может быть больше 5")
    private int rating;
}
