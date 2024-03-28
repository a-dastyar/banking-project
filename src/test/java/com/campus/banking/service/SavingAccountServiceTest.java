package com.campus.banking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.campus.banking.exception.IllegalBalanceStateException;
import com.campus.banking.exception.InvalidTransactionException;
import com.campus.banking.model.InterestPeriod;
import com.campus.banking.model.SavingAccount;
import com.campus.banking.model.User;
import com.campus.banking.persistence.SavingAccountDAO;
import com.campus.banking.persistence.TransactionDAO;

@ExtendWith(MockitoExtension.class)
public class SavingAccountServiceTest extends AbstractAccountServiceTest<SavingAccount> {

    @Mock
    private SavingAccountDAO dao;

    @Mock
    private UserService users;

    @Mock
    private AccountNumberGenerator generator;

    @Mock
    private TransactionDAO trxDao;

    private SavingAccountService service;

    @BeforeEach
    void setup() {
        service = new SavingAccountServiceImpl(dao, trxDao, generator, users, 50, 10);
        super.service = service;
        super.dao = dao;
    }

    @Test
    void add_withBalanceLessThanMinimumBalance_shouldFail() {
        var account = SavingAccount.builder()
                .accountHolder(User.builder().username("").build())
                .accountNumber("3000")
                .balance(100.0)
                .minimumBalance(200.0)
                .build();
        assertThatThrownBy(() -> service.add(account)).isInstanceOf(IllegalBalanceStateException.class);
    }

    @Test
    void add_withZeroBalance_shouldNotInsertTransaction() {
        var account = SavingAccount.builder()
                .accountHolder(User.builder().username("Tester").build())
                .accountNumber("3000")
                .balance(0.0)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        service.add(account);
        verify(trxDao, never()).transactionalPersist(any(), any());
        assertThatNoException();
    }

    @Test
    void add_withBalance_shouldInsertTransaction() {
        var account = SavingAccount.builder()
                .accountHolder(User.builder().username("Tester").build())
                .accountNumber("3000")
                .balance(10.0)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        service.add(account);
        verify(trxDao, only()).transactionalPersist(any(), any());
        assertThatNoException();
    }

    @Test
    void add_withValidAccount_shouldAdd() {
        var account = SavingAccount.builder()
                .accountHolder(User.builder().username("Tester").build())
                .accountNumber("3000")
                .build();
        service.add(account);
        assertThatNoException();
    }

    @Test
    void withdraw_withMoreThanBalance_shouldFail() {
        var account = SavingAccount.builder()
                .balance(10.0)
                .build();
        var accountNumber = "5000";
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        assertThatThrownBy(() -> service.withdraw(accountNumber, 11.0))
                .isInstanceOf(InvalidTransactionException.class);
    }

    @Test
    void withdraw_withMoreThanMinimumBalance_shouldFail() {
        var account = SavingAccount.builder()
                .balance(10.0)
                .minimumBalance(5.0)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        assertThatThrownBy(() -> service.withdraw(account.getAccountNumber(), 6.0))
                .isInstanceOf(InvalidTransactionException.class);
    }

    @Test
    void withdraw_withLessThanBalanceAndMinimumBalance_shouldWithdraw() {
        var account = SavingAccount.builder()
                .balance(10.0)
                .minimumBalance(5.0)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        service.withdraw(account.getAccountNumber(), 1.0);
        assertThat(account.getBalance()).isEqualTo(9.0);
    }

    @Test
    void deposit_withPositiveAmount_shouldDeposit() {
        var account = SavingAccount.builder()
                .balance(10.0)
                .minimumBalance(0.0)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        service.deposit(account.getAccountNumber(), 10.0);
        assertThat(account.getBalance()).isEqualTo(20.0);
    }

    @Test
    void applyInterest_withZeroBalance_shouldNotChangeBalance() {
        var balance = 0.0d;
        var account = SavingAccount.builder()
                .balance(balance)
                .minimumBalance(0.0)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        service.applyInterest(account.getAccountNumber());
        assertThat(account.getBalance()).isEqualTo(balance);
    }

    @Test
    void applyInterest_withPositiveBalance_shouldAddToBalance() {
        var account = SavingAccount.builder()
                .balance(10.0)
                .interestRate(10)
                .minimumBalance(0.0)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        service.applyInterest(account.getAccountNumber());
        assertThat(account.getBalance()).isEqualTo(11.0);
    }

    @Test
    void getMinimumDeposit_shouldReturnConstant() {
        var account = SavingAccount.builder()
                .accountHolder(User.builder().username("Tester").build())
                .accountNumber("3000")
                .balance(10.0)
                .build();
        var account2 = SavingAccount.builder()
                .accountHolder(User.builder().username("Tester2").build())
                .accountNumber("4000")
                .balance(500.0)
                .build();
        var first = service.getMinimumDeposit(account);
        var second = service.getMinimumDeposit(account2);
        assertThat(first).isEqualTo(second);
    }

    @Test
    void getAllowedWithdraw_withMinimumWithdraw_shouldReturnAmount() {
        var account = SavingAccount.builder()
                .accountHolder(User.builder().username("Tester").build())
                .accountNumber("3000")
                .balance(100.0)
                .minimumBalance(90.0)
                .build();
        var first = service.getAllowedWithdraw(account);
        assertThat(first).isEqualTo(10.0);
    }

    @Test
    void toSavingAccount_withEmptyMap_shouldReturnEmptyUser() {
        var account = SavingAccountService.toSavingAccount(Map.of());
        assertThat(account.getAccountNumber()).isNull();
        assertThat(account.getBalance()).isZero();
        assertThat(account.getMinimumBalance()).isZero();
        assertThat(account.getInterestRate()).isZero();
        assertThat(account.getInterestPeriod()).isNull();
        assertThat(account.getAccountHolder().getUsername()).isNull();
    }

    @Test
    void toSavingAccount_withNonNumeric_shouldReturnWithZero() {
        var map = Map.of(
                "account_number", new String[] { "test" },
                "username", new String[] { "tester" },
                "balance", new String[] { "test" },
                "minimum_balance", new String[] { "test" },
                "interest_rate", new String[] { "test" },
                "interest_period", new String[] { "YEARLY" });

        var account = SavingAccountService.toSavingAccount(map);
        assertThat(account.getAccountNumber()).isEqualTo("test");
        assertThat(account.getBalance()).isZero();
        assertThat(account.getMinimumBalance()).isZero();
        assertThat(account.getInterestRate()).isZero();
        assertThat(account.getInterestPeriod()).isEqualTo(InterestPeriod.YEARLY);
        assertThat(account.getAccountHolder().getUsername()).isEqualTo("tester");
    }

    @Test
    void toSavingAccount_withInvalid_shouldReturnWithNull() {
        var map = Map.of(
                "account_number", new String[] { "test" },
                "username", new String[] { "tester" },
                "balance", new String[] { "15.0" },
                "minimum_balance", new String[] { "5.0" },
                "interest_rate", new String[] { "10.0" },
                "interest_period", new String[] { "Invalid" });

        var account = SavingAccountService.toSavingAccount(map);
        assertThat(account.getAccountNumber()).isEqualTo("test");
        assertThat(account.getBalance()).isEqualTo(15.0);
        assertThat(account.getMinimumBalance()).isEqualTo(5.0);
        assertThat(account.getInterestRate()).isEqualTo(10.0);
        assertThat(account.getInterestPeriod()).isNull();
        assertThat(account.getAccountHolder().getUsername()).isEqualTo("tester");
    }

    @Test
    void toSavingAccount_withFull_shouldReturnAccount() {
        var map = Map.of(
                "account_number", new String[] { "test" },
                "username", new String[] { "tester" },
                "balance", new String[] { "15.0" },
                "minimum_balance", new String[] { "5.0" },
                "interest_rate", new String[] { "10.0" },
                "interest_period", new String[] { "YEARLY" });

        var account = SavingAccountService.toSavingAccount(map);
        assertThat(account.getAccountNumber()).isEqualTo("test");
        assertThat(account.getBalance()).isEqualTo(15.0);
        assertThat(account.getMinimumBalance()).isEqualTo(5.0);
        assertThat(account.getInterestRate()).isEqualTo(10.0);
        assertThat(account.getInterestPeriod()).isEqualTo(InterestPeriod.YEARLY);
        assertThat(account.getAccountHolder().getUsername()).isEqualTo("tester");
    }

    @Override
    Stream<SavingAccount> generate(int count) {
        return IntStream.range(0, count)
                .mapToObj(this::make);
    }

    SavingAccount make(int i) {
        return SavingAccount.builder()
                .accountHolder(User.builder().username("user" + i).build())
                .accountNumber("4000" + i)
                .balance(100.0 * i + 200)
                .build();
    }

}