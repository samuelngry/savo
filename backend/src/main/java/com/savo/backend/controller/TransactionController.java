package com.savo.backend.controller;

import com.savo.backend.dto.TransactionCreateDTO;
import com.savo.backend.dto.TransactionResponseDTO;
import com.savo.backend.model.Transaction;
import com.savo.backend.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
