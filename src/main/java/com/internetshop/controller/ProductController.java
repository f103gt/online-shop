package com.internetshop.controller;

import com.internetshop.dao.ProductRepository;
import com.internetshop.model.Product;
import com.internetshop.model.Role;
import com.internetshop.model.User;
import com.internetshop.services.ProductService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ProductController extends HttpServlet {
    private ProductService productService;

    @Override
    public void init() {
        this.productService = new ProductService(
                new ProductRepository()
        );
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        String path = request.getServletPath();

        try {
            if (path.equals("/add-product")) {
                if (user == null || user.getRole() != Role.ADMIN) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                request.getRequestDispatcher("/add-product.jsp").forward(request, response);
            }
            else if (path.equals("/edit-product")) {
                if (user == null || user.getRole() != Role.ADMIN) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                int productId = Integer.parseInt(request.getParameter("id"));
                Product product = productService.getProductById(productId);
                request.setAttribute("product", product);
                request.getRequestDispatcher("/edit-product.jsp").forward(request, response);
            }
            else {
                // Regular products listing
                List<Product> products = productService.getAllProducts();
                request.setAttribute("products", products);
                request.getRequestDispatcher("products.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getRole() != Role.ADMIN) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required");
            return;
        }

        String path = request.getServletPath();
        try {
            if (path.equals("/save-product")) {
                Product product = new Product(
                        0,
                        request.getParameter("name"),
                        request.getParameter("description"),
                        new BigDecimal(request.getParameter("price")),
                        Integer.parseInt(request.getParameter("stock"))
                );

                String idParam = request.getParameter("id");
                if (idParam != null && !idParam.isEmpty()) {
                    // Update existing product
                    product.setId(Integer.parseInt(idParam));
                    productService.updateProduct(product);
                } else {
                    // Create new product
                    productService.addProduct(product);
                }
                response.sendRedirect("products");
            }
            else if (path.equals("/delete-product")) {
                int productId = Integer.parseInt(request.getParameter("productId"));
                productService.deleteProduct(productId);
                response.sendRedirect("products");
            }
        } catch (SQLException | NumberFormatException e) {
            throw new ServletException(e);
        }
    }
}