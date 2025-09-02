package com.savo.backend.service;

import com.savo.backend.dto.CategoryCreateDTO;
import com.savo.backend.dto.CategoryResponseDTO;

import java.util.List;

public interface CategoryService {
    CategoryResponseDTO createCategory(String userId, CategoryCreateDTO dto);
    List<CategoryResponseDTO> getAllCategoriesForUser(String userId);
    List<CategoryResponseDTO> getParentCategoriesForUser(String userId);
    List<CategoryResponseDTO> getIncomeCategoriesForUser(String userId);
    List<CategoryResponseDTO> getExpenseCategoriesForUser(String userId);
}
