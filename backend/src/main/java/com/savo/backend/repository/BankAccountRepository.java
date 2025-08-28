package com.savo.backend.repository;

import com.savo.backend.model.BankAccount;
import com.savo.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {

    List<BankAccount> findByUserIdAndIsActiveTrue(String userId);
    List<BankAccount> findByUserId(String userId);
    Optional<BankAccount> findByUserIdAndId(String userId, String accountId);
}
}
