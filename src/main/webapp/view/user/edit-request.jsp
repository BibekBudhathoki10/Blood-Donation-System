<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.BloodRequest, java.time.LocalDate" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Blood Request - Blood Donation System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .request-form {
            max-width: 800px;
            margin: 0 auto;
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 30px;
        }
        
        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-bottom: 20px;
        }
        
        .info-section {
            background-color: #f8f9fa;
            border-radius: 5px;
            padding: 20px;
            margin-bottom: 30px;
        }
        
        .info-section h3 {
            margin-top: 0;
            color: #e74c3c;
            margin-bottom: 15px;
        }
        
        @media (max-width: 768px) {
            .form-row {
                grid-template-columns: 1fr;
                gap: 10px;
            }
        }
    </style>
</head>
<body>
    <jsp:include page="../common/header.jsp" />
    
    <div class="container">
        <h1>Edit Blood Request</h1>
        
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
        
        <% 
            BloodRequest bloodRequest = (BloodRequest) request.getAttribute("bloodRequest");
            if(bloodRequest != null) {
        %>
        
        <div class="info-section">
            <h3>Important Information</h3>
            <p>You can only edit pending requests. Once a request is approved, you can only cancel it.</p>
            <p>Please provide accurate information to help us process your blood request efficiently.</p>
        </div>
        
        <div class="request-form">
            <form action="${pageContext.request.contextPath}/user/edit-request" method="post">
                <input type="hidden" name="id" value="<%= bloodRequest.getId() %>">
                
                <h2>Patient & Hospital Information</h2>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="patientName">Patient Name:</label>
                        <input type="text" id="patientName" name="patientName" value="<%= bloodRequest.getPatientName() %>" required>
                        <% if(request.getAttribute("patientNameError") != null) { %>
                            <span class="error"><%= request.getAttribute("patientNameError") %></span>
                        <% } %>
                    </div>
                    
                    <div class="form-group">
                        <label for="bloodGroup">Blood Group Required:</label>
                        <select id="bloodGroup" name="bloodGroup" required>
                            <option value="A+" <%= "A+".equals(bloodRequest.getBloodGroup()) ? "selected" : "" %>>A+</option>
                            <option value="A-" <%= "A-".equals(bloodRequest.getBloodGroup()) ? "selected" : "" %>>A-</option>
                            <option value="B+" <%= "B+".equals(bloodRequest.getBloodGroup()) ? "selected" : "" %>>B+</option>
                            <option value="B-" <%= "B-".equals(bloodRequest.getBloodGroup()) ? "selected" : "" %>>B-</option>
                            <option value="AB+" <%= "AB+".equals(bloodRequest.getBloodGroup()) ? "selected" : "" %>>AB+</option>
                            <option value="AB-" <%= "AB-".equals(bloodRequest.getBloodGroup()) ? "selected" : "" %>>AB-</option>
                            <option value="O+" <%= "O+".equals(bloodRequest.getBloodGroup()) ? "selected" : "" %>>O+</option>
                            <option value="O-" <%= "O-".equals(bloodRequest.getBloodGroup()) ? "selected" : "" %>>O-</option>
                        </select>
                        <% if(request.getAttribute("bloodGroupError") != null) { %>
                            <span class="error"><%= request.getAttribute("bloodGroupError") %></span>
                        <% } %>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="quantity">Quantity (units):</label>
                        <input type="number" id="quantity" name="quantity" min="1" max="10" value="<%= bloodRequest.getQuantity() %>" required>
                        <% if(request.getAttribute("quantityError") != null) { %>
                            <span class="error"><%= request.getAttribute("quantityError") %></span>
                        <% } %>
                    </div>
                    
                    <div class="form-group">
                        <label for="urgency">Urgency Level:</label>
                        <select id="urgency" name="urgency" required>
                            <option value="normal" <%= "normal".equals(bloodRequest.getUrgency()) ? "selected" : "" %>>Normal</option>
                            <option value="urgent" <%= "urgent".equals(bloodRequest.getUrgency()) ? "selected" : "" %>>Urgent</option>
                            <option value="critical" <%= "critical".equals(bloodRequest.getUrgency()) ? "selected" : "" %>>Critical</option>
                        </select>
                        <% if(request.getAttribute("urgencyError") != null) { %>
                            <span class="error"><%= request.getAttribute("urgencyError") %></span>
                        <% } %>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="hospitalName">Hospital Name:</label>
                        <input type="text" id="hospitalName" name="hospitalName" value="<%= bloodRequest.getHospitalName() %>" required>
                        <% if(request.getAttribute("hospitalNameError") != null) { %>
                            <span class="error"><%= request.getAttribute("hospitalNameError") %></span>
                        <% } %>
                    </div>
                    
                    <div class="form-group">
                        <label for="hospitalAddress">Hospital Address:</label>
                        <input type="text" id="hospitalAddress" name="hospitalAddress" value="<%= bloodRequest.getHospitalAddress() %>" required>
                        <% if(request.getAttribute("hospitalAddressError") != null) { %>
                            <span class="error"><%= request.getAttribute("hospitalAddressError") %></span>
                        <% } %>
                    </div>
                </div>
                
                <h2>Contact Information</h2>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="contactPerson">Contact Person:</label>
                        <input type="text" id="contactPerson" name="contactPerson" value="<%= bloodRequest.getContactPerson() %>" required>
                        <% if(request.getAttribute("contactPersonError") != null) { %>
                            <span class="error"><%= request.getAttribute("contactPersonError") %></span>
                        <% } %>
                    </div>
                    
                    <div class="form-group">
                        <label for="contactPhone">Contact Phone:</label>
                        <input type="text" id="contactPhone" name="contactPhone" value="<%= bloodRequest.getContactPhone() %>" required>
                        <% if(request.getAttribute("contactPhoneError") != null) { %>
                            <span class="error"><%= request.getAttribute("contactPhoneError") %></span>
                        <% } %>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="requiredDate">Required By Date:</label>
                        <input type="date" id="requiredDate" name="requiredDate" 
                               min="<%= LocalDate.now() %>" 
                               value="<%= bloodRequest.getRequiredDate() %>" 
                               required>
                        <% if(request.getAttribute("requiredDateError") != null) { %>
                            <span class="error"><%= request.getAttribute("requiredDateError") %></span>
                        <% } %>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="reason">Reason for Request (Optional):</label>
                    <textarea id="reason" name="reason" rows="3"><%= bloodRequest.getReason() != null ? bloodRequest.getReason() : "" %></textarea>
                </div>
                
                <div class="form-group">
                    <button type="submit" class="btn btn-primary">Update Request</button>
                    <a href="${pageContext.request.contextPath}/user/my-requests" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>
        
        <% } else { %>
            <div class="alert alert-danger">
                Blood request not found or you don't have permission to edit this request.
            </div>
            <p><a href="${pageContext.request.contextPath}/user/my-requests" class="btn btn-primary">Back to My Requests</a></p>
        <% } %>
    </div>
    
    <jsp:include page="../common/footer.jsp" />
</body>
</html>

