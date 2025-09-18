package com.savo.backend.controller;

import com.savo.backend.dto.bankaccount.BankAccountCreateDTO;
import com.savo.backend.dto.bankaccount.BankAccountResponseDTO;
import com.savo.backend.dto.bankaccount.BankAccountUpdateDTO;
import com.savo.backend.service.BankAccountService;
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
@RequestMapping("/api/v1/users/bank-accounts")
@CrossOrigin(origins = "*")
@Tag(name = "Bank Account", description = "Manage user's bank accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping
    @Operation(
            summary = "Create bank account",
            description = "Create the authenticated user's bank account",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Created bank account successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<BankAccountResponseDTO> createBankAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody BankAccountCreateDTO createDTO) {

        String userId = userDetails.getUsername();

        BankAccountResponseDTO created =  bankAccountService.createBankAccount(userId, createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<BankAccountResponseDTO>> getAllBankAccounts(
            @PathVariable String userId) {

        List<BankAccountResponseDTO> bankAccounts = bankAccountService.getUserBankAccounts(userId);
        return ResponseEntity.ok(bankAccounts);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<BankAccountResponseDTO> getBankAccount(
            @PathVariable String userId,
            @PathVariable String accountId) {

        BankAccountResponseDTO bankAccount =  bankAccountService.getBankAccount(userId, accountId);
        return ResponseEntity.ok(bankAccount);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<BankAccountResponseDTO> updateBankAccount(
            @PathVariable String userId,
            @PathVariable String accountId,
            @Valid @RequestBody BankAccountUpdateDTO updateDTO) {

        BankAccountResponseDTO updated = bankAccountService.updateBankAccount(userId, accountId, updateDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<BankAccountResponseDTO> deleteBankAccount(
            @PathVariable String userId,
            @PathVariable String accountId) {

        BankAccountResponseDTO deactivatedAccount = bankAccountService.deactivateBankAccount(userId, accountId);
        return ResponseEntity.ok(deactivatedAccount);
    }

    @PutMapping("/{accountId}/activate")
    public ResponseEntity<BankAccountResponseDTO> activateBankAccount(
            @PathVariable String userId,
            @PathVariable String accountId) {

        BankAccountResponseDTO activatedAccount = bankAccountService.activateBankAccount(userId, accountId);
        return ResponseEntity.ok(activatedAccount);
    }
}
