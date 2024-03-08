package com.campus.banking.persistence;

import java.sql.Connection;
import java.sql.SQLException;

public interface Datasource {
    Connection getConnection() throws SQLException;
}
