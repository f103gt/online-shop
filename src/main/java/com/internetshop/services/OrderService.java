package com.internetshop.services;

import com.internetshop.dao.OrderRepository;
import com.internetshop.model.Order;
import com.internetshop.model.Product;
import com.internetshop.model.User;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
    }

    public Map<User, List<Order>> getUsersWithUnpaidOrders() throws SQLException {
        return orderRepository.getUsersWithUnpaidOrders();
    }

    public void processOrder(Order order) throws SQLException {
        try {
            List<Product> cartProducts = cartService.getCartProducts(order.getUserId());
            BigDecimal totalAmount = cartService.calculateProductsPrice(cartProducts);

            order.setTotalAmount(totalAmount);
            orderRepository.insert(order);

            cartService.clearCart(order.getUserId());
        } catch (SQLException e) {
            throw new SQLException("Failed to process order: " + e.getMessage(), e);
        }
    }
}