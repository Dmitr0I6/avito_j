package com.example.bulletinboard.response;

import com.example.bulletinboard.entity.Category;
import com.example.bulletinboard.entity.Image;
import com.example.bulletinboard.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AdvertisementResponse {
    private long id;
    private CategoryResponse category;
    private UserResponse user;
    private String title;
    private String description;
    private List<Image> images;
    private double price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
