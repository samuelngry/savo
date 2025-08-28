package com.savo.backend.dto;

import com.savo.backend.model.BankAccount;

import java.time.LocalDateTime;

public class BankAccountResponseDTO {
    private String id;
    private String bankName;
    private String accountType;
    private String accountNumberMasked;
    private String accountNickname;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public static BankAccountResponseDTO from(BankAccount bankAccount) {
        BankAccountResponseDTO dto = new BankAccountResponseDTO();
        dto.id = bankAccount.getId();
        dto.bankName = bankAccount.getBankName();
        dto.accountType = bankAccount.getAccountType();
        dto.accountNumberMasked = bankAccount.getAccountNumberMasked();
        dto.accountNickname = bankAccount.getAccountNickname();
        dto.isActive = bankAccount.getActive();
        dto.createdAt = bankAccount.getCreatedAt();
        return dto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountNumberMasked() {
        return accountNumberMasked;
    }

    public void setAccountNumberMasked(String accountNumberMasked) {
        this.accountNumberMasked = accountNumberMasked;
    }

    public String getAccountNickname() {
        return accountNickname;
    }

    public void setAccountNickname(String accountNickname) {
        this.accountNickname = accountNickname;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
