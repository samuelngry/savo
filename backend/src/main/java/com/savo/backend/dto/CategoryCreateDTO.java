package com.savo.backend.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class CategoryCreateDTO {

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name too long")
    private String name;

    @Size(max = 50, message = "Icon name too long")
    private String icon;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Invalid color format")
    private String color;

    private String parentCategoryId;

    private Boolean isIncomeCategory = false;

    @DecimalMin(value = "0.01", message = "Budget amount must be positive")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal budgetAmount;

    private String budgetPeriod;

    public CategoryCreateDTO() {}

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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(String parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
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
}
