package com.campus.banking.exception;

import lombok.experimental.StandardException;

@StandardException
public class IllegalBalanceStateException extends RuntimeException {

    static final String BALANCE_LESS_THAN_MINIMUM_MESSAGE = "Balance can not be less than minimum balance";
    public static final IllegalBalanceStateException BALANCE_LESS_THAN_MINIMUM;

    static final String IN_DEBT_WHILE_HAS_BALANCE_MESSAGE = "Can not have balance while in debt";
    public static final IllegalBalanceStateException IN_DEBT_WHILE_HAS_BALANCE;

    static final String DEBT_MORE_THAN_OVERDRAFT_LIMIT_MESSAGE = "Can not have debt more than overdraft limit";
    public static final IllegalBalanceStateException DEBT_MORE_THAN_OVERDRAFT_LIMIT;

    static {
        BALANCE_LESS_THAN_MINIMUM = new IllegalBalanceStateException(BALANCE_LESS_THAN_MINIMUM_MESSAGE);
        IN_DEBT_WHILE_HAS_BALANCE = new IllegalBalanceStateException(IN_DEBT_WHILE_HAS_BALANCE_MESSAGE);
        DEBT_MORE_THAN_OVERDRAFT_LIMIT = new IllegalBalanceStateException(DEBT_MORE_THAN_OVERDRAFT_LIMIT_MESSAGE);
    }
}
