package com.internetshop.configurations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {
    private static final String URL = "jdbc:postgresql://localhost:5432/university_db";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    // Singleton Connection instance
    private static Connection connection;

    // Private constructor to prevent instantiation
    private DatabaseConfig() {}

    // Get the Singleton Connection instance

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        connection.setAutoCommit(false);
        return connection;
    }

    public static void createTables() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Users table - updated to match User class
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "id SERIAL PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(100) NOT NULL, " +
                    "role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'CUSTOMER')), " +
                    "email VARCHAR(100) NOT NULL, " +
                    "address VARCHAR(200) NOT NULL)");

            // Products table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS products (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "description TEXT, " +
                    "price DECIMAL(10,2) NOT NULL, " +
                    "stock INTEGER NOT NULL DEFAULT 0)");

            // Blacklist table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS blacklist (" +
                    "id SERIAL PRIMARY KEY, " +
                    "user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE, " +
                    "order_id INTEGER NOT NULL)");

            // Cart items junction table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS cart_products (" +
                    "user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE, " +
                    "product_id INTEGER NOT NULL REFERENCES products(id) ON DELETE CASCADE, " +
                    "PRIMARY KEY (user_id, product_id))");

            // Orders table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS orders (" +
                    "id SERIAL PRIMARY KEY, " +
                    "user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE, " +
                    "order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "receival_date TIMESTAMP, " +
                    "total_amount DECIMAL(10,2) NOT NULL, " +
                    "status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'PROCESSING')))");

            // Order items junction table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS order_items (" +
                    "order_id INTEGER NOT NULL REFERENCES orders(id) ON DELETE CASCADE, " +
                    "product_id INTEGER NOT NULL REFERENCES products(id), " +
                    "quantity INTEGER NOT NULL, " +
                    "price_at_purchase DECIMAL(10,2) NOT NULL, " +
                    "PRIMARY KEY (order_id, product_id))");

            conn.commit();
            System.out.println("All tables created successfully");
        }
    }

    public static void dropTables() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Drop tables in reverse order of creation to respect foreign key constraints
            stmt.executeUpdate("DROP TABLE IF EXISTS order_items CASCADE");
            stmt.executeUpdate("DROP TABLE IF EXISTS orders CASCADE");
            stmt.executeUpdate("DROP TABLE IF EXISTS cart_products CASCADE");
            stmt.executeUpdate("DROP TABLE IF EXISTS carts CASCADE");
            stmt.executeUpdate("DROP TABLE IF EXISTS blacklist CASCADE");
            stmt.executeUpdate("DROP TABLE IF EXISTS products CASCADE");
            stmt.executeUpdate("DROP TABLE IF EXISTS users CASCADE");

            conn.commit();
            System.out.println("All tables dropped successfully");
        }
    }
}
