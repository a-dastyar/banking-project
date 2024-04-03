package com.campus.banking.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import com.campus.banking.model.CheckingAccount;
import com.campus.banking.model.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CheckingAccountDAOIT extends AbstractAccountDAOIT<CheckingAccount> {

    @BeforeEach
    void setup() {
        log.debug("setup");
        dao = new CheckingAccountDAOImpl(em);
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

    @Override
    protected Stream<CheckingAccount> generateAccounts() {
        return IntStream.range(0, 1_000_000)
                .mapToObj(this::createAccount);
    }

    private CheckingAccount createAccount(int i) {
        return CheckingAccount.builder()
                .accountHolder(user)
                .accountNumber("100000" + i)
                .balance(i * 10 + 500)
                .overdraftLimit(0.0)
                .debt(0.0)
                .build();
    }

    @Override
    protected void assertAccountsEqual(CheckingAccount first, CheckingAccount second) {
        assertThat(first.getAccountNumber()).isEqualTo(second.getAccountNumber());
        assertThat(first.getBalance()).isEqualTo(second.getBalance());
        assertThat(first.getOverdraftLimit()).isEqualTo(second.getOverdraftLimit());
        assertThat(first.getDebt()).isEqualTo(second.getDebt());
    }
}
