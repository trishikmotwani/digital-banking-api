package com.digitalbanking.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "transactions")
@Data
@EqualsAndHashCode(callSuper = true)
public class TransactionEntity extends BaseEntity { // Inherit createdAt as transaction date

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private double amount;

    private String description; // e.g., "Monthly Rent" or "ATM Withdrawal"

    @Enumerated(EnumType.STRING)
    private TransactionType type; // TRANSFER, DEPOSIT, WITHDRAWAL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_account_id") // Can be null for Deposits
    private AccountEntity senderAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_account_id") // Can be null for Withdrawals
    private AccountEntity receiverAccount;
}

