package com.internetshop.controller.products;

import com.internetshop.controller.Controller;
import com.internetshop.dao.ProductRepository;
import com.internetshop.model.Product;
import com.internetshop.model.Role;
import com.internetshop.model.User;
import com.internetshop.services.ProductService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

public class SaveProductController implements Controller {
    private static final Logger logger = LoggerFactory.getLogger(SaveProductController.class);
    private final ProductService productService;

    public SaveProductController() {
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
            Product product = new Product(
                    0,
                    request.getParameter("name"),
                    request.getParameter("description"),
                    new BigDecimal(request.getParameter("price")),
                    Integer.parseInt(request.getParameter("stock"))
            );

            String idParam = request.getParameter("id");
            if (idParam != null && !idParam.isEmpty()) {
                product.setId(Integer.parseInt(idParam));
                productService.updateProduct(product);
            } else {
                productService.addProduct(product);
            }
            response.sendRedirect("products");
        } catch (SQLException e) {
            logger.error("Database error saving product", e);
            throw new ServletException(e);
        }
    }
}