<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.time.LocalDate" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Blood Inventory - Blood Donation System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <jsp:include page="../../common/header.jsp" />
    
    <div class="container">
        <h1>Add Blood Inventory</h1>
        
        <% if(request.getAttribute("success") != null) { %>
            <div class="alert alert-success">
                <%= request.getAttribute("success") %>
            </div>
        <% } %>
        
        <% if(request.getAttribute("error") != null) { %>
            <div class="alert alert-danger">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <div class="auth-form">
            <form action="${pageContext.request.contextPath}/inventory/add" method="post">
                <div class="form-row">
                    <div class="form-group">
                        <label for="bloodGroup">Blood Group:</label>
                        <select id="bloodGroup" name="bloodGroup" required>
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
                        <label for="quantity">Quantity (units):</label>
                        <input type="number" id="quantity" name="quantity" min="1" max="10" value="${quantity != null ? quantity : '1'}" required>
                        <% if(request.getAttribute("quantityError") != null) { %>
                            <span class="error"><%= request.getAttribute("quantityError") %></span>
                        <% } %>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="collectionDate">Collection Date:</label>
                        <input type="date" id="collectionDate" name="collectionDate" 
                               max="<%= LocalDate.now() %>" 
                               value="${collectionDate != null ? collectionDate : LocalDate.now()}" 
                               required>
                        <% if(request.getAttribute("collectionDateError") != null) { %>
                            <span class="error"><%= request.getAttribute("collectionDateError") %></span>
                        <% } %>
                    </div>
                    
                    <div class="form-group">
                        <label for="expiryDate">Expiry Date:</label>
                        <input type="date" id="expiryDate" name="expiryDate" 
                               min="<%= LocalDate.now().plusDays(1) %>" 
                               value="${expiryDate != null ? expiryDate : LocalDate.now().plusDays(42)}" 
                               required>
                        <% if(request.getAttribute("expiryDateError") != null) { %>
                            <span class="error"><%= request.getAttribute("expiryDateError") %></span>
                        <% } %>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="status">Status:</label>
                        <select id="status" name="status" required>
                            <option value="available" ${status == 'available' ? 'selected' : ''}>Available</option>
                            <option value="reserved" ${status == 'reserved' ? 'selected' : ''}>Reserved</option>
                        </select>
                        <% if(request.getAttribute("statusError") != null) { %>
                            <span class="error"><%= request.getAttribute("statusError") %></span>
                        <% } %>
                    </div>
                    
                    <div class="form-group">
                        <label for="donorId">Donor ID:</label>
                        <input type="number" id="donorId" name="donorId" min="1" value="${donorId}" required>
                        <% if(request.getAttribute("donorIdError") != null) { %>
                            <span class="error"><%= request.getAttribute("donorIdError") %></span>
                        <% } %>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="location">Location:</label>
                    <input type="text" id="location" name="location" value="${location}" required>
                    <% if(request.getAttribute("locationError") != null) { %>
                        <span class="error"><%= request.getAttribute("locationError") %></span>
                    <% } %>
                </div>
                
                <div class="form-group">
                    <button type="submit" class="btn btn-primary">Add Blood Unit</button>
                    <a href="${pageContext.request.contextPath}/inventory/list" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>
    </div>
    
    <jsp:include page="../../common/footer.jsp" />
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Calculate expiry date (42 days from collection date)
            const collectionDateInput = document.getElementById('collectionDate');
            const expiryDateInput = document.getElementById('expiryDate');
            
            collectionDateInput.addEventListener('change', function() {
                const collectionDate = new Date(this.value);
                const expiryDate = new Date(collectionDate);
                expiryDate.setDate(collectionDate.getDate() + 42);
                
                // Format date as YYYY-MM-DD
                const year = expiryDate.getFullYear();
                const month = String(expiryDate.getMonth() + 1).padStart(2, '0');
                const day = String(expiryDate.getDate()).padStart(2, '0');
                
                expiryDateInput.value = `${year}-${month}-${day}`;
            });
        });
    </script>
</body>
</html>

