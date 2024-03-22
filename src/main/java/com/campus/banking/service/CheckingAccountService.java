package com.campus.banking.service;

import java.util.Arrays;
import java.util.Map;

import com.campus.banking.model.CheckingAccount;
import com.campus.banking.model.User;

public interface CheckingAccountService extends BankAccountService<CheckingAccount> {

    public static CheckingAccount toCheckingAccount(Map<String, String[]> properties) {
        var username = Arrays.stream(properties.get("username")).findFirst().orElse(null);
        return CheckingAccount.builder()
                .accountNumber(Arrays.stream(properties.get("account_number")).findFirst().orElse(null))
                .accountHolder(User.builder().username(username).build())
                .balance(Arrays.stream(properties.get("balance")).findFirst().map(Double::valueOf).orElse(0.0d))
                .overdraftLimit(
                        Arrays.stream(properties.get("overdraft_limit")).findFirst().map(Double::valueOf).orElse(0.0d))
                .debt(Arrays.stream(properties.get("debt")).findFirst().map(Double::valueOf).orElse(0.0d))
                .build();
    }
}
