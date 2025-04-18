package com.example.bulletinboard.service;

import com.example.bulletinboard.entity.Category;
import com.example.bulletinboard.exceptions.ResourceNotFoundException;
import com.example.bulletinboard.mapper.CategoryMapper;
import com.example.bulletinboard.repository.CategoryRepository;
import com.example.bulletinboard.request.CategoryRequest;
import com.example.bulletinboard.response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryResponse> getAllCategories(){
        List<Category>  categoryList = categoryRepository.getAllCategories();

        return categoryMapper.toCategoryResponseList(categoryList);
    }

    @Transactional
    public void createCategory(CategoryRequest categoryRequest){

        Category categoryParent = categoryRepository.findByCategoryNameIgnoreCase(categoryRequest.getParentCategoryName())
                .orElseThrow(()->new RuntimeException("Category did not exist with name"+ categoryRequest.getCategoryName()));
        Long newId = categoryRepository.findMaxId().orElse(0L) + 1;
        Category category = categoryMapper.toCategory(categoryRequest,categoryParent.getId());
        category.setId(newId);
        categoryRepository.save(category);
    }

    public CategoryResponse getCategory(Long id){
        return categoryMapper.toCategoryResponse(categoryRepository.findById(id).orElse(null));
    }

    public CategoryResponse updateCategory(Long id, Map<String, Object> updates){
        Category category = categoryRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("Category not found"));

        updates.forEach((key,value) -> {
            switch (key){
                case "parentCategoryId":
                    category.setParentCategoryId((Long)value);
                    break;
                case "categoryName":
                    category.setCategoryName((String) value);
                    break;
                case "description":
                    category.setDescription((String) value);
                    break;
            }
        });
        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    public void deleteCategory(Long id){
        if(categoryRepository.existsByParentCategoryId(id)){
            throw new IllegalStateException("Нельзя удалить категорию с дочерними элементами");
        }
        categoryRepository.deleteById(id);
    }

}
