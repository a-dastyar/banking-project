package com.campus.banking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import com.campus.banking.exception.InsufficientFundsException;
import com.campus.banking.model.BankAccount;

public class BankAccountServiceTest {

    BankAccountService<BankAccount> service = new BankAccountServiceImpl<>();

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

    @Test
    void sumBalance_withNoAccount_shouldReturnZero() {
        var sum = service.sumBalance(List.of(), acc -> true);
        assertThat(sum).isEqualTo(0.0d);
    }

    @Test
    void sumBalance_withFalsePredicate_shouldReturnZero() {
        var account = BankAccount.builder()
                .balance(10.0).build();
        var sum = service.sumBalance(List.of(account), acc -> false);
        assertThat(sum).isEqualTo(0.0d);
    }

    @Test
    void sumBalance_withAccountsAndTruePredicate_shouldReturnZero() {
        var account = BankAccount.builder().build();
        var accounts = List.of(
                account.withBalance(10.0),
                account.withBalance(12.0),
                account.withBalance(15.0),
                account.withBalance(25.0));
        var sum = service.sumBalance(accounts, acc -> true);
        var expected = accounts.stream().mapToDouble(BankAccount::getBalance).sum();
        assertThat(sum).isEqualTo(expected);
    }

    @Test
    void sumBalance_withAccountsAndPredicateOnBalance_shouldReturnZero() {
        var account = BankAccount.builder().build();
        var accounts = List.of(
                account.withBalance(10.0),
                account.withBalance(12.0),
                account.withBalance(15.0),
                account.withBalance(25.0));
        var sum = service.sumBalance(accounts, acc -> acc.getBalance() > 13.0);
        assertThat(sum).isEqualTo(40.0);
    }

    @Test
    void sumBalance_withAccountsAndMultiPredicateOnBalance_shouldReturnZero() {
        var account = BankAccount.builder().build();
        var accounts = List.of(
                account.withBalance(10.0),
                account.withBalance(12.0),
                account.withBalance(15.0),
                account.withBalance(25.0));
        Predicate<BankAccount> predicate = acc -> acc.getBalance() > 12;
        predicate = predicate.and(acc -> acc.getBalance() < 25);
        var sum = service.sumBalance(accounts, predicate);
        assertThat(sum).isEqualTo(15.0);
    }
}
