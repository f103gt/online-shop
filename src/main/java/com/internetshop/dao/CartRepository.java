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
        String sql = "INSERT INTO carts (user_id) VALUES (?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, cart.getUserId());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int cartId = rs.getInt(1);
                    insertCartProducts(cartId, cart.getProducts());
                }
            }
            conn.commit();
        }
    }

    @Override
    public void update(Cart cart) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            // First delete all existing products for this cart
            String deleteSql = "DELETE FROM cart_products WHERE cart_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
                stmt.setInt(1, cart.getUserId());
                stmt.executeUpdate();
            }

            // Then insert the current products
            insertCartProducts(cart.getUserId(), cart.getProducts());
            conn.commit();
        }
    }

    @Override
    public void delete(int userId) throws SQLException {
        String sql = "DELETE FROM carts WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            conn.commit();
        }
    }

    @Override
    public Cart getById(int userId) throws SQLException {
        String sql = "SELECT p.id, p.name, p.description, p.price, p.stock " +
                "FROM cart_products cp " +
                "JOIN products p ON cp.product_id = p.id " +
                "WHERE cp.cart_id = ?";

        Cart cart = new Cart(userId);
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
                    cart.addProduct(product);
                }
            }
        }
        return cart;
    }

    @Override
    public List<Cart> getAll() throws SQLException {
        return new ArrayList<>();
    }

    private void insertCartProducts(int cartId, List<Product> products) throws SQLException {
        String sql = "INSERT INTO cart_products (cart_id, product_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Product product : products) {
                stmt.setInt(1, cartId);
                stmt.setInt(2, product.getId());
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit();
        }
    }

    public List<Product> getProductsByCartId(int cartId) throws SQLException {
        String sql = "SELECT p.id, p.name, p.description, p.price, p.stock " +
                "FROM cart_products cp " +
                "JOIN products p ON cp.product_id = p.id " +
                "WHERE cp.cart_id = ?";

        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cartId);
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
}