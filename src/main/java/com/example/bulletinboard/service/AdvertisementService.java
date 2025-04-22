package com.example.bulletinboard.service;

import com.example.bulletinboard.entity.Advertisement;
import com.example.bulletinboard.entity.Category;
import com.example.bulletinboard.entity.Image;
import com.example.bulletinboard.entity.User;
import com.example.bulletinboard.exceptions.ResourceNotFoundException;
import com.example.bulletinboard.mapper.AdvertisementMapper;
import com.example.bulletinboard.repository.AdvertisementRepository;
import com.example.bulletinboard.repository.CategoryRepository;
import com.example.bulletinboard.repository.UserRepository;
import com.example.bulletinboard.request.AdvertisementRequest;
import com.example.bulletinboard.request.AdvertisementUpdateRequest;
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
    private final CategoryService categoryService;

    private final UserService userService;

    @Transactional
    public List<AdvertisementResponse> getAdvertisementsByCategory(String categoryName){
        Category category = categoryService.getCategoryByName(categoryName);
        Integer limit = 60;
        List<Advertisement> advertisementList = advertisementRepository.findByCategoryId(category.getId(), limit);
        return advertisementMapper.toAdvertisementResponses(advertisementList);
    }


    @Transactional
    public void createAdvertisement(AdvertisementRequest advertisementRequest, List<MultipartFile> images) {
        //Добавить получение пользователя, который инициировал создание объявления
        User user = userService.getCurrentUser();
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
        if(limit == null){
            limit = 20;
        }
        return advertisementMapper.toAdvertisementResponses(advertisementRepository.findLastAds(limit));
    }

    public AdvertisementResponse getAdvertisementById(Long id) {
        Optional<Advertisement> advertisement = advertisementRepository.findById(id);
        return advertisement.map(advertisementMapper::toAdvertisementResponse).orElse(null);
    }

    public void updateAdvertisement(Long id, AdvertisementUpdateRequest advertisementUpdateRequest, List<MultipartFile> images) {

    }
    public void deleteAdvertisementById(Long id) {
        if (advertisementRepository.existsById(id)) {
            advertisementRepository.deleteById(id);
        } else {
            throw new RuntimeException("Ad with id" + id + "doesn't exist");
        }
    }

    public List<AdvertisementResponse> getAdvertisementsCurrentUser(){

        return advertisementMapper.toAdvertisementResponses(advertisementRepository.findAllByUserId(
                userRepository.getIdByUsername(userService.getCurrentUsername())
                        .orElseThrow(()->{return new RuntimeException("User not found");})));
    }


    public boolean existWithId(Long id){
        return advertisementRepository.existsById(id);
    }

    public Advertisement getAdvertisementEntityById(Long id){
        return advertisementRepository.findById(id).
                orElseThrow(()->{return new ResourceNotFoundException("Advertisement not found id:"+ id);
        });
    }

}
