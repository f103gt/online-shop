package com.internetshop.services;

import com.internetshop.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest {
    private ProductService service;
    private MockProductRepository mockRepository;

    @BeforeEach
    void setUp() {
        mockRepository = new MockProductRepository();
        service = new ProductService(mockRepository);
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() throws SQLException {
        // When
        List<Product> products = service.getAllProducts();

        // Then
        assertEquals(3, products.size());
        assertEquals("Laptop", products.get(0).getName());
        assertEquals("Phone", products.get(1).getName());
    }

    @Test
    void getProductById_ShouldReturnCorrectProduct() throws SQLException {
        // When
        Product product = service.getProductById(1);

        // Then
        assertNotNull(product);
        assertEquals("Laptop", product.getName());
        assertEquals(0, new BigDecimal("999.99").compareTo(product.getPrice()));
    }

    @Test
    void getProductById_ShouldThrowWhenNotFound() {
        assertThrows(SQLException.class, () -> service.getProductById(999));
    }

    @Test
    void addProduct_ShouldCreateNewProduct() throws SQLException {
        // Given
        int initialCount = mockRepository.getProductCount();
        Product newProduct = new Product(0, "Tablet", "Portable device",
                new BigDecimal("299.99"), 15);

        // When
        service.addProduct(newProduct);

        // Then
        assertEquals(initialCount + 1, mockRepository.getProductCount());
        Product savedProduct = mockRepository.getById(newProduct.getId());
        assertEquals("Tablet", savedProduct.getName());
        assertTrue(savedProduct.getId() > 0);
    }

    @Test
    void updateProduct_ShouldModifyExistingProduct() throws SQLException {
        // Given
        Product product = mockRepository.getById(1);
        product.setPrice(new BigDecimal("899.99"));

        // When
        service.updateProduct(product);

        // Then
        Product updated = mockRepository.getById(1);
        assertEquals(0, new BigDecimal("899.99").compareTo(updated.getPrice()));
    }

    @Test
    void deleteProduct_ShouldRemoveProduct() throws SQLException {
        // Given
        int initialCount = mockRepository.getProductCount();

        // When
        service.deleteProduct(1);

        // Then
        assertEquals(initialCount - 1, mockRepository.getProductCount());
        assertThrows(SQLException.class, () -> mockRepository.getById(1));
    }
}