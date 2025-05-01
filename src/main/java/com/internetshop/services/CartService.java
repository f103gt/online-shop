package com.internetshop.services;

import com.internetshop.dao.CartRepositoryInterface;
import com.internetshop.model.Product;

import java.sql.SQLException;
import java.util.List;
import java.math.BigDecimal;


public class CartService {
    private final CartRepositoryInterface repository;

    public CartService(CartRepositoryInterface repository) {
        this.repository = repository;
    }

    public List<Product> getCartProducts(int userId) throws SQLException {
        return repository.getProductsByUserId(userId);
    }

    public void addProductToCart(int userId, int productId) throws SQLException {
        repository.addProductToCart(userId, productId);
    }

    public void removeProductFromCart(int userId, int productId) throws SQLException {
        repository.removeProductFromCart(userId, productId);
    }

    public void clearCart(int userId) throws SQLException {
        repository.delete(userId);
    }

    public BigDecimal calculateProductsPrice(List<Product> products) {
        return products.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}