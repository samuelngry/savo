package com.savo.backend.model;

import com.savo.backend.enums.BudgetStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "budget_progress")
public class BudgetProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @NotNull(message = "Period start date is required")
    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @NotNull(message = "Period end date is required")
    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @NotNull(message = "Spent amount is required")
    @DecimalMin(value = "0.0", message = "Spent amount cannot be negative")
    @Column(name = "spent_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal spentAmount = BigDecimal.ZERO;

    @NotNull(message = "Remaining amount is required")
    @Column(name = "remaining_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal remainingAmount;

    @Min(value = 0, message = "Percentage used cannot be negative")
    @Max(value = 999, message = "Percentage used cannot exceed 999%") // Allow over 100% for overspending
    @Column(name = "percentage_used", nullable = false)
    private Double percentageUsed = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BudgetStatus status = BudgetStatus.ON_TRACK;

    @Column(name = "last_calculated", nullable = false)
    private LocalDateTime lastCalculated;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Required no-arg constructor for JPA
    public BudgetProgress() {}

    // Constructor for new budget period
    public BudgetProgress(Budget budget, LocalDate periodStart, LocalDate periodEnd) {
        this.budget = budget;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.remainingAmount = budget.getAmount(); // Initially, full budget remains
        this.lastCalculated = LocalDateTime.now();
        updateStatus();
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.lastCalculated = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.lastCalculated = LocalDateTime.now();
    }

    // Business logic methods
    public void updateSpentAmount(BigDecimal newSpentAmount) {
        this.spentAmount = newSpentAmount;
        this.remainingAmount = budget.getAmount().subtract(spentAmount);
        calculatePercentageUsed();
        updateStatus();
    }

    private void calculatePercentageUsed() {
        if (budget.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            this.percentageUsed = spentAmount
                    .divide(budget.getAmount(), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .doubleValue();
        } else {
            this.percentageUsed = 0.0;
        }
    }

    private void updateStatus() {
        double alertThreshold = budget.getAlertThresholdDecimal() * 100; // Convert to percentage

        if (percentageUsed >= 100.0) {
            this.status = BudgetStatus.EXCEEDED;
        } else if (percentageUsed >= alertThreshold) {
            this.status = BudgetStatus.WARNING;
        } else {
            this.status = BudgetStatus.ON_TRACK;
        }
    }

    // Helper methods
    public boolean isCurrentPeriod() {
        LocalDate now = LocalDate.now();
        return !now.isBefore(periodStart) && !now.isAfter(periodEnd);
    }

    public boolean isOverBudget() {
        return percentageUsed >= 100.0;
    }

    public boolean isNearLimit() {
        return status == BudgetStatus.WARNING;
    }

    public BigDecimal getOverspentAmount() {
        if (isOverBudget()) {
            return spentAmount.subtract(budget.getAmount());
        }
        return BigDecimal.ZERO;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
        calculatePercentageUsed();
        updateStatus();
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public Double getPercentageUsed() {
        return percentageUsed;
    }

    public void setPercentageUsed(Double percentageUsed) {
        this.percentageUsed = percentageUsed;
    }

    public BudgetStatus getStatus() {
        return status;
    }

    public void setStatus(BudgetStatus status) {
        this.status = status;
    }

    public LocalDateTime getLastCalculated() {
        return lastCalculated;
    }

    public void setLastCalculated(LocalDateTime lastCalculated) {
        this.lastCalculated = lastCalculated;
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