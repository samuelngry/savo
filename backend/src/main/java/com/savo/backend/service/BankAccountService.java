package com.savo.backend.service;

import com.savo.backend.dto.BankAccountCreateDTO;
import com.savo.backend.dto.BankAccountResponseDTO;
import com.savo.backend.dto.BankAccountUpdateDTO;
import com.savo.backend.model.BankAccount;
import com.savo.backend.repository.BankAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    public BankAccountResponseDTO createBankAccount(String userId, BankAccountCreateDTO createDTO) {
        // Convert DTO to entity
        BankAccount bankAccount = new BankAccount();
        bankAccount.setUserId(userId);
        bankAccount.setBankName(createDTO.getBankName());
        bankAccount.setAccountType(createDTO.getAccountType());

        String maskedNumber = maskAccountNumber(createDTO.getAccountNumber());
        bankAccount.setAccountNumberMasked(maskedNumber);

        bankAccount.setAccountNickname(createDTO.getAccountNickname());
        bankAccount.setActive(true);
        bankAccount.setCreatedAt(LocalDateTime.now());

        BankAccount saved = bankAccountRepository.save(bankAccount);

        return BankAccountResponseDTO.from(saved);
    }

    public BankAccountResponseDTO getBankAccount(String userId, String bankAccountId) {
        BankAccount bankAccount = bankAccountRepository.findByUserIdAndId(userId, bankAccountId)
                .orElseThrow(() -> new EntityNotFoundException("Bank account not found or access denied"));

        return BankAccountResponseDTO.from(bankAccount);
    }

    public List<BankAccountResponseDTO> getUserBankAccounts(String userId) {
        List<BankAccount> bankAccounts = bankAccountRepository.findByUserIdAndIsActiveTrue(userId);

        return bankAccounts.stream()
                .map(BankAccountResponseDTO::from)
                .collect(Collectors.toList());
    }

    public BankAccountResponseDTO updateBankAccount(String userId, String accountId, BankAccountUpdateDTO updateDTO) {
        BankAccount existingAccount = bankAccountRepository.findByUserIdAndId(userId, accountId)
                .orElseThrow(() -> new EntityNotFoundException("Bank account not found or access denied"));

        if (updateDTO.getAccountNickname() != null) {
            existingAccount.setAccountNickname(updateDTO.getAccountNickname());
        }

        if (updateDTO.getActive() != null) {
            existingAccount.setActive(updateDTO.getActive());
        }

        existingAccount.setUpdatedAt(LocalDateTime.now());

        BankAccount updated = bankAccountRepository.save(existingAccount);
        return BankAccountResponseDTO.from(updated);
    }

    public BankAccountResponseDTO deactivateBankAccount(String userId, String accountId) {
        BankAccount account = bankAccountRepository.findByUserIdAndId(userId, accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found or access denied"));

        account.setActive(false);
        account.setUpdatedAt(LocalDateTime.now());
        bankAccountRepository.save(account);

        return BankAccountResponseDTO.from(account);
    }

    public BankAccountResponseDTO activateBankAccount(String userId, String accountId) {
        BankAccount account = bankAccountRepository.findByUserIdAndId(userId, accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found or access denied"));

        account.setActive(true);
        account.setUpdatedAt(LocalDateTime.now());
        bankAccountRepository.save(account);

        return BankAccountResponseDTO.from(account);
    }

    private String maskAccountNumber(String fullNumber) {
        if (fullNumber.length() <= 4) return fullNumber;
        return "****" + fullNumber.substring(fullNumber.length() - 4);
    }

}
