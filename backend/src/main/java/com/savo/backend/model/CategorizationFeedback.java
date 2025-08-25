package com.savo.backend.model;

import com.savo.backend.enums.FeedbackType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "categorization_feedback")
public class CategorizationFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @NotBlank(message = "Original category ID is required")
    @Column(name = "original_category_id", nullable = false)
    private String originalCategoryId;

    @Column(name = "corrected_category_id", nullable = false)
    private String correctedCategoryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "feedback_type", nullable = false)
    private FeedbackType feedbackType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public CategorizationFeedback() {}

    // Constructor for manual correction
    public CategorizationFeedback(User user, Transaction transaction,
                                  String originalCategoryId, String correctedCategoryId,
                                  FeedbackType feedbackType) {
        this.user = user;
        this.transaction = transaction;
        this.originalCategoryId = originalCategoryId;
        this.correctedCategoryId = correctedCategoryId;
        this.feedbackType = feedbackType;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public boolean isManualCorrection() {
        return feedbackType == FeedbackType.MANUAL_CORRECTION;
    }

    public boolean isBulkUpdate() {
        return feedbackType == FeedbackType.BULK_UPDATE;
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

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public String getOriginalCategoryId() {
        return originalCategoryId;
    }

    public void setOriginalCategoryId(String originalCategoryId) {
        this.originalCategoryId = originalCategoryId;
    }

    public String getCorrectedCategoryId() {
        return correctedCategoryId;
    }

    public void setCorrectedCategoryId(String correctedCategoryId) {
        this.correctedCategoryId = correctedCategoryId;
    }

    public FeedbackType getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(FeedbackType feedbackType) {
        this.feedbackType = feedbackType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
