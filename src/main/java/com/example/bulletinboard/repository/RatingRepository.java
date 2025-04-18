package com.example.bulletinboard.repository;

import com.example.bulletinboard.entity.Rating;
import com.example.bulletinboard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<List<Rating>> findByToUser(User user);

    Optional<List<Rating>> findAllByFromUser(User userFrom);
}
