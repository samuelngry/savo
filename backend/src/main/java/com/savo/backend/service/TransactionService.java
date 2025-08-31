package com.savo.backend.service;

import com.savo.backend.dto.TransactionCreateDTO;
import com.savo.backend.dto.TransactionResponseDTO;
import com.savo.backend.dto.TransactionUpdateDTO;
import com.savo.backend.model.BankAccount;
import com.savo.backend.model.Category;
import com.savo.backend.model.Transaction;
import com.savo.backend.model.User;
import com.savo.backend.repository.BankAccountRepository;
import com.savo.backend.repository.CategoryRepository;
import com.savo.backend.repository.TransactionRepository;
import com.savo.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, BankAccountRepository bankAccountRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.categoryRepository = categoryRepository;
    }

    public TransactionResponseDTO createTransaction(String userId, TransactionCreateDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        BankAccount bankAccount = bankAccountRepository.findByUserIdAndId(userId, dto.getBankAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Bank account not found"));

        Category category = categoryRepository.findByUserIdAndId(userId, dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAmount(dto.getAmount());
        transaction.setTransactionDate(dto.getTransactionDate());
        transaction.setBankAccount(bankAccount);
        transaction.setCategory(category);
        transaction.setTransactionType(dto.getTransactionType());
        transaction.setMerchantName(dto.getMerchantName());
        transaction.setBalanceAfter(dto.getBalanceAfter());

        // Use merchant name if description not provided
        transaction.setDescription(dto.getMerchantName());
        setPatternRecognitionData(transaction);

        transaction.setManuallyCategorized(true);
        transaction.setCategoryConfidence(1.0);

        Transaction savedTransaction = transactionRepository.save(transaction);
        return TransactionResponseDTO.from(savedTransaction);
    }

    public List<TransactionResponseDTO> getUserTransactions(String userId) {
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);

        return transactions.stream()
                .map(TransactionResponseDTO::from)
                .collect(Collectors.toList());
    }

    public TransactionResponseDTO getTransaction(String userId, String transactionId) {
        Transaction transaction = transactionRepository.findByUserIdAndId(userId, transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id: " + transactionId));

        return TransactionResponseDTO.from(transaction);
    }

    public TransactionResponseDTO updateTransaction(String userId, String transactionId, TransactionUpdateDTO dto) {
        Transaction existingTransaction = transactionRepository.findByUserIdAndId(userId, transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id: " + transactionId));

        if (dto.getAmount() != null) {
            existingTransaction.setAmount(dto.getAmount());
        }

        if (dto.getTransactionDate() != null) {
            existingTransaction.setTransactionDate(dto.getTransactionDate());
            setPatternRecognitionData(existingTransaction);
        }

        if (dto.getTransactionType() != null) {
            existingTransaction.setTransactionType(dto.getTransactionType());
        }

        if (dto.getDescription() != null) {
            existingTransaction.setDescription(dto.getDescription());
        }

        if (dto.getMerchantName() != null) {
            existingTransaction.setMerchantName(dto.getMerchantName());
        }

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            existingTransaction.setCategory(category);

            existingTransaction.setCategoryConfidence(1.0);
            existingTransaction.setManuallyCategorized(true);
        }

        Transaction updatedTransaction = transactionRepository.save(existingTransaction);
        return TransactionResponseDTO.from(updatedTransaction);
    }

    public void deleteTransaction(String userId, String transactionId) {
        Transaction transaction = transactionRepository.findByUserIdAndId(userId, transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id: " + transactionId));

        if (transaction.getStatementUpload() != null) {
            throw new IllegalStateException("Cannot delete transaction imported from bank statement. Please contact support.");
        }

        transactionRepository.delete(transaction);
    }

    private void setPatternRecognitionData(Transaction transaction) {
        LocalDate date = transaction.getTransactionDate();

        transaction.setDayOfWeek(date.getDayOfWeek().getValue());
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        transaction.setWeekend(dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);

        // Initialise subscription fields to defaults for manual entries
        if (transaction.getSubscription() != null) {
            transaction.setSubscription(false);
        }
    }
}
