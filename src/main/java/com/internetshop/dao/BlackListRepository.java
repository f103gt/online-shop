package com.internetshop.dao;

import com.internetshop.model.BlackList;
import com.internetshop.model.Role;
import com.internetshop.model.User;
import com.internetshop.configurations.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BlackListRepository implements BlackListRepositoryInterface {

    public BlackListRepository() {
    }

    @Override
    public void insert(BlackList blacklist) throws SQLException {
        String sql = "INSERT INTO blacklist (user_id, order_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, blacklist.getUserId());
            stmt.setInt(2, blacklist.getOrderId());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    blacklist.setId(rs.getInt(1));
                }
            }
            conn.commit();
        }
    }

    @Override
    public void update(BlackList blacklist) throws SQLException {
        String sql = "UPDATE blacklist SET user_id=?, order_id=? WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, blacklist.getUserId());
            stmt.setInt(2, blacklist.getOrderId());
            stmt.setInt(3, blacklist.getId());
            stmt.executeUpdate();
            conn.commit();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM blacklist WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            conn.commit();
        }
    }

    public BlackList getById(int id) throws SQLException {
        String sql = "SELECT * FROM blacklist WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new BlackList(rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getInt("order_id"));
                }
            }
        }
        return null;
    }

    @Override
    public List<BlackList> getAll() throws SQLException {
        List<BlackList> blacklists = new ArrayList<>();
        String sql = "SELECT * FROM blacklist";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                blacklists.add(new BlackList(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("order_id")
                ));
            }
        }
        return blacklists;
    }

    public List<User> getBlacklistedUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.* FROM users u JOIN blacklist b ON u.id = b.user_id";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        Role.valueOf(rs.getString("role")),
                        rs.getString("email"),
                        rs.getString("address")
                ));
            }
        }
        return users;
    }
}