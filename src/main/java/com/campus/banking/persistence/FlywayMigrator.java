package com.campus.banking.persistence;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.flywaydb.core.Flyway;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class FlywayMigrator implements DatabaseSchemaMigrator {

    private final Flyway flyway;
    
    @Inject
    public FlywayMigrator(@ConfigProperty(name = "datasource.url") String jdbcUrl,
            @ConfigProperty(name = "datasource.user") String username,
            @ConfigProperty(name = "datasource.password") String password) {
        flyway = Flyway.configure().dataSource(jdbcUrl, username, password).load();
    }

    @Override
    public void migrate() {
        flyway.migrate();
    }

}
