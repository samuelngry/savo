package com.savo.backend.service;

import com.savo.backend.model.BankAccount;
import com.savo.backend.model.User;
import com.savo.backend.repository.BankAccountRepository;
import com.savo.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
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
}
