package com.internetshop.services;

import com.internetshop.dao.OrderRepository;
import com.internetshop.model.Order;
import com.internetshop.model.OrderStatus;
import com.internetshop.model.Role;
import com.internetshop.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderService {
    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public List<User> getUsersWithPendingOrders() throws SQLException {
        return repository.findUsersByOrderStatus(OrderStatus.PENDING);
    }

    public Map<User, List<Order>> getUsersWithUnpaidOrders() throws SQLException {
        return repository.getUsersWithUnpaidOrders();
    }
}