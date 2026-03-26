package com.digitalbanking.entities;


import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customers")
@SQLDelete(sql = "UPDATE customers SET is_deleted = true WHERE user_id = ?")
@Where(clause = "is_deleted = false")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity extends UserEntity {

    private int age;
    private double income;
    private String mobileNumber;
    private boolean kycVerified = false;
    private boolean isDeleted;
    
    // A customer can have multiple accounts  cascade = CascadeType.ALL
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<AccountEntity> accounts;
    
// @OneToOne is usually used for has-a relationship, for is-a relationshil we use @Inheritence(strategy = InheritanceType.JOINED)
// JPA + hibernate works in the same way.
//    @OneToOne(mappedBy = "user_id", fetch = FetchType.LAZY)
//    private UserEntity user;
    
}

//1. mappedBy = "customer" (The "Who is in charge?")
//This is the most important part. It tells Hibernate: "I am not the owner of this relationship. Look at the customer field inside the AccountEntity class to find the actual database mapping."
//Without this: Hibernate will assume you want a Join Table (a third table just to link IDs), which is usually unnecessary and slower for a simple Customer-Account link.
//With this: It simply uses the existing Foreign Key in the accounts table.
//
//2. cascade = CascadeType.ALL (The "Follow the leader")
//This defines what happens to the Accounts when you perform an action on the Customer.
//PERSIST: If you save a new Customer with 2 new Accounts in the list, Hibernate saves all 3 automatically.
//REMOVE: If you delete a Customer, Hibernate automatically deletes all their Accounts.
//In Banking: Use this carefully! While ALL is convenient for setup, you might not want to accidentally delete all financial history just because a user profile was removed.
//
//3. fetch = FetchType.LAZY (The "Don't look yet")
//As we discussed before, this prevents Hibernate from loading the entire list of accounts every time you fetch a customer.
//Scenario: You want to change a customer's phone number.
//LAZY: Hibernate only fetches the Customer row. (Fast)
//EAGER: Hibernate fetches the Customer AND does a join to fetch all 5 of their Accounts. (Slower/Wastes memory).
