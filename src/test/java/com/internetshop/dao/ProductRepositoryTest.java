package com.internetshop.dao;

import com.internetshop.configurations.DatabaseConfig;
import com.internetshop.model.Product;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductRepositoryTest {
    private static ProductRepository productRepository;

    @BeforeAll
    static void setUpBeforeAll() throws SQLException {
        DatabaseConfig.createTables();
        productRepository = new ProductRepository();
    }

    @AfterAll
    static void tearDownAfterAll() throws SQLException {
        DatabaseConfig.dropTables();
    }

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseConfig.dropTables();
        DatabaseConfig.createTables();
    }

    private Product createTestProduct(String name, String description, BigDecimal price, int stock) throws SQLException {
        Product product = new Product(1, name, description, price, stock);
        productRepository.insert(product);
        return product;
    }

    @Test
    void testInsertAndGetById() throws SQLException {
        // Create test product
        Product product = createTestProduct("Test Product", "Test Description",
                new BigDecimal("19.99"), 10);

        // Retrieve and verify
        Product retrieved = productRepository.getById(product.getId());
        assertNotNull(retrieved);
        assertEquals(product.getId(), retrieved.getId());
        assertEquals("Test Product", retrieved.getName());
        assertEquals("Test Description", retrieved.getDescription());
        assertEquals(0, new BigDecimal("19.99").compareTo(retrieved.getPrice()));
        assertEquals(10, retrieved.getStock());
    }

    @Test
    void testUpdate() throws SQLException {
        // Create initial product
        Product product = createTestProduct("Old Name", "Old Description",
                new BigDecimal("9.99"), 5);

        // Update product
        product.setName("Updated Name");
        product.setDescription("Updated Description");
        product.setPrice(new BigDecimal("14.99"));
        product.setStock(20);
        productRepository.update(product);

        // Retrieve and verify updates
        Product updated = productRepository.getById(product.getId());
        assertEquals("Updated Name", updated.getName());
        assertEquals("Updated Description", updated.getDescription());
        assertEquals(0, new BigDecimal("14.99").compareTo(updated.getPrice()));
        assertEquals(20, updated.getStock());
    }

    @Test
    void testDelete() throws SQLException {
        // Create test product
        Product product = createTestProduct("To Delete", "Will be deleted",
                new BigDecimal("5.99"), 3);

        // Verify exists before deletion
        assertNotNull(productRepository.getById(product.getId()));

        // Delete product
        productRepository.delete(product.getId());

        // Verify deletion
        assertNull(productRepository.getById(product.getId()));
    }

    @Test
    void testGetAll() throws SQLException {
        // Create multiple products
        Product product1 = createTestProduct("Product 1", "Desc 1",
                new BigDecimal("10.00"), 5);
        Product product2 = createTestProduct("Product 2", "Desc 2",
                new BigDecimal("20.00"), 10);

        // Get all and verify
        List<Product> allProducts = productRepository.getAll();
        assertEquals(2, allProducts.size());
        assertTrue(allProducts.stream().anyMatch(p -> p.getName().equals("Product 1")));
        assertTrue(allProducts.stream().anyMatch(p -> p.getName().equals("Product 2")));
    }

    @Test
    void testGetByIdNonExistent() throws SQLException {
        assertNull(productRepository.getById(999));
    }

    @Test
    void testGetAllEmpty() throws SQLException {
        List<Product> allProducts = productRepository.getAll();
        assertTrue(allProducts.isEmpty());
    }

    @Test
    void testInsertWithNullDescription() throws SQLException {
        Product product = new Product(1, "No Description", null,
                new BigDecimal("15.99"), 7);
        assertDoesNotThrow(() -> productRepository.insert(product));

        Product retrieved = productRepository.getById(product.getId());
        assertNull(retrieved.getDescription());
    }

    @Test
    void testUpdateStockToZero() throws SQLException {
        Product product = createTestProduct("Limited Stock", "Almost gone",
                new BigDecimal("25.99"), 1);

        // Update stock to 0
        product.setStock(0);
        productRepository.update(product);

        Product updated = productRepository.getById(product.getId());
        assertEquals(0, updated.getStock());
    }

    @Test
    void testHighPrecisionPrice() throws SQLException {
        BigDecimal precisePrice = new BigDecimal("123.456789");
        Product product = createTestProduct("Precise Product", "High precision price",
                precisePrice, 3);

        Product retrieved = productRepository.getById(product.getId());
        assertEquals(0, precisePrice.compareTo(retrieved.getPrice()));
    }
}