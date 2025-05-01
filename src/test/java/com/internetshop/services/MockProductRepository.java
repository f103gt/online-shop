package com.internetshop.services;

import com.internetshop.dao.Repository;
import com.internetshop.model.Product;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockProductRepository implements Repository<Product> {
    private final Map<Integer, Product> products = new HashMap<>();
    private int nextId = 1;

    public MockProductRepository() {
        // Initialize with test data
        saveTestProduct("Laptop", "High performance laptop", "999.99", 10);
        saveTestProduct("Phone", "Latest smartphone", "699.99", 20);
        saveTestProduct("Headphones", "Noise cancelling", "199.99", 30);
    }

    private void saveTestProduct(String name, String description, String price, int stock) {
        Product product = new Product(nextId, name, description, new BigDecimal(price), stock);
        products.put(nextId++, product);
    }

    @Override
    public void insert(Product product) throws SQLException {
        if (product.getId() != 0) {
            throw new SQLException("New product should not have ID set");
        }
        product.setId(nextId++);
        products.put(product.getId(), product);
    }

    @Override
    public void update(Product product) throws SQLException {
        if (!products.containsKey(product.getId())) {
            throw new SQLException("Product not found with ID: " + product.getId());
        }
        products.put(product.getId(), product);
    }

    @Override
    public void delete(int id) throws SQLException {
        if (!products.containsKey(id)) {
            throw new SQLException("Product not found with ID: " + id);
        }
        products.remove(id);
    }

    @Override
    public Product getById(int id) throws SQLException {
        Product product = products.get(id);
        if (product == null) {
            throw new SQLException("Product not found with ID: " + id);
        }
        return product;
    }

    @Override
    public List<Product> getAll() throws SQLException {
        return new ArrayList<>(products.values());
    }

    // Helper method for testing
    public int getProductCount() {
        return products.size();
    }
}