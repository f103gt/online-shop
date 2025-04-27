<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Debtors Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <style>
        .debtor-card {
            margin-bottom: 20px;
            border: 1px solid #ddd;
            border-radius: 5px;
            padding: 15px;
        }
        .blacklisted {
            background-color: #ffe6e6;
        }
    </style>
</head>
<body>
<div class="container mt-4">
    <h2>Debtors List</h2>

    <div class="row">
        <div class="col-md-8">
            <c:forEach items="${debtors}" var="entry">
                <div class="debtor-card ${blacklistedUsers.contains(entry.key) ? 'blacklisted' : ''}">
                    <h5>${entry.key.username}</h5>
                    <table class="table table-sm">
                        <thead>
                        <tr>
                            <th>Order ID</th>
                            <th>Order Date</th>
                            <th>Amount</th>
                            <th>Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${entry.value}" var="order">
                            <tr>
                                <td>${order.id}</td>
                                <td>${order.orderDate}</td>
                                <td>$${order.totalAmount}</td>
                                <td>
                                    <button class="btn btn-danger btn-sm add-to-blacklist"
                                            data-user-id="${entry.key.id}"
                                            data-order-id="${order.id}">
                                        Add to Blacklist
                                    </button>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:forEach>
        </div>
    </div>
</div>

<script>
    $(document).ready(function() {
        $('.add-to-blacklist').click(function() {
            const userId = $(this).data('user-id');
            const orderId = $(this).data('order-id');

            $.ajax({
                type: "POST",
                url: "${pageContext.request.contextPath}/debtors",
                data: {
                    userId: userId,
                    orderId: orderId
                },
                success: function(response) {
                    if(response.status === "success") {
                        // Update UI without refresh
                        const card = $(this).closest('.debtor-card');
                        card.addClass('blacklisted');
                        $(this).prop('disabled', true).text('Blacklisted');
                    }
                }.bind(this),
                error: function(xhr) {
                    alert("Error adding to blacklist: " + xhr.responseJSON.message);
                }
            });
        });
    });
</script>
</body>
</html>