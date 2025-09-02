package com.savo.backend.service.impl;

import com.savo.backend.enums.BudgetPeriod;
import com.savo.backend.dto.CategoryCreateDTO;
import com.savo.backend.dto.CategoryResponseDTO;
import com.savo.backend.model.Category;
import com.savo.backend.model.User;
import com.savo.backend.repository.CategoryRepository;
import com.savo.backend.repository.UserRepository;
import com.savo.backend.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final UserRepository userRepository;
    private CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CategoryResponseDTO createCategory(String userId, CategoryCreateDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Category category = new Category();
        category.setUser(user);
        category.setName(dto.getName());
        category.setIcon(dto.getIcon());
        category.setColor(dto.getColor());
        category.setIncomeCategory(dto.getIncomeCategory());
        category.setBudgetAmount(dto.getBudgetAmount());

        // Handle parent category
        if (dto.getParentCategoryId() != null) {
            Category parent = categoryRepository.findByIdAndUserAccess(dto.getParentCategoryId(), userId)
                    .orElseThrow(() -> new EntityNotFoundException("Parent category not found"));
            category.setParentCategory(parent);
        }

        // Handle budget period
        if (dto.getBudgetPeriod() != null) {
            category.setBudgetPeriod(BudgetPeriod.valueOf(dto.getBudgetPeriod()));
        }

        Category savedCategory = categoryRepository.save(category);
        return CategoryResponseDTO.from(savedCategory);
    }
}
