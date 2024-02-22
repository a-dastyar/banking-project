package com.campus.banking;

import com.campus.banking.exception.LoadFailureException;
import com.campus.banking.exception.SaveFailureException;
import com.campus.banking.model.BankAccount;
import com.campus.banking.persistence.BankAccountDAO;
import com.campus.banking.persistence.BankAccountDAOImpl;
import com.campus.banking.persistence.Database;
import com.campus.banking.persistence.DatabaseImpl;
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

		Database db = DatabaseImpl.INSTANCE;
		BankAccountDAO dao = new BankAccountDAOImpl(db);
		
		String owner = dao.findByAccountNumber(account.getAccountNumber())
				.map(BankAccount::getAccountHolderName)
				.orElse("Not Found");
		System.out.println("Before adding: Account number " + account.getAccountNumber() + " in database "
				+ owner);
		dao.add(account);
		owner = dao.findByAccountNumber(account.getAccountNumber())
				.map(BankAccount::getAccountHolderName)
				.orElse("Not Found");
		System.out.println(
				"After adding: Account number " + account.getAccountNumber() + " in database " + owner);
		try {
			db.persist();
		} catch (SaveFailureException e) {
			System.err.println("Failed to save to file");
		}
		try {
			db.load();
		} catch (LoadFailureException e) {
			System.err.println("Failed to read from file");
		}
		dao.list().forEach(System.out::println);
	}
}