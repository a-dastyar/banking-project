package com.campus.banking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

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

	@Test
	void deposit_withConcurrentModification_shouldBeSafe() {
		var balance = 0.0d;
		var account = BankAccount.builder()
				.balance(balance)
				.build();

		var amount = 200.0d;
		IntFunction<Runnable> toRunnable = i -> (Runnable) () -> service.deposit(account, amount);

		var threads = 100;
		var deposits = IntStream.range(0, threads)
				.mapToObj(toRunnable)
				.toArray(Runnable[]::new);

		var iteration = 100;
		IntStream.range(0, iteration)
				.forEach(r -> modifyBalanceConcurrently(account, deposits));

		assertThat(account.getBalance()).isEqualTo(amount * threads * iteration);

	}

	@Test
	void withdraw_withConcurrentModification_shouldBeSafe() {
		var balance = 900000000000000.0d;
		var account = BankAccount.builder()
				.balance(balance)
				.build();

		var amount = 200.0d;
		IntFunction<Runnable> toRunnable = i -> (Runnable) () -> service.withdraw(account, amount);

		var threads = 100;
		var withdraws = IntStream.range(0, threads)
				.mapToObj(toRunnable)
				.toArray(Runnable[]::new);

		var iteration = 100;
		IntStream.range(0, iteration)
				.forEach(r -> modifyBalanceConcurrently(account, withdraws));

		assertThat(account.getBalance()).isEqualTo(balance - (amount * threads * iteration));

	}

	@Test
	void depositAndWithdraw_withConcurrentModification_shouldBeSafe() {
		var balance = 10000.0d;
		var account = BankAccount.builder()
				.balance(balance)
				.build();

		var amount = 200.0d;
		Runnable withdraw = () -> service.withdraw(account, amount);
		Runnable deposit = () -> service.deposit(account, amount);

		var iteration = 1000;
		IntStream.range(0, iteration)
				.forEach(r -> modifyBalanceConcurrently(account, deposit, withdraw));

		assertThat(account.getBalance()).isEqualTo(balance);
	}

	private void modifyBalanceConcurrently(BankAccount account, Runnable... runnables) {
		var startCountDown = new CountDownLatch(1);
		try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
			Arrays.stream(runnables)
					.map(runnable -> addAwait(runnable, startCountDown))
					.forEach(executor::submit);
			startCountDown.countDown();
		}
	}

	Runnable addAwait(Runnable runnable, CountDownLatch latch) {
		Runnable awaited = () -> {
			try {
				latch.await();
				runnable.run();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		};
		return awaited;
	}
}
