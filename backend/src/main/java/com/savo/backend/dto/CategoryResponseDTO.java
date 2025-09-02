package com.savo.backend.dto;

import com.savo.backend.model.Category;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CategoryResponseDTO {
    private String id;
    private String name;
    private String icon;
    private String color;
    private String parentCategoryId;
    private String parentCategoryName;
    private List<CategoryResponseDTO> subcategories;
    private Boolean isIncomeCategory;
    private Boolean isActive;
    private Boolean isSystemCategory; // from user = null
    private BigDecimal budgetAmount;
    private String budgetPeriod;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CategoryResponseDTO() {}

    public static CategoryResponseDTO from(Category category) {
        CategoryResponseDTO dto = new CategoryResponseDTO();

        dto.id = category.getId();
        dto.name = category.getName();
        dto.icon = category.getIcon();
        dto.color = category.getColor();
        dto.isIncomeCategory = category.getIncomeCategory();
        dto.isActive = category.getActive();
        dto.createdAt = category.getCreatedAt();
        dto.updatedAt = category.getUpdatedAt();

        dto.isSystemCategory = (category.getUser() == null);

        if (category.getBudgetPeriod() != null) {
            dto.budgetPeriod = category.getBudgetPeriod().name();
        }

        if (category.getParentCategory() != null) {
            dto.parentCategoryId = category.getParentCategory().getId();
            dto.parentCategoryName = category.getParentCategory().getName();
        }

        return dto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getSystemCategory() {
        return isSystemCategory;
    }

    public void setSystemCategory(Boolean systemCategory) {
        isSystemCategory = systemCategory;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(String parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public String getParentCategoryName() {
        return parentCategoryName;
    }

    public void setParentCategoryName(String parentCategoryName) {
        this.parentCategoryName = parentCategoryName;
    }

    public List<CategoryResponseDTO> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(List<CategoryResponseDTO> subcategories) {
        this.subcategories = subcategories;
    }

    public Boolean getIncomeCategory() {
        return isIncomeCategory;
    }

    public void setIncomeCategory(Boolean incomeCategory) {
        isIncomeCategory = incomeCategory;
    }

    public BigDecimal getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(BigDecimal budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public String getBudgetPeriod() {
        return budgetPeriod;
    }

    public void setBudgetPeriod(String budgetPeriod) {
        this.budgetPeriod = budgetPeriod;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
