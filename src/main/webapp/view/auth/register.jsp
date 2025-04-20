<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Register - Blood Donation System</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
  <jsp:include page="../common/header.jsp" />
  
  <div class="container">
      <div class="auth-form">
          <h2>Register</h2>
          
          <% if(request.getAttribute("error") != null) { %>
              <div class="alert alert-danger">
                  <%= request.getAttribute("error") %>
              </div>
          <% } %>
          
          <form action="${pageContext.request.contextPath}/auth/register" method="post">
              <div class="form-group">
                  <label for="name">Full Name:</label>
                  <input type="text" id="name" name="name" value="${name}" required>
                  <% if(request.getAttribute("nameError") != null) { %>
                      <span class="error"><%= request.getAttribute("nameError") %></span>
                  <% } %>
              </div>
              
              <div class="form-group">
                  <label for="email">Email:</label>
                  <input type="email" id="email" name="email" value="${email}" required>
                  <% if(request.getAttribute("emailError") != null) { %>
                      <span class="error"><%= request.getAttribute("emailError") %></span>
                  <% } %>
              </div>
              
              <div class="form-group">
                  <label for="password">Password:</label>
                  <input type="password" id="password" name="password" required>
                  <% if(request.getAttribute("passwordError") != null) { %>
                      <span class="error"><%= request.getAttribute("passwordError") %></span>
                  <% } %>
              </div>
              
              <div class="form-group">
                  <label for="confirmPassword">Confirm Password:</label>
                  <input type="password" id="confirmPassword" name="confirmPassword" required>
                  <% if(request.getAttribute("confirmPasswordError") != null) { %>
                      <span class="error"><%= request.getAttribute("confirmPasswordError") %></span>
                  <% } %>
              </div>
              
              <div class="form-group">
                  <label for="phone">Phone Number:</label>
                  <input type="text" id="phone" name="phone" value="${phone}" required>
                  <% if(request.getAttribute("phoneError") != null) { %>
                      <span class="error"><%= request.getAttribute("phoneError") %></span>
                  <% } %>
              </div>
              
              <div class="form-group">
                  <label for="address">Address:</label>
                  <textarea id="address" name="address" required>${address}</textarea>
                  <% if(request.getAttribute("addressError") != null) { %>
                      <span class="error"><%= request.getAttribute("addressError") %></span>
                  <% } %>
              </div>
              
              <div class="form-group">
                  <label>Register as:</label>
                  <div class="radio-group">
                      <label>
                          <input type="radio" name="role" value="donor" ${role == 'donor' ? 'checked' : ''} onclick="toggleDonorFields(true)"> Donor
                      </label>
                      <label>
                          <input type="radio" name="role" value="general" ${role == 'general' ? 'checked' : ''} onclick="toggleDonorFields(false)"> General User
                      </label>
                  </div>
              </div>
              
              <div id="donorFields" style="display: ${role == 'donor' ? 'block' : 'none'};">
                  <div class="form-group">
                      <label for="bloodGroup">Blood Group:</label>
                      <select id="bloodGroup" name="bloodGroup">
                          <option value="">Select Blood Group</option>
                          <option value="A+" ${bloodGroup == 'A+' ? 'selected' : ''}>A+</option>
                          <option value="A-" ${bloodGroup == 'A-' ? 'selected' : ''}>A-</option>
                          <option value="B+" ${bloodGroup == 'B+' ? 'selected' : ''}>B+</option>
                          <option value="B-" ${bloodGroup == 'B-' ? 'selected' : ''}>B-</option>
                          <option value="AB+" ${bloodGroup == 'AB+' ? 'selected' : ''}>AB+</option>
                          <option value="AB-" ${bloodGroup == 'AB-' ? 'selected' : ''}>AB-</option>
                          <option value="O+" ${bloodGroup == 'O+' ? 'selected' : ''}>O+</option>
                          <option value="O-" ${bloodGroup == 'O-' ? 'selected' : ''}>O-</option>
                      </select>
                      <% if(request.getAttribute("bloodGroupError") != null) { %>
                          <span class="error"><%= request.getAttribute("bloodGroupError") %></span>
                      <% } %>
                  </div>
                  
                  <div class="form-group">
                      <label for="medicalHistory">Medical History (Optional):</label>
                      <textarea id="medicalHistory" name="medicalHistory">${medicalHistory}</textarea>
                  </div>
              </div>
              
              <div class="form-group">
                  <button type="submit" class="btn btn-primary">Register</button>
              </div>
              
              <div class="form-footer">
                  <p>Already have an account? <a href="${pageContext.request.contextPath}/auth/login">Login</a></p>
              </div>
          </form>
      </div>
  </div>
  
  <jsp:include page="../common/footer.jsp" />
  
  <script>
      function toggleDonorFields(show) {
          document.getElementById('donorFields').style.display = show ? 'block' : 'none';
      }
      
      // Initialize on page load
      window.onload = function() {
          var donorRadio = document.querySelector('input[name="role"][value="donor"]');
          toggleDonorFields(donorRadio.checked);
      };
  </script>
</body>
</html>
