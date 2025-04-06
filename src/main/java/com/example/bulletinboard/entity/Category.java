package com.example.bulletinboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ad_category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")

    private Long id;

    @Column(name = "category_name",unique = true, nullable = false, length = 50)
    private String categoryName;

    @Column(name = "parent_categ_id",nullable = false)
    private Long parentCategoryId;

    @Column(name = "category_descr", nullable = false, length = 300)
    private String description;
}
