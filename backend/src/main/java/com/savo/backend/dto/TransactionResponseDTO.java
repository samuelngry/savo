package com.savo.backend.dto;

import com.savo.backend.enums.TransactionType;
import com.savo.backend.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TransactionResponseDTO {
    private String id;
    private String bankAccountId;
    private String bankAccountNickname;
    private LocalDate transactionDate;
    private String description;
    private BigDecimal amount;
    private TransactionType transactionType;
    private BigDecimal balanceAfter;
    private String merchantName;
    private String categoryId;
    private String categoryName;
    private String categoryIcon;
    private String categoryColor;
    private Boolean isManuallyCategorized;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TransactionResponseDTO() {}

    public static TransactionResponseDTO from(Transaction transaction) {
        TransactionResponseDTO dto = new TransactionResponseDTO();

        dto.id = transaction.getId();
        dto.transactionDate = transaction.getTransactionDate();
        dto.description = transaction.getDescription();
        dto.amount = transaction.getAmount();
        dto.transactionType = transaction.getTransactionType();
        dto.balanceAfter = transaction.getBalanceAfter();
        dto.merchantName = transaction.getMerchantName();
        dto.isManuallyCategorized = transaction.getManuallyCategorized();
        dto.createdAt = transaction.getCreatedAt();
        dto.updatedAt = transaction.getUpdatedAt();

        // Bank account info
        if (transaction.getBankAccount() != null) {
            dto.bankAccountId = transaction.getBankAccount().getId();
            dto.bankAccountNickname = transaction.getBankAccount().getAccountNickname();
        }

        // Category info
        if (transaction.getCategory() != null) {
            dto.categoryId = transaction.getCategory().getId();
            dto.categoryName = transaction.getCategory().getName();
            dto.categoryIcon = transaction.getCategory().getIcon();
            dto.categoryColor = transaction.getCategory().getColor();
        }

        return dto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBankAccountNickname() {
        return bankAccountNickname;
    }

    public void setBankAccountNickname(String bankAccountNickname) {
        this.bankAccountNickname = bankAccountNickname;
    }

    public String getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(String bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(String categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    public String getCategoryColor() {
        return categoryColor;
    }

    public void setCategoryColor(String categoryColor) {
        this.categoryColor = categoryColor;
    }

    public Boolean getManuallyCategorized() {
        return isManuallyCategorized;
    }

    public void setManuallyCategorized(Boolean manuallyCategorized) {
        isManuallyCategorized = manuallyCategorized;
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
