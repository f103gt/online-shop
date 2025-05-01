package com.internetshop.dao;

import com.internetshop.configurations.DatabaseConfig;
import com.internetshop.model.*;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartRepositoryTest {
    private static CartRepository cartRepository;
    private static UserRepository userRepository;
    private static ProductRepository productRepository;
    private static int testUserId;
    private static int testProductId1;
    private static int testProductId2;

    @BeforeAll
    static void setUpBeforeAll() throws SQLException {
        DatabaseConfig.createTables();
        cartRepository = new CartRepository();
        userRepository = new UserRepository();
        productRepository = new ProductRepository();

        // Create test user
        User user = new User(1, "testuser", "password", Role.CUSTOMER, "test@example.com", "123 Test St");
        userRepository.insert(user);
        testUserId = user.getId();

        // Create test products
        Product product1 = new Product(1, "Product 1", "Desc 1", new BigDecimal("10.00"), 5);
        Product product2 = new Product(2, "Product 2", "Desc 2", new BigDecimal("20.00"), 10);
        productRepository.insert(product1);
        productRepository.insert(product2);
        testProductId1 = product1.getId();
        testProductId2 = product2.getId();
    }

    @AfterAll
    static void tearDownAfterAll() throws SQLException {
        DatabaseConfig.dropTables();
    }

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseConfig.dropTables();
        DatabaseConfig.createTables();

        // Recreate test data
        User user = new User(1, "testuser", "password", Role.CUSTOMER, "test@example.com", "123 Test St");
        userRepository.insert(user);
        testUserId = user.getId();

        Product product1 = new Product(1, "Product 1", "Desc 1", new BigDecimal("10.00"), 5);
        Product product2 = new Product(2, "Product 2", "Desc 2", new BigDecimal("20.00"), 10);
        productRepository.insert(product1);
        productRepository.insert(product2);
        testProductId1 = product1.getId();
        testProductId2 = product2.getId();
    }

    @Test
    void testInsertAndGetById() throws SQLException {
        Cart cart = new Cart(testUserId);
        cart.addProductId(testProductId1);
        cart.addProductId(testProductId2);
        cartRepository.insert(cart);

        Cart retrieved = cartRepository.getById(testUserId);
        assertEquals(2, retrieved.getProductIds().size());
        assertTrue(retrieved.getProductIds().contains(testProductId1));
        assertTrue(retrieved.getProductIds().contains(testProductId2));
    }

    @Test
    void testUpdate() throws SQLException {
        // Initial cart with one product
        Cart initialCart = new Cart(testUserId);
        initialCart.addProductId(testProductId1);
        cartRepository.insert(initialCart);

        // Update cart with different products
        Cart updatedCart = new Cart(testUserId);
        updatedCart.addProductId(testProductId2);
        cartRepository.update(updatedCart);

        Cart retrieved = cartRepository.getById(testUserId);
        assertEquals(1, retrieved.getProductIds().size());
        assertTrue(retrieved.getProductIds().contains(testProductId2));
    }

    @Test
    void testDelete() throws SQLException {
        Cart cart = new Cart(testUserId);
        cart.addProductId(testProductId1);
        cartRepository.insert(cart);

        assertFalse(cartRepository.getById(testUserId).getProductIds().isEmpty());
        cartRepository.delete(testUserId);
        assertTrue(cartRepository.getById(testUserId).getProductIds().isEmpty());
    }

    @Test
    void testAddProductToCart() throws SQLException {
        cartRepository.addProductToCart(testUserId, testProductId1);

        Cart cart = cartRepository.getById(testUserId);
        assertEquals(1, cart.getProductIds().size());
        assertTrue(cart.getProductIds().contains(testProductId1));
    }

    @Test
    void testRemoveProductFromCart() throws SQLException {
        cartRepository.addProductToCart(testUserId, testProductId1);
        cartRepository.addProductToCart(testUserId, testProductId2);

        cartRepository.removeProductFromCart(testUserId, testProductId1);

        Cart cart = cartRepository.getById(testUserId);
        assertEquals(1, cart.getProductIds().size());
        assertFalse(cart.getProductIds().contains(testProductId1));
        assertTrue(cart.getProductIds().contains(testProductId2));
    }

    @Test
    void testGetProductsByUserId() throws SQLException {
        cartRepository.addProductToCart(testUserId, testProductId1);
        cartRepository.addProductToCart(testUserId, testProductId2);

        List<Product> products = cartRepository.getProductsByUserId(testUserId);
        assertEquals(2, products.size());
        assertTrue(products.stream().anyMatch(p -> p.getId() == testProductId1));
        assertTrue(products.stream().anyMatch(p -> p.getId() == testProductId2));
    }

    @Test
    void testGetEmptyCart() throws SQLException {
        Cart cart = cartRepository.getById(testUserId);
        assertTrue(cart.getProductIds().isEmpty());
    }
}