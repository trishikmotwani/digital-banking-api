package com.digitalbanking.dtos;

import lombok.Data;

@Data
public class TransactionRequest {
    private String accountNumber;
    private double amount;
}
