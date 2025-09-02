package com.savo.backend.service.impl;

import com.savo.backend.dto.CategoryUpdateDTO;
import com.savo.backend.enums.BudgetPeriod;
import com.savo.backend.dto.CategoryCreateDTO;
import com.savo.backend.dto.CategoryResponseDTO;
import com.savo.backend.model.Category;
import com.savo.backend.model.User;
import com.savo.backend.repository.CategoryRepository;
import com.savo.backend.repository.UserRepository;
import com.savo.backend.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

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

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategoriesForUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }

        List<Category> categories = categoryRepository.findAllAvaliableForUser(userId);
        return categories.stream()
                .map(CategoryResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getParentCategoriesForUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }

        List<Category> parentCategories = categoryRepository.findAllParentCategories(userId);

        return parentCategories.stream()
                .map(CategoryResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getIncomeCategoriesForUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }

        List<Category> incomeCategories = categoryRepository.findIncomeCategories(userId);
        return incomeCategories.stream()
                .map(CategoryResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getExpenseCategoriesForUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }

        List<Category> expenseCategories = categoryRepository.findExpenseCategories(userId);
        return expenseCategories.stream()
                .map(CategoryResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> searchCategories(String userId, String searchTerm) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }

        List<Category> categories = categoryRepository.searchCategoriesForUser(userId, searchTerm);
        return categories.stream()
                .map(CategoryResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategory(String userId, String categoryId) {
        Category category = categoryRepository.findByIdAndUserAccess(categoryId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryId));

        return CategoryResponseDTO.from(category);
    }

    @Override
    public CategoryResponseDTO updateCategory(String userId, String categoryId, CategoryUpdateDTO dto) {
        Category category = categoryRepository.findByIdAndUserAccess(categoryId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryId));

        if (dto.getName() != null) {
            category.setName(dto.getName());
        }

        if (dto.getIcon() != null) {
            category.setIcon(dto.getIcon());
        }

        if (dto.getColor() != null) {
            category.setColor(dto.getColor());
        }

        if (dto.getIncomeCategory() != null) {
            category.setIncomeCategory(dto.getIncomeCategory());
        }

        if (dto.getBudgetAmount() != null) {
            category.setBudgetAmount(dto.getBudgetAmount());
        }

        if (dto.getActive() != null) {
            category.setActive(dto.getActive());
        }

        if (dto.getParentCategoryId() != null) {
            Category parent = categoryRepository.findByIdAndUserAccess(dto.getParentCategoryId(), userId)
                    .orElseThrow(() -> new EntityNotFoundException("Parent category not found with id: " + dto.getParentCategoryId()));
            category.setParentCategory(parent);
        }

        if (dto.getBudgetPeriod() != null) {
            category.setBudgetPeriod(BudgetPeriod.valueOf(dto.getBudgetPeriod()));
        }

        Category updatedCategory = categoryRepository.save(category);
        return CategoryResponseDTO.from(updatedCategory);
    }

    @Override
    public void deleteCategory(String userId, String categoryId) {
        Category category = categoryRepository.findByIdAndUserAccess(categoryId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryId));

        categoryRepository.delete(category);
    }
}
