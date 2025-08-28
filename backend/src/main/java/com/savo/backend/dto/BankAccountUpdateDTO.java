package com.savo.backend.dto;

public class BankAccountUpdateDTO {

    private String accountNickname;
    private Boolean isActive;

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
}
