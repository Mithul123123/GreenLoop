package com.greenloop.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Change these values to match your local MySQL setup.
    private static final String URL =
            "jdbc:sqlserver://localhost:58323;databaseName=greenloop_db;encrypt=true;trustServerCertificate=true";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "Yethmin#123";

    // Each form calls this method when it needs to work with the database.
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
