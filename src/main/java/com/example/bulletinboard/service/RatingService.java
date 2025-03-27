package com.example.bulletinboard.service;

import com.example.bulletinboard.entity.Rating;
import com.example.bulletinboard.entity.User;
import com.example.bulletinboard.mapper.RatingMapper;
import com.example.bulletinboard.repository.RatingRepository;
import com.example.bulletinboard.repository.UserRepository;
import com.example.bulletinboard.request.RatingRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;
    private final UserRepository userRepository;


    public void rateUser(RatingRequest ratingRequest, User fromUser, User toUser) {
        if(fromUser.equals(toUser)) {
            throw new IllegalArgumentException("Отзыв самому себе");
        }
        Rating rating = ratingMapper.toRating(ratingRequest, fromUser, toUser);
        ratingRepository.save(rating);
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
