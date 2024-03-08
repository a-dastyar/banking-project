package com.campus.banking.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.experimental.SuperBuilder;

@Data
@With
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BankAccount {

    private long id;

    @EqualsAndHashCode.Include
    private String accountNumber;

    private String accountHolderName;

    private double balance;

    public BankAccount(String accountNumber, String accountHolderName) {
        this(0,accountNumber, accountHolderName, 0.0d);
    }
}
