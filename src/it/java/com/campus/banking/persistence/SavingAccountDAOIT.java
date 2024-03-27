package com.campus.banking.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.campus.banking.model.InterestPeriod;
import com.campus.banking.model.SavingAccount;
import com.campus.banking.model.User;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SavingAccountDAOIT extends AbstractAccountDAOIT<SavingAccount> {

    private SavingAccountDAO dao;

    private EntityManager em;

    private User user;

    @BeforeEach
    void setup() {
        log.debug("setup");
        em = super.emf.createEntityManager();
        dao = new SavingAccountDAOImpl(em);
        super.dao = dao;
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

    @Test
    void applyInterest_withMultipleAccountHigher_shouldAddInterestToBalance() {
        var list = List.of(
                SavingAccount.builder().accountNumber("5000")
                        .accountHolder(user)
                        .interestPeriod(InterestPeriod.YEARLY)
                        .balance(5000)
                        .interestRate(10.0).build(),
                SavingAccount.builder().accountNumber("8000")
                        .accountHolder(user)
                        .interestPeriod(InterestPeriod.YEARLY)
                        .balance(100)
                        .interestRate(20.0).build(),
                SavingAccount.builder().accountNumber("10000")
                        .accountHolder(user)
                        .interestPeriod(InterestPeriod.YEARLY)
                        .balance(2000)
                        .interestRate(0.0).build());
        dao.persist(list);
        dao.applyInterest();
        var sum = dao.sumBalanceHigherThan(0);
        assertThat(sum).isEqualTo(7620.0);
    }

    @Override
    protected Stream<SavingAccount> generateAccounts() {
        return IntStream.range(0, 1_000_000)
                .mapToObj(this::createAccount);
    }

    private SavingAccount createAccount(int i) {
        return SavingAccount.builder()
                .accountHolder(user)
                .accountNumber("100000" + i)
                .balance(i * 10 + 500)
                .minimumBalance(0)
                .interestRate(10.0)
                .interestPeriod(InterestPeriod.YEARLY)
                .build();
    }
    @Override
    protected void assertAccountsEqual(SavingAccount first, SavingAccount second) {
        assertThat(first.getAccountNumber()).isEqualTo(second.getAccountNumber());
        assertThat(first.getBalance()).isEqualTo(second.getBalance());
        assertThat(first.getMinimumBalance()).isEqualTo(second.getMinimumBalance());
        assertThat(first.getInterestRate()).isEqualTo(second.getInterestRate());
        assertThat(first.getInterestPeriod()).isEqualTo(second.getInterestPeriod());
    }
}
