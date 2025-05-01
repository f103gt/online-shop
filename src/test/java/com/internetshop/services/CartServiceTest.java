package com.internetshop.services;

import com.internetshop.model.Cart;
import com.internetshop.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartServiceTest {
    private CartService service;
    private MockCartRepository mockRepository;

    @BeforeEach
    void setUp() {
        mockRepository = new MockCartRepository();
        service = new CartService(mockRepository);
    }
    @Test
    void addProductToCart_ShouldAddProduct() throws SQLException {
        // Given
        int userId = 101;
        int productId = 2;

        // When
        service.addProductToCart(userId, productId);

        // Then
        Cart cart = mockRepository.getById(userId);
        assertTrue(cart.getProductIds().contains(productId));
    }

    @Test
    void removeProductFromCart_ShouldRemoveProduct() throws SQLException {
        // Given
        int userId = 102;
        int productId = 2;

        // When
        service.removeProductFromCart(userId, productId);

        // Then
        Cart cart = mockRepository.getById(userId);
        assertFalse(cart.getProductIds().contains(productId));
    }

    @Test
    void getCartProducts_ShouldReturnProducts() throws SQLException {
        // When
        List<Product> products = service.getCartProducts(102);

        // Then
        assertEquals(2, products.size());
        assertEquals("Product 1", products.get(0).getName());
        assertEquals("Product 2", products.get(1).getName());
    }

    @Test
    void deleteCart_ShouldRemoveCart() throws SQLException {
        // Given
        int userId = 101;
        int initialCount = mockRepository.getCartCount();

        // When
        service.clearCart(userId);

        // Then
        assertEquals(initialCount - 1, mockRepository.getCartCount());
        assertThrows(SQLException.class, () -> mockRepository.getById(userId));
    }
}