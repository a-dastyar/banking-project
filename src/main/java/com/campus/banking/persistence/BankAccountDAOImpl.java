package com.campus.banking.persistence;

import com.campus.banking.model.BankAccount;


public class BankAccountDAOImpl extends AbstractBankAccountDAOImpl<BankAccount> {

    public BankAccountDAOImpl(Database db) {
        super(db);
    }

    @Override
    Class<BankAccount> getType() {
        return BankAccount.class;
    }

}