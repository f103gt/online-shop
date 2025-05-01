<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
  <title>Blacklist Management</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <style>
    .blacklist-card {
      margin-bottom: 20px;
      border-left: 4px solid #dc3545;
      transition: all 0.3s ease;
    }
    .blacklist-card:hover {
      box-shadow: 0 5px 15px rgba(0,0,0,0.1);
    }
  </style>
</head>
<body>
<div class="container">
  <nav class="navbar navbar-expand-lg navbar-light bg-light mb-4">
    <div class="container-fluid">
      <a class="navbar-brand" href="${pageContext.request.contextPath}/">Internet Shop</a>
      <div class="collapse navbar-collapse">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
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

  <h1 class="my-4">Blacklisted Users</h1>

  <div class="card shadow-sm">
    <div class="card-body">
      <div class="table-responsive">
        <table class="table table-hover">
          <thead class="table-light">
          <tr>
            <th>User ID</th>
            <th>Username</th>
            <th>Email</th>
          </tr>
          </thead>
          <tbody>
          <c:forEach items="${blacklistedUsers}" var="user">
            <tr class="table-danger">
              <td>${user.id}</td>
              <td>${user.username}</td>
              <td>${user.email}</td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>