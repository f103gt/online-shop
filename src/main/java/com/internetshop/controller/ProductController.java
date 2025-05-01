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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ProductController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private ProductService productService;

    @Override
    public void init() {
        logger.info("Initializing ProductController");
        this.productService = new ProductService(new ProductRepository());
        logger.debug("ProductService initialized with ProductRepository");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("Processing GET request for product management");
        User user = (User) request.getSession().getAttribute("user");
        String path = request.getServletPath();

        try {
            if (path.equals("/add-product")) {
                if (user == null || user.getRole() != Role.ADMIN) {
                    logger.warn("Unauthorized access attempt to add product - user: {}, role: {}",
                            user != null ? user.getId() : "null",
                            user != null ? user.getRole() : "null");
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                logger.debug("Forwarding to add product page for admin user {}", user.getId());
                request.getRequestDispatcher("/add-product.jsp").forward(request, response);
            }
            else if (path.equals("/edit-product")) {
                if (user == null || user.getRole() != Role.ADMIN) {
                    logger.warn("Unauthorized access attempt to edit product - user: {}, role: {}",
                            user != null ? user.getId() : "null",
                            user != null ? user.getRole() : "null");
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                int productId = Integer.parseInt(request.getParameter("id"));
                logger.debug("Loading product {} for editing by admin {}", productId, user.getId());
                Product product = productService.getProductById(productId);
                request.setAttribute("product", product);
                request.getRequestDispatcher("/edit-product.jsp").forward(request, response);
            }
            else {
                logger.debug("Loading products listing page");
                List<Product> products = productService.getAllProducts();
                logger.info("Retrieved {} products for display", products.size());
                request.setAttribute("products", products);
                request.getRequestDispatcher("products.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            logger.error("Database error while processing product GET request", e);
            throw new ServletException(e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("Processing POST request for product management");
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getRole() != Role.ADMIN) {
            logger.warn("Unauthorized product modification attempt - user: {}, role: {}",
                    user != null ? user.getId() : "null",
                    user != null ? user.getRole() : "null");
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
                    product.setId(Integer.parseInt(idParam));
                    logger.info("Admin {} updating product ID {}", user.getId(), product.getId());
                    productService.updateProduct(product);
                    logger.debug("Product {} successfully updated", product.getId());
                } else {
                    logger.info("Admin {} creating new product", user.getId());
                    productService.addProduct(product);
                    logger.debug("New product created with ID {}", product.getId());
                }
                response.sendRedirect("products");
            }
            else if (path.equals("/delete-product")) {
                int productId = Integer.parseInt(request.getParameter("productId"));
                logger.info("Admin {} deleting product ID {}", user.getId(), productId);
                productService.deleteProduct(productId);
                logger.debug("Product {} successfully deleted", productId);
                response.sendRedirect("products");
            }
        } catch (SQLException e) {
            logger.error("Database error during product operation", e);
            throw new ServletException(e);
        } catch (NumberFormatException e) {
            logger.error("Invalid number format in product parameters", e);
            throw new ServletException(e);
        }
    }
}