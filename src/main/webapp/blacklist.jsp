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
            <th>Blacklisted Since</th>
            <th>Actions</th>
          </tr>
          </thead>
          <tbody>
          <c:forEach items="${blacklistedUsers}" var="user">
            <tr class="table-danger">
              <td>${user.id}</td>
              <td>${user.username}</td>
              <td>${user.email}</td>
              <td>N/A</td> <!-- You can add blacklist date if available -->
              <td>
                <button class="btn btn-sm btn-success remove-from-blacklist"
                        data-user-id="${user.id}">
                  Remove
                </button>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
  $(document).ready(function() {
    $('.remove-from-blacklist').click(function() {
      const userId = $(this).data('user-id');
      const button = $(this);

      if(confirm('Are you sure you want to remove this user from blacklist?')) {
        $.ajax({
          type: "POST",
          url: "${pageContext.request.contextPath}/blacklist/remove",
          data: { userId: userId },
          success: function(response) {
            if(response.status === "success") {
              button.closest('tr').fadeOut(300, function() {
                $(this).remove();
              });
            }
          },
          error: function(xhr) {
            alert("Error removing from blacklist: " + xhr.responseJSON.message);
          }
        });
      }
    });
  });
</script>
</body>
</html>