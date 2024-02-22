package com.campus.banking;

import com.campus.banking.model.BankAccount;
import com.campus.banking.service.BankAccountService;
import com.campus.banking.service.BankAccountServiceImpl;

public class Main {
    public static void main(String[] args) {
        BankAccount account = BankAccount.builder()
                .accountHolderName("Tester")
                .accountNumber("1000")
                .amount(0.0d)
                .build();
        BankAccountService service = new BankAccountServiceImpl();
        service.deposit(account, 10.0);

        System.out.println(account.getAmount());
    }
}