package com.campus.banking.persistence;

import com.campus.banking.model.CheckingAccount;

public class CheckingAccountDAOImpl extends AbstractBankAccountDAOImpl<CheckingAccount> {

    public CheckingAccountDAOImpl(Database db) {
        super(db);
    }

    @Override
    Class<CheckingAccount> getType() {
        return CheckingAccount.class;
    }

}