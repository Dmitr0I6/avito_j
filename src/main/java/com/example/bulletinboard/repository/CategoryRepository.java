package com.example.bulletinboard.repository;

import com.example.bulletinboard.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = "SELECT category_id FROM ad_category WHERE LOWER(category_name) = LOWER(:categoryName)",nativeQuery = true)
    Integer getCategoryIdByName(@Param("categoryName") String categoryName);

    @Query(value = "SELECT category_id, category_name, category_descr, parent_categ_id FROM ad_category order by parent_categ_id, category_id",nativeQuery = true)
    List<Category> getAllCategories();

    @Query(value = "SELECT MAX(category_id) FROM ad_category", nativeQuery = true)
    Optional<Long> findMaxId();

    Optional<Category> findByCategoryNameIgnoreCase(String categoryName);

    boolean existsByParentCategoryId(Long parentId);
}
