package com.campus.banking.model;

import java.util.stream.Stream;

public enum AccountType {
    BANK(1), SAVING(2), CHECKING(3);

    public final int value;

    AccountType(int id) {
        this.value = id;
    }

    public static Stream<String> stream() {
        return Stream.of(values())
                .map(AccountType::toString);
    }
}
