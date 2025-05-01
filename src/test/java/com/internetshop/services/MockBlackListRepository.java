package com.internetshop.services;

import com.internetshop.dao.BlackListRepositoryInterface;
import com.internetshop.model.BlackList;
import com.internetshop.model.User;
import com.internetshop.model.Role;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MockBlackListRepository implements BlackListRepositoryInterface {
    private final List<BlackList> blackLists = new ArrayList<>();
    private final List<User> blacklistedUsers = new ArrayList<>();
    private int nextId = 1;

    public MockBlackListRepository() {
        // Initialize with test data
        blackLists.add(new BlackList(1, 101, 1001));
        blackLists.add(new BlackList(2, 102, 1002));

        blacklistedUsers.add(new User(101, "user1", "pass1", Role.CUSTOMER, "user1@test.com", "Address 1"));
        blacklistedUsers.add(new User(102, "user2", "pass2", Role.CUSTOMER, "user2@test.com", "Address 2"));
    }

    @Override
    public void insert(BlackList blacklist) throws SQLException {
        blacklist.setId(nextId++);
        blackLists.add(blacklist);
    }

    @Override
    public void update(BlackList blacklist) throws SQLException {
        Optional<BlackList> existing = blackLists.stream()
                .filter(bl -> bl.getId() == blacklist.getId())
                .findFirst();

        if (existing.isPresent()) {
            int index = blackLists.indexOf(existing.get());
            blackLists.set(index, blacklist);
        } else {
            throw new SQLException("BlackList entry not found with id: " + blacklist.getId());
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        boolean removed = blackLists.removeIf(bl -> bl.getId() == id);
        if (!removed) {
            throw new SQLException("BlackList entry not found with id: " + id);
        }
    }

    @Override
    public BlackList getById(int id) throws SQLException {
        return blackLists.stream()
                .filter(bl -> bl.getId() == id)
                .findFirst()
                .orElseThrow(() -> new SQLException("BlackList entry not found with id: " + id));
    }

    @Override
    public List<BlackList> getAll() throws SQLException {
        return new ArrayList<>(blackLists);
    }

    @Override
    public List<User> getBlacklistedUsers() throws SQLException {
        return new ArrayList<>(blacklistedUsers);
    }

    // Helper method for testing
    public int getBlackListCount() {
        return blackLists.size();
    }
}