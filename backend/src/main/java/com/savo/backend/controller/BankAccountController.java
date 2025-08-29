package com.savo.backend.controller;

import com.savo.backend.dto.BankAccountCreateDTO;
import com.savo.backend.dto.BankAccountResponseDTO;
import com.savo.backend.dto.BankAccountUpdateDTO;
import com.savo.backend.model.BankAccount;
import com.savo.backend.service.BankAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users/{userId}/bank-accounts")
@CrossOrigin(origins = "*")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping
    public ResponseEntity<BankAccountResponseDTO> createBankAccount(
            @PathVariable String userId,
            @Valid @RequestBody BankAccountCreateDTO createDTO) {

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

}
