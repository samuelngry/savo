package com.savo.backend.service;

import com.savo.backend.dto.CategoryCreateDTO;
import com.savo.backend.dto.CategoryResponseDTO;
import com.savo.backend.dto.CategoryUpdateDTO;

import java.util.List;

public interface CategoryService {
    CategoryResponseDTO createCategory(String userId, CategoryCreateDTO dto);
    List<CategoryResponseDTO> getAllCategoriesForUser(String userId);
    List<CategoryResponseDTO> getParentCategoriesForUser(String userId);
    List<CategoryResponseDTO> getIncomeCategoriesForUser(String userId);
    List<CategoryResponseDTO> getExpenseCategoriesForUser(String userId);
    List<CategoryResponseDTO> searchCategories(String userId, String searchTerm);
    CategoryResponseDTO getCategory(String userId, String categoryId);
    CategoryResponseDTO updateCategory(String userId, String categoryId, CategoryUpdateDTO dto);
    void deleteCategory(String userId, String categoryId);
}
