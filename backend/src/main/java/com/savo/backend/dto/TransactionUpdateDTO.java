package com.savo.backend.dto;

import com.savo.backend.enums.TransactionType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionUpdateDTO {

    @PastOrPresent(message = "Transaction date cannot be in the future")
    private LocalDate transactionDate;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description too long")
    private String description;

    @DecimalMin(value = "0.01", message = "Amount must be positive")
    @Digits(integer = 8, fraction = 2, message = "Invalid amount format")
    private BigDecimal amount;

    private TransactionType transactionType;

    @Size(max = 255, message = "Merchant name too long")
    private String merchantName;

    private String categoryId;

    private BigDecimal balanceAfter;

    public TransactionUpdateDTO() {}

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

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }
}
