package com.example.bulletinboard.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {
        @NotBlank
        @Size(max = 100)
        private String categoryName;

        @NotBlank
        @Size(max = 100)
        private String parentCategoryName;

        @NotBlank
        @Size(max = 300)
        private String description;
}
