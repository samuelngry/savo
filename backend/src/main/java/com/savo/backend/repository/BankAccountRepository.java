package com.savo.backend.repository;

import com.savo.backend.model.BankAccount;
import com.savo.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {

    List<BankAccount> findByUserAndIsActiveTrue(User user);

    Optional<BankAccount> findById(String id);

    Optional<BankAccount> findByUserIdAndBankNameAndAccountNumberMasked(String id, String bankName, String accountNumberMasked);

    boolean existsByUserAndAccountNickname(User user, String accountNickname);
}
