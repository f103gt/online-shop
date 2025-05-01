package com.internetshop.services;

import com.internetshop.dao.OrderRepository;
import com.internetshop.model.Order;
import com.internetshop.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class OrderService {
    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    // accumulate orders?
    public Map<User, List<Order>> getUsersWithUnpaidOrders() throws SQLException {
        return repository.getUsersWithUnpaidOrders();
    }

    public void processOrder(Order order) throws SQLException {
        repository.insert(order);
    }
}