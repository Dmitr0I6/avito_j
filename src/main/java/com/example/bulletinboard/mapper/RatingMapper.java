package com.example.bulletinboard.mapper;

import com.example.bulletinboard.entity.Rating;
import com.example.bulletinboard.entity.User;
import com.example.bulletinboard.request.RatingRequest;
import com.example.bulletinboard.response.RatingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RatingMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created_at",ignore = true)
    @Mapping(target = "fromUser",source = "fromUser")
    @Mapping(target = "toUser", source = "toUser")
    @Mapping(target = "rating", source = "rating.rating")
    Rating toRating(RatingRequest rating,User fromUser, User toUser);

    @Mapping(target = "fromUserName", source = "rating.fromUser.username")
    RatingResponse toRatingResponse(Rating rating);

    List<RatingResponse> toRatingResponseList(List<Rating> list);
}
