package com.internetshop.controller;

import com.internetshop.dao.BlackListRepository;
import com.internetshop.model.User;
import com.internetshop.services.BlackListService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BlackListController extends HttpServlet {
    private BlackListService blackListService;

    @Override
    public void init() {
        Connection conn = (Connection) getServletContext().getAttribute("DBConnection");
        this.blackListService = new BlackListService(new BlackListRepository(conn));

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<User> blacklistedUsers = blackListService.getBlacklistedUsers();
            request.setAttribute("blacklistedUsers", blacklistedUsers);
            request.getRequestDispatcher("/blacklist.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Database error occurred", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String action = request.getParameter("action");

        try {
            if ("add".equals(action)) {
                int userId = Integer.parseInt(request.getParameter("userId"));
                int orderId = Integer.parseInt(request.getParameter("orderId"));
                blackListService.addToBlackList(userId, orderId);
            } else if ("remove".equals(action)) {
                int userId = Integer.parseInt(request.getParameter("userId"));
                blackListService.removeFromBlackList(userId);
            }

            response.sendRedirect(request.getContextPath() + "/black-list");
        } catch (SQLException e) {
            throw new ServletException("Database error occurred", e);
        } catch (NumberFormatException e) {
            throw new ServletException("Invalid user ID format", e);
        }
    }
}