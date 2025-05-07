package com.internetshop.controller.products;

import com.internetshop.controller.Controller;
import com.internetshop.dao.ProductRepository;
import com.internetshop.model.Role;
import com.internetshop.model.User;
import com.internetshop.services.ProductService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

public class DeleteProductController implements Controller {
    private static final Logger logger = LoggerFactory.getLogger(DeleteProductController.class);
    private ProductService productService;

    @Override
    public void init() {
        this.productService = new ProductService(new ProductRepository());
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getRole() != Role.ADMIN) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            productService.deleteProduct(productId);
            response.sendRedirect("products");
        } catch (SQLException e) {
            logger.error("Database error deleting product", e);
            throw new ServletException(e);
        }
    }
}