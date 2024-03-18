package com.campus.banking.util;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public interface ServletUtils {
    static enum TransactionType {
        WITHDRAW, DEPOSIT;

        public static Stream<String> stream() {
            return Stream.of(values())
                    .map(TransactionType::toString);
        }
    }

    public static int getPageNumber(String page) {
        return Optional.ofNullable(Optional.ofNullable(page).orElse("1"))
                .filter(str -> str.chars().allMatch(Character::isDigit))
                .map(Integer::valueOf)
                .filter(i -> i > 0)
                .orElseThrow(IllegalArgumentException::new);
    }

    public static double getDoubleValue(String number) {
        Function<String, Double> toDouble = str -> {
            try {
                return Double.valueOf(str);
            } catch (Exception e) {
                throw new IllegalArgumentException();
            }
        };
        return Optional.ofNullable(number)
                .map(toDouble)
                .orElseThrow(IllegalArgumentException::new);
    }

    public static TransactionType getTransactionType(String type) {
        return Optional.ofNullable(type)
                .filter(t -> ServletUtils.TransactionType.stream().anyMatch(t::equals))
                .map(ServletUtils.TransactionType::valueOf)
                .orElseThrow(IllegalArgumentException::new);
    }
}
