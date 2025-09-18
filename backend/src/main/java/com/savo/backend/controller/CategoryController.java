package com.savo.backend.controller;

import com.savo.backend.dto.category.CategoryCreateDTO;
import com.savo.backend.dto.category.CategoryResponseDTO;
import com.savo.backend.dto.category.CategoryUpdateDTO;
import com.savo.backend.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/categories")
@CrossOrigin(origins = "*")
@Tag(name = "Categories", description = "User categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @PathVariable String userId,
            @Valid @RequestBody CategoryCreateDTO dto) {
        CategoryResponseDTO createdCategory = categoryService.createCategory(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories(
            @PathVariable String userId) {
        List<CategoryResponseDTO> categories = categoryService.getAllCategoriesForUser(userId);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/parents")
    public ResponseEntity<List<CategoryResponseDTO>> getAllParentCategories(
            @PathVariable String userId) {
        List<CategoryResponseDTO> parents = categoryService.getParentCategoriesForUser(userId);
        return ResponseEntity.ok(parents);
    }

    @GetMapping("/income")
    public ResponseEntity<List<CategoryResponseDTO>> getAllIncomeCategories(
            @PathVariable String userId) {
        List<CategoryResponseDTO> incomes = categoryService.getIncomeCategoriesForUser(userId);
        return ResponseEntity.ok(incomes);
    }

    @GetMapping("/expense")
    public ResponseEntity<List<CategoryResponseDTO>> getAllExpenseCategories(
            @PathVariable String userId) {
        List<CategoryResponseDTO> expenses = categoryService.getExpenseCategoriesForUser(userId);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> getCategory(
            @PathVariable String userId,
            @PathVariable String categoryId) {
        CategoryResponseDTO category = categoryService.getCategory(userId, categoryId);
        return ResponseEntity.ok(category);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable String userId,
            @PathVariable String categoryId,
            @Valid @RequestBody CategoryUpdateDTO dto) {
        CategoryResponseDTO updatedCategory = categoryService.updateCategory(userId, categoryId, dto);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> deleteCategory(
            @PathVariable String userId,
            @PathVariable String categoryId) {
        categoryService.deleteCategory(userId, categoryId);
        return ResponseEntity.noContent().build();
    }
}
