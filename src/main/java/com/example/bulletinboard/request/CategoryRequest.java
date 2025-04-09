package com.example.bulletinboard.request;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {

        private String categoryName;

        private String parentCategoryName;

        private String description;
}
