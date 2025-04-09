package com.example.bulletinboard.request;

import com.example.bulletinboard.entity.Advertisement;
import com.example.bulletinboard.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequest {

    private Long advertisement;

    private String text;
}
