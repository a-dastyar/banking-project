package com.campus.banking;

import com.campus.banking.model.BankAccount;
import com.campus.banking.model.CheckingAccount;
import com.campus.banking.service.BankAccountService;
import com.campus.banking.service.BankAccountServiceImpl;
import com.campus.banking.service.CheckingAccountService;

public class Main {
    public static void main(String[] args) {
        BankAccount account = BankAccount.builder()
                .accountHolderName("Tester")
                .accountNumber("1000")
                .balance(0.0d)
                .build();
        BankAccountService service = new BankAccountServiceImpl();
        service.deposit(account, 10.0);

        System.out.println(account.getBalance());


        CheckingAccount checkingAccount = new CheckingAccount("1001", "CheckingTester", 10.0d, 10.0d, 0.0d);
        CheckingAccountService checkingAccountService = new CheckingAccountService();

        checkingAccountService.withdraw(checkingAccount, 5.0d);
        System.out.println(checkingAccount.getBalance());
        
        checkingAccountService.withdraw(checkingAccount, 10.0d);
        System.out.println(checkingAccount.getBalance());

        checkingAccountService.withdraw(checkingAccount, 5.0d);
        System.out.println(checkingAccount.getBalance());
    }
}