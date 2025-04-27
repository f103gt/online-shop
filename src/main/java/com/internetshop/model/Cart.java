package com.internetshop.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private final int userId;
    private final List<Product> products = new ArrayList<>();

    public Cart(int userId) {
        this.userId = userId;
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public void removeProduct(int productId) {
        products.removeIf(p -> p.getId() == productId);
    }

    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }

    public double getTotalPrice() {
        return products.stream()
                .mapToDouble(p -> p.getPrice().doubleValue())
                .sum();
    }

    public int getUserId() {
        return userId;
    }

    public void clear() {
        products.clear();
    }
}