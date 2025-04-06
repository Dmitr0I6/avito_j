package com.example.bulletinboard.service;

import com.example.bulletinboard.entity.Rating;
import com.example.bulletinboard.entity.User;
import com.example.bulletinboard.mapper.RatingMapper;
import com.example.bulletinboard.repository.RatingRepository;
import com.example.bulletinboard.repository.UserRepository;
import com.example.bulletinboard.request.RatingRequest;
import com.example.bulletinboard.response.RatingResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;


    public void rateUser(RatingRequest ratingRequest) {
        User toUser = userRepository.findByUsername(ratingRequest.getToUsername()).orElseThrow((() -> new RuntimeException("To user not found")));
        User fromUser = userRepository.findByUsername(currentUserService.getCurrentUsername()).orElseThrow((() -> new RuntimeException("From user not found")));
        if(fromUser.equals(toUser)) {
            throw new IllegalArgumentException("Отзыв самому себе");
        }
        Rating rating = ratingMapper.toRating(ratingRequest, fromUser, toUser);
        rating.setCreated_at(LocalDateTime.now());
        ratingRepository.save(rating);
    }

    public List<RatingResponse> getCurrentRate(){
        User user = userRepository.findByUsername(currentUserService.getCurrentUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Optional<List<Rating>> rating = ratingRepository.findByToUser(user);
        return rating.map(ratingMapper::toRatingResponseList).orElse(null);
    }

//    public List<Rating> getUserRatings(Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
//        return ratingRepository.findByToUser(user).stream()
//                .map(ratingMapper::toRatingResponse)
//                .collect(Collectors.toList());
//
//    }


}
