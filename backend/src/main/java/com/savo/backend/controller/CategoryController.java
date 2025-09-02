package com.savo.backend.controller;

import com.savo.backend.dto.CategoryCreateDTO;
import com.savo.backend.dto.CategoryResponseDTO;
import com.savo.backend.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/categories")
@CrossOrigin(origins = "*")
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
}
