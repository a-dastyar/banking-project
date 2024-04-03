package com.campus.banking.util;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import com.campus.banking.exception.InvalidArgumentException;
import com.campus.banking.model.AccountType;

public interface ServletUtils {
    static enum TransactionType {
        WITHDRAW, DEPOSIT;

        public static Stream<String> stream() {
            return Stream.of(values())
                    .map(TransactionType::toString);
        }
    }

    public static Optional<Integer> getPositiveIntWithDefault(String page, String defaultVal) {
        return Optional.ofNullable(Optional.ofNullable(page).orElse(defaultVal))
                .filter(str -> str.chars().allMatch(Character::isDigit))
                .map(Integer::valueOf)
                .filter(i -> i > 0);
    }

    public static Optional<Integer> getPositiveInt(String page) {
        return getPositiveIntWithDefault(page, null);
    }

    public static double getDoubleValue(String number) {
        Function<String, Double> toDouble = str -> {
            try {
                return Double.valueOf(str);
            } catch (Exception e) {
                throw InvalidArgumentException.NON_NUMERIC_VALUE;
            }
        };
        return Optional.ofNullable(number)
                .map(toDouble)
                .orElseThrow(() -> InvalidArgumentException.NON_NUMERIC_VALUE);
    }

    public static TransactionType getTransactionType(String type) {
        return Optional.ofNullable(type)
                .filter(t -> ServletUtils.TransactionType.stream().anyMatch(t::equals))
                .map(ServletUtils.TransactionType::valueOf)
                .orElseThrow(() -> InvalidArgumentException.INVALID_TRANSACTION_TYPE);
    }

    public static Optional<AccountType> getAccountType(String type) {
        return Optional.ofNullable(type)
                .filter(t -> AccountType.stream().anyMatch(t::equals))
                .map(AccountType::valueOf);
    }
}
