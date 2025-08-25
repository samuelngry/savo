package com.savo.backend.repository;

import com.savo.backend.model.BankAccount;
import com.savo.backend.model.Transaction;
import com.savo.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

    List<Transaction> findByUser(User user);
    List<Transaction> findByBankAccount(BankAccount bankAccount);
    List<Transaction> findByTransactionDate(LocalDate date);
    List<Transaction> findByUserAndTransactionDate(User user, LocalDate date);
    List<Transaction> findByIsSubscriptionTrue();
    List<Transaction> findByUserAndRecurrencePatternIsNotNull(User user);
    List<Transaction> findByDescriptionContainingIgnoreCase(String keyword);

    Optional<Transaction> findTopByUserOrderByTransactionDateDesc();

    Long countByUser(User user);

}
