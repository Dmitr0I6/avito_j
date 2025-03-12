package com.example.bulletinboard.entity;

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
    @JoinColumn(name = "ad_id")
    private Advertisement ad;
    @Column(name = "img_url")
    private String url;
}
