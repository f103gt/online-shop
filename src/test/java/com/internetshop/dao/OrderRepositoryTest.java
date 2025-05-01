package com.internetshop.dao;

import com.internetshop.configurations.DatabaseConfig;
import com.internetshop.model.*;
import com.internetshop.model.Order;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OrderRepositoryTest {
    private static OrderRepository orderRepository;
    private static UserRepository userRepository;
    private static int testUserId;

    @BeforeAll
    static void setUpBeforeAll() throws SQLException {
        DatabaseConfig.createTables();
        orderRepository = new OrderRepository();
        userRepository = new UserRepository();

        // Create a test user
        User user = new User(1, "testuser", "password", Role.CUSTOMER, "test@example.com", "123 Test St");
        userRepository.insert(user);
        testUserId = user.getId(); // Get the actual generated ID
    }

    @AfterAll
    static void tearDownAfterAll() throws SQLException {
        DatabaseConfig.dropTables();
    }

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseConfig.dropTables();
        DatabaseConfig.createTables();

        // Recreate test user since tables were dropped
        User user = new User(1, "testuser", "password", Role.CUSTOMER, "test@example.com", "123 Test St");
        userRepository.insert(user);
        testUserId = user.getId(); // Get the actual generated ID
    }

    private Order createTestOrder(OrderStatus status) throws SQLException {
        Order order = new Order.Builder()
                .userId(testUserId)
                .orderDate(LocalDateTime.now())
                .totalAmount(new BigDecimal("99.99"))
                .status(status)
                .build();
        orderRepository.insert(order);
        return order;
    }

    @Test
    void testInsertAndGetById() throws SQLException {
        Order order = createTestOrder(OrderStatus.PENDING);

        Order retrieved = orderRepository.getById(order.getId());
        assertNotNull(retrieved, "Retrieved order should not be null");
        assertEquals(order.getId(), retrieved.getId());
        assertEquals(testUserId, retrieved.getUserId());
        assertEquals(1, new BigDecimal("99.99").compareTo(retrieved.getTotalAmount()),
                "Total amount should match");
        assertEquals(OrderStatus.PENDING, retrieved.getStatus());
    }

    @Test
    void testUpdate() throws SQLException {
        Order order = createTestOrder(OrderStatus.PENDING);

        // Update order
        order.setTotalAmount(new BigDecimal("149.99"));
        orderRepository.update(order);

        Order updated = orderRepository.getById(order.getId());
        assertNotNull(updated, "Updated order should not be null");
        assertEquals(0, new BigDecimal("149.99").compareTo(updated.getTotalAmount()),
                "Updated total amount should match");
        assertEquals(order.getUserId(), updated.getUserId());
    }

    @Test
    void testDelete() throws SQLException {
        Order order = createTestOrder(OrderStatus.PAYED);

        // Verify exists before deletion
        assertNotNull(orderRepository.getById(order.getId()), "Order should exist before deletion");

        // Delete the order
        orderRepository.delete(order.getId());

        // Verify deletion
        assertNull(orderRepository.getById(order.getId()), "Order should be null after deletion");
    }

    @Test
    void testGetAll() throws SQLException {
        // Create multiple orders
        Order order1 = createTestOrder(OrderStatus.PENDING);
        Order order2 = createTestOrder(OrderStatus.PAYED);

        // Get all orders
        List<Order> orders = orderRepository.getAll();
        assertEquals(2, orders.size(), "Should retrieve 2 orders");
        assertTrue(orders.stream().anyMatch(o -> o.getId() == order1.getId()),
                "Should contain first order");
        assertTrue(orders.stream().anyMatch(o -> o.getId() == order2.getId()),
                "Should contain second order");
    }

    @Test
    void testGetUsersWithUnpaidOrders() throws SQLException {
        // Create pending order with receival date
        Order pendingOrder = new Order.Builder()
                .userId(testUserId)
                .orderDate(LocalDateTime.now())
                .receivalDate(LocalDateTime.now().plusDays(1))
                .totalAmount(new BigDecimal("50.00"))
                .status(OrderStatus.PENDING)
                .build();
        orderRepository.insert(pendingOrder);

        // Create paid order (should not be included)
        Order paidOrder = new Order.Builder()
                .userId(testUserId)
                .orderDate(LocalDateTime.now())
                .totalAmount(new BigDecimal("30.00"))
                .status(OrderStatus.PAYED)
                .build();
        orderRepository.insert(paidOrder);

        Map<User, List<Order>> debtors = orderRepository.getUsersWithUnpaidOrders();
        assertEquals(1, debtors.size(), "Should have one debtor");
        assertTrue(debtors.containsKey(userRepository.getById(testUserId)),
                "Should contain our test user");
        assertEquals(1, debtors.get(userRepository.getById(testUserId)).size(),
                "User should have one unpaid order");
    }

    @Test
    void testGetByIdNonExistent() throws SQLException {
        assertNull(orderRepository.getById(9999), "Non-existent ID should return null");
    }

    @Test
    void testGetAllEmpty() throws SQLException {
        assertTrue(orderRepository.getAll().isEmpty(), "Empty table should return empty list");
    }
}