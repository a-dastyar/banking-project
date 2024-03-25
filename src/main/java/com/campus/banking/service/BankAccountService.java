package com.campus.banking.service;

import java.util.List;
import java.util.Map;

import com.campus.banking.model.BankAccount;
import com.campus.banking.model.Transaction;
import com.campus.banking.model.User;
import com.campus.banking.persistence.Page;
import com.campus.banking.util.Utils;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public interface BankAccountService<T extends BankAccount> {

    public static BankAccount toBankAccount(Map<String, String[]> properties) {
        var accountNumber = Utils.first(properties, "account_number")
                .orElse(null);

        var username = Utils.first(properties, "username")
                .orElse(null);

        var balance = Utils.firstDouble(properties, "balance")
                .orElse(0.0d);

        return BankAccount.builder()
                .accountNumber(accountNumber)
                .accountHolder(User.builder().username(username).build())
                .balance(balance)
                .build();
    }

    void add(@NotNull @Valid T account);

    Page<T> getPage(@Positive int page);

    Page<Transaction> getTransactions(@NotNull @NotBlank String accountNumber, @Positive int page);

    T getByAccountNumber(@NotNull @NotBlank String accountNumber);

    void deposit(@NotNull @NotBlank String accountNumber, @Positive double amount);

    void withdraw(@NotNull @NotBlank String accountNumber, @Positive double amount);

    double sumBalanceHigherThan(@PositiveOrZero double min);

    List<T> getByUsername(@NotNull @NotBlank String username);

    double getAllowedWithdraw(@NotNull @Valid T account);

    double getMinimumDeposit(@NotNull @Valid T account);

}
