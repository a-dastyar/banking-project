package com.campus.banking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.With;

// TODO: search about @Builder
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@With
public class CheckingAccount extends BankAccount {
    public static final int TRANSACTION_FEE = 100;

    private double overDraftLimit;
    private double debt;

    public CheckingAccount(String accountNumber, String accountHolderName, double balance, double overDraftLimit, double debt) {
        super(accountNumber, accountHolderName, balance);
        this.overDraftLimit = overDraftLimit;
        this.debt = debt;
    }
}
