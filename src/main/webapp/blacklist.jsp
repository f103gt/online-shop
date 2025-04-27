<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
  <title>Blacklisted Users</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
  <style>
    .nav-link.active {
      font-weight: bold;
      border-bottom: 2px solid #0d6efd;
    }
  </style>
</head>
<body>
<div class="container mt-4">
  <ul class="nav nav-tabs mb-4">
    <li class="nav-item">
      <a class="nav-link active" href="${pageContext.request.contextPath}/black-list">Blacklist</a>
    </li>
    <li class="nav-item">
      <a class="nav-link" href="${pageContext.request.contextPath}/debtors">Debtors</a>
    </li>
  </ul>

  <h1>Blacklisted Users</h1>
  <table class="table table-bordered">
    <thead class="table-dark">
    <tr>
      <th>User ID</th>
      <th>Username</th>
      <th>Email</th>
      <th>Unpaid Order ID</th>
      <th>Actions</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${blacklistedUsers}" var="entry">
      <tr>
        <td>${entry.user.id}</td>
        <td>${entry.user.username}</td>
        <td>${entry.user.email}</td>
        <td>${entry.orderId}</td>
        <td>
          <div class="d-flex gap-2">
            <form method="post" action="${pageContext.request.contextPath}/black-list"
                  style="display: inline-block;">
              <input type="hidden" name="action" value="remove">
              <input type="hidden" name="userId" value="${entry.user.id}">
              <input type="hidden" name="orderId" value="${entry.orderId}">
              <button type="submit" class="btn btn-danger btn-sm">Remove</button>
            </form>
            <a href="${pageContext.request.contextPath}/debtors?userId=${entry.user.id}"
               class="btn btn-info btn-sm">View Debt</a>
          </div>
        </td>
      </tr>
    </c:forEach>
    </tbody>
  </table>
</div>
</body>
</html>