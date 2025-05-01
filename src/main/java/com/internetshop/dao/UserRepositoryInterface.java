package com.internetshop.dao;

import com.internetshop.model.User;

import java.sql.SQLException;
import java.util.Optional;

public interface UserRepositoryInterface extends Repository<User>{
    Optional<User> findByUsername(String username) throws SQLException;
}
