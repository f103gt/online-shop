<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Products</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .product-card {
            transition: transform 0.3s;
        }
        .product-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 20px rgba(0,0,0,0.1);
        }
        .stock-info {
            font-size: 0.9rem;
        }
        .in-stock {
            color: #28a745;
        }
        .low-stock {
            color: #ffc107;
        }
        .out-of-stock {
            color: #dc3545;
        }
    </style>
</head>
<body>
<div class="container">
    <nav class="navbar navbar-expand-lg navbar-light bg-light mb-4">
        <div class="container-fluid">
            <a class="navbar-brand" href="#">Internet Shop</a>
            <div class="d-flex">
                    <span class="navbar-text me-3">
                        Welcome, ${sessionScope.user.username} (${sessionScope.user.role})
                    </span>
                <a href="cart" class="btn btn-outline-primary position-relative me-2">
                    Cart
                    <c:if test="${sessionScope.cart != null && !sessionScope.cart.products.isEmpty()}">
                            <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                                    ${sessionScope.cart.products.size()}
                            </span>
                    </c:if>
                </a>
                <a href="logout" class="btn btn-outline-danger">Logout</a>
            </div>
        </div>
    </nav>

    <h1 class="my-4">Our Products</h1>

    <div class="row row-cols-1 row-cols-md-3 g-4">
        <c:forEach items="${products}" var="product">
            <div class="col">
                <div class="card h-100 product-card">
                    <div class="card-body">
                        <h5 class="card-title">${product.name}</h5>
                        <p class="card-text">${product.description}</p>
                        <p class="text-success fw-bold">
                            <fmt:formatNumber value="${product.price}" type="currency" currencyCode="USD"/>
                        </p>
                        <p class="stock-info
                                <c:choose>
                                    <c:when test="${product.stock > 10}">in-stock</c:when>
                                    <c:when test="${product.stock > 0}">low-stock</c:when>
                                    <c:otherwise>out-of-stock</c:otherwise>
                                </c:choose>">
                            <c:choose>
                                <c:when test="${product.stock > 10}">In Stock (${product.stock})</c:when>
                                <c:when test="${product.stock > 0}">Low Stock (${product.stock} left)</c:when>
                                <c:otherwise>Out of Stock</c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                    <div class="card-footer bg-transparent">
                        <form action="add-to-cart" method="post">
                            <input type="hidden" name="productId" value="${product.id}">
                            <button type="submit" class="btn btn-primary w-100"
                                    <c:if test="${product.stock <= 0}">disabled</c:if>>
                                Add to Cart
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>