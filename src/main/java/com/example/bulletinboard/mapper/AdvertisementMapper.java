package com.example.bulletinboard.mapper;


import com.example.bulletinboard.entity.Advertisement;
import com.example.bulletinboard.entity.Category;
import com.example.bulletinboard.entity.User;
import com.example.bulletinboard.request.AdvertisementRequest;
import com.example.bulletinboard.response.AdvertisementResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdvertisementMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "description", source = "advertisementRequest.description")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Advertisement toAdvertisement(AdvertisementRequest advertisementRequest, User user, Category category);

    AdvertisementResponse toAdvertisementResponse(Advertisement advertisement);

    List<AdvertisementResponse> toAdvertisementResponses(List<Advertisement> advertisements);
}
