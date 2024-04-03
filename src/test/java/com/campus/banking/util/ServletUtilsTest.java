package com.campus.banking.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.campus.banking.exception.InvalidArgumentException;
import com.campus.banking.model.AccountType;

public class ServletUtilsTest {

    @Test
    void getPageNumber_withNull_shouldReturnReturnEmpty() {
        var val = ServletUtils.getPositiveInt(null);
        assertThat(val).isEmpty();
    }

    @Test
    void getPageNumber_withZero_shouldReturnEmpty() {
        assertThat(ServletUtils.getPositiveInt("0")).isEmpty();
    }

    @Test
    void getPageNumber_withNegativeNumber_shouldReturnEmpty() {
        assertThat(ServletUtils.getPositiveInt("-1")).isEmpty();
    }

    @Test
    void getPageNumber_withPositive_shouldReturnTow() {
        var val = ServletUtils.getPositiveInt("2").get();
        assertThat(val).isEqualTo(2);
    }

    @Test
    void getDoubleValue_withValidDouble_shouldReturnDouble() {
        var val = ServletUtils.getDoubleValue("1.0");
        assertThat(val).isEqualTo(1.0);
    }

    @Test
    void getDoubleValue_withInvalidDouble_shouldReturnDouble() {
        assertThatThrownBy(() -> ServletUtils.getDoubleValue("test"))
                .isInstanceOf(InvalidArgumentException.class);
    }

    @Test
    void getTransactionType_withNull_shouldFail() {
        assertThatThrownBy(() -> ServletUtils.getTransactionType(null))
                .isInstanceOf(InvalidArgumentException.class);
    }

    @Test
    void getTransactionType_withInvalidType_shouldFail() {
        assertThatThrownBy(() -> ServletUtils.getTransactionType("Invalid"))
                .isInstanceOf(InvalidArgumentException.class);
    }

    @Test
    void getTransactionType_withValidType_shouldReturnType() {
        var val = ServletUtils.getTransactionType("WITHDRAW");
        assertThat(val).isEqualTo(ServletUtils.TransactionType.WITHDRAW);
    }

    @Test
    void getAccountType_withNull_shouldReturnEmpty() {
        var result = ServletUtils.getAccountType(null);
        assertThat(result).isEmpty();
    }

    @Test
    void getAccountType_withInvalidType_shouldReturnEmpty() {
        var result = ServletUtils.getAccountType("Invalid");
        assertThat(result).isEmpty();
    }

    @Test
    void getAccountType_withValidType_shouldReturnType() {
        var val = ServletUtils.getAccountType("BANK").get();
        assertThat(val).isEqualTo(AccountType.BANK);
    }


}
