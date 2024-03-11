package com.campus.banking.persistence;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;

import org.apache.ibatis.jdbc.ScriptRunner;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DatabaseImpl implements Database {

    private static final String CREATE_TABLE_SCRIPT = "scripts/create_tables.sql";
    private static final String DROP_TABLE_SCRIPT = "scripts/drop_tables.sql";

    private Datasource datasource;

    @Override
    public void createTables() throws SQLException {
        runScript(CREATE_TABLE_SCRIPT);
    }

    @Override
    public void dropTables() throws SQLException {
        runScript(DROP_TABLE_SCRIPT);
    }

    private void runScript(String filePath) throws SQLException {
        var file = ClassLoader.getSystemClassLoader().getResource(filePath).getFile();
        try (var conn = getConnection()){
            ScriptRunner scriptRunner = new ScriptRunner(conn);
            scriptRunner.setSendFullScript(false);
            scriptRunner.setStopOnError(true);
            scriptRunner.runScript(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {
        try {
            dropTables();
            createTables();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connection getConnection() {
        try {
            return datasource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void runInTransaction(Consumer<Connection> dbTask) {
        try (var conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                dbTask.accept(conn);
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw new RuntimeException(ex);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T runStatement(Connection conn, String query, SQLExceptionFunction<PreparedStatement, T> func) {
        try (var statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            return func.apply(statement);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public <T> T runStatementWithConnection(String query, SQLExceptionFunction<PreparedStatement, T> func) {
        try (var conn = getConnection()) {
            return runStatement(conn, query, func);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}