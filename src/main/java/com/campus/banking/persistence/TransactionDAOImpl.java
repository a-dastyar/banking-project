package com.campus.banking.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.campus.banking.model.Transaction;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TransactionDAOImpl implements TransactionDAO{
    
    private Database db;

    @Override
    public void addTransaction(Connection conn, Transaction transaction) {
        var insert = """
                INSERT INTO transactions(
                    type,
                    amount,
                    bank_account_id,
                    date
                ) VALUES (?,?,?,?)
                """;
        try(var statement = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1, transaction.getType().toString());
            statement.setDouble(2, transaction.getAmount());
            statement.setLong(3, transaction.getAccount().getId());
            statement.setObject(4, transaction.getDate());
            statement.execute();
            extractId(transaction, statement);
        }catch(SQLException ex){
            throw new RuntimeException(ex);
        }
    }


    private void extractId(Transaction transaction, PreparedStatement statement) throws SQLException {
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                transaction.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("Creating account failed, no ID obtained.");
            }
        }
    }

}
