package com.campus.banking.service;

import java.util.List;

import com.campus.banking.model.SavingAccount;

public interface SavingAccountService extends BankAccountService {
    void applyInterest(SavingAccount account);
    void applyInterest(List<SavingAccount> accounts);
}
