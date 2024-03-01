package com.campus.banking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

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
    void applyInterest_withZeroBalance_shouldNotChangeBalance() {
        var balance = 0.0d;
        var account = SavingAccount.builder()
                .balance(balance)
                .minimumBalance(0.0)
                .build();
        service.applyInterest(account);
        assertThat(account.getBalance()).isEqualTo(balance);
    }

    @Test
    void applyInterest_witPositiveBalance_shouldAddToBalance() {
        var account = SavingAccount.builder()
                .balance(10.0)
                .minimumBalance(0.0)
                .build();
        service.applyInterest(account);
        assertThat(account.getBalance()).isEqualTo(11.0);
    }

    @Test
    void deposit_withConcurrentModification_shouldBeSafe() {
        var balance = 0.0d;
        var account = SavingAccount.builder()
                .balance(balance)
                .minimumBalance(0.0)
                .build();

        var amount = 200.0d;
        IntFunction<Runnable> toDeposit = i -> (Runnable) () -> service.deposit(account, amount);

        var concurrentDeposits = 100;
        var deposits = IntStream.range(0, concurrentDeposits)
                .mapToObj(toDeposit)
                .toArray(Runnable[]::new);

        var iterations = 100;
        IntStream.range(0, iterations)
                .forEach(r -> runConcurrently(deposits));

        assertThat(account.getBalance()).isEqualTo(amount * concurrentDeposits * iterations);

    }

    @Test
    void withdraw_withConcurrentModification_shouldBeSafe() {
        var balance = 900000000000000.0d;
        var account = SavingAccount.builder()
                .balance(balance)
                .minimumBalance(0.0)
                .build();

        var amount = 200.0d;
        IntFunction<Runnable> toWithdraw = i -> (Runnable) () -> service.withdraw(account, amount);

        var concurrentWithdraws = 100;
        var withdraws = IntStream.range(0, concurrentWithdraws)
                .mapToObj(toWithdraw)
                .toArray(Runnable[]::new);

        var iterations = 100;
        IntStream.range(0, iterations)
                .forEach(r -> runConcurrently(withdraws));

        assertThat(account.getBalance()).isEqualTo(balance - (amount * concurrentWithdraws * iterations));

    }

    @Test
    void depositAndWithdraw_withConcurrentModification_shouldBeSafe() {
        var balance = 10000.0d;
        var account = SavingAccount.builder()
                .balance(balance)
                .minimumBalance(0.0)
                .build();

        var amount = 200.0d;
        Runnable withdraw = () -> service.withdraw(account, amount);
        Runnable deposit = () -> service.deposit(account, amount);

        var iterations = 1000;
        IntStream.range(0, iterations)
                .forEach(r -> runConcurrently(deposit, withdraw));

        assertThat(account.getBalance()).isEqualTo(balance);
    }

    private void runConcurrently(Runnable... tasks) {
        var start = new CountDownLatch(1);
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Arrays.stream(tasks)
                    .map(task -> addAwait(task, start))
                    .forEach(executor::submit);
            start.countDown();
        }
    }

    Runnable addAwait(Runnable task, CountDownLatch start) {
        Runnable awaited = () -> {
            try {
                start.await();
                task.run();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        return awaited;
    }
}
