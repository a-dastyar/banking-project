package com.campus.banking.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.experimental.SuperBuilder;

@Data
@With
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = SavingAccount.class, name = "saving"),
        @Type(value = CheckingAccount.class, name = "checking")
})
@JsonTypeName("normal")
public class BankAccount {

    @EqualsAndHashCode.Include
    private String accountNumber;

    private String accountHolderName;

    private double balance;

    public BankAccount(String accountNumber, String accountHolderName) {
        this(accountNumber, accountHolderName, 0.0d);
    }
}
