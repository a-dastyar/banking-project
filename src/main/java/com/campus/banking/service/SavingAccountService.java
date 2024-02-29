package com.campus.banking.service;

import com.campus.banking.model.SavingAccount;

public interface SavingAccountService extends BankAccountService<SavingAccount> {
    void applyInterest(SavingAccount account);
}
