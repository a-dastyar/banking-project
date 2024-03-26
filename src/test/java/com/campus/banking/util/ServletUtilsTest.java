package com.campus.banking.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;


public class ServletUtilsTest {

    @Test
    void getPageNumber_withNull_shouldReturnOne() {
        var val = ServletUtils.getPositiveInt(null);
        assertThat(val).isEqualTo(1);
    }

    @Test
    void getPageNumber_withZero_shouldFail() {
        assertThatThrownBy(() -> ServletUtils.getPositiveInt("0"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getPageNumber_withNegativeNumber_shouldFail() {
        assertThatThrownBy(() -> ServletUtils.getPositiveInt("-1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getPageNumber_withPositive_shouldReturnOne() {
        var val = ServletUtils.getPositiveInt("2");
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
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getTransactionType_withNull_shouldFail() {
        assertThatThrownBy(() -> ServletUtils.getTransactionType(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getTransactionType_withInvalidType_shouldFail() {
        assertThatThrownBy(() -> ServletUtils.getTransactionType("Invalid"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getTransactionType_withValidType_shouldReturnType() {
        var val = ServletUtils.getTransactionType("WITHDRAW");
        assertThat(val).isEqualTo(ServletUtils.TransactionType.WITHDRAW);
    }

}
