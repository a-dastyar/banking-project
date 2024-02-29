package com.campus.banking.persistence;

import com.campus.banking.model.SavingAccount;

public class SavingAccountDAOImpl extends AbstractBankAccountDAOImpl<SavingAccount> {

    public SavingAccountDAOImpl(Database db) {
        super(db);
    }

    @Override
    Class<SavingAccount> getType() {
        return SavingAccount.class;
    }

}