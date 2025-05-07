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
import java.sql.SQLException;
import java.util.Optional;

public class RegistrationController implements Controller {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    private final UserService userService;

    public RegistrationController() {
        this.userService = new UserService(new UserRepository());
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("Showing registration page");
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("Processing registration request");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String address = request.getParameter("address");

        try {
            Optional<User> existingUser = userService.findByUsername(username);
            if (existingUser.isPresent()) {
                response.sendRedirect(request.getContextPath() + "/register?error=exists");
                return;
            }

            User newUser = new User(0, username, password, Role.CUSTOMER, address, email);
            User registeredUser = userService.register(newUser);

            HttpSession session = request.getSession(true);
            session.setAttribute("user", registeredUser);
            session.setAttribute("role", registeredUser.getRole());
            response.sendRedirect(request.getContextPath() + "/");

        } catch (SQLException e) {
            logger.error("Database error during registration", e);
            response.sendRedirect(request.getContextPath() + "/register?error=db");
        }
    }
}