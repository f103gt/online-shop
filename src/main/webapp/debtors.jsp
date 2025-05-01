<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html>
<head>
    <title>Debtors Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <style>
        /* Your existing styles remain the same */
    </style>
</head>
<body>
<div class="container">
    <div class="container">
        <nav class="navbar navbar-expand-lg navbar-light bg-light mb-4">
            <div class="container-fluid">
                <a class="navbar-brand" href="${pageContext.request.contextPath}/products">Internet Shop</a>
                <div class="collapse navbar-collapse">
                    <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/products">
                                <i class="bi bi-shop"></i> Products
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/black-list">
                                <i class="bi bi-list-ul"></i> Blacklist
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/debtors">
                                <i class="bi bi-people-fill"></i> Debtors
                            </a>
                        </li>
                    </ul>
                </div>
                <div class="d-flex">
                <span class="navbar-text me-3">
                    Welcome, ${sessionScope.user.username} (${sessionScope.user.role})
                </span>
                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-danger">Logout</a>
                </div>
            </div>
        </nav>

        <h1 class="my-4">Debtors Management</h1>

        <div class="card shadow-sm mb-5">
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead class="table-light">
                        <tr>
                            <th>User</th>
                            <th>Order ID</th>
                            <th>Order Date</th>
                            <th>Receival Date</th>
                            <th>Amount</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${debtors}" var="entry">
                            <c:forEach items="${entry.value}" var="order">
                                <tr class="${blacklistedUsers.contains(entry.key) ? 'table-danger' : ''}">
                                    <td>${entry.key.username}</td>
                                    <td>${order.id}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty order.orderDate}">
                                                ${order.orderDate.format(DateTimeFormatter.ofPattern('yyyy-MM-dd HH:mm'))}
                                            </c:when>
                                            <c:otherwise>
                                                N/A
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${order.receivalDate.present}">
                                                ${order.receivalDate.get().format(DateTimeFormatter.ofPattern('yyyy-MM-dd HH:mm'))}
                                            </c:when>
                                            <c:otherwise>
                                                N/A
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><fmt:formatNumber value="${order.totalAmount}" type="currency" currencyCode="USD"/></td>
                                    <td class="action-buttons">
                                        <c:if test="${!blacklistedUsers.contains(entry.key)}">
                                            <button class="btn btn-sm btn-danger add-to-blacklist"
                                                    data-user-id="${entry.key.id}"
                                                    data-order-id="${order.id}">
                                                <i class="bi bi-ban"></i> Blacklist
                                            </button>
                                        </c:if>
                                        <c:if test="${blacklistedUsers.contains(entry.key)}">
                                            <span class="badge bg-secondary">Blacklisted</span>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        $(document).ready(function() {
            $('.add-to-blacklist').click(function() {
                const userId = $(this).data('user-id');
                const orderId = $(this).data('order-id');
                const button = $(this);

                $.ajax({
                    type: "POST",
                    url: "${pageContext.request.contextPath}/black-list",
                    data: {
                        action: "add",
                        userId: userId,
                        orderId: orderId
                    },
                    dataType: "json",
                });
            });
        });
    </script>
</body>
</html>