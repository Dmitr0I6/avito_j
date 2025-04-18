package com.example.bulletinboard.service;

import com.example.bulletinboard.entity.Category;
import com.example.bulletinboard.exceptions.ResourceNotFoundException;
import com.example.bulletinboard.mapper.CategoryMapper;
import com.example.bulletinboard.repository.CategoryRepository;
import com.example.bulletinboard.response.CategoryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;


    @Test
    void getAllCategories_ShouldReturnListOfCategoryResponses() {
        List<Category> mockCategories = List.of(
                new Category(1L, "Electronics", 0L, "Electronic devices"),
                new Category(2L, "Books", 0L, "All kinds of books")
        );

        List<CategoryResponse> mockResponses = List.of(
                new CategoryResponse(1L, "Electronics", 0L, "Electronic devices"),
                new CategoryResponse(2L, "Books", 0L, "All kinds of books")
        );

        when(categoryRepository.getAllCategories()).thenReturn(mockCategories);
        when(categoryMapper.toCategoryResponseList(mockCategories)).thenReturn(mockResponses);

        List<CategoryResponse> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        assertEquals("Electronics", result.get(0).getCategoryName());
        verify(categoryRepository,times(1)).getAllCategories();
    }

    @Test
    void getCategory_ShouldReturnCategoryResponse() {
        // Arrange
        Category category = new Category(1L, "Phones", 0L, "Mobile phones");
        CategoryResponse response = new CategoryResponse(1L, "Phones", 0L, "Mobile phones");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toCategoryResponse(category)).thenReturn(response);

        // Act
        CategoryResponse result = categoryService.getCategory(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Phones", result.getCategoryName());
    }

    @Test
    void updateCategory_ShouldUpdateExistingCategory() {
        // Arrange
        Category category = new Category(1L, "OldName", 0L, "OldDesc");
        Category updatedCategory = new Category(1L, "NewName", 0L, "NewDesc");
        CategoryResponse response = new CategoryResponse(1L, "NewName", 0L, "NewDesc");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
        when(categoryMapper.toCategoryResponse(updatedCategory)).thenReturn(response);

        // Act
        CategoryResponse result = categoryService.updateCategory(1L, Map.of(
                "categoryName", "NewName",
                "description", "NewDesc"
        ));

        // Assert
        assertEquals("NewName", result.getCategoryName());
        assertEquals("NewDesc", result.getDescription());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void updateCategory_ShouldThrowExceptionWhenCategoryNotFound() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.updateCategory(1L, Map.of("categoryName", "NewName")));
    }

    @Test
    void deleteCategory_ShouldDeleteWhenNoChildren() {
        // Arrange
        when(categoryRepository.existsByParentCategoryId(1L)).thenReturn(false);

        // Act
        categoryService.deleteCategory(1L);

        // Assert
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteCategory_ShouldThrowExceptionWhenHasChildren() {
        // Arrange
        when(categoryRepository.existsByParentCategoryId(1L)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> categoryService.deleteCategory(1L));
        verify(categoryRepository, never()).deleteById(any());
    }
}