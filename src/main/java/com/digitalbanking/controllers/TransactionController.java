package com.digitalbanking.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digitalbanking.dtos.TransactionRequest;
import com.digitalbanking.dtos.TransferRequest;
import com.digitalbanking.entities.TransactionEntity;
import com.digitalbanking.services.TransactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

	private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionEntity> deposit(@RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.depositMoney(
            request.getAccountNumber(), 
            request.getAmount()));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionEntity> withdraw(@RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.withdrawMoney(
            request.getAccountNumber(), 
            request.getAmount()));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionEntity> transfer(@RequestBody TransferRequest request) {
        return ResponseEntity.ok(transactionService.transferMoney(
            request.getFromAccountNumber(), 
            request.getToAccountNumber(), 
            request.getAmount()));
    }

 // CUSTOMER: View only their specific history
    @GetMapping("/history/{accountNumber}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<TransactionEntity>> getHistory(@PathVariable("accountNumber") String accountNumber) {
        return ResponseEntity.ok(transactionService.getTransactionHistory(accountNumber));
    }

    // ADMIN: View every transaction in the system
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<List<TransactionEntity>> viewAll() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }
}

