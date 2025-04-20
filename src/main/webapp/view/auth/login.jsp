<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Login - Blood Donation System</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
  <jsp:include page="../common/header.jsp" />
  
  <div class="container">
      <div class="auth-form">
          <h2>Login</h2>
          
          <% if(request.getAttribute("error") != null) { %>
              <div class="alert alert-danger">
                  <%= request.getAttribute("error") %>
              </div>
          <% } %>
          
          <% if(request.getAttribute("success") != null) { %>
              <div class="alert alert-success">
                  <%= request.getAttribute("success") %>
              </div>
          <% } %>
          
          <form action="${pageContext.request.contextPath}/auth/login" method="post">
              <div class="form-group">
                  <label for="email">Email:</label>
                  <input type="email" id="email" name="email" required>
              </div>
              
              <div class="form-group">
                  <label for="password">Password:</label>
                  <input type="password" id="password" name="password" required>
              </div>
              
              <div class="form-group">
                  <button type="submit" class="btn btn-primary">Login</button>
              </div>
              
              <div class="form-footer">
                  <p>Don't have an account? <a href="${pageContext.request.contextPath}/auth/register">Register</a></p>
              </div>
          </form>
      </div>
  </div>
  
  <jsp:include page="../common/footer.jsp" />
</body>
</html>
