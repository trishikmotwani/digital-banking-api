package com.digitalbanking.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.digitalbanking.entities.AccountEntity;
import com.digitalbanking.entities.AccountStatus;
import com.digitalbanking.entities.TransactionEntity;
import com.digitalbanking.entities.TransactionType;
import com.digitalbanking.repositories.AccountRepository;
import com.digitalbanking.repositories.TransactionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepo;
    private final AccountRepository accountRepo;

    @Transactional
    public TransactionEntity depositMoney(String accountNumber, double amount) {
        AccountEntity account = getActiveAccount(accountNumber);
        account.setBalance(account.getBalance() + amount);
        
        TransactionEntity txn = new TransactionEntity();
        txn.setAmount(amount);
        txn.setType(TransactionType.DEPOSIT);
        txn.setReceiverAccount(account);
        txn.setDescription("Cash Deposit");
        
        return transactionRepo.save(txn);
    }

    @Transactional
    public TransactionEntity withdrawMoney(String accountNumber, double amount) {
        AccountEntity account = getActiveAccount(accountNumber);
        if (account.getBalance() < amount) throw new RuntimeException("Insufficient Balance");
        
        account.setBalance(account.getBalance() - amount);
        
        TransactionEntity txn = new TransactionEntity();
        txn.setAmount(amount);
        txn.setType(TransactionType.WITHDRAWAL);
        txn.setSenderAccount(account);
        txn.setDescription("ATM Withdrawal");
        
        return transactionRepo.save(txn);
    }

    @Transactional
    public TransactionEntity transferMoney(String senderAccNo, String receiverAccNo, double amount) {
        AccountEntity sender = getActiveAccount(senderAccNo);
        AccountEntity receiver = getActiveAccount(receiverAccNo);
        
        if (sender.getBalance() < amount) throw new RuntimeException("Insufficient Balance");

        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        TransactionEntity txn = new TransactionEntity();
        txn.setAmount(amount);
        txn.setType(TransactionType.TRANSFER);
        txn.setSenderAccount(sender);
        txn.setReceiverAccount(receiver);
        txn.setDescription("Transfer to " + receiverAccNo);
        
        return transactionRepo.save(txn);
    }

    public List<TransactionEntity> getTransactionHistory(String accountNumber) {
        AccountEntity account = accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return transactionRepo.findBySenderAccount_IdOrReceiverAccount_IdOrderByCreatedAtDesc(account.getId(), account.getId());
    }

    public List<TransactionEntity> getAllTransactions() {
        return transactionRepo.findAllByOrderByCreatedAtDesc();
    }

    private AccountEntity getActiveAccount(String accountNumber) {
        AccountEntity account = accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (account.getStatus() != AccountStatus.ACTIVE) 
            throw new RuntimeException("Account is " + account.getStatus());
        return account;
    }
}
