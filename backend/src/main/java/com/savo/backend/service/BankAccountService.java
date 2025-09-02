package com.savo.backend.service;

import com.savo.backend.dto.BankAccountCreateDTO;
import com.savo.backend.dto.BankAccountResponseDTO;
import com.savo.backend.dto.BankAccountUpdateDTO;

import java.util.List;

public interface BankAccountService {
    BankAccountResponseDTO createBankAccount(String userId, BankAccountCreateDTO createDTO);
    BankAccountResponseDTO getBankAccount(String userId, String bankAccountId);
    List<BankAccountResponseDTO> getUserBankAccounts(String userId);
    BankAccountResponseDTO updateBankAccount(String userId, String bankAccountId, BankAccountUpdateDTO updateDTO);
    BankAccountResponseDTO deactivateBankAccount(String userId, String bankAccountId);
    BankAccountResponseDTO activateBankAccount(String userId, String bankAccountId);
}
