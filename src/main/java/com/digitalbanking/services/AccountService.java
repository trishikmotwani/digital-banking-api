package com.digitalbanking.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.digitalbanking.entities.AccountEntity;
import com.digitalbanking.entities.AccountStatus;
import com.digitalbanking.entities.CustomerEntity;
import com.digitalbanking.entities.TransactionEntity;
import com.digitalbanking.repositories.AccountRepository;
import com.digitalbanking.repositories.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    // --- Admin/Manager Methods ---

    public AccountEntity createAccount(String customerId, AccountEntity account) {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        account.setCustomer(customer);
        account.setAccountNumber("DB-" + System.currentTimeMillis()); // Simple auto-gen logic
        account.setStatus(AccountStatus.ACTIVE);
        return accountRepository.save(account);
    }

    public AccountEntity updateStatus(Long id, AccountStatus status) {
        AccountEntity account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setStatus(status);
        return accountRepository.save(account);
    }

    public List<AccountEntity> getAllAccounts() {
        return accountRepository.findAll();
    }

    // --- Customer Methods ---

    public double getBalance(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .map(AccountEntity::getBalance)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public List<TransactionEntity> getStatement(String accountNumber) {
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        // Combines sent and received transactions
        List<TransactionEntity> allTransactions = new ArrayList<>();
        allTransactions.addAll(account.getSentTransactions());
        allTransactions.addAll(account.getReceivedTransactions());
        return allTransactions;
    }
}
