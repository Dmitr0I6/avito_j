package com.example.bulletinboard.mapper;

import com.example.bulletinboard.entity.User;
import com.example.bulletinboard.request.UserRequest;
import com.example.bulletinboard.response.UserResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "ratings",ignore = true)
    User toUser(UserRequest userRequest);

    UserResponse toUserResponse(User user);

    List<UserResponse> toUserResponses(List<User> user);
}
