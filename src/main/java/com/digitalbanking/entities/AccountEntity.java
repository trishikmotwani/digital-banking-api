package com.digitalbanking.entities;

import java.util.List;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accounts")
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    private double balance;

    @Enumerated(EnumType.STRING)
    private AccountStatus status = AccountStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;
    
    @OneToMany(mappedBy = "senderAccount", fetch = FetchType.LAZY)
    private List<TransactionEntity> sentTransactions;

    // All money received TO this account
    @OneToMany(mappedBy = "receiverAccount", fetch = FetchType.LAZY)
    private List<TransactionEntity> receivedTransactions;
}

//	@JoinColumn(name = "customer_id")
//	private CustomerEntity customer;
//    Think of these as the "When" and the "What" of your database relationship.
//    1. @JoinColumn(name = "customer_id") — The "What"
//    This defines the physical Foreign Key column in your MySQL table.
//    name = "customer_id": This tells Hibernate to create a column named customer_id in the accounts table. This column will store the primary key of the CustomerEntity.
//    nullable = false: This is a database constraint. It ensures that an account must belong to a customer. You cannot save an "orphan" account.
//    Without this annotation: Hibernate would pick a default name (like customer_id_unique_string), which is usually messy.
//    
//    2. FetchType — The "When"
//    This tells Hibernate when to load the customer's data from the database into Java memory.
//    FetchType.LAZY (Recommended for Banking)
//    How it works: When you find an account (accountRepo.findById(1)), Hibernate only fetches the account details. The customer object is a "Proxy" (an empty shell).
//    The Query: SELECT * FROM accounts WHERE id = 1;
//    When is the customer loaded?: Only when you actually call account.getCustomer().getName(). At that exact moment, Hibernate triggers a second query to fetch the customer data.
//    Why use it?: It saves memory and performance. If you just want to check an account balance, you don't need to load the entire customer profile.
//    FetchType.EAGER
//    How it works: Hibernate fetches everything at once using a SQL JOIN.
//    The Query: SELECT * FROM accounts a JOIN customers c ON a.customer_id = c.id WHERE a.id = 1;
//    Why avoid it?: If you have many relationships (Accounts -> Transactions -> Branch), EAGER fetching can cause a "Select N+1" problem, where one simple request triggers dozens of unnecessary database queries.
//    
//    3.Summary Table
//    Feature	JoinColumn	FetchType.LAZY
//    Role	Physical Database Column (FK)	Memory Loading Strategy
//    Goal	Link two tables together	Performance optimization
//    Analogy	The "Address" on an envelope	"Wait to open" the envelope until needed


