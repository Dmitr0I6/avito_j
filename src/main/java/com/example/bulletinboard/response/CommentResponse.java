package com.example.bulletinboard.response;

import com.example.bulletinboard.entity.Advertisement;
import com.example.bulletinboard.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private Long id;

    private Long advertisement;

    private String user;

    private String text;

    private LocalDateTime createdAt;

}
