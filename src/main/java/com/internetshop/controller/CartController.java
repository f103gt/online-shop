package com.internetshop.controller;

import com.internetshop.dao.CartRepository;
import com.internetshop.model.Cart;
import com.internetshop.model.Product;
import com.internetshop.model.User;
import com.internetshop.services.CartService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class CartController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    private CartService cartService;

    @Override
    public void init() {
        logger.info("Initializing CartController");
        this.cartService = new CartService(new CartRepository());
        logger.debug("CartService initialized with CartRepository");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("Processing GET request for cart page");

        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            if (user == null) {
                logger.warn("Unauthorized access attempt to cart - redirecting to login");
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            logger.debug("Retrieving cart for user ID: {}", user.getId());
            List<Product> cartProducts = cartService.getCartProducts(user.getId());
            BigDecimal totalPrice = cartService.calculateProductsPrice(cartProducts);

            logger.info("Retrieved {} products in cart for user {} (Total: {})",
                    cartProducts.size(), user.getId(), totalPrice);

            request.setAttribute("products", cartProducts);
            request.setAttribute("totalPrice", totalPrice);

            RequestDispatcher dispatcher = request.getRequestDispatcher("cart.jsp");
            dispatcher.forward(request, response);
            logger.debug("Successfully forwarded to cart.jsp");

        } catch (SQLException e) {
            logger.error("Database error while retrieving cart contents", e);
            throw new ServletException("Database error occurred", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("Processing POST request for cart operation");

        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            if (user == null) {
                logger.warn("Unauthorized cart modification attempt - redirecting to login");
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            String path = request.getServletPath();
            int productId = Integer.parseInt(request.getParameter("productId"));
            logger.debug("Processing action: {} for product ID: {} (User ID: {})",
                    path, productId, user.getId());

            switch (path) {
                case "/add-to-cart":
                    logger.info("Adding product {} to cart for user {}", productId, user.getId());
                    cartService.addProductToCart(user.getId(), productId);
                    session.setAttribute("successMessage", "Product added to cart");
                    logger.debug("Product {} successfully added to cart for user {}", productId, user.getId());
                    response.sendRedirect(request.getContextPath() + "/products");
                    break;
                case "/remove-from-cart":
                    logger.info("Removing product {} from cart for user {}", productId, user.getId());
                    cartService.removeProductFromCart(user.getId(), productId);
                    session.setAttribute("successMessage", "Product removed from cart");
                    logger.debug("Product {} successfully removed from cart for user {}", productId, user.getId());
                    response.sendRedirect(request.getContextPath() + "/cart");
                    break;
                default:
                    logger.warn("Unknown cart operation path: {}", path);
                    response.sendRedirect(request.getContextPath() + "/cart");
                    break;
            }

        } catch (SQLException e) {
            logger.error("Database error during cart operation", e);
            throw new ServletException("Error processing cart action", e);
        } catch (NumberFormatException e) {
            logger.error("Invalid product ID format in request", e);
            throw new ServletException("Error processing cart action", e);
        } catch (Exception e) {
            logger.error("Unexpected error during cart operation", e);
            throw new ServletException("Error processing cart action", e);
        }
    }
}