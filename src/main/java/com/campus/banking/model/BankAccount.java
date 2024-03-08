package com.campus.banking.model;

import java.util.concurrent.locks.ReentrantLock;

import com.campus.banking.utils.AutoCloseableLock;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;
import lombok.experimental.SuperBuilder;

@Data
@With
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = SavingAccount.class, name = "saving"),
        @Type(value = CheckingAccount.class, name = "checking")
})
@JsonTypeName("normal")
@Entity
@Table(name = "bank_accounts")
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ToString.Exclude
    @Transient
    private final AutoCloseableLock lock = new AutoCloseableLock(new ReentrantLock());

    @EqualsAndHashCode.Include
    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_holder_name")
    private String accountHolderName;

    private double balance;

    public BankAccount(String accountNumber, String accountHolderName, double balance) {
        // TODO: another work around could be using @RequiredArgsConstructor and @NonNull
        this(null, accountNumber, accountHolderName, balance);
    }

    public BankAccount(String accountNumber, String accountHolderName) {
        this(accountNumber, accountHolderName, 0.0d);
    }
}
