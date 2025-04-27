<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
  <title>Blacklisted Users</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
<div class="container mt-4">
  <h1>Blacklisted Users</h1>
  <table class="table table-bordered">
    <thead class="table-dark">
    <tr>
      <th>User ID</th>
      <th>Username</th>
      <th>Unpaid Order ID</th>
      <th>Actions</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${blacklistedUsers}" var="user">
      <tr>
        <td>${user.id}</td>
        <td>${user.username}</td>
        <td>
          <!-- Assuming you have a way to get the order ID -->
            ${user.orderId} <!-- You'll need to adjust this based on your data structure -->
        </td>
        <td>
          <form method="post" action="${pageContext.request.contextPath}/black-list"
                style="display: inline-block;">
            <input type="hidden" name="action" value="remove">
            <input type="hidden" name="userId" value="${user.id}">
            <button type="submit" class="btn btn-danger btn-sm">Remove</button>
          </form>
        </td>
      </tr>
    </c:forEach>
    </tbody>
  </table>
</div>
</body>
</html>