package com.internetshop.services;

import com.internetshop.dao.OrderRepositoryInterface;
import com.internetshop.model.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class MockOrderRepository implements OrderRepositoryInterface {
    private final Map<Integer, Order> orders = new HashMap<>();
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    public MockOrderRepository() {
        // Initialize with test data
        User user1 = new User(1, "user1", "pass1", Role.CUSTOMER,
                "user1@test.com", "Address 1");
        User user2 = new User(2, "user2", "pass2", Role.CUSTOMER,
                "user2@test.com", "Address 2");

        users.put(1, user1);
        users.put(2, user2);

        LocalDateTime now = LocalDateTime.now();

        Order order1 = new Order.Builder()
                .id(nextId++)
                .userId(1)
                .orderDate(now.minusDays(2))
                .totalAmount(new BigDecimal("99.99"))
                .status(OrderStatus.PENDING)
                .build();

        Order order2 = new Order.Builder()
                .id(nextId++)
                .userId(2)
                .orderDate(now.minusDays(1))
                .totalAmount(new BigDecimal("149.99"))
                .status(OrderStatus.PAYED)
                .build();

        orders.put(order1.getId(), order1);
        orders.put(order2.getId(), order2);
    }

    @Override
    public void insert(Order order) throws SQLException {
        if (order.getId() != 0) {
            throw new SQLException("New order should not have ID set");
        }
        order.setId(nextId++);
        orders.put(order.getId(), order);
    }

    @Override
    public void update(Order order) throws SQLException {
        if (!orders.containsKey(order.getId())) {
            throw new SQLException("Order not found with ID: " + order.getId());
        }
        orders.put(order.getId(), order);
    }

    @Override
    public void delete(int id) throws SQLException {
        if (!orders.containsKey(id)) {
            throw new SQLException("Order not found with ID: " + id);
        }
        orders.remove(id);
    }

    @Override
    public Order getById(int id) throws SQLException {
        Order order = orders.get(id);
        if (order == null) {
            throw new SQLException("Order not found with ID: " + id);
        }
        return order;
    }

    @Override
    public List<Order> getAll() throws SQLException {
        return new ArrayList<>(orders.values());
    }

    @Override
    public Map<User, List<Order>> getUsersWithUnpaidOrders() throws SQLException {
        Map<User, List<Order>> result = new HashMap<>();

        for (Order order : orders.values()) {
            if (order.getStatus() == OrderStatus.PENDING) {
                User user = users.get(order.getUserId());
                if (user != null) {
                    result.computeIfAbsent(user, k -> new ArrayList<>()).add(order);
                }
            }
        }

        return result;
    }

    // Helper method for testing
    public int getOrderCount() {
        return orders.size();
    }
}