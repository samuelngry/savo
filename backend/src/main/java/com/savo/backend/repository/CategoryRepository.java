package com.savo.backend.repository;

import com.savo.backend.model.Category;
import com.savo.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {

    List<Category> findByUser(User user);
    List<Category> findByUserAndIsActiveTrue(User user);

    Optional<Category> findByIdAndUser(String id, User user);

    // Get only parent categories (those without a parent)
    List<Category> findByUserAndParentCategoryIsNullAndIsActiveTrue(User user);

    List<Category> findByParentCategory(Category parentCategory);

    // Check if a category name already exists for user (prevent duplications)
    boolean existsByUserAndName(User user, String name);

    // Get all income categories for a user
    List<Category> findByUserAndIsIncomeCategoryTrueAndIsActiveTrue(User user);

    // Get all expense categories for a user
    List<Category> findByUserAndIsIncomeCategoryFalseAndIsActiveTrue(User user);
}
