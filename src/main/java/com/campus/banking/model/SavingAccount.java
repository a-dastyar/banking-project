package com.campus.banking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "saving_accounts")
@PrimaryKeyJoinColumn(name = "id")
public class SavingAccount extends BankAccount {

    @PositiveOrZero
    @Column(name = "interest_rate")
    private double interestRate;

    @NotNull
    @Column(name = "interest_period")
    private InterestPeriod interestPeriod;

    @PositiveOrZero
    @Column(name = "minimum_balance")
    private double minimumBalance;

}
