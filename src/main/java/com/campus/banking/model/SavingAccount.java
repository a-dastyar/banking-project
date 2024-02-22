package com.campus.banking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;

// TODO: add Data annotation & delete Getter and Setter annotations
@AllArgsConstructor
@With
public class SavingAccount extends BankAccount {
    @Getter
    private double minimumBalance;

    public SavingAccount(String accountNumber, String accountHolderName, double balance, double minimumBalance) {
        super(accountNumber, accountHolderName, balance);
        this.minimumBalance = minimumBalance;
    }
}
