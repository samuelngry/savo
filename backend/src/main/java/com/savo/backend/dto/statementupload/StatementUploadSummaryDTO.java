package com.savo.backend.dto.statementupload;

import com.savo.backend.model.StatementUpload;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StatementUploadSummaryDTO {
    private String id;
    private String fileName;
    private String uploadStatus;
    private LocalDate statementPeriodStart;
    private LocalDate statementPeriodEnd;
    private Integer totalTransactionsExtracted;
    private LocalDateTime createdAt;
    private String bankAccountNickname;

    public StatementUploadSummaryDTO(StatementUpload upload) {
        this.id = upload.getId();
        this.fileName = upload.getFileName();
        this.uploadStatus = upload.getUploadStatus().name();
        this.statementPeriodStart = upload.getStatementPeriodStart();
        this.statementPeriodEnd = upload.getStatementPeriodEnd();
        this.totalTransactionsExtracted = upload.getTotalTransactionsExtracted();
        this.createdAt = upload.getCreatedAt();
        this.bankAccountNickname = upload.getBankAccount().getAccountNickname();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getBankAccountNickname() {
        return bankAccountNickname;
    }

    public void setBankAccountNickname(String bankAccountNickname) {
        this.bankAccountNickname = bankAccountNickname;
    }
}
