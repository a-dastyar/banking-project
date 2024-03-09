package com.campus.banking;

import com.campus.banking.model.BankAccount;
import com.campus.banking.model.CheckingAccount;
import com.campus.banking.model.SavingAccount;
import com.campus.banking.persistence.BankAccountDAOImpl;
import com.campus.banking.persistence.CheckingAccountDAOImpl;
import com.campus.banking.persistence.DatabaseImpl;
import com.campus.banking.persistence.SavingAccountDAOImpl;
import com.campus.banking.persistence.TransactionDAOImpl;
import com.campus.banking.service.BankAccountServiceImpl;
import com.campus.banking.service.CheckingAccountServiceImpl;
import com.campus.banking.service.SavingAccountServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

	public static void main(String[] args) {
		log.debug("Checking services");

		var account = BankAccount.builder()
				.accountNumber("2000")
				.accountHolderName("Tester")
				.balance(5000)
				.build();

		var saving = SavingAccount.builder()
				.accountNumber("3000")
				.accountHolderName("Saving")
				.balance(5000)
				.interestRate(10.0)
				.minimumBalance(100)
				.build();

		var checking = CheckingAccount.builder()
				.accountNumber("4000")
				.accountHolderName("Saving")
				.balance(5000)
				.overDraftLimit(100)
				.debt(0)
				.build();

		var db = DatabaseImpl.INSTANCE;

		var trxDao = new TransactionDAOImpl(db);
		var dao = new BankAccountDAOImpl(db);
		var bankService = new BankAccountServiceImpl(dao, trxDao);

		bankService.add(account);
		bankService.deposit(account.getAccountNumber(), 200);
		bankService.withdraw(account.getAccountNumber(), 100);
		var found = bankService.getByAccountNumber(account.getAccountNumber());
		log.info(found.toString());

		var savingDao = new SavingAccountDAOImpl(db);
		var savingService = new SavingAccountServiceImpl(savingDao, trxDao);

		savingService.add(saving);
		savingService.deposit(saving.getAccountNumber(), 200);
		savingService.withdraw(saving.getAccountNumber(), 100);
		var foundSaving = savingService.getByAccountNumber(saving.getAccountNumber());
		log.info(foundSaving.toString());

		var checkingDao = new CheckingAccountDAOImpl(db);
		var checkingService = new CheckingAccountServiceImpl(checkingDao, trxDao);

		checkingService.add(checking);
		checkingService.deposit(checking.getAccountNumber(), 200);
		checkingService.withdraw(checking.getAccountNumber(), 200);
		var foundChecking = checkingService.getByAccountNumber(checking.getAccountNumber());
		log.info(foundChecking.toString());

		db.closeEntityManagerFactory();
	}

}