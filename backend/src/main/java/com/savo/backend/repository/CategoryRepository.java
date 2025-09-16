package com.savo.backend.repository;

import com.savo.backend.model.Category;
import com.savo.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {

    // User-specific categories
    List<Category> findByUser(User user);
    List<Category> findByUserAndIsActiveTrue(User user);

    // System categories
    @Query("SELECT c FROM Category c WHERE c.user IS NULL AND c.isActive = true ORDER BY c.name")
    List<Category> findSystemCategories();

    // System + User categories
    @Query("SELECT c FROM Category c WHERE (c.user.id = :userId OR c.user IS NULL) AND c.isActive = true ORDER BY c.name")
    List<Category> findAllAvaliableForUser(@Param("userId") String userId);

    // Parent categories for user (system + custom)
    @Query("SELECT c FROM Category c WHERE (c.user.id = :userId OR c.user IS NULL) AND c.parentCategory IS NULL AND c.isActive ORDER BY c.name")
    List<Category> findAllParentCategories(@Param("userId") String userId);

    // User's custom parent categories only
    List<Category> findByUserAndParentCategoryIsNullAndIsActiveTrue(User user);

    Optional<Category> findByUserIdAndId(String userId, String categoryId);

    Optional<Category> findByNameAndUserIdIsNull(String name);

    Optional<Category> findByNameAndUserId(String name, String userId);

    boolean existsByNameAndUserIdIsNull(String name);

    // System parent categories only
    @Query("SELECT c FROM Category c WHERE c.user IS NULL AND c.parentCategory IS NULL AND c.isActive ORDER BY c.name")
    List<Category> findSystemParentCategories();

    // Subcategories of a parent
    List<Category> findByParentCategoryAndIsActiveTrueOrderByName(Category parentCategory);

    // Income categories (system + user)
    @Query("SELECT c FROM Category c WHERE (c.user.id = :userId OR c.user IS NULL) AND c.isIncomeCategory = true AND c.isActive ORDER BY c.name")
    List<Category> findIncomeCategories(@Param("userId") String userId);

    // Expense categories (system + user)
    @Query("SELECT c FROM Category c WHERE (c.user.id = :userId OR c.user IS NULL) AND c.isIncomeCategory = false AND c.isActive ORDER BY c.name")
    List<Category> findExpenseCategories(@Param("userId") String userId);

    // User's custom income categories only
    List<Category> findByUserAndIsIncomeCategoryTrueAndIsActiveTrue(User user);

    // User's custom expense categories only
    List<Category> findByUserAndIsIncomeCategoryFalseAndIsActiveTrue(User user);

    // Check if category belongs to user or is system category
    @Query("SELECT c FROM Category c WHERE c.id = :categoryId AND (c.user.id = :userId OR c.user IS NULL) AND c.isIncomeCategory = :isIncome AND c.isActive = true ORDER BY c.name")
    Optional<Category> findByIdAndUserAccess(@Param("categoryId") String categoryId, @Param("userId") String userId);

    // Search
    @Query("SELECT c FROM Category c WHERE (c.user.id = :userId OR c.user IS NULL) AND LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND c.isActive = true ORDER BY c.name")
    List<Category> searchCategoriesForUser(@Param("userId") String userId, @Param("searchTerm") String searchTerm);
}
