package com.campus.banking.service;

import java.util.List;

import com.campus.banking.model.SavingAccount;

public interface SavingAccountService extends BankAccountService<SavingAccount> {
    void applyInterest(SavingAccount account);

    void applyInterest(List<SavingAccount> accounts);
    
    void applyInterestConcurrently(List<SavingAccount> accounts);
}
