package com.campus.banking.service;

import java.util.Arrays;
import java.util.Map;

import com.campus.banking.model.CheckingAccount;

public interface CheckingAccountService extends BankAccountService<CheckingAccount> {

    public static CheckingAccount toCheckingAccount(Map<String, String[]> properties) {
        return CheckingAccount.builder()
                .accountNumber(Arrays.stream(properties.get("checking_account_number")).findFirst().orElse(null))
                .accountHolderName(Arrays.stream(properties.get("checking_account_holder_name")).findFirst().orElse(null))
                .balance(Arrays.stream(properties.get("checking_balance")).findFirst().map(Double::valueOf).orElse(0.0d))
                .overDraftLimit(Arrays.stream(properties.get("checking_over_draft_limit")).findFirst().map(Double::valueOf).orElse(0.0d))
                .debt(Arrays.stream(properties.get("checking_debt")).findFirst().map(Double::valueOf).orElse(0.0d))
                .build();
    }
}
