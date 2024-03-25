package com.campus.banking.service;

import java.util.Map;
import java.util.stream.Stream;

import com.campus.banking.model.InterestPeriod;
import com.campus.banking.model.SavingAccount;
import com.campus.banking.model.User;
import com.campus.banking.util.Utils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface SavingAccountService extends BankAccountService<SavingAccount> {

    public static SavingAccount toSavingAccount(Map<String, String[]> properties) {

        var periods = Stream.of(InterestPeriod.values()).map(InterestPeriod::toString).toList();

        var accountNumber = Utils.first(properties, "account_number")
                .orElse(null);

        var username = Utils.first(properties, "username")
                .orElse(null);

        var balance = Utils.firstDouble(properties, "balance")
                .orElse(0.0d);

        var minimumBalance = Utils.firstDouble(properties, "minimum_balance")
                .orElse(0.0d);

        var interestRate = Utils.firstDouble(properties, "interest_rate")
                .orElse(0.0d);

        var interestPeriod = Utils.first(properties, "interest_period")
                .filter(str -> periods.stream().anyMatch(str::equals))
                .map(InterestPeriod::valueOf)
                .orElse(null);

        return SavingAccount.builder()
                .accountNumber(accountNumber)
                .accountHolder(User.builder().username(username).build())
                .balance(balance)
                .minimumBalance(minimumBalance)
                .interestRate(interestRate)
                .interestPeriod(interestPeriod)
                .build();
    }

    void applyInterest(@NotNull @NotBlank String accountNumber);

    void applyInterest();
}
