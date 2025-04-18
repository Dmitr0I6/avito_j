package com.example.bulletinboard.service;

import com.example.bulletinboard.entity.Rating;
import com.example.bulletinboard.entity.User;
import com.example.bulletinboard.exceptions.ResourceNotFoundException;
import com.example.bulletinboard.mapper.RatingMapper;
import com.example.bulletinboard.repository.RatingRepository;
import com.example.bulletinboard.repository.UserRepository;
import com.example.bulletinboard.request.RatingRequest;
import com.example.bulletinboard.response.RatingResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;
    private final UserService userService;

    @Transactional
    public void rateUser(RatingRequest ratingRequest) {
        User toUser = userService.getUserByUsername(ratingRequest.getToUsername());
        User fromUser = userService.getCurrentUser();
        if (fromUser.equals(toUser)) {
            throw new IllegalArgumentException("Отзыв самому себе");
        }
        Rating rating = ratingMapper.toRating(ratingRequest, fromUser, toUser);
        rating.setCreatedAt(LocalDateTime.now());
        ratingRepository.save(rating);
    }

    @Transactional
    public List<RatingResponse> getCurrentUserRate() {
        User user = userService.getCurrentUser();
        Optional<List<Rating>> rating = ratingRepository.findByToUser(user);
        return rating.map(ratingMapper::toRatingResponseList).orElse(null);
    }

    @Transactional
    public List<RatingResponse> getUserRating(String userId) {
        User user = userService.getUserById(userId);
        return ratingMapper.toRatingResponseList(ratingRepository.findByToUser(user).orElse(null));
    }

    @Transactional
    public List<RatingResponse> getRatingByUserFrom(String userFromId){
        User userFrom = userService.getUserById(userFromId);
        return ratingMapper.toRatingResponseList(ratingRepository.findAllByFromUser(userFrom).orElse(null));
    }


    @Transactional
    public void deleteRating(Long id) {

        Rating rating = ratingRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Rating not found with id:" + id));
        if (userService.getCurrentUser().equals(rating.getFromUser())
                || userService.isAdminOrModerator()) {
            ratingRepository.deleteById(id);
        }
        else {
            throw new AccessDeniedException("Can not delete rating");
        }
    }


}
