package com.example.clinicmanagement3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/clinicdb";
    private static final String USER = "root";
    private static final String PASSWORD = "@Northpointhk2006";

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connected to database.");
            return conn;
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            return null;
        }
    }

    public static boolean testConnection() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}