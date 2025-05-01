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

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DebtorsController extends HttpServlet {
    private OrderService orderService;
    private BlackListService blackListService;

    @Override
    public void init() {
        this.orderService = new OrderService(
                new OrderRepository(),
                new CartService(new CartRepository())
        );
        this.blackListService = new BlackListService(new BlackListRepository());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Get map of users to their unpaid orders
            Map<User, List<Order>> debtors = orderService.getUsersWithUnpaidOrders();
            request.setAttribute("debtors", debtors);

            // Get current blacklist entries
            List<User> blacklistedUsers = blackListService.getBlacklistedUsers();
            request.setAttribute("blacklistedUsers", blacklistedUsers);

            request.getRequestDispatcher("debtors.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Database error occurred", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            int orderId = Integer.parseInt(request.getParameter("orderId"));

            blackListService.addToBlackList(userId, orderId);

            // Return success response (no redirect)
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"status\":\"success\"}");

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Database error\"}");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Invalid user or order ID\"}");
        }
    }
}