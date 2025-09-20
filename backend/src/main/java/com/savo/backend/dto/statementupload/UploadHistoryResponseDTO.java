package com.savo.backend.dto.statementupload;

import com.savo.backend.enums.UploadStatus;
import com.savo.backend.model.StatementUpload;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UploadHistoryResponseDTO {
    private String id;
    private String fileName;
    private String bankName;
    private String accountType;
    private String accountNumberMasked;
    private UploadStatus status;
    private LocalDateTime uploadedAt;
    private LocalDate statementPeriodStart;
    private LocalDate statementPeriodEnd;
    private Integer totalTransactionsExtracted;
    private Long fileSize;

    public static UploadHistoryResponseDTO from(StatementUpload upload) {
        UploadHistoryResponseDTO dto = new UploadHistoryResponseDTO();
        dto.setId(upload.getId());
        dto.setFileName(upload.getFileName());
        dto.setStatus(upload.getUploadStatus());
        dto.setUploadedAt(upload.getCreatedAt());
        dto.setStatementPeriodStart(upload.getStatementPeriodStart());
        dto.setStatementPeriodEnd(upload.getStatementPeriodEnd());
        dto.setTotalTransactionsExtracted(upload.getTotalTransactionsExtracted());
        dto.setFileSize(upload.getFileSize());

        if (upload.getBankAccount() != null) {
            dto.setBankName(upload.getBankAccount().getBankName());
            dto.setAccountType(upload.getBankAccount().getAccountType());
            dto.setAccountNumberMasked(upload.getBankAccount().getAccountNumberMasked());
        }

        return dto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumberMasked() {
        return accountNumberMasked;
    }

    public void setAccountNumberMasked(String accountNumberMasked) {
        this.accountNumberMasked = accountNumberMasked;
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

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Integer getTotalTransactionsExtracted() {
        return totalTransactionsExtracted;
    }

    public void setTotalTransactionsExtracted(Integer totalTransactionsExtracted) {
        this.totalTransactionsExtracted = totalTransactionsExtracted;
    }

    public LocalDate getStatementPeriodEnd() {
        return statementPeriodEnd;
    }

    public void setStatementPeriodEnd(LocalDate statementPeriodEnd) {
        this.statementPeriodEnd = statementPeriodEnd;
    }

    public LocalDate getStatementPeriodStart() {
        return statementPeriodStart;
    }

    public void setStatementPeriodStart(LocalDate statementPeriodStart) {
        this.statementPeriodStart = statementPeriodStart;
    }
}
