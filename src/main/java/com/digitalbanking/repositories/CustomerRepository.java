package com.digitalbanking.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.digitalbanking.entities.CustomerEntity;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, String> {
    
    // Spring will automatically join with the UserEntity table to find by username
    Optional<CustomerEntity> findByUsername(String username);

    Optional<CustomerEntity> findByMobileNumber(String mobileNumber);
}


