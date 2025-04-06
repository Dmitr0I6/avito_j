package com.example.bulletinboard.service;

import com.example.bulletinboard.entity.Advertisement;
import com.example.bulletinboard.entity.Category;
import com.example.bulletinboard.entity.Image;
import com.example.bulletinboard.entity.User;
import com.example.bulletinboard.mapper.AdvertisementMapper;
import com.example.bulletinboard.repository.AdvertisementRepository;
import com.example.bulletinboard.repository.CategoryRepository;
import com.example.bulletinboard.repository.UserRepository;
import com.example.bulletinboard.request.AdvertisementRequest;
import com.example.bulletinboard.response.AdvertisementResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final AdvertisementMapper advertisementMapper;
    private final CloudinaryService cloudinary;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public void createAdvertisement(AdvertisementRequest advertisementRequest, List<MultipartFile> images) {
        //Добавить получение пользователя, который инициировал создание объявления
        User user = userRepository.findByUsername(currentUserService.getCurrentUsername()).orElseThrow(()->new RuntimeException("User not found"));
        Category category = categoryRepository.getReferenceById(advertisementRequest.getCategory());
        Advertisement advertisement = advertisementMapper.toAdvertisement(advertisementRequest, user, category);
        advertisement.setCreatedAt(LocalDateTime.now());
        advertisement.setUpdatedAt(LocalDateTime.now());
        //Загрузка изображений в облако и получение списка url
        List<String> imagesUrl = cloudinary.uploadImages(images);
        for (String imageUrl : imagesUrl) {
            Image image = new Image();
            image.setUrl(imageUrl);
            image.setAd(advertisement);
            advertisement.addImage(image);
        }
        advertisementRepository.save(advertisement);
    }

    public List<AdvertisementResponse> getAdvertisementList(Integer limit) {
        return advertisementMapper.toAdvertisementResponses(advertisementRepository.findLastAds(limit));
    }

    public AdvertisementResponse getAdvertisementById(Long id) {
        Optional<Advertisement> advertisement = advertisementRepository.findById(id);
        return advertisement.map(advertisementMapper::toAdvertisementResponse).orElse(null);
    }

    public void updateAdvertisement(Long id, AdvertisementRequest advertisementRequest, List<MultipartFile> images) {

    }
    public void deleteAdvertisementById(Long id) {
        advertisementRepository.deleteById(id);
    }
}
