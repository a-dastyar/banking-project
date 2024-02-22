package com.campus.banking.persistence;

import java.util.List;

import com.campus.banking.exception.LoadFailureException;
import com.campus.banking.exception.SaveFailureException;
import com.campus.banking.model.BankAccount;

public interface Database {

    void add(BankAccount account);

    BankAccount get(String accountNumber);

    void remove(String accountNumber);
    
    List<BankAccount> list();

    void persist() throws SaveFailureException;

    void load() throws LoadFailureException;

}
