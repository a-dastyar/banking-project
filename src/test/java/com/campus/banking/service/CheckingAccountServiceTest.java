package com.campus.banking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.campus.banking.exception.InsufficientFundsException;
import com.campus.banking.exception.InvalidAccountTypeException;
import com.campus.banking.exception.LessThanMinimumTransactionException;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.CheckingAccount;

public class CheckingAccountServiceTest {

    CheckingAccountService service = new CheckingAccountServiceImpl();

    @Test
    void withdraw_withDifferentAccountType_shouldFail() {
        var account = BankAccount.builder()
                .balance(10.0)
                .build();
        assertThatThrownBy(() -> service.withdraw(account, 200.0))
                .isInstanceOf(InvalidAccountTypeException.class);
    }

    @Test
    void withdraw_withNegativeAmount_shouldFail() {
        var account = CheckingAccount.builder()
                .balance(10.0)
                .build();
        assertThatThrownBy(() -> service.withdraw(account, -2.0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void withdraw_withAmountLessThanTransactionFee_shouldFail() {
        var account = CheckingAccount.builder()
                .balance(10.0)
                .build();
        assertThatThrownBy(() -> service.withdraw(account, CheckingAccount.TRANSACTION_FEE - 1.0))
                .isInstanceOf(LessThanMinimumTransactionException.class);
    }

    @Test
    void withdraw_withZeroBalanceAndZeroOverdraftLimit_shouldFail() {
        var account = CheckingAccount.builder()
                .balance(0.0)
                .overDraftLimit(0.0)
                .build();
        assertThatThrownBy(() -> service.withdraw(account, 200.0))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void withdraw_withEnoughBalanceButNotEnoughForTrxFee_shouldFail() {
        var account = CheckingAccount.builder()
                .balance(1000.0)
                .overDraftLimit(0.0)
                .build();
        assertThatThrownBy(() -> service.withdraw(account, 1000.0))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void withdraw_withZeroBalanceAndEnoughOverdraftButNotEnoughForTrxFee_shouldFail() {
        var account = CheckingAccount.builder()
                .balance(0.0)
                .overDraftLimit(1000.0)
                .build();
        assertThatThrownBy(() -> service.withdraw(account, 1000.0))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void withdraw_withSomeBalanceAndSomeOverdraftButNotEnough_shouldFail() {
        var account = CheckingAccount.builder()
                .balance(500.0)
                .overDraftLimit(500.0)
                .build();
        assertThatThrownBy(() -> service.withdraw(account, 2000.0))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void withdraw_withSomeBalanceAndSomeOverdraftButNotEnoughForTrxFee_shouldFail() {
        var account = CheckingAccount.builder()
                .balance(500.0)
                .overDraftLimit(500.0)
                .build();
        assertThatThrownBy(() -> service.withdraw(account, 1000.0))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void withdraw_withEnoughBalanceButNoTrxFeeLeftForDeposit_shouldFail() {
        var account = CheckingAccount.builder()
                .balance(500.0)
                .overDraftLimit(0.0)
                .build();
        assertThatThrownBy(() -> service.withdraw(account, 400.0))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void withdraw_withZeroBalanceButEnoughOverdraftLimit_shouldWithdraw() {
        var account = CheckingAccount.builder()
                .balance(0.0)
                .overDraftLimit(1000.0)
                .build();
        service.withdraw(account, 200.0);
        assertThat(account.getDebt()).isEqualTo(200.0 + CheckingAccount.TRANSACTION_FEE);
    }

    @Test
    void withdraw_withEnoughBalanceButNotEnoughForTrxFeeAndEnoughOverdraftLimit_shouldWithdraw() {
        var account = CheckingAccount.builder()
                .balance(1000.0)
                .overDraftLimit(1000.0)
                .build();
        service.withdraw(account, 1000.0);
        assertThat(account.getBalance()).isEqualTo(0.0d);
        assertThat(account.getDebt()).isEqualTo(CheckingAccount.TRANSACTION_FEE);
    }

    @Test
    void withdraw_withEnoughBalanceAndEnoughOverdraftLimit_shouldWithdraw() {
        var account = CheckingAccount.builder()
                .balance(1000.0)
                .overDraftLimit(1000.0)
                .build();
        service.withdraw(account, 500.0);
        assertThat(account.getBalance()).isEqualTo(500.0 - CheckingAccount.TRANSACTION_FEE);
        assertThat(account.getDebt()).isEqualTo(0.0d);
    }

    @Test
    void deposit_withDifferentAccountType_shouldFail() {
        var account = BankAccount.builder()
                .balance(1000.0)
                .build();
        assertThatThrownBy(() -> service.deposit(account, 200))
                .isInstanceOf(InvalidAccountTypeException.class);
    }

    @Test
    void deposit_withNegativeAmount_shouldFail() {
        var account = CheckingAccount.builder()
                .balance(10.0)
                .build();
        assertThatThrownBy(() -> service.withdraw(account, -2.0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deposit_withLessThanDebt_shouldDeposit() {
        var account = CheckingAccount.builder()
                .balance(0.0)
                .debt(300)
                .build();
        service.deposit(account, 200.0);
        assertThat(account.getDebt()).isEqualTo(300.0 - (200.0 - CheckingAccount.TRANSACTION_FEE));
    }

    @Test
    void deposit_withAsMuchAsDebt_shouldDeposit() {
        var account = CheckingAccount.builder()
                .balance(0.0)
                .debt(300)
                .build();
        service.deposit(account, 300.0 + CheckingAccount.TRANSACTION_FEE);
        assertThat(account.getDebt()).isEqualTo(0.0);
    }
    
    @Test
    void deposit_withMoreThanDebt_shouldDeposit() {
        var account = CheckingAccount.builder()
                .balance(0.0)
                .debt(300)
                .build();
        service.deposit(account, 500.0 + CheckingAccount.TRANSACTION_FEE);
        assertThat(account.getDebt()).isEqualTo(0.0);
        assertThat(account.getBalance()).isEqualTo(200.0);
    }

    @Test
    void deposit_witNoDebt_shouldDeposit() {
        var account = CheckingAccount.builder()
                .balance(0.0)
                .debt(0.0)
                .build();
        service.deposit(account, 500.0 + CheckingAccount.TRANSACTION_FEE);
        assertThat(account.getDebt()).isEqualTo(0.0);
        assertThat(account.getBalance()).isEqualTo(500.0);
    }
}
