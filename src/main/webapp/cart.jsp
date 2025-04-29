<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Shopping Cart</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .cart-container {
            max-width: 1200px;
            margin: 30px auto;
        }
        .cart-item {
            border-bottom: 1px solid #eee;
            padding: 15px 0;
        }
        .cart-summary {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 5px;
        }
        .empty-cart {
            text-align: center;
            padding: 50px 0;
        }
    </style>
</head>
<body>
<div class="container cart-container">
    <h2 class="mb-4">Your Shopping Cart</h2>

    <c:choose>
        <c:when test="${empty products}">
            <div class="empty-cart">
                <h4>Your cart is empty</h4>
                <p>Start shopping to add items to your cart</p>
                <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">Browse Products</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="row">
                <div class="col-md-8">
                    <div class="card">
                        <div class="card-header">
                            <h5>Cart Items</h5>
                        </div>
                        <div class="card-body">
                            <c:forEach items="${products}" var="product">
                                <div class="row cart-item align-items-center">
                                    <div class="col-md-4">
                                        <h6>${product.name}</h6>
                                        <p class="text-muted small">${product.description}</p>
                                    </div>
                                    <div class="col-md-2">
                                        <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="$"/>
                                    </div>
                                    <div class="col-md-2">
                                        <div class="input-group">
                                            <input type="number" class="form-control" value="1" min="1">
                                        </div>
                                    </div>
                                    <div class="col-md-2 text-end">
                                        <form method="post" action="${pageContext.request.contextPath}/cart">
                                            <input type="hidden" name="productId" value="${product.id}">
                                            <input type="hidden" name="action" value="remove">
                                            <button type="submit" class="btn btn-danger btn-sm">Remove</button>
                                        </form>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="card cart-summary">
                        <div class="card-body">
                            <h5 class="card-title">Order Summary</h5>
                            <div class="d-flex justify-content-between mb-2">
                                <span>Subtotal (${products.size()} items)</span>
                                <span><fmt:formatNumber value="${totalPrice}" type="currency" currencySymbol="$"/></span>
                            </div>
                            <div class="d-flex justify-content-between mb-3">
                                <span>Shipping</span>
                                <span>Shipping</span>
                                <span>Free</span>
                            </div>
                            <hr>
                            <div class="d-flex justify-content-between fw-bold mb-4">
                                <span>Total</span>
                                <span><fmt:formatNumber value="${totalPrice}" type="currency" currencySymbol="$"/></span>
                            </div>
                            <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary w-100 mb-2">Proceed to Checkout</a>
                            <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-secondary w-100">Continue Shopping</a>
                        </div>
                    </div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Quantity update functionality
    document.querySelectorAll('.quantity-input').forEach(input => {
        input.addEventListener('change', function() {
            // In a real app, you would send an AJAX request to update quantity
            console.log('Quantity changed to:', this.value);
        });
    });
</script>
</body>
</html>