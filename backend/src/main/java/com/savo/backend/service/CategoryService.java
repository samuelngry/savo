package com.savo.backend.service;

import com.savo.backend.dto.CategoryCreateDTO;
import com.savo.backend.dto.CategoryResponseDTO;

public interface CategoryService {
    CategoryResponseDTO createCategory(String userId, CategoryCreateDTO dto);
}
