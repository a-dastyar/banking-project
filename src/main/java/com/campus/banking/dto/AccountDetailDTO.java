package com.campus.banking.dto;

import com.campus.banking.model.BankAccount;
import com.campus.banking.model.Transaction;
import com.campus.banking.persistence.Page;

import lombok.Builder;

@Builder
public record AccountDetailDTO<T extends BankAccount>(
        T account,
        double maxWithdraw,
        double minWithdraw,
        double minDeposit,
        Page<Transaction> transactions) {

}
