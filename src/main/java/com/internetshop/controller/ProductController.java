package com.internetshop.controller;

import com.internetshop.dao.ProductRepository;
import com.internetshop.model.Product;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

// TODO: add cookies and sessions
public class ProductController extends HttpServlet {
    private ProductRepository productRepo;

    public void init() {
        productRepo = new ProductRepository();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        try {
            List<Product> products = productRepo.getAll();
            request.setAttribute("products", products);
            RequestDispatcher dispatcher = request.getRequestDispatcher("products.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException | IOException e) {
            throw new ServletException(e);
        }
    }
}

