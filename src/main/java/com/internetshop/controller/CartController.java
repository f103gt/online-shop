package com.internetshop.controller;

import com.internetshop.dao.CartRepository;
import com.internetshop.model.Cart;
import com.internetshop.model.Product;
import com.internetshop.model.User;
import com.internetshop.services.CartService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CartController extends HttpServlet {
    private CartService cartService;

    @Override
    public void init() {
        Connection conn = (Connection) getServletContext().getAttribute("DBConnection");
        this.cartService = new CartService(new CartRepository(conn));
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            // Get or create cart for user
            Cart cart = cartService.getOrCreateCart(user.getId());

            // Get products specific to this cart
            List<Product> cartProducts = cartService.getCartProducts(user.getId());

            // Set attributes for JSP
            request.setAttribute("products", cartProducts);
            request.setAttribute("cart", cart);

            // Forward to cart page
            RequestDispatcher dispatcher = request.getRequestDispatcher("cart.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Database error occurred", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            String action = request.getParameter("action");
            int productId = Integer.parseInt(request.getParameter("productId"));

            if ("add".equals(action)) {
                // In a real app, you'd get the Product from ProductService
                Product product = new Product(productId, "", "",
                        BigDecimal.ZERO, 0);
                cartService.addProductToCart(user.getId(), product);
            } else if ("remove".equals(action)) {
                cartService.removeProductFromCart(user.getId(), productId);
            }

            response.sendRedirect(request.getContextPath() + "/cart");

        } catch (SQLException | NumberFormatException e) {
            throw new ServletException("Error processing cart action", e);
        }
    }
}