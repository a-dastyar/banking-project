package com.campus.banking.service;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Consumer;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import com.campus.banking.model.AccountNumberSequence;
import com.campus.banking.model.AccountType;
import com.campus.banking.persistence.AccountNumberSequenceDAO;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
public class AccountNumberGeneratorTest {

    @Mock
    private AccountNumberSequenceDAO dao;

    private AccountNumberGenerator service;

    @BeforeEach
    void setup() {
        service = new BasicAccountNumberGenerator(dao);
    }

    @SuppressWarnings("unchecked")
    protected Answer<Object> executeConsumer(InvocationOnMock invocation) {
        var consumer = (Consumer<EntityManager>) invocation.getArgument(0);
        consumer.accept(mock(EntityManager.class));
        return null;
    }

    @Test
    void transactionalGenerate_withBankType_shouldGenerate() {
        var sequence = AccountNumberSequence.builder()
                .sequence(0L)
                .year(LocalDate.now().getYear())
                .build();
        when(dao.findForUpdate(any())).thenReturn(sequence);
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        var list = new ArrayList<String>();
        dao.inTransaction(em -> list.add(service.transactionalGenerate(em, AccountType.BANK)));
        assertThat(list.getFirst()).isEqualTo(LocalDate.now().getYear() + "00100000001");
    }

    @Test
    void transactionalGenerate_withSavingType_shouldGenerate() {
        var sequence = AccountNumberSequence.builder()
                .sequence(0L)
                .year(LocalDate.now().getYear())
                .build();
        when(dao.findForUpdate(any())).thenReturn(sequence);
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        var list = new ArrayList<String>();
        dao.inTransaction(em -> list.add(service.transactionalGenerate(em, AccountType.SAVING)));
        assertThat(list.getFirst()).isEqualTo(LocalDate.now().getYear() + "00200000001");
    }

    @Test
    void transactionalGenerate_withCheckingType_shouldGenerate() {
        var sequence = AccountNumberSequence.builder()
                .sequence(0L)
                .year(LocalDate.now().getYear())
                .build();
        when(dao.findForUpdate(any())).thenReturn(sequence);
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        var list = new ArrayList<String>();
        dao.inTransaction(em -> list.add(service.transactionalGenerate(em, AccountType.CHECKING)));
        assertThat(list.getFirst()).isEqualTo(LocalDate.now().getYear() + "00300000001");
    }

    @Test
    void transactionalGenerate_withHighSeqNumber_shouldGenerate() {
        var sequence = AccountNumberSequence.builder()
                .sequence(555L)
                .year(LocalDate.now().getYear())
                .build();
        when(dao.findForUpdate(any())).thenReturn(sequence);
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        var list = new ArrayList<String>();
        dao.inTransaction(em -> list.add(service.transactionalGenerate(em, AccountType.BANK)));
        assertThat(list.getFirst()).isEqualTo(LocalDate.now().getYear() + "00100000556");
    }

    @Test
    void transactionalGenerate_withYearChange_shouldResetSeq() {
        var sequence = AccountNumberSequence.builder()
                .sequence(500L)
                .year(2020)
                .build();
        when(dao.findForUpdate(any())).thenReturn(sequence);
        doAnswer(this::executeConsumer).when(dao).inTransaction(any());
        var list = new ArrayList<String>();
        dao.inTransaction(em -> list.add(service.transactionalGenerate(em, AccountType.BANK)));
        assertThat(list.getFirst()).isEqualTo(LocalDate.now().getYear() + "00100000001");
    }

    @Test
    void setup_withNoSeq_shouldInsert() {
        when(dao.exists()).thenReturn(false);
        service.setupNumberGenerator();
        verify(dao, times(1)).persist(any());
    }

    @Test
    void setup_withSeq_shouldInsert() {
        when(dao.exists()).thenReturn(true);
        service.setupNumberGenerator();
        verify(dao, never()).persist(any());
    }
}
