package com.campus.banking.service;

import java.util.Map;

import com.campus.banking.model.CheckingAccount;
import com.campus.banking.model.User;
import com.campus.banking.util.Utils;

public interface CheckingAccountService extends BankAccountService<CheckingAccount> {

    public static CheckingAccount toCheckingAccount(Map<String, String[]> properties) {
        var accountNumber = Utils.first(properties, "account_number")
                .orElse(null);

        var username = Utils.first(properties, "username")
                .orElse(null);

        var balance = Utils.firstDouble(properties, "balance")
                .orElse(0.0d);
                
        var overdraftLimit = Utils.firstDouble(properties, "overdraft_limit")
                .orElse(0.0d);

        var debt = Utils.firstDouble(properties, "debt")
                .orElse(0.0d);

        return CheckingAccount.builder()
                .accountNumber(accountNumber)
                .accountHolder(User.builder().username(username).build())
                .balance(balance)
                .overdraftLimit(overdraftLimit)
                .debt(debt)
                .build();
    }
}
