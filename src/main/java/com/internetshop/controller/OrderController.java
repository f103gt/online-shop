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

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class OrderController extends HttpServlet {
    private OrderService orderService;

    @Override
    public void init() {

        this.orderService = new OrderService(new OrderRepository(), new CartService(new CartRepository()));
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            request.getRequestDispatcher("/checkout.jsp").forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Error processing order", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws
            ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");

            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            String paymentStatus = request.getParameter("paymentStatus");
            OrderStatus status = "PAYED".equalsIgnoreCase(paymentStatus) ? OrderStatus.PAYED : OrderStatus.PENDING;

            Order order = new Order.Builder()
                    .userId(user.getId())
                    .orderDate(LocalDateTime.now())
                    .status(status).build();
            orderService.processOrder(order);

            response.sendRedirect(request.getContextPath() + "/products");

        } catch (SQLException | NumberFormatException e) {
            throw new ServletException("Error processing order", e);
        }
    }
}