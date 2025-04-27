package com.internetshop.services;

import com.internetshop.dao.UserRepository;
import com.internetshop.model.Role;
import com.internetshop.model.User;

import java.sql.SQLException;
import java.util.Optional;

public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Optional<User> authenticate(String username, String password) throws SQLException {
        return repository.findByUsername(username)
                .filter(user -> user.getPassword().equals(password));
    }

    public Optional<User> findByUsername(String username) throws SQLException {
        return repository.findByUsername(username);
    }

    public User register(User user) throws SQLException {
        // Set default role if not specified
        if (user.getRole() == null) {
            user.setRole(Role.CUSTOMER);
        }
        repository.insert(user);
        return user;
    }
}
