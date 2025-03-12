package com.example.bulletinboard.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ad_category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private long id;
    @Column(name = "category_name",unique = true, nullable = false, length = 50)
    private String categoryName;
    @Column(name = "category_descr",unique = true, nullable = false, length = 300)
    private String description;
}
