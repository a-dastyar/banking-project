package com.campus.banking.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import com.campus.banking.model.BankAccount;
import com.campus.banking.model.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BankAccountDAOIT extends AbstractAccountDAOIT<BankAccount> {

    @BeforeEach
    void setup() {
        log.debug("setup");
        dao = new BankAccountDAOImpl(em);
        user = User.builder()
                .username("test")
                .password("test")
                .email("test@test.test")
                .build();
        dao.inTransaction(em -> em.persist(user));
    }

    @AfterEach
    void teardown() {
        log.debug("teardown");
        em.close();
    }

    protected void assertAccountsEqual(BankAccount result, BankAccount expected) {
        assertThat(result.getAccountNumber()).isEqualTo(expected.getAccountNumber());
        assertThat(result.getBalance()).isEqualTo(expected.getBalance());
    }

    @Override
    protected Stream<BankAccount> generateAccounts() {
        return IntStream.range(0, 1_000_000)
                .mapToObj(this::createAccount);
    }

    private BankAccount createAccount(int i) {
        return BankAccount.builder()
                .accountHolder(user)
                .accountNumber("100000" + i)
                .balance(i * 10 + 500)
                .build();
    }
}
