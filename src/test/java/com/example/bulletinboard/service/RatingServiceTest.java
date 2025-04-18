package com.example.bulletinboard.service;

import com.example.bulletinboard.entity.Rating;
import com.example.bulletinboard.entity.User;
import com.example.bulletinboard.exceptions.ResourceNotFoundException;
import com.example.bulletinboard.mapper.RatingMapper;
import com.example.bulletinboard.repository.RatingRepository;
import com.example.bulletinboard.request.RatingRequest;
import com.example.bulletinboard.response.RatingResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RatingServiceTest {
    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private RatingMapper ratingMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private RatingService ratingService;

    private User testFromUser;
    private User testToUser;
    private Rating testRating;
    private RatingRequest testRatingRequest;
    private RatingResponse testRatingResponse;

    @BeforeEach
    void setUp() {
        testFromUser = new User();
        testFromUser.setId("from-user-id");
        testFromUser.setUsername("fromUser");

        testToUser = new User();
        testToUser.setId("to-user-id");
        testToUser.setUsername("toUser");

        testRating = new Rating();
        testRating.setId(1L);
        testRating.setFromUser(testFromUser);
        testRating.setToUser(testToUser);
        testRating.setText("Great service!");
        testRating.setRating(5);
        testRating.setCreatedAt(LocalDateTime.now());

        testRatingRequest = new RatingRequest();
        testRatingRequest.setToUsername("toUser");
        testRatingRequest.setText("Great service!");
        testRatingRequest.setRating(5);

        testRatingResponse = new RatingResponse();
        testRatingResponse.setId(1L);
        testRatingResponse.setFromUserId("from-user-id");
        testRatingResponse.setFromUserName("fromUser");
        testRatingResponse.setText("Great service!");
        testRatingResponse.setRating(5);
        testRatingResponse.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void rateUser_ShouldSaveNewRating() {
        // Arrange
        when(userService.getUserByUsername("toUser")).thenReturn(testToUser);
        when(userService.getCurrentUser()).thenReturn(testFromUser);
        when(ratingMapper.toRating(any(), any(), any())).thenReturn(testRating);
        when(ratingRepository.save(any(Rating.class))).thenReturn(testRating);

        // Act
        ratingService.rateUser(testRatingRequest);

        // Assert
        verify(ratingRepository).save(testRating);
        assertNotNull(testRating.getCreatedAt());
    }

    @Test
    void rateUser_ShouldThrowExceptionWhenRatingSelf() {
        // Arrange
        when(userService.getUserByUsername("toUser")).thenReturn(testFromUser);
        when(userService.getCurrentUser()).thenReturn(testFromUser);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                ratingService.rateUser(testRatingRequest));
        verify(ratingRepository, never()).save(any());
    }

    @Test
    void getCurrentUserRate_ShouldReturnRatingsForCurrentUser() {
        // Arrange
        when(userService.getCurrentUser()).thenReturn(testToUser);
        when(ratingRepository.findByToUser(testToUser)).thenReturn(Optional.of(List.of(testRating)));
        when(ratingMapper.toRatingResponseList(List.of(testRating))).thenReturn(List.of(testRatingResponse));

        // Act
        List<RatingResponse> result = ratingService.getCurrentUserRate();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testRatingResponse, result.get(0));
    }

    @Test
    void getCurrentUserRate_ShouldReturnNullWhenNoRatings() {
        // Arrange
        when(userService.getCurrentUser()).thenReturn(testToUser);
        when(ratingRepository.findByToUser(testToUser)).thenReturn(Optional.empty());

        // Act
        List<RatingResponse> result = ratingService.getCurrentUserRate();

        // Assert
        assertNull(result);
    }

    @Test
    void getUserRating_ShouldReturnRatingsForUser() {
        // Arrange
        when(userService.getUserById("to-user-id")).thenReturn(testToUser);
        when(ratingRepository.findByToUser(testToUser)).thenReturn(Optional.of(List.of(testRating)));
        when(ratingMapper.toRatingResponseList(List.of(testRating))).thenReturn(List.of(testRatingResponse));

        // Act
        List<RatingResponse> result = ratingService.getUserRating("to-user-id");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testRatingResponse, result.get(0));
    }

    @Test
    void getRatingByUserFrom_ShouldReturnRatingsFromUser() {
        // Arrange
        when(userService.getUserById("from-user-id")).thenReturn(testFromUser);
        when(ratingRepository.findAllByFromUser(testFromUser)).thenReturn(Optional.of(List.of(testRating)));
        when(ratingMapper.toRatingResponseList(List.of(testRating))).thenReturn(List.of(testRatingResponse));

        // Act
        List<RatingResponse> result = ratingService.getRatingByUserFrom("from-user-id");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testRatingResponse, result.get(0));
    }

    @Test
    void deleteRating_ShouldDeleteWhenOwner() {
        // Arrange
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(testRating));
        when(userService.getCurrentUser()).thenReturn(testFromUser);

        // Act
        ratingService.deleteRating(1L);

        // Assert
        verify(ratingRepository).deleteById(1L);
    }

    @Test
    void deleteRating_ShouldDeleteWhenAdmin() {
        // Arrange
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(testRating));
        when(userService.getCurrentUser()).thenReturn(new User()); // Not owner
        when(userService.isAdminOrModerator()).thenReturn(true);

        // Act
        ratingService.deleteRating(1L);

        // Assert
        verify(ratingRepository).deleteById(1L);
    }

    @Test
    void deleteRating_ShouldThrowWhenNotFound() {
        // Arrange
        when(ratingRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                ratingService.deleteRating(1L));
        verify(ratingRepository, never()).deleteById(any());
    }

    @Test
    void deleteRating_ShouldThrowWhenNotAuthorized() {
        // Arrange
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(testRating));
        when(userService.getCurrentUser()).thenReturn(new User()); // Not owner
        when(userService.isAdminOrModerator()).thenReturn(false);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () ->
                ratingService.deleteRating(1L));
        verify(ratingRepository, never()).deleteById(any());
    }
}
