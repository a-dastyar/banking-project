package com.campus.banking.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.campus.banking.model.BankAccount;
import com.campus.banking.model.Transaction;
import com.campus.banking.model.User;
import com.campus.banking.persistence.Page;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public interface BankAccountService<T extends BankAccount> {

    public static BankAccount toBankAccount(Map<String, String[]> properties) {
        var username = Arrays.stream(properties.get("username")).findFirst().orElse(null);
        return BankAccount.builder()
                .accountNumber(Arrays.stream(properties.get("account_number")).findFirst().orElse(null))
                .accountHolder(User.builder().username(username).build())
                .balance(Arrays.stream(properties.get("balance")).findFirst().map(Double::valueOf).orElse(0.0d))
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
