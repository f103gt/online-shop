package com.internetshop.controller.cart;

import com.internetshop.controller.Controller;
import com.internetshop.dao.CartRepository;
import com.internetshop.model.User;
import com.internetshop.services.CartService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

public class RemoveFromCartController implements Controller {
    private static final Logger logger = LoggerFactory.getLogger(RemoveFromCartController.class);
    private final CartService cartService;

    public RemoveFromCartController() {
        this.cartService = new CartService(new CartRepository());
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            cartService.removeProductFromCart(user.getId(), productId);
            request.getSession().setAttribute("successMessage", "Product removed from cart");
            response.sendRedirect(request.getContextPath() + "/cart");
        } catch (SQLException e) {
            logger.error("Database error removing from cart", e);
            throw new ServletException(e);
        }
    }
}