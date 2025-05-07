package com.internetshop.controller;

import com.internetshop.controller.authentication.HomeController;
import com.internetshop.controller.authentication.LoginController;
import com.internetshop.controller.authentication.LogoutController;
import com.internetshop.controller.authentication.RegistrationController;
import com.internetshop.controller.cart.AddToCartController;
import com.internetshop.controller.cart.CartController;
import com.internetshop.controller.cart.RemoveFromCartController;
import com.internetshop.controller.products.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FrontController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(FrontController.class);
    private final Map<String, Controller> controllers = new HashMap<>();

    @Override
    public void init() throws ServletException {
        logger.info("Initializing FrontController with route mappings");
        // Authentication routes
        controllers.put("/", new HomeController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegistrationController());
        controllers.put("/logout", new LogoutController());

        // Product routes
        controllers.put("/products", new ProductController());
        controllers.put("/add-product", new AddProductController());
        controllers.put("/edit-product", new EditProductController());
        controllers.put("/save-product", new SaveProductController());
        controllers.put("/delete-product", new DeleteProductController());

        // Cart routes
        controllers.put("/cart", new CartController());
        controllers.put("/add-to-cart", new AddToCartController());
        controllers.put("/remove-from-cart", new RemoveFromCartController());

        // Other routes
        controllers.put("/black-list", new BlackListController());
        controllers.put("/checkout", new OrderController());
        controllers.put("/debtors", new DebtorsController());

        for (Controller controller : controllers.values()) {
            controller.init();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    // Add similar methods for other HTTP verbs if needed (doPut, doDelete, etc.)

    private void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Skip forwarded/included requests
        if (req.getDispatcherType() != DispatcherType.REQUEST) {
            // Let the container handle the forward
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String path = getRequestPath(req);

        if (path.endsWith(".jsp")) {
            req.getRequestDispatcher(path).forward(req, resp);
            return;
        }

        String method = req.getMethod().toUpperCase();
        logger.debug("Processing {} request for path: {}", method, path);

        Controller controller = findController(path);
        if (controller != null) {
            try {
                if ("GET".equals(method)) {
                    controller.doGet(req, resp);
                    logger.info("got controller");
                } else if ("POST".equals(method)) {
                    controller.doPost(req, resp);
                }
            } catch (Exception e) {
                logger.error("Error processing request for path: {}", path, e);
                throw new ServletException("Error processing request", e);
            }
        } else {
            logger.warn("No controller found for path: {}", path);
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private String getRequestPath(HttpServletRequest request) {
        String path = request.getServletPath();
        if (path == null || path.isEmpty()) {
            path = request.getPathInfo();
        }

        // Handle root context case
        if (path == null || path.isEmpty() || path.equals("/")) {
            path = "/";
        }
        return path;
    }

    private Controller findController(String path) {
        // Exact match first
        if (controllers.containsKey(path)) {
            return controllers.get(path);
        }

        // If no exact match, try to find the most specific controller
        for (Map.Entry<String, Controller> entry : controllers.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }

        return null;
    }
}