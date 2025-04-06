package com.example.bulletinboard.request;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementRequest {

    @NotBlank(message = "Название должно быть передано")
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private Long category;

    @Positive
    @NotBlank
    private double price;

    private List<MultipartFile> images;
}
