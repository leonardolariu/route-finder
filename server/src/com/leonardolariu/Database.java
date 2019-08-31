package com.leonardolariu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String USER = "project";
    private static final String PASSWORD = "null";

    private static Connection conn = null;
    private static boolean connInUse = false;

    private Database() {}

    public synchronized static Connection getConnection() {
        if (conn == null) createConnection();

        while (connInUse); //wait for conn to be released

        connInUse = true;
        return conn;
    }

    public static void releaseConnection () {
        connInUse = false;
    }

    private static void createConnection () {
        try {
            System.out.println("Create database connection...");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void closeConnection() {
        if (conn != null)
            try {
                System.out.println("Close database connection...");
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
    }
}
