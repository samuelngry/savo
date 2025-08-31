package com.savo.backend.controller;

import com.savo.backend.dto.TransactionCreateDTO;
import com.savo.backend.dto.TransactionResponseDTO;
import com.savo.backend.dto.TransactionUpdateDTO;
import com.savo.backend.model.Transaction;
import com.savo.backend.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @PathVariable String userId,
            @Valid @RequestBody TransactionCreateDTO dto) {

        TransactionResponseDTO createdTransaction = transactionService.createTransaction(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getTransactions(
            @PathVariable String userId) {

        List<TransactionResponseDTO> transactions = transactionService.getUserTransactions(userId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> getTransaction(
            @PathVariable String userId,
            @PathVariable String transactionId) {

        TransactionResponseDTO transaction = transactionService.getTransaction(userId, transactionId);
        return ResponseEntity.ok(transaction);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
            @PathVariable String userId,
            @PathVariable String transactionId,
            @Valid @RequestBody TransactionUpdateDTO dto) {

        TransactionResponseDTO updatedTransaction = transactionService.updateTransaction(userId, transactionId, dto);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> deleteTransaction(
            @PathVariable String userId,
            @PathVariable String transactionId) {

        transactionService.deleteTransaction(userId, transactionId);
        return ResponseEntity.noContent().build();
    }
}
