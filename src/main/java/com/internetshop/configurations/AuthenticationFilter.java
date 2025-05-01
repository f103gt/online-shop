package com.internetshop.configurations;

import com.internetshop.model.Role;
import com.internetshop.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // Allow public paths
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Check session and cookies
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            if (!checkRememberMeCookie(httpRequest)) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
                return;
            }
        }

        // Get user role from session
        User user = (User) session.getAttribute("user");
        Role userRole = user.getRole();

        // Check role-based access
        if (path.startsWith("/black-list") || path.startsWith("/debtors")) {
            // ADMIN-only paths
            if (userRole != Role.ADMIN) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }
        } else if (path.startsWith("/cart")) {
            // CUSTOMER-only paths
            if (userRole != Role.CUSTOMER) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }
        } else if (path.startsWith("/products")) {
            // Allow both ADMIN (for management) and CUSTOMER (for shopping)
            if (userRole != Role.CUSTOMER && userRole != Role.ADMIN) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return path.equals("/login") || path.equals("/register") || path.equals("/");
    }

    private boolean checkRememberMeCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("sessionToken".equals(cookie.getName())) {
                    // In production, validate token against database
                    return true;
                }
            }
        }
        return false;
    }
}