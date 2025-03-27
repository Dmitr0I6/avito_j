package com.example.bulletinboard.request;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDateTime;
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

    @NotBlank
    private Long userId;

    @PastOrPresent
    private LocalDateTime createdAt;

    @PastOrPresent
    private LocalDateTime updatedAt;
    private List<MultipartFile> images;
//    @JsonCreator
//    public AdvertisementRequest(
//            @JsonProperty("title") String title,
//            @JsonProperty("description") String description,
//            @JsonProperty("category") Long category,
//            @JsonProperty("price") Double price,
//            @JsonProperty("userId") Long userId,
//            @JsonProperty("createdAt") LocalDateTime createdAt,
//            @JsonProperty("updatedAt") LocalDateTime updatedAt) {
//        this.title = title;
//        this.description = description;
//        this.category = category;
//        this.price = price;
//        this.userId = userId;
//        this.createdAt = createdAt;
//        this.updatedAt = updatedAt;
//    }
}
