<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Debtors Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <style>
        .debtor-card {
            margin-bottom: 20px;
            border: 1px solid #ddd;
            border-radius: 5px;
            padding: 20px;
            transition: all 0.3s ease;
        }
        .debtor-card:hover {
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        }
        .blacklisted {
            background-color: #ffe6e6;
            border-left: 4px solid #dc3545;
        }
        .badge-danger {
            background-color: #dc3545;
        }
        .action-buttons .btn {
            margin-right: 5px;
        }
        .nav-link {
            margin-right: 10px;
        }
    </style>
</head>
<body>
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
                        <th>Amount</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${debtors}" var="entry">
                        <c:forEach items="${entry.value}" var="order">
                            <tr class="${blacklistedUsers.contains(entry.key) ? 'table-danger' : ''}">
                                <td>${entry.key.username}</td>
                                <td>${order.id}</td>
                                <td><fmt:formatDate value="${order.orderDate}" pattern="yyyy-MM-dd HH:mm"/></td>
                                <td><fmt:formatNumber value="${order.totalAmount}" type="currency" currencyCode="USD"/></td>
                                <td>
                                        <span class="badge ${order.status == 'PAID' ? 'bg-success' : 'bg-warning'}">
                                                ${order.status}
                                        </span>
                                </td>
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
                url: "${pageContext.request.contextPath}/blacklist",
                data: {
                    userId: userId,
                    orderId: orderId
                },
                success: function(response) {
                    if(response.status === "success") {
                        button.replaceWith('<span class="badge bg-secondary">Blacklisted</span>');
                        button.closest('tr').addClass('table-danger');
                    }
                },
                error: function(xhr) {
                    alert("Error adding to blacklist: " + xhr.responseJSON.message);
                }
            });
        });
    });
</script>
</body>
</html>