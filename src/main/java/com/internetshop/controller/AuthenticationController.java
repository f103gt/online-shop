package com.internetshop.controller;

import com.internetshop.dao.CartRepository;
import com.internetshop.dao.UserRepository;
import com.internetshop.model.Cart;
import com.internetshop.model.Role;
import com.internetshop.model.User;
import com.internetshop.services.CartService;
import com.internetshop.services.UserService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class AuthenticationController extends HttpServlet {
    private static final int MAX_SESSION_PERIOD = 7 * 24 * 60 * 60; // 1 week

    private UserService userService;
    private CartService cartService;

    @Override
    public void init() {
        Connection conn = (Connection) getServletContext().getAttribute("DBConnection");
        this.userService = new UserService(new UserRepository(conn));
        this.cartService = new CartService(new CartRepository(conn));
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
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        String path = request.getRequestURI().substring(request.getContextPath().length());
//
//        if (path.equals("/login")) {
//            RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
//            dispatcher.forward(request, response);
//        } else if (path.equals("/register")) {
//            RequestDispatcher dispatcher = request.getRequestDispatcher("register.jsp");
//            dispatcher.forward(request, response);
//        } else {
//            response.sendError(HttpServletResponse.SC_NOT_FOUND);
//        }
//    }

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
                response.sendRedirect(request.getContextPath() + "/black-list");
            } else {
                response.sendRedirect(request.getContextPath() + "/");
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

        // Get or create cart for user
        Cart userCart = cartService.getOrCreateCart(user.getId());
        newSession.setAttribute("cart", userCart);

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
}
// Alt + Shift + ,  or Alt + Shift + .
