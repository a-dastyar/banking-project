package com.campus.banking.persistence;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.microprofile.config.ConfigProvider;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public enum HikariDatasource implements Datasource{
    INSTANCE;

    private HikariConfig hikariConfig = new HikariConfig();
    private HikariDataSource ds;

    {
        var config = ConfigProvider.getConfig();
        hikariConfig.setJdbcUrl(config.getValue("datasource.url",String.class));
        hikariConfig.setUsername(config.getValue("datasource.user",String.class));
        hikariConfig.setPassword(config.getValue("datasource.password",String.class));
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(hikariConfig);
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}