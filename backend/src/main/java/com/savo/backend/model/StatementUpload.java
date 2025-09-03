package com.savo.backend.model;

import com.savo.backend.enums.UploadStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "statement_upload")
public class StatementUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccount bankAccount;

    @NotBlank(message = "File name is required")
    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_size", nullable = false)
    @Positive(message = "File size must be positive")
    private Long fileSize;

    @NotBlank(message = "S3 key is required")
    @Column(name = "s3_key", nullable = false)
    private String s3Key;

    @Enumerated(EnumType.STRING)
    @Column(name = "upload_status", nullable = false)
    private UploadStatus uploadStatus = UploadStatus.UPLOADING;

    @Column(name = "processing_started_at")
    private LocalDateTime processingStartedAt;

    @Column(name = "processing_completed_at")
    private LocalDateTime processingCompletedAt;

    @Column(name = "statement_period_start")
    private LocalDate statementPeriodStart;

    @Column(name = "statement_period_end")
    private LocalDate statementPeriodEnd;

    @Column(name = "total_transactions_extracted")
    private Integer totalTransactionsExtracted;

    @Column(name = "ocr_confidence_score", precision = 3, scale = 2)
    private BigDecimal ocrConfidenceScore;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "statementUpload", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

    public StatementUpload() {}

    public void startProcessing() {
        this.uploadStatus = UploadStatus.PROCESSING;
        this.processingStartedAt = LocalDateTime.now();
    }

    public void completeProcessing() {
        this.uploadStatus = UploadStatus.COMPLETED;
        this.processingCompletedAt = LocalDateTime.now();
    }

    public void failProcessing(String errorMessage) {
        this.uploadStatus = UploadStatus.FAILED;
        this.processingCompletedAt = LocalDateTime.now();
        this.errorMessage = errorMessage;
    }

    public boolean isProcessingComplete() {
        return uploadStatus == UploadStatus.COMPLETED;
    }

    public boolean hasProcessingFailed() {
        return uploadStatus == UploadStatus.FAILED;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public UploadStatus getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(UploadStatus uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public LocalDateTime getProcessingStartedAt() {
        return processingStartedAt;
    }

    public void setProcessingStartedAt(LocalDateTime processingStartedAt) {
        this.processingStartedAt = processingStartedAt;
    }

    public LocalDate getStatementPeriodStart() {
        return statementPeriodStart;
    }

    public void setStatementPeriodStart(LocalDate statementPeriodStart) {
        this.statementPeriodStart = statementPeriodStart;
    }

    public LocalDateTime getProcessingCompletedAt() {
        return processingCompletedAt;
    }

    public void setProcessingCompletedAt(LocalDateTime processingCompletedAt) {
        this.processingCompletedAt = processingCompletedAt;
    }

    public LocalDate getStatementPeriodEnd() {
        return statementPeriodEnd;
    }

    public void setStatementPeriodEnd(LocalDate statementPeriodEnd) {
        this.statementPeriodEnd = statementPeriodEnd;
    }

    public Integer getTotalTransactionsExtracted() {
        return totalTransactionsExtracted;
    }

    public void setTotalTransactionsExtracted(Integer totalTransactionsExtracted) {
        this.totalTransactionsExtracted = totalTransactionsExtracted;
    }

    public BigDecimal getOcrConfidenceScore() {
        return ocrConfidenceScore;
    }

    public void setOcrConfidenceScore(BigDecimal ocrConfidenceScore) {
        this.ocrConfidenceScore = ocrConfidenceScore;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
