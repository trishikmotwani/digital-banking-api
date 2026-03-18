package com.digitalbanking.dtos;

import lombok.Data;

@Data
public class TransferRequest {
    private String fromAccountNumber;
    private String toAccountNumber;
    private double amount;
    private String otp; // New field
}
