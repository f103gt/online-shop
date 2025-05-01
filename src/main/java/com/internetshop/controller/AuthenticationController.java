package com.internetshop.controller;

import com.internetshop.dao.UserRepository;
import com.internetshop.model.Role;
import com.internetshop.model.User;
import com.internetshop.services.UserService;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class AuthenticationController extends HttpServlet {
    private static final int MAX_SESSION_PERIOD = 7 * 24 * 60 * 60; // 1 week

    private UserService userService;

    @Override
    public void init() {
        this.userService = new UserService(new UserRepository());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getRequestURI().substring(request.getContextPath().length());

        switch (path) {
            case "/":
                request.getRequestDispatcher("home.jsp").forward(request, response);
                break;
            case "/login":
                request.getRequestDispatcher("login.jsp").forward(request, response);
                break;
            case "/register":
                request.getRequestDispatcher("register.jsp").forward(request, response);
                break;
            case "/logout":
                handleLogout(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String path = request.getRequestURI().substring(request.getContextPath().length());

        if (path.equals("/login")) {
            handleLogin(request, response);
        } else if (path.equals("/register")) {
            handleRegistration(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            Optional<User> authenticatedUser = userService.authenticate(username, password);

            if (authenticatedUser.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/register");
                return;
            }

            User user = authenticatedUser.get();
            setupUserSession(request, response, user);

            if (user.getRole() == Role.ADMIN) {
                response.sendRedirect(request.getContextPath() + "/debtors");
            } else {
                response.sendRedirect(request.getContextPath() + "/products");
            }

        } catch (SQLException e) {
            throw new ServletException("Database error during authentication", e);
        }
    }

    private void handleRegistration(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String address = request.getParameter("address");

        try {
            // Check if user already exists using findByUsername
            Optional<User> existingUser = userService.findByUsername(username);
            if (existingUser.isPresent()) {
                response.sendRedirect(request.getContextPath() + "/register?error=exists");
                return;
            }

            // Create and register new user
            User newUser = new User(0, username, password, Role.CUSTOMER, address, email);
            User registeredUser = userService.register(newUser);

            // Auto-login after registration
            setupUserSession(request, response, registeredUser);
            response.sendRedirect(request.getContextPath() + "/");

        } catch (SQLException e) {
            response.sendRedirect(request.getContextPath() + "/register?error=db");
        }
    }

    private void setupUserSession(HttpServletRequest request, HttpServletResponse response, User user)
            throws SQLException {
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }

        HttpSession newSession = request.getSession(true);
        newSession.setAttribute("user", user);
        newSession.setAttribute("role", user.getRole());

        // Set cookies
        Cookie userCookie = new Cookie("userAuth", user.getUsername());
        userCookie.setMaxAge(MAX_SESSION_PERIOD);
        userCookie.setPath("/");
        userCookie.setHttpOnly(true);
        response.addCookie(userCookie);

        Cookie roleCookie = new Cookie("userRole", user.getRole().name());
        roleCookie.setMaxAge(MAX_SESSION_PERIOD);
        roleCookie.setPath("/");
        response.addCookie(roleCookie);
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // Invalidate the session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Clear cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("userAuth") || cookie.getName().equals("userRole")) {
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }

        // Redirect to homepage
        response.sendRedirect(request.getContextPath() + "/");
    }
}
// Alt + Shift + ,  or Alt + Shift + .
