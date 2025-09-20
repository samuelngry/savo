package com.savo.backend.dto.statementupload;

import com.savo.backend.enums.UploadStatus;
import com.savo.backend.model.StatementUpload;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class UploadStatusResponseDTO {
    private String id;
    private String fileName;
    private UploadStatus status;
    private LocalDateTime uploadedAt;
    private LocalDateTime processingStartedAt;
    private LocalDateTime processingCompletedAt;
    private LocalDate statementPeriodStart;
    private LocalDate statementPeriodEnd;
    private Integer totalTransactionsExtracted;
    private BigDecimal ocrConfidenceScore;
    private String errorMessage;
    private BankAccountInfo bankAccount;

    public static UploadStatusResponseDTO from(StatementUpload upload) {
        UploadStatusResponseDTO dto = new UploadStatusResponseDTO();
        dto.setId(upload.getId());
        dto.setFileName(upload.getFileName());
        dto.setStatus(upload.getUploadStatus());
        dto.setUploadedAt(upload.getCreatedAt());
        dto.setProcessingStartedAt(upload.getProcessingStartedAt());
        dto.setProcessingCompletedAt(upload.getProcessingCompletedAt());
        dto.setStatementPeriodStart(upload.getStatementPeriodStart());
        dto.setStatementPeriodEnd(upload.getStatementPeriodEnd());
        dto.setTotalTransactionsExtracted(upload.getTotalTransactionsExtracted());
        dto.setOcrConfidenceScore(upload.getOcrConfidenceScore());
        dto.setErrorMessage(upload.getErrorMessage());

        if (upload.getBankAccount() != null) {
            dto.setBankAccount(BankAccountInfo.from(upload.getBankAccount()));
        }

        return dto;
    }

    public static class BankAccountInfo {
        private String id;
        private String bankName;
        private String accountType;
        private String accountNumberMasked;
        private String accountNickname;

        public static BankAccountInfo from(com.savo.backend.model.BankAccount bankAccount) {
            BankAccountInfo info = new BankAccountInfo();
            info.setId(bankAccount.getId());
            info.setBankName(bankAccount.getBankName());
            info.setAccountType(bankAccount.getAccountType());
            info.setAccountNumberMasked(bankAccount.getAccountNumberMasked());
            info.setAccountNickname(bankAccount.getAccountNickname());
            return info;
        }

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getBankName() { return bankName; }
        public void setBankName(String bankName) { this.bankName = bankName; }
        public String getAccountType() { return accountType; }
        public void setAccountType(String accountType) { this.accountType = accountType; }
        public String getAccountNumberMasked() { return accountNumberMasked; }
        public void setAccountNumberMasked(String accountNumberMasked) { this.accountNumberMasked = accountNumberMasked; }
        public String getAccountNickname() { return accountNickname; }
        public void setAccountNickname(String accountNickname) { this.accountNickname = accountNickname; }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public UploadStatus getStatus() {
        return status;
    }

    public void setStatus(UploadStatus status) {
        this.status = status;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public BankAccountInfo getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccountInfo bankAccount) {
        this.bankAccount = bankAccount;
    }
}
