package com.savo.backend.controller;

import com.savo.backend.dto.transaction.TransactionCreateDTO;
import com.savo.backend.dto.transaction.TransactionResponseDTO;
import com.savo.backend.dto.transaction.TransactionUpdateDTO;
import com.savo.backend.service.impl.TransactionServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/transactions")
@CrossOrigin(origins = "*")
@Tag(name = "Transactions", description = "User transactions API")
public class TransactionController {

    private final TransactionServiceImpl transactionService;

    public TransactionController(TransactionServiceImpl transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    @Operation(
            summary = "Create user transaction",
            description = "Create the authenticated user's transaction",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created transaction successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TransactionCreateDTO dto) {

        String userId = userDetails.getUsername();

        TransactionResponseDTO createdTransaction = transactionService.createTransaction(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
    }

    @GetMapping
    @Operation(
            summary = "Get user transactions",
            description = "Get the authenticated user's transactions",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<List<TransactionResponseDTO>> getTransactions(
            @AuthenticationPrincipal UserDetails userDetails) {

        String userId = userDetails.getUsername();

        List<TransactionResponseDTO> transactions = transactionService.getUserTransactions(userId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{transactionId}")
    @Operation(
            summary = "Get user transaction",
            description = "Get the authenticated user's specific transaction",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transaction retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<TransactionResponseDTO> getTransaction(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String transactionId) {

        String userId = userDetails.getUsername();

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
