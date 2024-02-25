package com.campus.banking.model;

import com.campus.banking.utils.InterestType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.With;

// TODO: search about @Builder
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@With
public class SavingAccount extends BankAccount {
    public static final double INTEREST_RATE = 0.1d;

    private InterestType interestType;
    private double minimumBalance;

    public SavingAccount(String accountNumber, String accountHolderName, double balance, InterestType interestType, double minimumBalance) {
        super(accountNumber, accountHolderName, balance);
        this.interestType = interestType;
        this.minimumBalance = minimumBalance;
    }
}
