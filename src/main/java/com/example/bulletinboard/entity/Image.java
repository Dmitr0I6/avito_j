package com.example.bulletinboard.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "img_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "ad_id",nullable = false)
    @JsonIgnore
    private Advertisement ad;

    @Column(name = "img_url",nullable = false)
    private String url;
}
