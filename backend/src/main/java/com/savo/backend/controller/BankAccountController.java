package com.savo.backend.controller;

import com.savo.backend.dto.BankAccountCreateDTO;
import com.savo.backend.dto.BankAccountResponseDTO;
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

}
