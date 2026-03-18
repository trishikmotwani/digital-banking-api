package com.digitalbanking.dtos;

import com.digitalbanking.entities.TransactionType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TransactionDto {
    private String id;
    private double amount;
    private String description;
    private TransactionType type;
    private String senderAccountNumber;   // Just the string, not the object
    private String receiverAccountNumber; // Just the string, not the object
    private LocalDateTime createdAt;
}