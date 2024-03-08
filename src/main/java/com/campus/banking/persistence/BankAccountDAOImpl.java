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

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BankAccountDAOImpl implements BankAccountDAO<BankAccount> {

    protected Database db;

    @Override
    public void add(BankAccount account) {
        db.runInTransaction(conn -> insertBankAccount(account, conn, 0));
    }

    @Override
    public void transactionalAdd(Connection trxConn, BankAccount account) {
        insertBankAccount(account, trxConn, 1);
    }

    private void insertBankAccount(BankAccount account, Connection conn, int subclass) {
        var insert = """
                    INSERT
                      INTO bank_accounts(
                                account_number,
                                account_holder_name,
                                balance,
                                is_subclass)
                    VALUES (?,?,?,?)
                """;
        db.runStatementWithConnection(insert, statement -> {
            statement.setString(1, account.getAccountNumber());
            statement.setString(2, account.getAccountHolderName());
            statement.setDouble(3, account.getBalance());
            statement.setDouble(4, subclass);
            statement.execute();
            extractId(account, statement);
            return account;
        });
    }

    private void extractId(BankAccount account, PreparedStatement statement) throws SQLException {
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                account.setId(generatedKeys.getLong(1));
            } else {
                throw new RuntimeException(new SQLException("Creating account failed, no ID obtained."));
            }
        }
    }

    @Override
    public Optional<BankAccount> findByAccountNumber(String accountNumber) {
        try (var conn = db.getConnection()) {
            var account = getBankAccount(conn, accountNumber, false);
            return Optional.ofNullable(account);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Optional<BankAccount> findByAccountNumberForUpdate(Connection trxConn, String accountNumber) {
        var account = getBankAccount(trxConn, accountNumber, true);
        return Optional.ofNullable(account);
    }

    private BankAccount getBankAccount(Connection conn, String accountNumber, boolean lock) {
        var query = """
                    SELECT id,
                           account_number,
                           account_holder_name,
                           balance
                      FROM bank_accounts
                     WHERE account_number = ?
                """;
        if (lock) {
            query += " FOR UPDATE";
        }
        var account = db.runStatement(conn, query, statement -> {
            statement.setString(1, accountNumber);
            try (var result = statement.executeQuery()) {
                return result.next() ? toAccount(result) : null;
            }
        });
        return account;
    }

    private BankAccount toAccount(ResultSet result) throws SQLException {
        return BankAccount.builder()
                .id(result.getLong("id"))
                .accountNumber(result.getNString("account_number"))
                .accountHolderName(result.getString("account_holder_name"))
                .balance(result.getDouble("balance"))
                .build();
    }

    @Override
    public List<BankAccount> list() {
        var query = """
                SELECT id,
                       account_number,
                       account_holder_name,
                       balance
                  FROM bank_accounts
                 WHERE is_subclass = 0
                """;
        var accounts = db.runStatementWithConnection(query, statement -> {
            try (var result = statement.executeQuery()) {
                return extractList(result);
            }
        });
        return accounts;
    }

    private List<BankAccount> extractList(ResultSet result) throws SQLException {
        var list = new ArrayList<BankAccount>();
        while (result.next()) {
            list.add(toAccount(result));
        }
        return list;
    }

    @Override
    public void update(BankAccount account) {
        try (var conn = db.getConnection()) {
            doUpdate(conn, account);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void transactionalUpdate(Connection conn, BankAccount account) {
        doUpdate(conn, account);
    }

    private void doUpdate(Connection conn, BankAccount account) {
        var update = """
                    UPDATE bank_accounts account
                       SET account_number = ?,
                           account_holder_name = ?,
                           balance = ?
                     WHERE account.id = ?
                """;
        db.runStatement(conn, update, statement -> {
            statement.setString(1, account.getAccountNumber());
            statement.setString(2, account.getAccountHolderName());
            statement.setDouble(3, account.getBalance());
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
                SELECT sum(balance)
                  FROM bank_accounts
                 WHERE is_subclass = 0
                   AND balance > ?
                """;
        return db.runStatementWithConnection(query, statement->{
            statement.setDouble(1, min);
            try (var result = statement.executeQuery()) {
                result.next();
                return result.getDouble(1);
            }
        });
    }

}
