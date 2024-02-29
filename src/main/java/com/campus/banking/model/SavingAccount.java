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

    // TODO: make interest rate an instance field and make it percentage
    public static final double INTEREST_RATE = 0.1d;

    private InterestPeriod interestPeriod;
    
    private double minimumBalance;

    public SavingAccount(String accountNumber, String accountHolderName, double balance, InterestPeriod interestPeriod, double minimumBalance) {
        super(accountNumber, accountHolderName, balance);
        this.interestPeriod = interestPeriod;
        this.minimumBalance = minimumBalance;
    }
}
