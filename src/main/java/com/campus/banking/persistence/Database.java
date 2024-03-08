package com.campus.banking.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Consumer;



public interface Database {

    void createTables() throws SQLException;

    void dropTables() throws SQLException;

    Connection getConnection();

    void runInTransaction(Consumer<Connection> dbTask);

    void clear();

    <T> T runStatement(Connection conn, String query, SQLExceptionFunction<PreparedStatement, T> func);

    <T> T runStatementWithConnection(String query, SQLExceptionFunction<PreparedStatement, T> func);
}

interface SQLExceptionFunction<T, U> {
    U apply(T t) throws SQLException;
}