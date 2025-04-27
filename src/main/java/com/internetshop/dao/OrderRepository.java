package com.internetshop.dao;

import com.internetshop.model.Order;
import com.internetshop.model.OrderStatus;
import com.internetshop.model.Role;
import com.internetshop.model.User;
import com.internetshop.configurations.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderRepository implements Repository<Order> {

    public OrderRepository() {}

    public void insert(Order order) throws SQLException {
        String sql = "INSERT INTO orders (user_id, order_date, total_amount, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, order.getUserId());
            stmt.setDate(2, new java.sql.Date(order.getOrderDate().getTime()));
            stmt.setBigDecimal(3, order.getTotalAmount());
            stmt.setString(4, order.getStatus().toString());
            stmt.executeUpdate();
            conn.commit();
        }
    }

    public void update(Order order) throws SQLException {
        String sql = "UPDATE orders SET user_id=?, order_date=?, total_amount=?, status=? WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, order.getUserId());
            stmt.setDate(2, new java.sql.Date(order.getOrderDate().getTime()));
            stmt.setBigDecimal(3, order.getTotalAmount());
            stmt.setString(4, order.getStatus().toString());
            stmt.setInt(5, order.getId());
            stmt.executeUpdate();
            conn.commit();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM orders WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            conn.commit();
        }
    }

    public Order getById(int id) throws SQLException {
        String sql = "SELECT * FROM orders WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    try {
                        return new Order(
                                rs.getInt("id"),
                                rs.getInt("user_id"),
                                rs.getDate("order_date"),
                                rs.getBigDecimal("total_amount"),
                                OrderStatus.fromString(rs.getString("status"))
                        );
                    } catch (IllegalArgumentException e) {
                        throw new SQLException("Invalid order status in database", e);
                    }
                }
            }
        }
        return null;
    }

    public List<Order> getAll() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                try {
                    orders.add(new Order(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getDate("order_date"),
                            rs.getBigDecimal("total_amount"),
                            OrderStatus.fromString(rs.getString("status"))
                    ));
                } catch (IllegalArgumentException e) {
                    throw new SQLException(
                            String.format("Invalid order status in database for order ID %d: %s",
                                    rs.getInt("id"),
                                    e.getMessage()),
                            e
                    );
                }
            }
        }
        return orders;
    }

    public List<User> findUsersByOrderStatus(OrderStatus status) throws SQLException {
        String sql = "SELECT u.* FROM users u " +
                "JOIN orders o ON u.id = o.user_id " +
                "WHERE o.status = ? AND o.receival_date IS NOT NULL";

        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            Role.fromString(rs.getString("role")),
                            rs.getString("email"),
                            rs.getString("address")
                    ));
                }
            }
        }
        return users;
    }

    public Map<User, List<Order>> getUsersWithUnpaidOrders() throws SQLException {
        String sql = "SELECT u.*, o.* FROM users u " +
                "JOIN orders o ON u.id = o.user_id " +
                "WHERE o.status = 'PENDING' AND o.receival_date IS NOT NULL";

        Map<User, List<Order>> debtors = new HashMap<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User(
                        rs.getInt("u.id"),
                        rs.getString("u.username"),
                        rs.getString("u.password"),
                        Role.valueOf(rs.getString("u.role")),
                        rs.getString("u.email"),
                        rs.getString("u.address")
                );

                Order order = new Order(
                        rs.getInt("o.id"),
                        rs.getInt("o.user_id"),
                        rs.getTimestamp("o.order_date"),
                        rs.getBigDecimal("o.total_amount"),
                        OrderStatus.valueOf(rs.getString("o.status"))
                );

                debtors.computeIfAbsent(user, k -> new ArrayList<>()).add(order);
            }
        }
        return debtors;
    }
}