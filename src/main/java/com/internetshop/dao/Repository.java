package com.internetshop.dao;

import java.sql.SQLException;
import java.util.List;

public interface Repository<T> {
    void insert(T obj) throws SQLException;
    void update(T obj) throws SQLException;
    void delete(int id) throws SQLException;
    T getById(int id) throws SQLException;
    List<T> getAll() throws SQLException;
}
