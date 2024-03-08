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
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class SavingAccount extends BankAccount {
    
    private double interestRate;

    private InterestPeriod interestPeriod;
    
    private double minimumBalance;

    public SavingAccount(String accountNumber, String accountHolderName, double balance, double interestRate, InterestPeriod interestPeriod, double minimumBalance) {
        super(0,accountNumber, accountHolderName, balance);
        this.interestRate = interestRate;
        this.interestPeriod = interestPeriod;
        this.minimumBalance = minimumBalance;
    }
}
