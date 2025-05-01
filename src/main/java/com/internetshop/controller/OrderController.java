package com.internetshop.controller;

import com.internetshop.dao.CartRepository;
import com.internetshop.dao.OrderRepository;
import com.internetshop.model.Order;
import com.internetshop.model.OrderStatus;
import com.internetshop.model.User;
import com.internetshop.services.CartService;
import com.internetshop.services.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class OrderController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private OrderService orderService;

    @Override
    public void init() {
        logger.info("Initializing OrderController");
        this.orderService = new OrderService(new OrderRepository(), new CartService(new CartRepository()));
        logger.debug("OrderService initialized with OrderRepository and CartService");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        logger.debug("Processing GET request for order checkout");
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            if (user == null) {
                logger.warn("Unauthorized access attempt to checkout - redirecting to login");
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            logger.debug("Forwarding to checkout page for user ID: {}", user.getId());
            request.getRequestDispatcher("/checkout.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error processing order GET request", e);
            throw new ServletException("Error processing order", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws
            ServletException, IOException {
        logger.debug("Processing POST request for new order");
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            if (user == null) {
                logger.warn("Unauthorized order submission attempt - redirecting to login");
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            String paymentStatus = request.getParameter("paymentStatus");
            OrderStatus status = "PAYED".equalsIgnoreCase(paymentStatus) ? OrderStatus.PAYED : OrderStatus.PENDING;
            logger.info("Processing order for user {} with status: {}", user.getId(), status);

            Order order = new Order.Builder()
                    .userId(user.getId())
                    .orderDate(LocalDateTime.now())
                    .status(status).build();

            logger.debug("Created order object for processing");
            orderService.processOrder(order);
            logger.info("Successfully processed order for user {}", user.getId());

            response.sendRedirect(request.getContextPath() + "/products");
            logger.debug("Redirected to products page after order processing");

        } catch (SQLException | NumberFormatException e) {
            logger.error("Error processing order submission", e);
            throw new ServletException("Error processing order", e);
        }
    }
}