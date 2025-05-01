package com.internetshop.services;

import com.internetshop.dao.UserRepositoryInterface;
import com.internetshop.model.Role;
import com.internetshop.model.User;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MockUserRepository implements UserRepositoryInterface {
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<String, User> usernameIndex = new HashMap<>();
    private int nextId = 1;

    public MockUserRepository() {
        // Initialize with test data
        User admin = new User(1, "admin", "admin123", Role.ADMIN, "admin@shop.com", "Admin Address");
        User customer = new User(2, "customer", "customer123", Role.CUSTOMER, "customer@shop.com", "Customer Address");

        users.put(1, admin);
        users.put(2, customer);
        usernameIndex.put("admin", admin);
        usernameIndex.put("customer", customer);
        nextId = 3;
    }

    @Override
    public void insert(User user) throws SQLException {
        if (usernameIndex.containsKey(user.getUsername())) {
            throw new SQLException("Username already exists: " + user.getUsername());
        }

        user.setId(nextId++);
        users.put(user.getId(), user);
        usernameIndex.put(user.getUsername(), user);
    }

    @Override
    public void update(User user) throws SQLException {
        if (!users.containsKey(user.getId())) {
            throw new SQLException("User not found with ID: " + user.getId());
        }

        User existing = users.get(user.getId());
        if (!existing.getUsername().equals(user.getUsername())) {
            if (usernameIndex.containsKey(user.getUsername())) {
                throw new SQLException("Username already exists: " + user.getUsername());
            }
            usernameIndex.remove(existing.getUsername());
            usernameIndex.put(user.getUsername(), user);
        }

        users.put(user.getId(), user);
    }

    @Override
    public void delete(int id) throws SQLException {
        User user = users.get(id);
        if (user == null) {
            throw new SQLException("User not found with ID: " + id);
        }

        users.remove(id);
        usernameIndex.remove(user.getUsername());
    }

    @Override
    public List<User> getAll() throws SQLException {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(int id) throws SQLException {
        User user = users.get(id);
        if (user == null) {
            throw new SQLException("User not found with ID: " + id);
        }
        return user;
    }

    @Override
    public Optional<User> findByUsername(String username) throws SQLException {
        return Optional.ofNullable(usernameIndex.get(username));
    }

    // Helper method for testing
    public int getUserCount() {
        return users.size();
    }
}
