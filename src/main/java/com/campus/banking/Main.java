package com.campus.banking;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import com.campus.banking.model.BankAccount;

public class Main {

	private static SessionFactory sessionFactory;

	public static void main(String[] args) {
		setUp();
		sessionFactory.inTransaction(session -> {
			session.persist(new BankAccount().withBalance(200.0d));
		});
		sessionFactory.inTransaction(session -> {
			session.createSelectionQuery("from bank_accounts", BankAccount.class)   
					.getResultList()   
					.forEach(account -> System.out.println("BankAccount (" + account.getBalance() + ") : " + account.getId()));
		});
	}

	private static void setUp() {
		// A SessionFactory is set up once for an application!
		final StandardServiceRegistry registry =
				new StandardServiceRegistryBuilder()
						.build();
		try {
			sessionFactory =
					new MetadataSources(registry)
							.addAnnotatedClass(BankAccount.class)
							.buildMetadata()
							.buildSessionFactory();
		}
		catch (Exception e) {
			// The registry would be destroyed by the SessionFactory, but we
			// had trouble building the SessionFactory so destroy it manually.
			e.printStackTrace();
			StandardServiceRegistryBuilder.destroy(registry);
		}
	}
}