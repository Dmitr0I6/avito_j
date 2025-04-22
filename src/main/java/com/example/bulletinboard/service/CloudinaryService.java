package com.example.bulletinboard.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
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
            log.error("Error uploading image to Cloudinary{}", e.getMessage());
            throw new RuntimeException("Failed to upload image to Cloudinary: " + e.getMessage(), e);
        }
    }

}

