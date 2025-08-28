package com.savo.backend.service;

import com.savo.backend.dto.BankAccountCreateDTO;
import com.savo.backend.dto.BankAccountResponseDTO;
import com.savo.backend.model.BankAccount;
import com.savo.backend.model.User;
import com.savo.backend.repository.BankAccountRepository;
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

    public List<BankAccountResponseDTO> getUserBankAccounts(String userId) {
        List<BankAccount> bankAccounts = bankAccountRepository.findByUserIdAndIsActiveTrue(userId);

        return bankAccounts.stream()
                .map(BankAccountResponseDTO::from)
                .collect(Collectors.toList());
    }

    private String maskAccountNumber(String fullNumber) {
        if (fullNumber.length() <= 4) return fullNumber;
        return "****" + fullNumber.substring(fullNumber.length() - 4);
    }

    public List<BankAccount> getAllActiveAccounts(User user) {
        return bankAccountRepository.findByUserAndIsActiveTrue(user);
    }

    public BankAccount findOrCreateBankAccount(User user, String bankName, String bankNumberMasked, String accountType) {
        Optional<BankAccount> existing = bankAccountRepository
                .findByUserIdAndBankNameAndAccountNumberMasked(user.getId(), bankName, bankNumberMasked);

        if (existing.isPresent()) {
            return existing.get();
        }

        BankAccount newAccount = new BankAccount();
        newAccount.setUser(user);
        newAccount.setBankName(bankName);
        newAccount.setAccountNumberMasked(bankNumberMasked);
        newAccount.setAccountType(accountType);
        newAccount.setActive(true);
        newAccount.setCreatedAt(LocalDateTime.now());

        return bankAccountRepository.save(newAccount);
    }

    public BankAccount updateNickname(User user, String accountId, String newNickname) {

        BankAccount account = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Bank account not found"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to update this bank account");
        }

        if (bankAccountRepository.existsByUserAndAccountNickname(user, newNickname)) {
            throw new RuntimeException("Nickname already exists");
        }

        account.setAccountNickname(newNickname);
        return bankAccountRepository.save(account);
    }
}
