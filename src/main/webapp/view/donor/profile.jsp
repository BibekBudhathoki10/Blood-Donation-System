<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Donor, model.User" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Donor Profile - Blood Donation System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .profile-container {
            display: grid;
            grid-template-columns: 1fr 2fr;
            gap: 30px;
        }
        
        .profile-sidebar {
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 20px;
        }
        
        .profile-sidebar .profile-image {
            width: 150px;
            height: 150px;
            border-radius: 50%;
            background-color: #f0f0f0;
            margin: 0 auto 20px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 3rem;
            color: #e74c3c;
        }
        
        .profile-sidebar .profile-info {
            text-align: center;
        }
        
        .profile-sidebar .profile-info h2 {
            margin-bottom: 5px;
            color: #333;
        }
        
        .profile-sidebar .profile-info p {
            color: #666;
            margin-bottom: 15px;
        }
        
        .profile-sidebar .profile-stats {
            margin-top: 20px;
            border-top: 1px solid #eee;
            padding-top: 20px;
        }
        
        .profile-sidebar .profile-stats .stat {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
        }
        
        .profile-content {
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 20px;
        }
        
        .profile-content h2 {
            margin-bottom: 20px;
            color: #e74c3c;
            border-bottom: 1px solid #eee;
            padding-bottom: 10px;
        }
        
        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-bottom: 20px;
        }
        
        @media (max-width: 768px) {
            .profile-container {
                grid-template-columns: 1fr;
            }
            
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
        <h1>My Profile</h1>
        
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
            User user = (User) request.getAttribute("user");
            Donor donor = (Donor) request.getAttribute("donor");
            
            if(user != null && donor != null) {
        %>
        
        <div class="profile-container">
            <div class="profile-sidebar">
                <div class="profile-image">
                    <%= user.getName().charAt(0) %>
                </div>
                
                <div class="profile-info">
                    <h2><%= user.getName() %></h2>
                    <p><%= user.getEmail() %></p>
                    <p><strong>Blood Group:</strong> <%= donor.getBloodGroup() %></p>
                    <p><strong>Status:</strong> 
                        <span class="<%= donor.isAvailable() ? "status-available" : "status-unavailable" %>">
                            <%= donor.isAvailable() ? "Available" : "Not Available" %>
                        </span>
                    </p>
                </div>
                
                <div class="profile-stats">
                    <div class="stat">
                        <span>Donations:</span>
                        <span><strong><%= donor.getDonationCount() %></strong></span>
                    </div>
                    <div class="stat">
                        <span>Last Donation:</span>
                        <span><strong><%= donor.getLastDonationDate() != null ? donor.getLastDonationDate() : "Never" %></strong></span>
                    </div>
                    <div class="stat">
                        <span>Member Since:</span>
                        <span><strong><%= user.getRegistrationDate() %></strong></span>
                    </div>
                </div>
            </div>
            
            <div class="profile-content">
                <h2>Edit Profile</h2>
                
                <form action="${pageContext.request.contextPath}/donor/profile" method="post">
                    <div class="form-row">
                        <div class="form-group">
                            <label for="name">Full Name:</label>
                            <input type="text" id="name" name="name" value="<%= user.getName() %>" required>
                            <% if(request.getAttribute("nameError") != null) { %>
                                <span class="error"><%= request.getAttribute("nameError") %></span>
                            <% } %>
                        </div>
                        
                        <div class="form-group">
                            <label for="email">Email:</label>
                            <input type="email" id="email" name="email" value="<%= user.getEmail() %>" required>
                            <% if(request.getAttribute("emailError") != null) { %>
                                <span class="error"><%= request.getAttribute("emailError") %></span>
                            <% } %>
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="phone">Phone Number:</label>
                            <input type="text" id="phone" name="phone" value="<%= user.getPhone() %>" required>
                            <% if(request.getAttribute("phoneError") != null) { %>
                                <span class="error"><%= request.getAttribute("phoneError") %></span>
                            <% } %>
                        </div>
                        
                        <div class="form-group">
                            <label for="bloodGroup">Blood Group:</label>
                            <select id="bloodGroup" name="bloodGroup" required>
                                <option value="A+" <%= "A+".equals(donor.getBloodGroup()) ? "selected" : "" %>>A+</option>
                                <option value="A-" <%= "A-".equals(donor.getBloodGroup()) ? "selected" : "" %>>A-</option>
                                <option value="B+" <%= "B+".equals(donor.getBloodGroup()) ? "selected" : "" %>>B+</option>
                                <option value="B-" <%= "B-".equals(donor.getBloodGroup()) ? "selected" : "" %>>B-</option>
                                <option value="AB+" <%= "AB+".equals(donor.getBloodGroup()) ? "selected" : "" %>>AB+</option>
                                <option value="AB-" <%= "AB-".equals(donor.getBloodGroup()) ? "selected" : "" %>>AB-</option>
                                <option value="O+" <%= "O+".equals(donor.getBloodGroup()) ? "selected" : "" %>>O+</option>
                                <option value="O-" <%= "O-".equals(donor.getBloodGroup()) ? "selected" : "" %>>O-</option>
                            </select>
                            <% if(request.getAttribute("bloodGroupError") != null) { %>
                                <span class="error"><%= request.getAttribute("bloodGroupError") %></span>
                            <% } %>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="address">Address:</label>
                        <textarea id="address" name="address" rows="3" required><%= user.getAddress() %></textarea>
                        <% if(request.getAttribute("addressError") != null) { %>
                            <span class="error"><%= request.getAttribute("addressError") %></span>
                        <% } %>
                    </div>
                    
                    <div class="form-group">
                        <label for="medicalHistory">Medical History (Optional):</label>
                        <textarea id="medicalHistory" name="medicalHistory" rows="3"><%= donor.getMedicalHistory() != null ? donor.getMedicalHistory() : "" %></textarea>
                    </div>
                    
                    <div class="form-group">
                        <label>
                            <input type="checkbox" name="available" <%= donor.isAvailable() ? "checked" : "" %>>
                            I am available to donate blood
                        </label>
                    </div>
                    
                    <h3>Change Password (Optional)</h3>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="currentPassword">Current Password:</label>
                            <input type="password" id="currentPassword" name="currentPassword">
                            <% if(request.getAttribute("currentPasswordError") != null) { %>
                                <span class="error"><%= request.getAttribute("currentPasswordError") %></span>
                            <% } %>
                        </div>
                        
                        <div class="form-group">
                            <label for="newPassword">New Password:</label>
                            <input type="password" id="newPassword" name="newPassword">
                            <% if(request.getAttribute("newPasswordError") != null) { %>
                                <span class="error"><%= request.getAttribute("newPasswordError") %></span>
                            <% } %>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="confirmPassword">Confirm New Password:</label>
                        <input type="password" id="confirmPassword" name="confirmPassword">
                        <% if(request.getAttribute("confirmPasswordError") != null) { %>
                            <span class="error"><%= request.getAttribute("confirmPasswordError") %></span>
                        <% } %>
                    </div>
                    
                    <div class="form-group">
                        <button type="submit" class="btn btn-primary">Update Profile</button>
                    </div>
                </form>
            </div>
        </div>
        
        <% } else { %>
            <div class="alert alert-danger">
                User or donor information not found.
            </div>
        <% } %>
    </div>
    
    <jsp:include page="../common/footer.jsp" />
</body>
</html>

