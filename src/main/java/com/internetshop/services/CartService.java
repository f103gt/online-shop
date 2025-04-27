package com.internetshop.services;

import com.internetshop.dao.CartRepository;
import com.internetshop.model.Cart;
import com.internetshop.model.Product;
import java.sql.SQLException;
import java.util.List;

public class CartService {
    private final CartRepository repository;

    public CartService(CartRepository repository){
        this.repository = repository;
    }

    public List<Product> getCartProducts(int cartId) throws SQLException {
        return repository.getProductsByCartId(cartId);
    }

    public Cart getOrCreateCart(int userId) throws SQLException {
        Cart cart = repository.getById(userId);
        if (cart.getProducts().isEmpty()) {
            try {
                repository.insert(cart);
            } catch (SQLException e) {
                // Cart might already exist, try to update
                repository.update(cart);
            }
        }
        return cart;
    }

    public void addProductToCart(int cartId, Product product) throws SQLException {
        Cart cart = repository.getById(cartId);
        cart.addProduct(product);
        repository.update(cart);
    }

    public void removeProductFromCart(int cartId, int productId) throws SQLException {
        Cart cart = repository.getById(cartId);
        cart.removeProduct(productId);
        repository.update(cart);
    }

    public void clearCart(int cartId) throws SQLException {
        Cart cart = repository.getById(cartId);
        cart.clear();
        repository.update(cart);
    }
}