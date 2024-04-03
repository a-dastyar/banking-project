package com.campus.banking.config;

import java.util.Arrays;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.campus.banking.persistence.DatabaseSchemaMigrator;
import com.campus.banking.service.AccountNumberGenerator;
import com.campus.banking.service.UserService;

import jakarta.enterprise.context.control.RequestContextController;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebListener
public class ApplicationLifecycle implements ServletContextListener {

    @Inject
    private Provider<RequestContextController> context;

    @Inject
    private UserService users;

    @Inject
    private AccountNumberGenerator generator;

    @Inject
    private DatabaseSchemaMigrator migrator;

    @Inject
    @ConfigProperty(name = "datasource.schema.migration")
    private boolean migration;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.debug("Setting up database");
        if (migration) {
            log.debug("Migrating database schema");
            migrator.migrate();
        }
        inRequestScope(
                users::setupAdminAccount,
                generator::setupNumberGenerator);
    }

    void inRequestScope(Runnable... runnables) {
        RequestContextController requestScope = context.get();
        requestScope.activate();
        try {
            Arrays.stream(runnables)
                    .forEach(Runnable::run);
        } finally {
            requestScope.deactivate();
        }
    }

}
