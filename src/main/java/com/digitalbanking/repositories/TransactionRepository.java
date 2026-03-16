package com.digitalbanking.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.digitalbanking.entities.TransactionEntity;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {
    
    // Find all transactions for a specific account (both as sender and receiver)
    List<TransactionEntity> findBySenderAccount_IdOrReceiverAccount_IdOrderByCreatedAtDesc(Long senderId, Long receiverId);
    
    // Admin view: fetch all transactions sorted by newest first
    List<TransactionEntity> findAllByOrderByCreatedAtDesc();
}

