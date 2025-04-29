package com.internetshop.dao;

import com.internetshop.model.Cart;
import com.internetshop.model.Product;
import com.internetshop.configurations.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartRepository implements Repository<Cart> {

    public CartRepository() {}

    @Override
    public void insert(Cart cart) throws SQLException {
        if (!cart.getProductIds().isEmpty()) {
            insertCartProducts(cart.getUserId(), cart.getProductIds());
        }
    }

    @Override
    public void update(Cart cart) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String deleteSql = "DELETE FROM cart_products WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
                stmt.setInt(1, cart.getUserId());
                stmt.executeUpdate();
            }

            // Then insert the current products if any exist
            if (!cart.getProductIds().isEmpty()) {
                insertCartProducts(cart.getUserId(), cart.getProductIds());
            }
            conn.commit();
        }
    }

    @Override
    public void delete(int userId) throws SQLException {
        String sql = "DELETE FROM cart_products WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            conn.commit();
        }
    }

    @Override
    public Cart getById(int userId) throws SQLException {
        Cart cart = new Cart(userId);
        String sql = "SELECT product_id FROM cart_products WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cart.addProductId(rs.getInt("product_id"));
                }
            }
        }
        return cart;
    }

    @Override
    public List<Cart> getAll() throws SQLException {
        return new ArrayList<>();
    }

    public void addProductToCart(int userId, int productId) throws SQLException {
        String sql = "INSERT INTO cart_products (user_id, product_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
            conn.commit();
        }
    }

    public void removeProductFromCart(int userId, int productId) throws SQLException {
        String sql = "DELETE FROM cart_products WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
            conn.commit();
        }
    }

    public List<Product> getProductsByUserId(int userId) throws SQLException {
        String sql = "SELECT p.id, p.name, p.description, p.price, p.stock " +
                "FROM cart_products cp " +
                "JOIN products p ON cp.product_id = p.id " +
                "WHERE cp.user_id = ?";

        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getBigDecimal("price"),
                            rs.getInt("stock")
                    );
                    products.add(product);
                }
            }
        }
        return products;
    }


    private void insertCartProducts(int userId, List<Integer> productIds) throws SQLException {
        String sql = "INSERT INTO cart_products (user_id, product_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Integer productId : productIds) {
                stmt.setInt(1, userId);
                stmt.setInt(2, productId);
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit();
        }
    }

}