package com.campus.banking.persistence;

import com.campus.banking.model.SavingAccount;

public interface SavingAccountDAO extends BankAccountDAO<SavingAccount> {

    void applyInterest();

}
