package com.internetshop.controller;

import com.internetshop.dao.BlackListRepository;
import com.internetshop.dao.CartRepository;
import com.internetshop.dao.OrderRepository;
import com.internetshop.model.User;
import com.internetshop.model.Order;
import com.internetshop.services.CartService;
import com.internetshop.services.OrderService;
import com.internetshop.services.BlackListService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DebtorsController implements Controller {
    private static final Logger logger = LoggerFactory.getLogger(DebtorsController.class);
    private final OrderService orderService;
    private final BlackListService blackListService;

    public DebtorsController() {
        logger.info("Initializing DebtorsController");
        this.orderService = new OrderService(
                new OrderRepository(),
                new CartService(new CartRepository())
        );
        this.blackListService = new BlackListService(new BlackListRepository());
        logger.debug("Services initialized with their repositories");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("Processing GET request for debtors page");

        try {
            logger.info("Retrieving users with unpaid orders");
            Map<User, List<Order>> debtors = orderService.getUsersWithUnpaidOrders();
            logger.info("Found {} debtors with unpaid orders", debtors.size());

            logger.debug("Retrieving blacklisted users");
            List<User> blacklistedUsers = blackListService.getBlacklistedUsers();
            logger.info("Found {} blacklisted users", blacklistedUsers.size());

            request.setAttribute("debtors", debtors);
            request.setAttribute("blacklistedUsers", blacklistedUsers);

            request.getRequestDispatcher("debtors.jsp").forward(request, response);
            logger.debug("Forwarded to debtors.jsp");

        } catch (SQLException e) {
            logger.error("Database error while retrieving debtors information", e);
            throw new ServletException("Database error occurred", e);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        logger.debug("Processing POST request to add debtor to blacklist");

        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            logger.info("Adding user {} to blacklist for order {}", userId, orderId);

            blackListService.addToBlackList(userId, orderId);
            logger.info("Successfully blacklisted user {} for order {}", userId, orderId);

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"status\":\"success\"}");
            logger.debug("Sent success response for blacklist operation");

        } catch (SQLException e) {
            logger.error("Database error while adding to blacklist", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Database error\"}");
        } catch (NumberFormatException e) {
            logger.error("Invalid ID format in request parameters", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Invalid user or order ID\"}");
        }
    }
}