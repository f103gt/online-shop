package com.internetshop.dao;

import com.internetshop.model.Cart;
import com.internetshop.model.Product;
import java.sql.SQLException;
import java.util.List;

public interface CartRepositoryInterface extends Repository<Cart> {
    void addProductToCart(int userId, int productId) throws SQLException;
    void removeProductFromCart(int userId, int productId) throws SQLException;
    List<Product> getProductsByUserId(int userId) throws SQLException;
}
