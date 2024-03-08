package com.campus.banking;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.campus.banking.persistence.Datasource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Testcontainers
public abstract class AbstractIT {

  static String DB_IMAGE = "mysql:8.0.36-bookworm";

  public static Datasource ds;

  private static HikariDataSource hds;

  @Container
  static MySQLContainer<?> mysql = new MySQLContainer<>(DB_IMAGE);

  @BeforeAll
  public static void beforeAll() {
    var hikariConfig = new HikariConfig();
    hikariConfig.setJdbcUrl(mysql.getJdbcUrl());
    hikariConfig.setUsername(mysql.getUsername());
    hikariConfig.setPassword(mysql.getPassword());
    hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
    hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
    hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    hds = new HikariDataSource(hikariConfig);
    ds = hds::getConnection;
  }

  @AfterAll
  public static void afterAll() {
    hds.close();
  }
}
