package com.campus.banking.persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.campus.banking.model.BankAccount;
import com.campus.banking.model.CheckingAccount;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CheckingAccountDAOImpl implements BankAccountDAO<CheckingAccount> {

    private Database db;

    private BankAccountDAO<BankAccount> bankAccountDAO;

    @Override
    public void add(CheckingAccount account) {
        db.runInTransaction(conn -> insertCheckingAccount(account, conn));
    }

    private void insertCheckingAccount(CheckingAccount account, Connection conn) {
        var insert = """
                    INSERT
                      INTO checking_accounts(
                                id,
                                overdraft_limit,
                                debt)
                    VALUES (?,?,?)
                """;
        bankAccountDAO.transactionalAdd(conn, account);
        db.runStatement(conn, insert, statement -> {
            statement.setDouble(1, account.getId());
            statement.setDouble(2, account.getOverDraftLimit());
            statement.setDouble(3, account.getDebt());
            return statement.execute();
        });
    }

    @Override
    public void transactionalAdd(Connection trxConn, CheckingAccount account) {
        insertCheckingAccount(account, trxConn);
    }

    @Override
    public Optional<CheckingAccount> findByAccountNumber(String accountNumber) {
        try (var conn = db.getConnection()) {
            var account = getCheckingAccount(conn, accountNumber, false);
            return Optional.ofNullable(account);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Optional<CheckingAccount> findByAccountNumberForUpdate(Connection trxConn, String accountNumber) {
        var account = getCheckingAccount(trxConn, accountNumber, true);
        return Optional.ofNullable(account);
    }

    private CheckingAccount getCheckingAccount(Connection conn, String accountNumber, boolean lock) {
        var query = """
                    SELECT account.account_number,
                           account.account_holder_name,
                           account.balance,
                           checking.id,
                           checking.overdraft_limit,
                           checking.debt
                      FROM bank_accounts account
                      JOIN checking_accounts checking
                        ON checking.id = account.id
                     WHERE account.account_number = ?
                """;

        if (lock) {
            query += " for update";
        }
        var account = db.runStatement(conn, query, statement -> {
            statement.setString(1, accountNumber);
            try (var result = statement.executeQuery()) {
                return result.next() ? toAccount(result) : null;
            }
        });
        return account;
    }

    private CheckingAccount toAccount(ResultSet result) throws SQLException {
        return CheckingAccount.builder()
                .id(result.getLong("id"))
                .accountNumber(result.getString("account_number"))
                .accountHolderName(result.getString("account_holder_name"))
                .balance(result.getDouble("balance"))
                .overDraftLimit(result.getDouble("overdraft_limit"))
                .debt(result.getDouble("debt"))
                .build();
    }

    public void deleteByAccountNumber(Connection conn, String accountNumber) {
        var delete = """
                DELETE
                  FROM checking_accounts checking
                  JOIN bank_accounts account
                 WHERE checking.id = account.id
                   AND account.account_number = ?
                """;
        db.runStatement(conn, delete, statement -> {
            statement.setString(1, accountNumber);
            return statement.execute();
        });
    }

    @Override
    public List<CheckingAccount> list() {
        var query = """
                SELECT account.account_number,
                       account.account_holder_name,
                       account.balance,
                       checking.id,
                       checking.overdraft_limit,
                       checking.debt,
                  FROM bank_accounts account
                  JOIN checking_accounts checking
                    ON checking.id = account.id
                """;
        var accounts = db.runStatementWithConnection(query, statement -> {
            try (var result = statement.executeQuery()) {
                var list = new ArrayList<CheckingAccount>();
                while (result.next()) {
                    list.add(toAccount(result));
                }
                return list;
            }
        });
        return accounts;
    }

    @Override
    public void update(CheckingAccount account) {
        try (var conn = db.getConnection()) {
            bankAccountDAO.transactionalUpdate(conn, account);
            doUpdate(conn, account);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void transactionalUpdate(Connection conn, CheckingAccount account) {
        bankAccountDAO.transactionalUpdate(conn, account);
        doUpdate(conn, account);
    }

    private void doUpdate(Connection conn, CheckingAccount account) {
        var update = """
                    UPDATE checking_accounts
                       SET overdraft_limit = ?,
                           debt = ?
                     WHERE id = ?
                """;
        db.runStatement(conn, update, statement -> {
            statement.setDouble(1, account.getOverDraftLimit());
            statement.setDouble(2, account.getDebt());
            statement.setLong(3, account.getId());
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
                SELECT sum(balance)
                  FROM checking_accounts checking
                  JOIN bank_accounts account
                    ON account.id = checking.id
                 WHERE balance > ?
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
}