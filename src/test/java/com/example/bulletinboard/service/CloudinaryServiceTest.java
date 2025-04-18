package com.example.bulletinboard.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudinaryServiceTest {

    @Mock
    private Cloudinary cloudinary;
    @Mock
    private Uploader uploader;
    @Mock
    private MultipartFile validFile;
    @Mock
    private MultipartFile invalidFile;
    @Mock
    private MultipartFile emptyFile;

    private CloudinaryService cloudinaryService;

    @BeforeEach
    void setUp() {
        when(cloudinary.uploader()).thenReturn(uploader);
        cloudinaryService = new CloudinaryService(cloudinary);
    }

    @Test
    void constructor_ShouldInitializeWithCloudinary() {
        assertNotNull(cloudinaryService);
        verify(cloudinary).uploader();
    }

    @Test
    void uploadImage_ShouldReturnUrlWhenUploadSuccessful() throws Exception {
        // Arrange
        String expectedUrl = "https://res.cloudinary.com/demo/image/upload/test.jpg";
        when(uploader.upload(any(byte[].class), anyMap()))
                .thenReturn(Map.of("url", expectedUrl));
        when(validFile.getBytes()).thenReturn("test image content".getBytes());

        // Act
        String result = cloudinaryService.uploadImage(validFile);

        // Assert
        assertEquals(expectedUrl, result);
        verify(uploader).upload(any(byte[].class), anyMap());
    }

    @Test
    void uploadImage_ShouldThrowExceptionWhenFileReadFails() throws Exception {
        // Arrange
        when(invalidFile.getBytes()).thenThrow(new IOException("File read error"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> cloudinaryService.uploadImage(invalidFile),
                "Expected RuntimeException when file read fails");

        verifyNoInteractions(uploader);
    }

    @Test
    void uploadImages_ShouldReturnListOfUrlsForMultipleFiles() throws Exception {
        // Arrange
        String url1 = "https://res.cloudinary.com/demo/image/upload/test1.jpg";
        String url2 = "https://res.cloudinary.com/demo/image/upload/test2.jpg";

        when(uploader.upload(any(byte[].class), anyMap()))
                .thenReturn(Map.of("url", url1))
                .thenReturn(Map.of("url", url2));

        when(validFile.getBytes()).thenReturn("content1".getBytes());
        when(emptyFile.getBytes()).thenReturn("content2".getBytes());

        // Act
        List<String> results = cloudinaryService.uploadImages(List.of(validFile, emptyFile));

        // Assert
        assertEquals(2, results.size());
        assertTrue(results.containsAll(List.of(url1, url2)));
        verify(uploader, times(2)).upload(any(byte[].class), anyMap());
    }

    @Test
    void uploadImages_ShouldReturnEmptyListForEmptyInput() {
        // Act
        List<String> results = cloudinaryService.uploadImages(Collections.emptyList());

        // Assert
        assertTrue(results.isEmpty());
        verifyNoInteractions(uploader);
    }

    @Test
    void uploadImage_ShouldHandleNullFile() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> cloudinaryService.uploadImage(null),
                "Expected IllegalArgumentException for null file");

        verifyNoInteractions(uploader);
    }
}
