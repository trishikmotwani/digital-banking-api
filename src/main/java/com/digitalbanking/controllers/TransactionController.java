package com.digitalbanking.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.digitalbanking.dtos.TransactionDto;
import com.digitalbanking.dtos.TransactionRequest;
import com.digitalbanking.dtos.TransferRequest;
import com.digitalbanking.services.TransactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionDto> deposit(@RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.depositMoney(request.getAccountNumber(), request.getAmount()));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionDto> withdraw(@RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.withdrawMoney(request.getAccountNumber(), request.getAmount()));
    }

    // Step 1: Initiate and get WhatsApp link
    @PostMapping("/transfer/initiate")
    public ResponseEntity<String> initiateTransfer(@RequestBody TransferRequest request) {
        String waLink = transactionService.initiateTransfer(request.getFromAccountNumber());
        return ResponseEntity.ok(waLink);
    }

    // Step 2: Confirm with OTP
    @PostMapping("/transfer/confirm")
    public ResponseEntity<TransactionDto> confirmTransfer(@RequestBody TransferRequest request) {
        return ResponseEntity.ok(transactionService.confirmAndTransfer(request));
    }

    @GetMapping("/history/{accountNumber}")
    public ResponseEntity<List<TransactionDto>> getHistory(@PathVariable("accountNumber") String accountNumber) {
        return ResponseEntity.ok(transactionService.getTransactionHistory(accountNumber));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<TransactionDto>> viewAll() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }
}