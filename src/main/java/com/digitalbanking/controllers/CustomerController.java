package com.digitalbanking.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digitalbanking.entities.CustomerEntity;
import com.digitalbanking.services.CustomerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/add")
    public ResponseEntity<CustomerEntity> addCustomer(@RequestBody CustomerEntity customer) {
        return new ResponseEntity<>(customerService.addCustomer(customer), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerEntity> updateCustomer(@PathVariable("id") String id, @RequestBody CustomerEntity customer) {
        return ResponseEntity.ok(customerService.updateCustomer(id, customer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable("id") String id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Customer archived successfully.");
    }

    @GetMapping("/all")
    public ResponseEntity<List<CustomerEntity>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @PatchMapping("/verify-kyc/{mobileNumber}")
    public ResponseEntity<CustomerEntity> verifyKyc(@PathVariable("mobileNumber") String mobileNumber) {
        return ResponseEntity.ok(customerService.verifyKyc(mobileNumber));
    }
}

