package com.internetshop.dao;

import com.internetshop.model.Role;
import com.internetshop.model.User;
import com.internetshop.configurations.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository implements UserRepositoryInterface{

    public UserRepository() {}

    public void insert(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, role, email, address) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole().toString());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getAddress());
            stmt.executeUpdate();
            connection.commit();
        }
    }

    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET username=?, password=?, role=?, email=?, address=? WHERE id=?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole().toString());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getAddress());
            stmt.setInt(6, user.getId());
            stmt.executeUpdate();
            connection.commit();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id=?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            connection.commit();
        }
    }

    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection connection = DatabaseConfig.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                try {
                    users.add(new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            Role.fromString(rs.getString("role")),
                            rs.getString("email"),
                            rs.getString("address")
                    ));
                } catch (IllegalArgumentException e) {
                    throw new SQLException(
                            String.format("Invalid role in database for user ID %d: %s",
                                    rs.getInt("id"),
                                    e.getMessage()),
                            e
                    );
                }
            }
        }
        return users;
    }

    public User getById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id=?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    try {
                        return new User(
                                rs.getInt("id"),
                                rs.getString("username"),
                                rs.getString("password"),
                                Role.fromString(rs.getString("role")),
                                rs.getString("address"),
                                rs.getString("email")
                        );
                    } catch (IllegalArgumentException e) {
                        throw new SQLException(
                                String.format("Invalid role in database for user ID %d: %s",
                                        id,
                                        e.getMessage()),
                                e
                        );
                    }
                }
            }
        }
        return null;
    }

    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String password = rs.getString("password");
                    String roleName = rs.getString("role");
                    Role role = Role.valueOf(roleName);
                    String email = rs.getString("email");
                    String address = rs.getString("address");
                    User user = new User(id, username, password, role, email, address);
                    return Optional.of(user);
                }
            }
        }
        return Optional.empty();
    }
}