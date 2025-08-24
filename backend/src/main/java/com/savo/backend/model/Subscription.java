package com.savo.backend.model;

import com.savo.backend.enums.BillingCycle;
import com.savo.backend.enums.UsageEstimate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Service name is required")
    @Column(name = "service_name", nullable = false)
    private String serviceName;

    // Billing Details

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle", nullable = false)
    private BillingCycle billingCycle;

    @Column(name = "next_billing_date", nullable = false)
    private LocalDate nextBillingDate;

    @Column(name = "currency", nullable = false)
    private String currency;

    // Detection & Tracking

    @Column(name = "first_detected", nullable = false)
    private LocalDateTime firstDetected;

    @Column(name = "last_transaction_date")
    private LocalDateTime lastTransactionDate;

    @Column(name = "detection_confidence")
    private Double detectionConfidence;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    // Multiple cards detection

    @ManyToMany
    @JoinTable(
            name = "subscription_bank_accounts",
            joinColumns = @JoinColumn(name = "subscription_id"),
            inverseJoinColumns = @JoinColumn(name = "bank_account_id")
    )
    private List<BankAccount> bankAccounts;

    @Column(name = "is_duplicate", nullable = false)
    private boolean isDuplicate = false;

    // Group duplicates together
    @Column(name = "duplicate_group_id")
    private String duplicateGroupId;

    // Optimization

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "usage_estimated")
    private UsageEstimate usageEstimated;

    @Column(name = "cost_per_usage", precision = 10, scale = 2)
    private BigDecimal costPerUsage;

    @Column(name = "optimization_suggestion", columnDefinition = "TEXT")
    private String optimizationSuggestion;

    @Column(name = "potential_savings", precision = 10, scale = 2)
    private BigDecimal potentialSavings;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL)
    private List<SubscriptionTransaction> subscriptionTransactions = new ArrayList<>();

    public Subscription() {}

    public Subscription(User user, String serviceName, BigDecimal amount,
                        BillingCycle billingCycle, LocalDate nextBillingDate,
                        String currency, Category category) {
        this.user = user;
        this.serviceName = serviceName;
        this.amount = amount;
        this.billingCycle = billingCycle;
        this.nextBillingDate = nextBillingDate;
        this.currency = currency;
        this.category = category;
        this.firstDetected = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BillingCycle getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(BillingCycle billingCycle) {
        this.billingCycle = billingCycle;
    }

    public LocalDate getNextBillingDate() {
        return nextBillingDate;
    }

    public void setNextBillingDate(LocalDate nextBillingDate) {
        this.nextBillingDate = nextBillingDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getFirstDetected() {
        return firstDetected;
    }

    public void setFirstDetected(LocalDateTime firstDetected) {
        this.firstDetected = firstDetected;
    }

    public LocalDateTime getLastTransactionDate() {
        return lastTransactionDate;
    }

    public void setLastTransactionDate(LocalDateTime lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    public Double getDetectionConfidence() {
        return detectionConfidence;
    }

    public void setDetectionConfidence(Double detectionConfidence) {
        this.detectionConfidence = detectionConfidence;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<BankAccount> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(List<BankAccount> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    public boolean isDuplicate() {
        return isDuplicate;
    }

    public void setDuplicate(boolean duplicate) {
        isDuplicate = duplicate;
    }

    public String getDuplicateGroupId() {
        return duplicateGroupId;
    }

    public void setDuplicateGroupId(String duplicateGroupId) {
        this.duplicateGroupId = duplicateGroupId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public UsageEstimate getUsageEstimated() {
        return usageEstimated;
    }

    public void setUsageEstimated(UsageEstimate usageEstimated) {
        this.usageEstimated = usageEstimated;
    }

    public BigDecimal getCostPerUsage() {
        return costPerUsage;
    }

    public void setCostPerUsage(BigDecimal costPerUsage) {
        this.costPerUsage = costPerUsage;
    }

    public String getOptimizationSuggestion() {
        return optimizationSuggestion;
    }

    public void setOptimizationSuggestion(String optimizationSuggestion) {
        this.optimizationSuggestion = optimizationSuggestion;
    }

    public BigDecimal getPotentialSavings() {
        return potentialSavings;
    }

    public void setPotentialSavings(BigDecimal potentialSavings) {
        this.potentialSavings = potentialSavings;
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

    public List<SubscriptionTransaction> getSubscriptionTransactions() {
        return subscriptionTransactions;
    }

    public void setSubscriptionTransactions(List<SubscriptionTransaction> subscriptionTransactions) {
        this.subscriptionTransactions = subscriptionTransactions;
    }
}
