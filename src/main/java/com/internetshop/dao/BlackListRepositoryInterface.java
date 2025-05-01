package com.internetshop.dao;

import com.internetshop.model.BlackList;
import com.internetshop.model.User;

import java.sql.SQLException;
import java.util.List;

public interface BlackListRepositoryInterface extends Repository<BlackList>{
    List<User> getBlacklistedUsers() throws SQLException;
}
