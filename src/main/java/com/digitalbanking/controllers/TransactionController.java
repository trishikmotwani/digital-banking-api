package com.digitalbanking.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digitalbanking.entities.TransactionEntity;
import com.digitalbanking.services.TransactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionEntity> deposit(@RequestParam String accountNumber, @RequestParam double amount) {
        return ResponseEntity.ok(transactionService.depositMoney(accountNumber, amount));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionEntity> withdraw(@RequestParam String accountNumber, @RequestParam double amount) {
        return ResponseEntity.ok(transactionService.withdrawMoney(accountNumber, amount));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionEntity> transfer(@RequestParam String from, @RequestParam String to, @RequestParam double amount) {
        return ResponseEntity.ok(transactionService.transferMoney(from, to, amount));
    }

    @GetMapping("/history/{accountNumber}")
    public ResponseEntity<List<TransactionEntity>> getHistory(@PathVariable String accountNumber) {
        return ResponseEntity.ok(transactionService.getTransactionHistory(accountNumber));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<TransactionEntity>> viewAll() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }
}

