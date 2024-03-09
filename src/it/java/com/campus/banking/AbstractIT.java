package com.campus.banking;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.campus.banking.persistence.Database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.AllArgsConstructor;

@Testcontainers
public abstract class AbstractIT {

    @AllArgsConstructor
    static class DatabaseWrapper implements Database {
        private EntityManagerFactory emf;

        @Override
        public EntityManager getEntityManager() {
            return emf.createEntityManager();
        }

        @Override
        public EntityManagerFactory getEntityManagerFactory() {
            return emf;
        }

        @Override
        public void closeEntityManagerFactory() {
            emf.close();
        }

    }

    static String DB_IMAGE = "mysql:8.0.36-bookworm";

    protected DatabaseWrapper db;

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>(DB_IMAGE);

    @BeforeEach
    public void beforeEach() {
        var properties = Map.of(
                "hibernate.hikari.jdbcUrl", mysql.getJdbcUrl(),
                "hibernate.hikari.dataSource.user", mysql.getUsername(),
                "hibernate.hikari.dataSource.password", mysql.getPassword(),
                "hibernate.show_sql", "false",
                "jakarta.persistence.schema-generation.database.action", "create-drop");

        var emf = Persistence.createEntityManagerFactory("App", properties);
        db = new DatabaseWrapper(emf);
    }
    
    @AfterEach
    public void afterEach() {
        db.closeEntityManagerFactory();
    }
}