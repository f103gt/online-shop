package com.internetshop.controller;

import com.internetshop.dao.BlackListRepository;
import com.internetshop.model.User;
import com.internetshop.services.BlackListService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class BlackListController implements Controller {
    private static final Logger logger = LoggerFactory.getLogger(BlackListController.class);
    private BlackListService blackListService;

    @Override
    public void init() {
        logger.info("Initializing BlackListController");
        this.blackListService = new BlackListService(new BlackListRepository());
        logger.debug("BlackListService initialized with BlackListRepository");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("Processing GET request for blacklist");

        try {
            List<User> blacklistedUsers = blackListService.getBlacklistedUsers();
            logger.info("Retrieved {} blacklisted users", blacklistedUsers.size());

            request.setAttribute("blacklistedUsers", blacklistedUsers);
            request.getRequestDispatcher("/blacklist.jsp").forward(request, response);
            logger.debug("Forwarded to blacklist.jsp");
        } catch (SQLException e) {
            logger.error("Database error while retrieving blacklisted users", e);
            throw new ServletException("Database error occurred", e);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        logger.debug("Processing POST request for blacklist");
        String action = request.getParameter("action");
        logger.debug("Action parameter: {}", action);

        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            logger.info("Adding user {} to blacklist for order {}", userId, orderId);

            blackListService.addToBlackList(userId, orderId);
            logger.info("Successfully added user {} to blacklist", userId);

            response.sendRedirect(request.getContextPath() + "/black-list");
            logger.debug("Redirected to black-list");

        } catch (SQLException e) {
            logger.error("Database error while processing blacklist action: {}", action, e);
            throw new ServletException("Database error occurred", e);
        } catch (NumberFormatException e) {
            logger.error("Invalid ID format in request parameters", e);
            throw new ServletException("Invalid user ID format", e);
        } catch (Exception e) {
            logger.error("Unexpected error processing blacklist request", e);
            throw new ServletException("An unexpected error occurred", e);
        }
    }
}