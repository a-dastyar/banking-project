package com.campus.banking;

import com.campus.banking.model.BankAccount;
import com.campus.banking.model.CheckingAccount;
import com.campus.banking.service.BankAccountService;
import com.campus.banking.service.BankAccountServiceImpl;
import com.campus.banking.service.CheckingAccountServiceImpl;

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


        CheckingAccount checkingAccount = new CheckingAccount("1001", "CheckingTester", 110.0d, 10.0d, 0.0d);
        CheckingAccountServiceImpl checkingAccountService = new CheckingAccountServiceImpl();

        checkingAccountService.withdraw(checkingAccount, 5.0d);
        System.out.println(checkingAccount.getBalance() + " | " + checkingAccount.getDebt());


        CheckingAccount checkingAccount2 = new CheckingAccount("null", "null", 10000, 100000, 0);

        checkingAccountService.withdraw(checkingAccount2, 100000);
        System.out.println(checkingAccount2.getBalance() + " | " + checkingAccount2.getDebt());

        checkingAccountService.withdraw(checkingAccount2, 9800);
        System.out.println(checkingAccount2.getBalance() + " | " + checkingAccount2.getDebt());

        checkingAccountService.deposit(checkingAccount2, 100);
        System.out.println(checkingAccount2.getBalance() + " | " + checkingAccount2.getDebt());

        checkingAccountService.deposit(checkingAccount2, 101);
        System.out.println(checkingAccount2.getBalance() + " | " + checkingAccount2.getDebt());
    }
}