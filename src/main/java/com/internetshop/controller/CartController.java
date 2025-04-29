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
import java.sql.SQLException;
import java.util.List;

public class CartController extends HttpServlet {
    private CartService cartService;

    @Override
    public void init() {
        this.cartService = new CartService(new CartRepository());
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

            // Get cart products
            List<Product> cartProducts = cartService.getCartProducts(user.getId());
            BigDecimal totalPrice = cartService.calculateProductsPrice(cartProducts);

            // Set attributes for JSP
            request.setAttribute("products", cartProducts);
            request.setAttribute("totalPrice", totalPrice);

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

            String path = request.getServletPath();
            int productId = Integer.parseInt(request.getParameter("productId"));

            switch (path) {
                case "/add-to-cart":
                    cartService.addProductToCart(user.getId(), productId);
                    session.setAttribute("successMessage", "Product added to cart");
                    response.sendRedirect(request.getContextPath() + "/products");
                    break;
                case "/remove-from-cart":
                    cartService.removeProductFromCart(user.getId(), productId);
                    session.setAttribute("successMessage", "Product removed from cart");
                    response.sendRedirect(request.getContextPath() + "/cart");
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/cart");
                    break;
            }

        } catch (SQLException | NumberFormatException e) {
            throw new ServletException("Error processing cart action", e);
        }
    }
}