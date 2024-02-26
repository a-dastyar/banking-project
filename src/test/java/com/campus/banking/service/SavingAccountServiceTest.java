package com.campus.banking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.campus.banking.exception.InvalidAccountTypeException;
import com.campus.banking.exception.InvalidTransactionException;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.SavingAccount;

public class SavingAccountServiceTest {

    SavingAccountService service = new SavingAccountServiceImpl();

    @Test
    void withdraw_withDifferentAccountType_shouldFail() {
        var account = BankAccount.builder()
                .balance(10.0)
                .build();
        assertThatThrownBy(() -> service.withdraw(account, 5.0))
                .isInstanceOf(InvalidAccountTypeException.class);
    }

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
    void applyInterest_witPositiveBalance_shouldAddToBalance(){
        var account = SavingAccount.builder()
                .balance(10.0)
                .minimumBalance(0.0)
                .build();
        service.applyInterest(account);
        assertThat(account.getBalance()).isEqualTo(11.0);
    }
}
