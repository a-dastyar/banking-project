package com.campus.banking;

import java.io.IOException;
import java.sql.SQLException;

import com.campus.banking.model.BankAccount;
import com.campus.banking.model.CheckingAccount;
import com.campus.banking.model.InterestPeriod;
import com.campus.banking.model.SavingAccount;
import com.campus.banking.persistence.BankAccountDAOImpl;
import com.campus.banking.persistence.CheckingAccountDAOImpl;
import com.campus.banking.persistence.DatabaseImpl;
import com.campus.banking.persistence.HikariDatasource;
import com.campus.banking.persistence.SavingAccountDAOImpl;
import com.campus.banking.persistence.TransactionDAOImpl;
import com.campus.banking.service.BankAccountServiceImpl;
import com.campus.banking.service.CheckingAccountServiceImpl;
import com.campus.banking.service.SavingAccountServiceImpl;

public class Main {

	public static void main(String[] args) throws SQLException, IOException {
		var account = BankAccount.builder()
				.accountHolderName("Tester")
				.accountNumber("1000")
				.balance(0.0d)
				.build();

		var saving = SavingAccount.builder()
				.accountHolderName("Saver")
				.accountNumber("2002")
				.balance(1000.0)
				.interestRate(10.0)
				.minimumBalance(300)
				.interestPeriod(InterestPeriod.MONTHLY).build();

		var checking = CheckingAccount.builder()
				.accountHolderName("Checker")
				.accountNumber("2003")
				.balance(1000.0)
				.overDraftLimit(100.0)
				.debt(0.0).build();

		var db = new DatabaseImpl(HikariDatasource.INSTANCE);
		db.clear();
		var dao = new BankAccountDAOImpl(db);
		var trxDao = new TransactionDAOImpl(db);
		var bankService = new BankAccountServiceImpl(dao, trxDao);

		bankService.add(account);
		bankService.deposit(account.getAccountNumber(), 2000);
		bankService.withdraw(account.getAccountNumber(), 1000);
		var found = bankService.getByAccountNumber(account.getAccountNumber());
		System.out.println(found);

		var checkingDao = new CheckingAccountDAOImpl(db, dao);
		var checkingService = new CheckingAccountServiceImpl(checkingDao, trxDao);

		checkingService.add(checking);
		checkingService.deposit(checking.getAccountNumber(), 2000.0);
		checkingService.withdraw(checking.getAccountNumber(), 1000.0);
		var foundChecking = checkingService.getByAccountNumber(checking.getAccountNumber());
		System.out.println(foundChecking);

		var savingDao = new SavingAccountDAOImpl(db, dao);
		var savingService = new SavingAccountServiceImpl(savingDao, trxDao);
		savingService.add(saving);
		var foundSaving = savingService.getByAccountNumber(saving.getAccountNumber());
		System.out.println(foundSaving);
	}
}