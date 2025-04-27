package com.internetshop.configurations;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import java.sql.Connection;
import java.sql.SQLException;

public class AppInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            DatabaseConfig.createTables();
            System.out.println("Database tables initialized successfully");
        } catch (SQLException e) {
            throw new RuntimeException("Database initialization failed", e);
        }
    }
}
