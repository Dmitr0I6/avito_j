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
    @Mapping(target = "createdAt",ignore = true)
    @Mapping(target = "fromUser",source = "fromUser")
    @Mapping(target = "toUser", source = "toUser")
    @Mapping(target = "rating", source = "rating.rating")
    Rating toRating(RatingRequest rating,User fromUser, User toUser);

    @Mapping(target = "fromUserId",source = "rating.fromUser.id")
    @Mapping(target = "fromUserName", source = "rating.fromUser.username")
    @Mapping(target = "createdAt", source = "rating.createdAt")
    RatingResponse toRatingResponse(Rating rating);

    @Mapping(target = "fromUserId",source = "rating.fromUser.id")
    @Mapping(target = "fromUserName", source = "rating.fromUser.username")
    List<RatingResponse> toRatingResponseList(List<Rating> list);
}
