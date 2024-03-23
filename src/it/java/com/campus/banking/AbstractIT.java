package com.campus.banking;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
abstract class AbstractIT {

    protected static String DB_IMAGE = "mysql:8.0.36-bookworm";

    @Container
    protected static MySQLContainer<?> mysql = new MySQLContainer<>(DB_IMAGE);

}