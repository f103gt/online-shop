package com.internetshop.services;

import com.internetshop.dao.BlackListRepository;
import com.internetshop.model.BlackList;
import com.internetshop.model.User;

import java.sql.SQLException;
import java.util.List;

public class BlackListService {
    private final BlackListRepository repository;

    public BlackListService(BlackListRepository repository) {
        this.repository = repository;
    }

    public void addToBlackList(int userId, int orderId) throws SQLException {
        BlackList entry = new BlackList(0, userId, orderId);
        repository.insert(entry);
    }

    public void removeFromBlackList(int id) throws SQLException {
        repository.delete(id);
    }

    public List<User> getBlacklistedUsers() throws SQLException {
        return repository.getBlacklistedUsers();
    }
}