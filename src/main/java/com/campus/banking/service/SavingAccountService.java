package com.campus.banking.service;

import java.util.Arrays;
import java.util.Map;

import com.campus.banking.model.InterestPeriod;
import com.campus.banking.model.SavingAccount;
import com.campus.banking.model.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface SavingAccountService extends BankAccountService<SavingAccount> {

    public static SavingAccount toSavingAccount(Map<String, String[]> properties) {
        var username = Arrays.stream(properties.get("username")).findFirst().orElse(null);
        return SavingAccount.builder()
                .accountNumber(Arrays.stream(properties.get("account_number")).findFirst().orElse(null))
                .accountHolder(User.builder().username(username).build())
                .balance(Arrays.stream(properties.get("balance")).findFirst().map(Double::valueOf).orElse(0.0d))
                .interestRate(Arrays.stream(properties.get("interest_rate")).findFirst().map(Double::valueOf).orElse(0.0d))
                .interestPeriod(Arrays.stream(properties.get("interest_period")).findFirst().map(InterestPeriod::valueOf).orElse(null))
                .minimumBalance(Arrays.stream(properties.get("minimum_balance")).findFirst().map(Double::valueOf).orElse(0.0d))
                .build();
    }

    void applyInterest(@NotNull @NotBlank String accountNumber);

    void applyInterest();
}
