package com.example.bulletinboard.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingResponse {
    private Long id;
    private Long fromUserId;
    private String fromUserName;
    private String text;
    private int rating;
    private LocalDateTime createdAt;
}
