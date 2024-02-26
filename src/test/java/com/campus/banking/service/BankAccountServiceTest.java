package com.campus.banking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.campus.banking.exception.InsufficientFundsException;
import com.campus.banking.model.BankAccount;

public class BankAccountServiceTest {

    BankAccountService service = new BankAccountServiceImpl();

    @Test
    void withdraw_withNegativeAmount_shouldFail() {
        var account = BankAccount.builder()
                .balance(10.0).build();
        assertThatThrownBy(() -> service.withdraw(account, -10.0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void withdraw_withMoreThanBalance_shouldFail() {
        var account = BankAccount.builder()
                .balance(10.0).build();
        assertThatThrownBy(() -> service.withdraw(account, 11.0))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void withdraw_withLessThanBalance_shouldWithdraw() {
        var account = BankAccount.builder()
                .balance(10.0).build();
        service.withdraw(account, 9.0);
        assertThat(account.getBalance()).isEqualTo(1.0);
    }

    @Test
    void deposit_withNegativeAmount_shouldFail() {
        var account = BankAccount.builder()
                .balance(10.0).build();
        assertThatThrownBy(() -> service.deposit(account, -10.0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deposit_withPositiveAmount_shouldDeposit() {
        var account = BankAccount.builder()
                .balance(10.0).build();
        service.deposit(account, 10.0);
        assertThat(account.getBalance()).isEqualTo(20.0);
    }
}