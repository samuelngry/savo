package com.savo.backend.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_transactions")
public class SubscriptionTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @Column(name = "billing_period_start", nullable = false)
    private LocalDate billingPeriodStart;

    @Column(name = "billing_period_end", nullable = false)
    private LocalDate billingPeriodEnd;

    @Column(name = "was_predicted", nullable = false)
    private Boolean wasPredicted = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public SubscriptionTransaction() {}

    public SubscriptionTransaction(Subscription subscription, Transaction transaction,
                                   LocalDate billingPeriodStart, LocalDate billingPeriodEnd) {
        this.subscription = subscription;
        this.transaction = transaction;
        this.billingPeriodStart = billingPeriodStart;
        this.billingPeriodEnd = billingPeriodEnd;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public LocalDate getBillingPeriodStart() {
        return billingPeriodStart;
    }

    public void setBillingPeriodStart(LocalDate billingPeriodStart) {
        this.billingPeriodStart = billingPeriodStart;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public LocalDate getBillingPeriodEnd() {
        return billingPeriodEnd;
    }

    public void setBillingPeriodEnd(LocalDate billingPeriodEnd) {
        this.billingPeriodEnd = billingPeriodEnd;
    }

    public Boolean getWasPredicted() {
        return wasPredicted;
    }

    public void setWasPredicted(Boolean wasPredicted) {
        this.wasPredicted = wasPredicted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
