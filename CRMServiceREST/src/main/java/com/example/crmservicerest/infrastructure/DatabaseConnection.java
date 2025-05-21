package com.example.crmservicerest.infrastructure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/ProgramacaoInternet";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver do PostgreSQL não encontrado no classpath.", e);
        }

        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        conn.setAutoCommit(true);
        return conn;
    }
}
