package com.internetshop.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private final int userId;
    private final List<Integer> productIds = new ArrayList<>();
    public Cart(int userId) {
        this.userId = userId;
    }

    public void addProductId(int productId) {
        if (!productIds.contains(productId)) {
            productIds.add(productId);
        }
    }

    public void removeProductId(int productId) {
        productIds.remove(Integer.valueOf(productId));
    }

    public List<Integer> getProductIds() {
        return new ArrayList<>(productIds);
    }

    public int getUserId() {
        return userId;
    }

    public void clear() {
        productIds.clear();
    }
}