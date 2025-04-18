package com.example.bulletinboard.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;
    private final Uploader uploader;

    public CloudinaryService() {
        Dotenv dotenv = Dotenv.load();
        this.cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));
        this.uploader = cloudinary.uploader();
    }

    // Package-private конструктор для тестов
    CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
        this.uploader = cloudinary.uploader();
    }

    public List<String> uploadImages(List<MultipartFile> images) {
        if (images == null) {
            return Collections.emptyList();
        }

        return images.stream()
                .filter(Objects::nonNull)
                .map(this::uploadImage)
                .collect(Collectors.toList());
    }

    public String uploadImage(MultipartFile image) {
        if (image == null) {
            throw new IllegalArgumentException("Image file cannot be null");
        }

        try {
            Map<?, ?> uploadResult = uploader.upload(image.getBytes(), ObjectUtils.emptyMap());
            return (String) uploadResult.get("url");
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image to Cloudinary: " + e.getMessage(), e);
        }
    }

//    public CloudinaryService() {
//        Dotenv dotenv = Dotenv.load();
//        this.cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));
//        System.out.println(cloudinary.config.cloudName);
//    }
//
//    public List<String> uploadImages(List<MultipartFile> images) {
//        return images.stream().map(this::uploadImage).collect(Collectors.toList());
//    }
//
//    private String uploadImage(MultipartFile image) {
//        try {
//            Map<?, ?> uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
//            return (String) uploadResult.get("url");
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to upload image to Cloudinary" + e.getMessage());
//        }
//    }

}

