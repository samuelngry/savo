package com.savo.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class BankAccountCreateDTO {
    @NotBlank(message = "Bank name is required")
    private String bankName;

    @NotBlank(message = "Account type is required")
    private String accountType;

    @NotBlank(message = "Account number is required")
    @Pattern(regexp = "\\d{8,12}", message = "Account number must be 8-12 digits")
    private String accountNumber;

    private String accountNickname;

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNickname() {
        return accountNickname;
    }

    public void setAccountNickname(String accountNickname) {
        this.accountNickname = accountNickname;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
