<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Welcome to Our Store</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css">
    <style>
        .welcome-container {
            height: 100vh;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            text-align: center;
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
        }
        .welcome-card {
            padding: 3rem;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
            background-color: white;
            max-width: 800px;
            width: 100%;
        }
        .btn-xl {
            padding: 1rem 2rem;
            font-size: 1.25rem;
            border-radius: 0.5rem;
        }
        .nav-links {
            margin-top: 2rem;
        }
        .nav-links a {
            margin: 0 10px;
            font-size: 1.1rem;
        }
        .cart-icon {
            font-size: 1.5rem;
            margin-right: 8px;
        }
    </style>
</head>
<body>
<div class="welcome-container">
    <div class="welcome-card card">
        <div class="card-body">
            <h1 class="card-title mb-4 display-4">Welcome to Our Online Store</h1>
            <p class="card-text mb-4 lead">Discover amazing products at great prices</p>

            <div class="d-grid gap-3 col-md-6 mx-auto">
                <a href="${pageContext.request.contextPath}/register" class="btn btn-primary btn-xl">
                    <i class="bi bi-person-plus"></i> Register Now
                </a>
                <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-primary btn-xl">
                    <i class="bi bi-box-arrow-in-right"></i> Login
                </a>
            </div>

            <div class="nav-links mt-4">
                <a href="${pageContext.request.contextPath}/products" class="text-decoration-none">
                    <i class="bi bi-shop"></i> Browse Products
                </a>
                <a href="${pageContext.request.contextPath}/cart" class="text-decoration-none position-relative">
                    <i class="bi bi-cart3 cart-icon"></i> View Cart
                    <c:if test="${sessionScope.cart != null && !sessionScope.cart.products.isEmpty()}">
                        <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger" style="font-size: 0.9rem;">
                                ${sessionScope.cart.products.size()}
                        </span>
                    </c:if>
                </a>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>