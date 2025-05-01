package com.internetshop.dao;

import com.internetshop.model.Order;
import com.internetshop.model.User;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface OrderRepositoryInterface extends Repository<Order> {
    Map<User, List<Order>> getUsersWithUnpaidOrders() throws SQLException;
}