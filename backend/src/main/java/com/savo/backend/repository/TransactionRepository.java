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

public interface TransactionRepository extends JpaRepository<Transaction, String> {

    // Get all transactions for a user, ordered by date
    List<Transaction> findByUserIdOrderByTransactionDateDesc(String userId);

    Optional<Transaction> findByUserIdAndId(String userId, String transactionId);
    List<Transaction> findByUserIdAndBankAccountIdOrderByTransactionDateDesc(String userId, String bankAccountId);

    // Filtering
    List<Transaction> findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(String userId, LocalDate startDate, LocalDate endDate);
    List<Transaction> findByUserIdAndTransactionTypeOrderByTransactionDateDesc(String userId, TransactionType transactionType);
    List<Transaction> findByUserIdAndCategoryIdOrderByTransactionDateDesc(String userId, String categoryId);

    // Analytics

    // Total spending/income for user
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId AND t.transactionType = :type")
    BigDecimal sumAmountByUserIdAndTransactionType(@Param("userId") String userId, @Param("type") TransactionType type);

    // Monthly spending by category
    @Query("SELECT t.category.name, SUM(t.amount) FROM Transaction t " + "WHERE t.user.id = :userId AND t.transactionType = 'Debit' " +
    "AND t.transactionDate BETWEEN :startDate and :endDate " + "GROUP BY t.category.id, t.category.name " + "ORDER BY SUM(t.amount) DESC")
    List<Object[]> getSpendingByCategoryForPeriod(
            @Param("userId") String userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Recent transactions (limit)
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId ORDER BY t.transactionDate DESC, t.createdAt DESC LIMIT :limit")
    List<Transaction> findRecentTransactions(@Param("userId") String userId, @Param("limit") int limit);

    long countByUserId(String userId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.bankAccount.id = :bankAccountId " +
            "AND t.transactionDate BETWEEN :startDate AND :endDate")
    long countByBankAccountIdAndDateRange(
            @Param("bankAccountId") String bankAccountId,
            @Param("startDate") LocalDate StartDate,
            @Param("endDate") LocalDate EndDate);

    @Query("SELECT COUNT(t) > 0 FROM Transaction t WHERE t.bankAccount.id = :bankAccountId " +
            "AND t.transactionDate = :date AND t.description = :description AND t.amount = :amount")
    boolean existsByBankAccountIdAndDateAndDescriptionAndAmount(
            @Param("bankAccountId") String bankAccountId,
            @Param("date") LocalDate date,
            @Param("description") String description,
            @Param("amount") BigDecimal amount);
}
