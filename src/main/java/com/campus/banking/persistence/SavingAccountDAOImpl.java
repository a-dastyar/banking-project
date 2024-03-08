package com.campus.banking.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.campus.banking.model.BankAccount;
import com.campus.banking.model.InterestPeriod;
import com.campus.banking.model.SavingAccount;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SavingAccountDAOImpl implements SavingAccountDAO {

    private Database db;

    private BankAccountDAO<BankAccount> bankAccountDAO;

    @Override
    public void add(SavingAccount account) {
        db.runInTransaction(conn -> insertSavingAccount(conn, account));
    }

    private void insertSavingAccount(Connection conn, SavingAccount account) {
        var insert = """
                    INSERT 
                      INTO saving_accounts(
                                id,
                                interest_rate,
                                interest_period,
                                minimum_balance)
                    VALUES (?,?,?,?)
                """;
        bankAccountDAO.transactionalAdd(conn, account);
        db.runStatement(conn, insert, statement -> {
            statement.setDouble(1, account.getId());
            statement.setDouble(2, account.getInterestRate());
            statement.setString(3, account.getInterestPeriod().toString());
            statement.setDouble(4, account.getMinimumBalance());
            return statement.execute();
        });
    }

    @Override
    public void transactionalAdd(Connection trxConn, SavingAccount account) {
        insertSavingAccount(trxConn, account);
    }

    @Override
    public Optional<SavingAccount> findByAccountNumber(String accountNumber) {
        try (var conn = db.getConnection()) {
            var account = getSavingAccount(conn, accountNumber, false);
            return Optional.ofNullable(account);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Optional<SavingAccount> findByAccountNumberForUpdate(Connection trxConn, String accountNumber) {
        var account = getSavingAccount(trxConn, accountNumber, true);
        return Optional.ofNullable(account);
    }

    private SavingAccount getSavingAccount(Connection conn, String accountNumber, boolean lock) {
        var query = """
                    SELECT account.account_number,
                           account.account_holder_name,
                           account.balance,
                           saving.id,
                           saving.interest_rate,
                           saving.interest_period,
                           saving.minimum_balance
                      FROM bank_accounts account
                      JOIN saving_accounts saving
                        ON saving.id = account.id
                     WHERE account.account_number = ?
                """;
        if (lock) {
            query += " FOR UPDATE";
        }
        var account = db.runStatementWithConnection(query, statement -> {
            statement.setString(1, accountNumber);
            try (var result = statement.executeQuery()) {
                return result.next() ? toAccount(result) : null;
            }
        });
        return account;
    }

    private SavingAccount toAccount(ResultSet result) throws SQLException {
        return SavingAccount.builder()
                .id(result.getLong("id"))
                .accountNumber(result.getString("account_number"))
                .accountHolderName(result.getString("account_holder_name"))
                .balance(result.getDouble("balance"))
                .minimumBalance(result.getDouble("minimum_balance"))
                .interestRate(result.getDouble("interest_rate"))
                .interestPeriod(InterestPeriod.valueOf(result.getString("interest_period")))
                .build();
    }

    @Override
    public List<SavingAccount> list() {
        var query = """
                SELECT account.account_number,
                       account.account_holder_name,
                       account.balance,
                       saving.id,
                       saving.interest_rate,
                       saving.interest_period,
                       saving.minimum_balance
                  FROM bank_accounts account
                  JOIN saving_accounts saving
                    ON saving.id = account.id
                """;
        var accounts = db.runStatementWithConnection(query, statement -> {
            try (var result = statement.executeQuery()) {
                return extractList(result);
            }
        });
        return accounts;
    }

    private List<SavingAccount> extractList(ResultSet result) throws SQLException {
        var list = new ArrayList<SavingAccount>();
        while (result.next()) {
            var account = SavingAccount.builder()
                    .id(result.getLong("id"))
                    .accountNumber(result.getString("account_number"))
                    .accountHolderName(result.getString("account_holder_name"))
                    .balance(result.getDouble("balance"))
                    .interestRate(result.getDouble("minimum_balance"))
                    .interestRate(result.getDouble("interest_rate"))
                    .interestPeriod(InterestPeriod.valueOf(result.getString("interest_period")))
                    .build();
            list.add(account);
        }
        return list;
    }

    @Override
    public void update(SavingAccount account) {
        try (var conn = db.getConnection()) {
            bankAccountDAO.transactionalUpdate(conn, account);
            doUpdate(conn, account);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void transactionalUpdate(Connection conn, SavingAccount account) {
        bankAccountDAO.transactionalUpdate(conn, account);
        doUpdate(conn, account);
    }

    private void doUpdate(Connection conn, SavingAccount account) {
        var update = """
                    UPDATE saving_accounts
                       SET interest_rate = ?,
                           interest_period = ?,
                           minimum_balance = ?
                     WHERE id = ?
                """;
        db.runStatementWithConnection(update, statement -> {
            statement.setDouble(1, account.getInterestRate());
            statement.setString(2, account.getInterestPeriod().toString());
            statement.setDouble(3, account.getMinimumBalance());
            statement.setLong(4, account.getId());
            return statement.execute();
        });
    }

    @Override
    public void inTransaction(Consumer<Connection> consumer) {
        db.runInTransaction(consumer);
    }

    @Override
    public double sumBalanceHigherThan(double min) {
        var query = """
                SELECT sum(account.balance)
                  FROM saving_accounts saving
                  JOIN bank_accounts account
                    ON account.id = saving.id
                 WHERE account.balance > ?
                """;
        var sum = db.runStatementWithConnection(query, statement -> {
            statement.setDouble(1, min);
            try (var result = statement.executeQuery()) {
                result.next();
                return result.getDouble(1);
            }
        });
        return sum;
    }

    @Override
    public void applyInterest() {
        var update = """
                UPDATE bank_accounts account
                  JOIN saving_accounts saving
                  ON saving.id = account.id
                  SET account.balance = account.balance + (account.balance * saving.interest_rate/100.0)
                  """;
        var insertTransactions = """
                INSERT INTO transactions(
                    type,
                    amount,
                    bank_account_id,
                    date)
                SELECT 'INTEREST',
                        account.balance * saving.interest_rate / 100.0,
                        account.id,
                        NOW()
                  FROM saving_accounts saving
                  JOIN bank_accounts account
                    ON saving.id = account.id
                    """;
        var lock = """
                SELECT *
                  FROM bank_accounts account
                  JOIN saving_accounts saving
                    ON account.id = saving.id
                  FOR UPDATE
                  """;
        db.runInTransaction(conn -> {
            db.runStatement(conn, lock, PreparedStatement::execute);
            db.runStatement(conn, insertTransactions, PreparedStatement::executeUpdate);
            db.runStatement(conn, update, PreparedStatement::executeUpdate);
        });
    }
}