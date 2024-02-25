package com.campus.banking.model;

import com.campus.banking.utils.InterestType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;

// TODO: add Data annotation & delete Getter and Setter annotations
@AllArgsConstructor
@With
public class SavingAccount extends BankAccount {
    @Getter
    private double interestRate;
    @Getter
    private InterestType interestType;
    @Getter
    private double minimumBalance;

    public SavingAccount(String accountNumber, String accountHolderName, double balance, double interestRate, InterestType interestType, double minimumBalance) {
        super(accountNumber, accountHolderName, balance);
        this.interestRate = interestRate;
        this.interestType = interestType;
        this.minimumBalance = minimumBalance;
    }
}
