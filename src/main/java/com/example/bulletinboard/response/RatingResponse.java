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
    private String fromUserId;
    private String fromUserName;
    private String text;
    private Integer rating;
    private LocalDateTime createdAt;
}
