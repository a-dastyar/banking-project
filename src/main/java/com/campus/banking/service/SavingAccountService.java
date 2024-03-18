package com.campus.banking.service;

import java.util.Arrays;
import java.util.Map;

import com.campus.banking.model.InterestPeriod;
import com.campus.banking.model.SavingAccount;
import com.campus.banking.model.User;

public interface SavingAccountService extends BankAccountService<SavingAccount> {

    public static SavingAccount toSavingAccount(Map<String, String[]> properties) {
        var userId = Arrays.stream(properties.get("user_id")).map(Long::valueOf).findFirst().orElse(null);
        return SavingAccount.builder()
                .accountNumber(Arrays.stream(properties.get("saving_account_number")).findFirst().orElse(null))
                .accountHoler(User.builder().id(userId).build())
                .balance(Arrays.stream(properties.get("saving_balance")).findFirst().map(Double::valueOf).orElse(0.0d))
                .interestRate(Arrays.stream(properties.get("saving_interest_rate")).findFirst().map(Double::valueOf).orElse(0.0d))
                .interestPeriod(Arrays.stream(properties.get("saving_interest_period")).findFirst().map(InterestPeriod::valueOf).orElse(null))
                .minimumBalance(Arrays.stream(properties.get("saving_minimum_balance")).findFirst().map(Double::valueOf).orElse(0.0d))
                .build();
    }

    void applyInterest(String accountNumber);

    void applyInterest();
}
