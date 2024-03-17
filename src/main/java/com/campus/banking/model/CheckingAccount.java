package com.campus.banking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.experimental.SuperBuilder;

@Data
@With
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
@Table(name = "checking_accounts")
@PrimaryKeyJoinColumn(name = "id")
public class CheckingAccount extends BankAccount {

    public static final int TRANSACTION_FEE = 100;

    @PositiveOrZero
    @Column(name = "overdraft_limit")
    private double overDraftLimit;

    @PositiveOrZero
    @Column(name = "debt")
    private double debt;

}
