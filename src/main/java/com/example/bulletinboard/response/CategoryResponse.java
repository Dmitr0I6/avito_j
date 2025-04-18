package com.example.bulletinboard.response;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
        private Long id;

        private String categoryName;

        private Long parentCategoryId;

        private String description;
}
