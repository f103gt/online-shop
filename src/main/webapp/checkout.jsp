<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Checkout</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <div class="row">
        <div class="col-md-8 offset-md-2">
            <div class="card">
                <div class="card-header">
                    <h4>Checkout</h4>
                </div>
                <div class="card-body">
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger">${errorMessage}</div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/checkout" method="post">
                        <input type="hidden" name="totalAmount" value="${totalPrice}">

                        <div class="mb-3">
                            <label for="cardNumber" class="form-label">Card Number</label>
                            <input type="text" class="form-control" id="cardNumber" name="cardNumber" required>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label for="expiryDate" class="form-label">Expiry Date</label>
                                <input type="text" class="form-control" id="expiryDate" name="expiryDate" placeholder="MM/YY" required>
                            </div>
                            <div class="col-md-6">
                                <label for="cvv" class="form-label">CVV</label>
                                <input type="text" class="form-control" id="cvv" name="cvv" required>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="nameOnCard" class="form-label">Name on Card</label>
                            <input type="text" class="form-control" id="nameOnCard" name="nameOnCard" required>
                        </div>

                        <div class="d-grid gap-2">
                            <button type="submit" name="paymentStatus" value="PAID" class="btn btn-primary">Pay Now</button>
                            <button type="submit" name="paymentStatus" value="PENDING" class="btn btn-secondary">Pay Later</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>