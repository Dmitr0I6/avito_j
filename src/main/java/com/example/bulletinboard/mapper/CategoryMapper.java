package com.example.bulletinboard.mapper;

import com.example.bulletinboard.entity.Category;
import com.example.bulletinboard.request.CategoryRequest;
import com.example.bulletinboard.response.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentCategoryId", source = "parentCategoryId")
    Category toCategory(CategoryRequest categoryRequest, Integer parentCategoryId);

    CategoryResponse toCategoryResponse(Category category);

    List<CategoryResponse> toCategoryResponseList(List<Category> categoryList);
}
