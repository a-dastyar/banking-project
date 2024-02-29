package com.campus.banking;

import com.campus.banking.exception.LoadFailureException;
import com.campus.banking.exception.SaveFailureException;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.InterestPeriod;
import com.campus.banking.model.SavingAccount;
import com.campus.banking.persistence.BankAccountDAO;
import com.campus.banking.persistence.BankAccountDAOImpl;
import com.campus.banking.persistence.Database;
import com.campus.banking.persistence.DatabaseImpl;
import com.campus.banking.persistence.SavingAccountDAOImpl;

public class Main {
	public static void main(String[] args) {
		var account = BankAccount.builder()
				.accountHolderName("Tester")
				.accountNumber("1000")
				.balance(0.0d)
				.build();
		var saving = SavingAccount.builder()
				.accountNumber("2000")
				.interestPeriod(InterestPeriod.MONTHLY).build();

		Database db = DatabaseImpl.INSTANCE;
		BankAccountDAO<BankAccount> bankAccountDAO = new BankAccountDAOImpl(db);
		BankAccountDAO<SavingAccount> savingAccountDAO = new SavingAccountDAOImpl(db);

		bankAccountDAO.add(account);
		savingAccountDAO.add(saving);

		try {
			db.persist();
			db.load();
		} catch (SaveFailureException e) {
			System.err.println("Failed to save to file");
			e.printStackTrace();
		} catch (LoadFailureException e) {
			System.err.println("Failed to read from file");
			e.printStackTrace();
		}
		bankAccountDAO.list().forEach(System.out::println);
	}
}