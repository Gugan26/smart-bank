package com.example.smartbank.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.smartbank.model.Account;
import com.example.smartbank.model.TransactionRecord;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionRecord, Long> {
    List<TransactionRecord> findByAccountOrderByCreatedAtDesc(Account account);
}
