package com.digitalbanking.services;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.digitalbanking.entities.CustomerEntity;
import com.digitalbanking.entities.UserRole;
import com.digitalbanking.repositories.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public CustomerEntity addCustomer(CustomerEntity customer) {
        // Hash password before saving as it's a UserEntity
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customer.setRole(UserRole.ROLE_USER);
        return customerRepository.save(customer);
    }

    public CustomerEntity updateCustomer(String id, CustomerEntity details) {
        CustomerEntity customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        customer.setMobileNumber(details.getMobileNumber());
        customer.setIncome(details.getIncome());
        customer.setAge(details.getAge());
        return customerRepository.save(customer);
    }

    public void deleteCustomer(String id) {
        // This triggers the @SQLDelete soft delete we configured
        customerRepository.deleteById(id);
    }

    public List<CustomerEntity> getAllCustomers() {
        return customerRepository.findAll();
    }

    public CustomerEntity verifyKyc(String mobileNumber) {
        CustomerEntity customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new RuntimeException("Customer with mobile " + mobileNumber + " not found"));
        
        customer.setKycVerified(true);
        return customerRepository.save(customer);
    }
}

