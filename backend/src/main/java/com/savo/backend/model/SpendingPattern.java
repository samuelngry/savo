package com.savo.backend.model;

import com.savo.backend.enums.PatternType;
import com.savo.backend.enums.TrendDirection;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "spending_patterns")
public class SpendingPattern {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "pattern_type", nullable = false)
    private PatternType patternType; // "Time", "Location"

    @NotBlank(message = "Pattern name is required")
    @Column(name = "pattern_name", nullable = false)
    private String patternName;

    // Pattern Strength

    @Column(name = "confidence_score", precision = 4, scale = 2)
    private BigDecimal confidenceScore;

    @Column(name = "frequency_count")
    private Integer frequencyCount;

    @Column(name = "average_amount", precision = 10, scale = 2)
    private BigDecimal averageAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "trend_direction")
    private TrendDirection trendDirection;

    // Pattern Conditions

    @Column(name = "trigger_conditions", columnDefinition = "TEXT")
    private String triggerConditions;

    @ElementCollection
    @CollectionTable(name = "pattern_categories", joinColumns = @JoinColumn(name = "pattern_id"))
    @Column(name = "category_id")
    private List<String> affectedCategories;

    // Financial Impact

    @Column(name = "monthly_cost", precision = 10, scale = 2)
    private BigDecimal monthlyCost;

    @Column(name = "potential_savings", precision = 10, scale = 2)
    private BigDecimal potentialSavings;

    @Column(name = "priority_score", precision = 4, scale = 2)
    private BigDecimal priorityScore;

    @Column(name = "first_detected", nullable = false)
    private LocalDateTime firstDetected;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    public SpendingPattern() {}

    @PrePersist
    public void prePersist() {
        this.firstDetected = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PatternType getPatternType() {
        return patternType;
    }

    public void setPatternType(PatternType patternType) {
        this.patternType = patternType;
    }

    public String getPatternName() {
        return patternName;
    }

    public void setPatternName(String patternName) {
        this.patternName = patternName;
    }

    public BigDecimal getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(BigDecimal confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public void setPriorityScore(BigDecimal priorityScore) {
        this.priorityScore = priorityScore;
    }

    public BigDecimal getAverageAmount() {
        return averageAmount;
    }

    public void setAverageAmount(BigDecimal averageAmount) {
        this.averageAmount = averageAmount;
    }

    public Integer getFrequencyCount() {
        return frequencyCount;
    }

    public void setFrequencyCount(Integer frequencyCount) {
        this.frequencyCount = frequencyCount;
    }

    public TrendDirection getTrendDirection() {
        return trendDirection;
    }

    public void setTrendDirection(TrendDirection trendDirection) {
        this.trendDirection = trendDirection;
    }

    public String getTriggerConditions() {
        return triggerConditions;
    }

    public void setTriggerConditions(String triggerConditions) {
        this.triggerConditions = triggerConditions;
    }

    public List<String> getAffectedCategories() {
        return affectedCategories;
    }

    public void setAffectedCategories(List<String> affectedCategories) {
        this.affectedCategories = affectedCategories;
    }

    public BigDecimal getMonthlyCost() {
        return monthlyCost;
    }

    public void setMonthlyCost(BigDecimal monthlyCost) {
        this.monthlyCost = monthlyCost;
    }

    public BigDecimal getPotentialSavings() {
        return potentialSavings;
    }

    public void setPotentialSavings(BigDecimal potentialSavings) {
        this.potentialSavings = potentialSavings;
    }

    public LocalDateTime getFirstDetected() {
        return firstDetected;
    }

    public void setFirstDetected(LocalDateTime firstDetected) {
        this.firstDetected = firstDetected;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isActive() {
        return isActive;
    }

    public BigDecimal getPriorityScore() {
        return priorityScore;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
