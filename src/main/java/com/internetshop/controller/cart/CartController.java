package com.internetshop.controller.cart;

import com.internetshop.controller.Controller;
import com.internetshop.dao.CartRepository;
import com.internetshop.model.Product;
import com.internetshop.model.User;
import com.internetshop.services.CartService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class CartController implements Controller {
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    private final CartService cartService;

    public CartController() {
        this.cartService = new CartService(new CartRepository());
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            List<Product> cartProducts = cartService.getCartProducts(user.getId());
            BigDecimal totalPrice = cartService.calculateProductsPrice(cartProducts);
            request.setAttribute("products", cartProducts);
            request.setAttribute("totalPrice", totalPrice);
            request.getRequestDispatcher("/cart.jsp").forward(request, response);
        } catch (SQLException e) {
            logger.error("Database error retrieving cart", e);
            throw new ServletException(e);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }
}