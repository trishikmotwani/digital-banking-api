package com.digitalbanking.services;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digitalbanking.dtos.TransactionDto;
import com.digitalbanking.dtos.TransferRequest;
import com.digitalbanking.entities.AccountEntity;
import com.digitalbanking.entities.AccountStatus;
import com.digitalbanking.entities.TransactionEntity;
import com.digitalbanking.entities.TransactionType;
import com.digitalbanking.repositories.AccountRepository;
import com.digitalbanking.repositories.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepo;
    private final AccountRepository accountRepo;
    private final ModelMapper modelMapper;
    
    // In-memory OTP storage
    private final Map<String, String> otpCache = new ConcurrentHashMap<>();

    // --- STEP 1: INITIATE ---
    public String initiateTransfer(String senderAccNo) {
        AccountEntity account = accountRepo.findByAccountNumber(senderAccNo)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        String phoneNumber = account.getCustomer().getMobileNumber(); 
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new RuntimeException("No mobile number found for this account. Please update profile.");
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        otpCache.put(senderAccNo, otp);
        
        // Prepare WhatsApp link
        String message = "Your Digital Banking OTP is: " + otp;
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
        
        // Ensure phone number doesn't have '+' for the wa.me URL
        String cleanPhone = phoneNumber.replace("+", "");
        return "https://wa.me/" + cleanPhone + "?text=" + encodedMessage;
    }

    // --- STEP 2: CONFIRM ---
    @Transactional
    public TransactionDto confirmAndTransfer(TransferRequest request) {
        // 1. Validate OTP
        String cachedOtp = otpCache.get(request.getFromAccountNumber());
        if (cachedOtp == null || !cachedOtp.equals(request.getOtp())) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        // 2. Clear OTP used
        otpCache.remove(request.getFromAccountNumber());

        // 3. Execute money movement
        return transferMoney(
            request.getFromAccountNumber(), 
            request.getToAccountNumber(), 
            request.getAmount()
        );
    }

    @Transactional
    public TransactionDto depositMoney(String accountNumber, double amount) {
        AccountEntity account = getActiveAccount(accountNumber);
        account.setBalance(account.getBalance() + amount);
        
        TransactionEntity txn = new TransactionEntity();
        txn.setAmount(amount);
        txn.setType(TransactionType.DEPOSIT);
        txn.setReceiverAccount(account);
        txn.setDescription("Cash Deposit");
        
        return modelMapper.map(transactionRepo.save(txn), TransactionDto.class);
    }

    @Transactional
    public TransactionDto withdrawMoney(String accountNumber, double amount) {
        AccountEntity account = getActiveAccount(accountNumber);
        if (account.getBalance() < amount) throw new RuntimeException("Insufficient Balance");
        
        account.setBalance(account.getBalance() - amount);
        
        TransactionEntity txn = new TransactionEntity();
        txn.setAmount(amount);
        txn.setType(TransactionType.WITHDRAWAL);
        txn.setSenderAccount(account);
        txn.setDescription("ATM Withdrawal");
        
        return modelMapper.map(transactionRepo.save(txn), TransactionDto.class);
    }

    // Internal helper called by confirmAndTransfer
    @Transactional
    public TransactionDto transferMoney(String senderAccNo, String receiverAccNo, double amount) {
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
        
        return modelMapper.map(transactionRepo.save(txn), TransactionDto.class);
    }

    @Transactional(readOnly = true)
    public List<TransactionDto> getTransactionHistory(String accountNumber) {
        AccountEntity account = accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        List<TransactionEntity> transactions = transactionRepo
                .findBySenderAccount_IdOrReceiverAccount_IdOrderByCreatedAtDesc(account.getId(), account.getId());

        return transactions.stream()
                .map(entity -> modelMapper.map(entity, TransactionDto.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionDto> getAllTransactions() {
        return transactionRepo.findAllByOrderByCreatedAtDesc().stream()
                .map(entity -> modelMapper.map(entity, TransactionDto.class))
                .collect(Collectors.toList());
    }

    private AccountEntity getActiveAccount(String accountNumber) {
        AccountEntity account = accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (account.getStatus() != AccountStatus.ACTIVE) 
            throw new RuntimeException("Account is " + account.getStatus());
        return account;
    }
}