package com.example.smartbank.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.smartbank.model.Account;
import com.example.smartbank.model.TransactionRecord;
import com.example.smartbank.service.AccountService;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Get account balance
    @GetMapping("/balance/{userId}")
    public Map<String, Object> getBalance(@PathVariable Long userId) {
        try {
            Account acc = accountService.getAccountByUserId(userId);
            return Map.of(
                "accountNumber", acc.getAccountNumber(),
                "balance", acc.getBalance()
            );
        } catch (Exception e) {
            return Map.of("error", "Account not found");
        }
    }

    // Withdraw
    @PostMapping("/withdraw")
    public Map<String, Object> withdraw(@RequestBody Map<String, Object> body) {
        try {
            Long userId = Long.valueOf(body.get("userId").toString());
            BigDecimal amount = new BigDecimal(body.get("amount").toString());
            String note = body.getOrDefault("note", "").toString();

            Account acc = accountService.withdraw(userId, amount, note);

            return Map.of(
                "accountNumber", acc.getAccountNumber(),
                "balance", acc.getBalance()
            );
        } catch (Exception e) {
            return Map.of("error", "Withdrawal failed: " + e.getMessage());
        }
    }

    // Get transactions
    @GetMapping("/transactions/{userId}")
    public List<TransactionRecord> getTransactions(@PathVariable Long userId) {
        return accountService.getTransactions(userId);
    }

    // Deposit
    @PostMapping("/deposit/{accountNumber}")
    public Map<String, Object> deposit(
            @PathVariable String accountNumber,
            @RequestBody Map<String, Object> body) {
        try {
            BigDecimal amount = new BigDecimal(body.get("amount").toString());
            Account acc = accountService.getAccountByNumber(accountNumber);

            accountService.deposit(acc, amount);

            return Map.of(
                "message", "Deposit successful",
                "newBalance", acc.getBalance()
            );
        } catch (Exception e) {
            return Map.of("error", "Deposit failed: " + e.getMessage());
        }
    }
}
