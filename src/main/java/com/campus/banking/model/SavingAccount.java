package com.campus.banking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
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

    @Column(name = "interest_rate")
    private double interestRate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "interest_period")
    private InterestPeriod interestPeriod;
    
    @Column(name = "minimum_balance")
    private double minimumBalance;

}
