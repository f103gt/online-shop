package com.internetshop.services;

import com.internetshop.dao.CartRepositoryInterface;
import com.internetshop.model.Cart;
import com.internetshop.model.Product;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockCartRepository implements CartRepositoryInterface {
    private final Map<Integer, Cart> carts = new HashMap<>();
    private final Map<Integer, Product> products = new HashMap<>();

    public MockCartRepository() {
        // Initialize with test data
        products.put(1, new Product(1, "Product 1", "Description 1",
                new BigDecimal("10.99"), 100));
        products.put(2, new Product(2, "Product 2", "Description 2",
                new BigDecimal("20.99"), 50));

        Cart cart1 = new Cart(101);
        cart1.addProductId(1);
        carts.put(101, cart1);

        Cart cart2 = new Cart(102);
        cart2.addProductId(1);
        cart2.addProductId(2);
        carts.put(102, cart2);
    }

    @Override
    public void insert(Cart cart) throws SQLException {
        if (carts.containsKey(cart.getUserId())) {
            throw new SQLException("Cart already exists for user: " + cart.getUserId());
        }
        carts.put(cart.getUserId(), cart);
    }

    @Override
    public void update(Cart cart) throws SQLException {
        if (!carts.containsKey(cart.getUserId())) {
            throw new SQLException("Cart not found for user: " + cart.getUserId());
        }
        carts.put(cart.getUserId(), cart);
    }

    @Override
    public void delete(int userId) throws SQLException {
        if (!carts.containsKey(userId)) {
            throw new SQLException("Cart not found for user: " + userId);
        }
        carts.remove(userId);
    }

    @Override
    public Cart getById(int userId) throws SQLException {
        Cart cart = carts.get(userId);
        if (cart == null) {
            throw new SQLException("Cart not found for user: " + userId);
        }
        return cart;
    }

    @Override
    public List<Cart> getAll() throws SQLException {
        return new ArrayList<>(carts.values());
    }

    @Override
    public void addProductToCart(int userId, int productId) throws SQLException {
        Cart cart = carts.get(userId);
        if (cart == null) {
            cart = new Cart(userId);
            carts.put(userId, cart);
        }
        if (!products.containsKey(productId)) {
            throw new SQLException("Product not found: " + productId);
        }
        cart.addProductId(productId);
    }

    @Override
    public void removeProductFromCart(int userId, int productId) throws SQLException {
        Cart cart = carts.get(userId);
        if (cart == null) {
            throw new SQLException("Cart not found for user: " + userId);
        }
        cart.removeProductId(productId);
    }

    @Override
    public List<Product> getProductsByUserId(int userId) throws SQLException {
        Cart cart = carts.get(userId);
        if (cart == null) {
            return new ArrayList<>();
        }

        List<Product> result = new ArrayList<>();
        for (Integer productId : cart.getProductIds()) {
            Product product = products.get(productId);
            if (product != null) {
                result.add(product);
            }
        }
        return result;
    }

    // Helper method for testing
    public int getCartCount() {
        return carts.size();
    }
}