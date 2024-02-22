package com.campus.banking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.With;

// TODO: add Data annotation & delete Getter and Setter annotations
@AllArgsConstructor
@With
public class CheckingAccount extends BankAccount {
    @Getter
    private double overDraftLimit;
    @Getter @Setter
    private double overDraft;

    public CheckingAccount(String accountNumber, String accountHolderName, double balance, double overDraftLimit, double overDraft) {
        super(accountNumber, accountHolderName, balance);
        this.overDraftLimit = overDraftLimit;
        this.overDraft = overDraft;
    }
}
