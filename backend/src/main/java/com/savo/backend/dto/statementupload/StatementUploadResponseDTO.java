package com.savo.backend.dto.statementupload;

import com.savo.backend.model.StatementUpload;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class StatementUploadResponseDTO {
    private String id;
    private String bankAccountId;
    private String bankAccountNickname;
    private String fileName;
    private Long fileSize;
    private String uploadStatus;
    private LocalDateTime processingStartedAt;
    private LocalDateTime processingCompletedAt;
    private LocalDate statementPeriodStart;
    private LocalDate statementPeriodEnd;
    private Integer totalTransactionsExtracted;
    private BigDecimal ocrConfidenceScore;
    private String errorMessage;
    private LocalDateTime createdAt;

    public StatementUploadResponseDTO() {}

    public static StatementUploadResponseDTO from(StatementUpload statementUpload) {
        StatementUploadResponseDTO dto = new StatementUploadResponseDTO();

        dto.id = statementUpload.getId();
        dto.bankAccountId = statementUpload.getBankAccount().getId();
        dto.bankAccountNickname = statementUpload.getBankAccount().getAccountNickname();
        dto.fileName = statementUpload.getFileName();
        dto.fileSize = statementUpload.getFileSize();
        dto.uploadStatus = statementUpload.getUploadStatus().name();
        dto.processingStartedAt = statementUpload.getProcessingStartedAt();
        dto.processingCompletedAt = statementUpload.getProcessingCompletedAt();
        dto.statementPeriodStart = statementUpload.getStatementPeriodStart();
        dto.statementPeriodEnd = statementUpload.getStatementPeriodEnd();
        dto.totalTransactionsExtracted = statementUpload.getTotalTransactionsExtracted();
        dto.ocrConfidenceScore = statementUpload.getOcrConfidenceScore();
        dto.errorMessage = statementUpload.getErrorMessage();
        dto.createdAt = statementUpload.getCreatedAt();

        return dto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(String bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public String getBankAccountNickname() {
        return bankAccountNickname;
    }

    public void setBankAccountNickname(String bankAccountNickname) {
        this.bankAccountNickname = bankAccountNickname;
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

    public String getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public LocalDateTime getProcessingStartedAt() {
        return processingStartedAt;
    }

    public void setProcessingStartedAt(LocalDateTime processingStartedAt) {
        this.processingStartedAt = processingStartedAt;
    }

    public LocalDateTime getProcessingCompletedAt() {
        return processingCompletedAt;
    }

    public void setProcessingCompletedAt(LocalDateTime processingCompletedAt) {
        this.processingCompletedAt = processingCompletedAt;
    }

    public LocalDate getStatementPeriodStart() {
        return statementPeriodStart;
    }

    public void setStatementPeriodStart(LocalDate statementPeriodStart) {
        this.statementPeriodStart = statementPeriodStart;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
