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

import com.campus.banking.exception.InsufficientFundsException;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.User;
import com.campus.banking.persistence.BankAccountDAO;
import com.campus.banking.persistence.TransactionDAO;

@ExtendWith(MockitoExtension.class)
public class BankAccountServiceTest extends AbstractAccountServiceTest<BankAccount> {

    @Mock
    private BankAccountDAO<BankAccount> dao;

    @Mock
    private UserService users;

    @Mock
    private TransactionDAO trxDao;

    private int maxPageSize = 10;

    private BankAccountService<BankAccount> service;

    @BeforeEach
    void setup() {
        service = new BankAccountServiceImpl(dao, trxDao, users, maxPageSize);
        super.service = service;
        super.dao = dao;
    }

    @Test
    void add_withZeroBalance_shouldNotInsertTransaction() {
        var account = BankAccount.builder()
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
        var account = BankAccount.builder()
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
        var account = BankAccount.builder()
                .accountHolder(User.builder().username("Tester").build())
                .accountNumber("3000")
                .balance(10)
                .build();
        service.add(account);
        assertThatNoException();
    }

    @Test
    void withdraw_withMoreThanBalance_shouldFail() {
        var account = BankAccount.builder()
                .accountNumber("3000")
                .accountHolder(User.builder().username("Tester").build())
                .balance(10.0)
                .build();
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        assertThatThrownBy(() -> service.withdraw(account.getAccountNumber(), 11.0))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void withdraw_withLessThanBalance_shouldWithdraw() {
        var account = BankAccount.builder()
                .accountHolder(User.builder().username("Tester").build())
                .accountNumber("3000")
                .balance(10.0)
                .build();

        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        service.withdraw(account.getAccountNumber(), 9.0);
        assertThat(account.getBalance()).isEqualTo(1.0);
    }

    @Test
    void deposit_withPositiveAmount_shouldDeposit() {
        var account = BankAccount.builder()
                .accountHolder(User.builder().username("Tester").build())
                .accountNumber("3000")
                .balance(10.0)
                .build();

        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        when(dao.findByAccountNumberForUpdate(any(), any())).thenReturn(Optional.of(account));
        service.deposit(account.getAccountNumber(), 10.0);
        assertThat(account.getBalance()).isEqualTo(20.0);
    }
    
    @Test
    void getMinimumDeposit_shouldReturnConstant(){
        var account = BankAccount.builder()
                .accountHolder(User.builder().username("Tester").build())
                .accountNumber("3000")
                .balance(10.0)
                .build();
        var account2 = BankAccount.builder()
                .accountHolder(User.builder().username("Tester2").build())
                .accountNumber("4000")
                .balance(500.0)
                .build();
        var first = service.getMinimumDeposit(account);
        var second = service.getMinimumDeposit(account2);
        assertThat(first).isEqualTo(second);
    }

    @Test
    void getAllowedWithdraw_withNoBalance_shouldReturnZero(){
        var account = BankAccount.builder()
                .accountHolder(User.builder().username("Tester").build())
                .accountNumber("3000")
                .balance(0.0)
                .build();
        var first = service.getAllowedWithdraw(account);
        assertThat(first).isEqualTo(0.0);
    }

    @Test
    void getAllowedWithdraw_withBalance_shouldReturnBalance(){
        var account = BankAccount.builder()
                .accountHolder(User.builder().username("Tester").build())
                .accountNumber("3000")
                .balance(0.0)
                .build();
        var first = service.getAllowedWithdraw(account);
        assertThat(first).isEqualTo(account.getBalance());
    }

    @Test
    void toBankAccount_withEmptyMap_shouldReturnEmptyUser() {
        var account = BankAccountService.toBankAccount(Map.of());
        assertThat(account.getAccountNumber()).isNull();
        assertThat(account.getBalance()).isZero();
        assertThat(account.getAccountHolder().getUsername()).isNull();
    }

    @Test
    void toBankAccount_withNonNumericBalance_shouldReturnZeroBalance() {
        var map = Map.of(
                "account_number", new String[] { "test" },
                "username", new String[] { "tester" },
                "balance", new String[] { "test" });
        var account = BankAccountService.toBankAccount(map);
        assertThat(account.getAccountNumber()).isEqualTo("test");
        assertThat(account.getBalance()).isZero();
        assertThat(account.getAccountHolder().getUsername()).isEqualTo("tester");
    }

    @Test
    void toBankAccount_withFull_shouldReturnAccount() {
        var map = Map.of(
                "account_number", new String[] { "test" },
                "username", new String[] { "tester" },
                "balance", new String[] { "10.0" });
        var account = BankAccountService.toBankAccount(map);
        assertThat(account.getAccountNumber()).isEqualTo("test");
        assertThat(account.getBalance()).isEqualTo(10.0);
        assertThat(account.getAccountHolder().getUsername()).isEqualTo("tester");
    }


    @Override
    Stream<BankAccount> generate(int count) {
        return IntStream.range(0, count)
                .mapToObj(this::make);
    }

    BankAccount make(int i) {
        return BankAccount.builder()
                .accountHolder(User.builder().username("user" + i).build())
                .accountNumber("4000" + i)
                .balance(100.0 * i + 200)
                .build();
    }

}
