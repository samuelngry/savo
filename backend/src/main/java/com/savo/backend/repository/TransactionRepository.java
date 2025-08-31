package com.savo.backend.repository;

import com.savo.backend.enums.TransactionType;
import com.savo.backend.model.BankAccount;
import com.savo.backend.model.Transaction;
import com.savo.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// View all transactions for a bank account
// Filter by date, category, or amount
// Add manual transactions
// Delete/undo a transaction
// Edit category or merchant
// Mark as subscription

public interface TransactionRepository extends JpaRepository<Transaction, String> {

    // Get all transactions for a user, ordered by date
    List<Transaction> findByUserIdOrderByTransactionDateDesc(String userId);

    Optional<Transaction> findByUserIdAndId(String userId, String transactionId);
    List<Transaction> findByUserIdAndBankAccountIdOrderByTransactionDateDesc(String userId, String bankAccountId);

    // Filtering
    List<Transaction> findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(String userId, LocalDate startDate, LocalDate endDate);
    List<Transaction> findByUserIdAndTransactionTypeOrderByTransactionDateDesc(String userId, TransactionType transactionType);
    List<Transaction> findByUserIdAndCategoryIdOrderByTransactionDateDesc(String userId, String categoryId);

}
