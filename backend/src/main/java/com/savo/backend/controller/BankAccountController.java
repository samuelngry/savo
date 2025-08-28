package com.savo.backend.controller;

import com.savo.backend.model.BankAccount;
import com.savo.backend.model.User;
import com.savo.backend.service.BankAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PutMapping("/{id}/nickname")
    public ResponseEntity<?> updateAccountNickname(@PathVariable String id, @RequestBody Map<String, String> request, @AuthenticationPrincipal User user) {
        try {
            String newNickname = request.get("nickname");

            if (newNickname == null || newNickname.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Nickname is required"));
            }

            bankAccountService.updateNickname(user, id, newNickname);

            return ResponseEntity.ok(Map.of("message", "Successfully updated nickname"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
