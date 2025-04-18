package com.example.bulletinboard.service;

import com.example.bulletinboard.entity.Advertisement;
import com.example.bulletinboard.entity.Category;
import com.example.bulletinboard.entity.User;
import com.example.bulletinboard.exceptions.ResourceNotFoundException;
import com.example.bulletinboard.mapper.AdvertisementMapper;
import com.example.bulletinboard.repository.AdvertisementRepository;
import com.example.bulletinboard.repository.CategoryRepository;
import com.example.bulletinboard.repository.UserRepository;
import com.example.bulletinboard.request.AdvertisementRequest;
import com.example.bulletinboard.response.AdvertisementResponse;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdvertisementServiceTest {

    @Mock
    private AdvertisementRepository advertisementRepository;

    @Mock
    private AdvertisementMapper advertisementMapper;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AdvertisementService advertisementService;

    private User testUser;
    private Category testCategory;
    private Advertisement testAd;
    private AdvertisementRequest testAdRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("userId");
        testUser.setUsername("testuser");

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setCategoryName("Test Category");

        testAd = new Advertisement();
        testAd.setId(1L);
        testAd.setUser(testUser);
        testAd.setCategory(testCategory);
        testAd.setTitle("Test Ad");
        testAd.setDescription("Test Description");
        testAd.setPrice(100.0);
        testAd.setCreatedAt(LocalDateTime.now());
        testAd.setUpdatedAt(LocalDateTime.now());

        testAdRequest = new AdvertisementRequest();
        testAdRequest.setTitle("Test Ad");
        testAdRequest.setDescription("Test Description");
        testAdRequest.setPrice(100.0);
        testAdRequest.setCategory(1L);
    }

    @Test
    @Transactional
    void createAdvertisement_ShouldSaveNewAdvertisementWithImages() {
        // Arrange
        List<MultipartFile> testImages = new ArrayList<>();
        List<String> imageUrls = List.of("url1", "url2");

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(categoryRepository.getReferenceById(anyLong())).thenReturn(testCategory);
        when(cloudinaryService.uploadImages(anyList())).thenReturn(imageUrls);
        when(advertisementMapper.toAdvertisement(any(), any(), any())).thenReturn(testAd);
        when(advertisementRepository.save(any(Advertisement.class))).thenReturn(testAd);

        // Act
        advertisementService.createAdvertisement(testAdRequest, testImages);

        // Assert
        verify(advertisementRepository).save(testAd);
        assertEquals(2, testAd.getImages().size());
        assertNotNull(testAd.getCreatedAt());
        assertNotNull(testAd.getUpdatedAt());
    }

    @Test
    void getAdvertisementList_ShouldReturnLimitedAdvertisements() {
        // Arrange
        int limit = 5;
        List<Advertisement> ads = new ArrayList<>();
        ads.add(testAd);

        when(advertisementRepository.findLastAds(limit)).thenReturn(ads);
        when(advertisementMapper.toAdvertisementResponses(ads)).thenReturn(List.of(new AdvertisementResponse()));

        // Act
        List<AdvertisementResponse> result = advertisementService.getAdvertisementList(limit);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(advertisementRepository).findLastAds(limit);
    }

    @Test
    void getAdvertisementList_ShouldUseDefaultLimitWhenNull() {
        // Arrange
        List<Advertisement> ads = new ArrayList<>();
        ads.add(testAd);

        when(advertisementRepository.findLastAds(20)).thenReturn(ads);
        when(advertisementMapper.toAdvertisementResponses(ads)).thenReturn(List.of(new AdvertisementResponse()));

        // Act
        List<AdvertisementResponse> result = advertisementService.getAdvertisementList(null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(advertisementRepository).findLastAds(20);
    }

    @Test
    void getAdvertisementById_ShouldReturnAdvertisementWhenExists() {
        // Arrange
        AdvertisementResponse expectedResponse = new AdvertisementResponse();
        expectedResponse.setId(1L);

        when(advertisementRepository.findById(1L)).thenReturn(Optional.of(testAd));
        when(advertisementMapper.toAdvertisementResponse(testAd)).thenReturn(expectedResponse);

        // Act
        AdvertisementResponse result = advertisementService.getAdvertisementById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getAdvertisementById_ShouldReturnNullWhenNotExists() {
        // Arrange
        when(advertisementRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        AdvertisementResponse result = advertisementService.getAdvertisementById(1L);

        // Assert
        assertNull(result);
    }

    @Test
    void deleteAdvertisementById_ShouldDeleteWhenExists() {
        // Arrange
        when(advertisementRepository.existsById(1L)).thenReturn(true);

        // Act
        advertisementService.deleteAdvertisementById(1L);

        // Assert
        verify(advertisementRepository).deleteById(1L);
    }

    @Test
    void deleteAdvertisementById_ShouldThrowExceptionWhenNotExists() {
        // Arrange
        when(advertisementRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> advertisementService.deleteAdvertisementById(1L));
        verify(advertisementRepository, never()).deleteById(anyLong());
    }

    @Test
    void getAdvertisementsCurrentUser_ShouldReturnUserAdvertisements() {
        // Arrange
        List<Advertisement> userAds = new ArrayList<>();
        userAds.add(testAd);

        when(userService.getCurrentUsername()).thenReturn("testuser");
        when(userRepository.getIdByUsername("testuser")).thenReturn(Optional.of("userId"));
        when(advertisementRepository.findAllByUserId("userId")).thenReturn(userAds);
        when(advertisementMapper.toAdvertisementResponses(userAds)).thenReturn(List.of(new AdvertisementResponse()));

        // Act
        List<AdvertisementResponse> result = advertisementService.getAdvertisementsCurrentUser();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(advertisementRepository).findAllByUserId("userId");
    }

    @Test
    void existWithId_ShouldReturnTrueWhenExists() {
        // Arrange
        when(advertisementRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = advertisementService.existWithId(1L);

        // Assert
        assertTrue(result);
    }

    @Test
    void existWithId_ShouldReturnFalseWhenNotExists() {
        // Arrange
        when(advertisementRepository.existsById(1L)).thenReturn(false);

        // Act
        boolean result = advertisementService.existWithId(1L);

        // Assert
        assertFalse(result);
    }

    @Test
    void getAdvertisementEntityById_ShouldReturnAdvertisementWhenExists() {
        // Arrange
        when(advertisementRepository.findById(1L)).thenReturn(Optional.of(testAd));

        // Act
        Advertisement result = advertisementService.getAdvertisementEntityById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getAdvertisementEntityById_ShouldThrowExceptionWhenNotExists() {
        // Arrange
        when(advertisementRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                advertisementService.getAdvertisementEntityById(1L));
    }


}
