package com.example.smartbank.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.smartbank.model.Account;
import com.example.smartbank.model.User;
import com.example.smartbank.repository.AccountRepository;
import com.example.smartbank.repository.UserRepository;
@Service

public class AuthService {
       private final UserRepository userRepo;
    private final AccountRepository accountRepo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepo, AccountRepository accountRepo) {
        this.userRepo = userRepo;
        this.accountRepo = accountRepo;
    }

    public User register(String name, String email, String rawPassword) {
        if (userRepo.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already in use");
        }
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(encoder.encode(rawPassword));
        User saved = userRepo.save(u);

        Account a = new Account();
        a.setUser(saved);
        a.setAccountNumber("SBK" + UUID.randomUUID().toString().substring(0,8).toUpperCase());
        a.setBalance(BigDecimal.valueOf(1000.00)); 
        accountRepo.save(a);
        return saved;
    }

    public User login(String email, String rawPassword) {
        Optional<User> ou = userRepo.findByEmail(email);
        if (ou.isEmpty()) throw new RuntimeException("Invalid credentials");
        User u = ou.get();
        if (!encoder.matches(rawPassword, u.getPassword())) throw new RuntimeException("Invalid credentials");
        return u;
    }
}
