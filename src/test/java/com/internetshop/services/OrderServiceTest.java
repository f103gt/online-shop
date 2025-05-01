package com.internetshop.services;

import com.internetshop.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {
    private OrderService service;
    private MockOrderRepository mockOrderRepository;
    private MockCartRepository mockCartRepository;

    @BeforeEach
    void setUp() {
        mockOrderRepository = new MockOrderRepository();
        mockCartRepository = new MockCartRepository();
        CartService cartService = new CartService(mockCartRepository);
        service = new OrderService(mockOrderRepository, cartService);
    }

    @Test
    void getUsersWithUnpaidOrders_ShouldReturnOnlyPendingOrders() throws SQLException {
        // When
        Map<User, List<Order>> result = service.getUsersWithUnpaidOrders();

        // Then
        assertEquals(1, result.size());
        assertTrue(result.values().stream()
                .flatMap(List::stream)
                .allMatch(order -> order.getStatus() == OrderStatus.PENDING));
    }

    @Test
    void processOrder_ShouldCreateOrderAndClearCart() throws SQLException {
        // Given
        int userId = 101; // Using existing test user from MockOrderRepository
        Order newOrder = new Order.Builder()
                .userId(userId)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .build();

        // Add products to user's cart
        mockCartRepository.addProductToCart(userId, 1);
        mockCartRepository.addProductToCart(userId, 2);

        // When
        service.processOrder(newOrder);

        // Then
        // Verify order was created with correct total
        assertEquals(3, mockOrderRepository.getOrderCount());
        Order savedOrder = mockOrderRepository.getById(newOrder.getId());
        assertEquals(0, new BigDecimal("31.98").compareTo(savedOrder.getTotalAmount()));

        // Verify cart was cleared
        assertTrue(mockCartRepository.getProductsByUserId(userId).isEmpty());
    }

    @Test
    void processOrder_ShouldCalculateCorrectTotal() throws SQLException {
        // Given
        int userId = 101;
        Order newOrder = new Order.Builder()
                .userId(userId)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .build();

        // Add specific products to cart
        mockCartRepository.addProductToCart(userId, 1); // $10.99
        mockCartRepository.addProductToCart(userId, 2); // $20.99

        // When
        service.processOrder(newOrder);

        // Then
        Order savedOrder = mockOrderRepository.getById(newOrder.getId());
        BigDecimal expectedTotal = new BigDecimal("10.99").add(new BigDecimal("20.99"));
        assertEquals(0, expectedTotal.compareTo(savedOrder.getTotalAmount()));
    }
}