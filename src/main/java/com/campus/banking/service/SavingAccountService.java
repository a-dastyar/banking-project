package com.campus.banking.service;

import java.util.Arrays;
import java.util.Map;

import com.campus.banking.model.InterestPeriod;
import com.campus.banking.model.SavingAccount;

public interface SavingAccountService extends BankAccountService<SavingAccount> {

    public static SavingAccount toSavingAccount(Map<String, String[]> properties) {
        return SavingAccount.builder()
                .accountNumber(Arrays.stream(properties.get("saving_account_number")).findFirst().orElse(null))
                .accountHolderName(Arrays.stream(properties.get("saving_account_holder_name")).findFirst().orElse(null))
                .balance(Arrays.stream(properties.get("saving_balance")).findFirst().map(Double::valueOf).orElse(0.0d))
                .interestRate(Arrays.stream(properties.get("saving_interest_rate")).findFirst().map(Double::valueOf).orElse(0.0d))
                .interestPeriod(Arrays.stream(properties.get("saving_interest_period")).findFirst().map(InterestPeriod::valueOf).orElse(null))
                .minimumBalance(Arrays.stream(properties.get("saving_minimum_balance")).findFirst().map(Double::valueOf).orElse(0.0d))
                .build();
    }

    void applyInterest(String accountNumber);

    void applyInterest();
}
