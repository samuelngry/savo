package com.savo.backend.controller;

import com.savo.backend.model.BankAccount;
import com.savo.backend.model.User;
import com.savo.backend.service.BankAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bank-accounts")
@CrossOrigin(origins = "*")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping
    public ResponseEntity<?> getActiveBankAccounts(@AuthenticationPrincipal User user) {
        List<BankAccount> accounts = bankAccountService.getAllActiveAccounts(user);
        return ResponseEntity.ok(accounts);
    }
}
