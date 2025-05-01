package com.internetshop.services;

import com.internetshop.dao.ProductRepository;
import com.internetshop.dao.Repository;
import com.internetshop.model.Product;
import java.sql.SQLException;
import java.util.List;

public class ProductService {
    private final Repository<Product> productRepository;

    public ProductService(Repository<Product> productRepository) {

        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() throws SQLException {
        return productRepository.getAll();
    }

    public Product getProductById(int id) throws SQLException {
        return productRepository.getById(id);
    }

    public void addProduct(Product product) throws SQLException {
        productRepository.insert(product);
    }

    public void updateProduct(Product product) throws SQLException {
        productRepository.update(product);
    }

    public void deleteProduct(int id) throws SQLException {
        productRepository.delete(id);
    }
}