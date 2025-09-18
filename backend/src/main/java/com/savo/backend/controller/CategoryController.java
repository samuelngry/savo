package com.savo.backend.controller;

import com.savo.backend.dto.category.CategoryCreateDTO;
import com.savo.backend.dto.category.CategoryResponseDTO;
import com.savo.backend.dto.category.CategoryUpdateDTO;
import com.savo.backend.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    @Operation(
            summary = "Create category",
            description = "Create a new category for the authenticated user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Category created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CategoryCreateDTO dto) {

        String userId = userDetails.getUsername();

        CategoryResponseDTO createdCategory = categoryService.createCategory(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    @GetMapping
    @Operation(
            summary = "Get all categories",
            description = "Get all categories for the authenticated user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories(
            @AuthenticationPrincipal UserDetails userDetails) {

        String userId = userDetails.getUsername();

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
