package com.example.smartbank.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.smartbank.model.Account;
import com.example.smartbank.model.TransactionRecord;
import com.example.smartbank.model.TransactionRecord.TxType;
import com.example.smartbank.repository.AccountRepository;
import com.example.smartbank.repository.TransactionRepository;

@Service
public class AccountService {

    private final AccountRepository accountRepo;
    private final TransactionRepository txRepo;

    public AccountService(AccountRepository accountRepo, TransactionRepository txRepo) {
        this.accountRepo = accountRepo;
        this.txRepo = txRepo;
    }

    public Account getAccountByUserId(Long userId) {
        return accountRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Transactional
    public synchronized Account withdraw(Long userId, BigDecimal amount, String note) {
        Account acc = getAccountByUserId(userId);

        if (acc.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        acc.setBalance(acc.getBalance().subtract(amount));
        accountRepo.save(acc);

        TransactionRecord tr = new TransactionRecord();
        tr.setAccount(acc);
        tr.setAmount(amount);
        tr.setType(TxType.DEBIT);
        tr.setNote(note);
        tr.setCreatedAt(LocalDateTime.now());
        txRepo.save(tr);

        return acc;
    }

    public List<TransactionRecord> getTransactions(Long userId) {
        Account acc = getAccountByUserId(userId);
        return txRepo.findByAccountOrderByCreatedAtDesc(acc);
    }

    public void deposit(Account acc, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        acc.setBalance(acc.getBalance().add(amount));
        accountRepo.save(acc);

        TransactionRecord tx = new TransactionRecord();
        tx.setAccount(acc);
        tx.setAmount(amount);
        tx.setType(TxType.CREDIT);
        tx.setCreatedAt(LocalDateTime.now());
        txRepo.save(tx);
    }

    public Account getAccountByNumber(String accountNumber) {
        return accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }
}
