package com.campus.banking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import com.campus.banking.exception.InvalidTransactionException;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.SavingAccount;

public class SavingAccountServiceTest {

    SavingAccountService service = new SavingAccountServiceImpl();

    @Test
    void withdraw_withNegativeAmount_shouldFail() {
        var account = SavingAccount.builder()
                .balance(10.0)
                .minimumBalance(0.0)
                .build();
        assertThatThrownBy(() -> service.withdraw(account, -10.0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void withdraw_withMoreThanBalance_shouldFail() {
        var account = SavingAccount.builder()
                .balance(10.0)
                .minimumBalance(0.0)
                .build();
        assertThatThrownBy(() -> service.withdraw(account, 11.0))
                .isInstanceOf(InvalidTransactionException.class);
    }

    @Test
    void withdraw_withMoreThanMinimumBalance_shouldFail() {
        var account = SavingAccount.builder()
                .balance(10.0)
                .minimumBalance(5.0)
                .build();
        assertThatThrownBy(() -> service.withdraw(account, 6.0))
                .isInstanceOf(InvalidTransactionException.class);
    }

    @Test
    void withdraw_withLessThanBalanceAndMinimumBalance_shouldWithdraw() {
        var account = SavingAccount.builder()
                .balance(10.0)
                .minimumBalance(5.0)
                .build();
        service.withdraw(account, 1.0);
        assertThat(account.getBalance()).isEqualTo(9.0);
    }



    @Test
    void deposit_withNegativeAmount_shouldFail() {
        var account = SavingAccount.builder()
                .balance(10.0)
                .minimumBalance(0.0)
                .build();
        assertThatThrownBy(() -> service.deposit(account, -10.0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deposit_withPositiveAmount_shouldDeposit() {
        var account = SavingAccount.builder()
                .balance(10.0)
                .minimumBalance(0.0)
                .build();
        service.deposit(account, 10.0);
        assertThat(account.getBalance()).isEqualTo(20.0);
    }

    @Test
    void applyInterest_withZeroBalance_shouldNotChangeBalance(){
        var balance = 0.0d;
        var account = SavingAccount.builder()
                .balance(balance)
                .minimumBalance(0.0)
                .build();
        service.applyInterest(account);
        assertThat(account.getBalance()).isEqualTo(balance);
    }

    @Test
    void applyInterest_withPositiveBalance_shouldAddToBalance(){
        var account = SavingAccount.builder()
                .balance(10.0)
                .interestRate(10)
                .minimumBalance(0.0)
                .build();
        service.applyInterest(account);
        assertThat(account.getBalance()).isEqualTo(11.0);
    }

    @Test
    void applyInterest_withList_shouldAddToBalance(){
        var account = SavingAccount.builder()
                .balance(10.0)
                .interestRate(10)
                .minimumBalance(0.0)
                .build();
        var account2 = SavingAccount.builder()
                .balance(10.0)
                .interestRate(20)
                .minimumBalance(0.0)
                .build();
        service.applyInterest(List.of(account,account2));
        assertThat(account.getBalance()).isEqualTo(11.0);
        assertThat(account2.getBalance()).isEqualTo(12.0);
    }

    @Test
    void sumBalance_withFalsePredicate_shouldReturnZero() {
        var account = SavingAccount.builder()
                .balance(10.0).build();
        var sum = service.sumBalance(List.of(account), acc -> false);
        assertThat(sum).isEqualTo(0.0d);
    }

    @Test
    void sumBalance_withAccountsAndTruePredicate_shouldReturnZero() {
        var accounts = List.of(
                SavingAccount.builder().balance(10.0).build(),
                SavingAccount.builder().balance(12.0).build(),
                SavingAccount.builder().balance(15.0).build(),
                SavingAccount.builder().balance(25.0).build());
        var sum = service.sumBalance(accounts, acc -> true);
        var expected = accounts.stream().mapToDouble(BankAccount::getBalance).sum();
        assertThat(sum).isEqualTo(expected);
    }

    @Test
    void sumBalance_withAccountsAndPredicateOnBalance_shouldReturnZero() {
        var accounts = List.of(
                SavingAccount.builder().balance(10.0).build(),
                SavingAccount.builder().balance(12.0).build(),
                SavingAccount.builder().balance(15.0).build(),
                SavingAccount.builder().balance(25.0).build());
        var sum = service.sumBalance(accounts, acc -> acc.getBalance() > 13.0);
        assertThat(sum).isEqualTo(40.0);
    }

    @Test
    void sumBalance_withAccountsAndMultiPredicateOnBalance_shouldReturnZero() {
        var accounts = List.of(
                SavingAccount.builder().balance(10.0).build(),
                SavingAccount.builder().balance(12.0).build(),
                SavingAccount.builder().balance(15.0).build(),
                SavingAccount.builder().balance(25.0).build());
        Predicate<SavingAccount> predicate = acc -> acc.getBalance() > 12;
        predicate = predicate.and(acc -> acc.getBalance() < 25);
        var sum = service.sumBalance(accounts, predicate);
        assertThat(sum).isEqualTo(15.0);
    }
}
