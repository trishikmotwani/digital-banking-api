package com.digitalbanking.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digitalbanking.entities.AccountEntity;
import com.digitalbanking.entities.AccountStatus;
import com.digitalbanking.entities.TransactionEntity;
import com.digitalbanking.services.AccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // Manager: Create Account for a specific customer
    @PostMapping("/manager/create/{customerId}")
    public ResponseEntity<AccountEntity> createAccount(@PathVariable("customerId") String customerId, @RequestBody AccountEntity account) {
        return new ResponseEntity<>(accountService.createAccount(customerId, account), HttpStatus.CREATED);
    }
    
 // Updated: Create Account using username
    @PostMapping("/manager/create-by-username/{username}")
    public ResponseEntity<AccountEntity> createAccountByUsername(
            @PathVariable("username") String username, 
            @RequestBody AccountEntity account) {
        
        // Ensure your service logic is updated to find the customer by username
        return new ResponseEntity<>(
            accountService.createAccountByUsername(username, account), 
            HttpStatus.CREATED
        );
    }

    // Manager: Change status (Block/Unblock)
    @PatchMapping("/manager/status/{id}")
    public ResponseEntity<AccountEntity> setStatus(@PathVariable("id") Long id, @RequestParam AccountStatus status) {
        return ResponseEntity.ok(accountService.updateStatus(id, status));
    }

    // Manager: View All Accounts
    @GetMapping("/manager/all")
    public ResponseEntity<List<AccountEntity>> viewAll() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    // Customer: View Balance
    @GetMapping("/customer/balance/{accountNumber}")
    public ResponseEntity<Double> viewBalance(@PathVariable("accountNumber") String accountNumber) {
        return ResponseEntity.ok(accountService.getBalance(accountNumber));
    }

    // Customer: View Statement (Data for the list)
    @GetMapping("/customer/statement/{accountNumber}")
    public ResponseEntity<List<TransactionEntity>> viewStatement(@PathVariable("accountNumber") String accountNumber) {
        return ResponseEntity.ok(accountService.getStatement(accountNumber));
    }
    
    // Customer: Get all accounts by username
    @GetMapping("/customer/{username}")
    public ResponseEntity<List<AccountEntity>> getAccountsByUsername(@PathVariable("username") String username) {
        // This will call the service layer to find accounts associated with the customer's username
        List<AccountEntity> accounts = accountService.getAccountsByUsername(username);
        return ResponseEntity.ok(accounts);
    }

}
