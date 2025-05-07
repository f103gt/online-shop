package com.internetshop.controller.authentication;

import com.internetshop.controller.Controller;
import com.internetshop.dao.UserRepository;
import com.internetshop.model.Role;
import com.internetshop.model.User;
import com.internetshop.services.UserService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class LoginController implements Controller {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private UserService userService;

    @Override
    public void init() {
        this.userService = new UserService(new UserRepository());
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("Showing login page");
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("Processing login request");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            Optional<User> authenticatedUser = userService.authenticate(username, password);

            if (authenticatedUser.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/register");
                return;
            }

            User user = authenticatedUser.get();
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("role", user.getRole());

            String redirectPath = user.getRole() == Role.ADMIN ? "/debtors" : "/products";
            response.sendRedirect(request.getContextPath() + redirectPath);
        } catch (SQLException e) {
            logger.error("Database error during login", e);
            throw new ServletException("Database error during login", e);
        }
    }
}