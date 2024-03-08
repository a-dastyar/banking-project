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
public class CheckingAccount extends BankAccount {
    
    public static final int TRANSACTION_FEE = 100;

    private double overDraftLimit;

    private double debt;

    public CheckingAccount(String accountNumber, String accountHolderName, double balance, double overDraftLimit,
            double debt) {
        super(0, accountNumber, accountHolderName, balance);
        this.overDraftLimit = overDraftLimit;
        this.debt = debt;
    }
}
