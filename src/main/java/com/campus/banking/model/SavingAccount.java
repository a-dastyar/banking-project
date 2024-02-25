package com.campus.banking.model;

import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.experimental.SuperBuilder;

@Data
@With
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class SavingAccount extends BankAccount {

    public static final double INTEREST_RATE = 0.1d;

    private InterestPeriod interestType;
    
    private double minimumBalance;

    public SavingAccount(String accountNumber, String accountHolderName, double balance, InterestPeriod interestType, double minimumBalance) {
        super(accountNumber, accountHolderName, balance);
        this.interestType = interestType;
        this.minimumBalance = minimumBalance;
    }
}
