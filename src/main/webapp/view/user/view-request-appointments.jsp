<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.BloodRequest, model.Appointment, model.User, java.util.List, java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>View Appointments - Blood Donation System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .appointment-card {
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 20px;
            margin-bottom: 20px;
            position: relative;
            overflow: hidden;
        }
        
        .appointment-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 5px;
            height: 100%;
        }
        
        .appointment-card.scheduled::before {
            background-color: #3498db;
        }
        
        .appointment-card.completed::before {
            background-color: #2ecc71;
        }
        
        .appointment-card.cancelled::before {
            background-color: #e74c3c;
        }
        
        .appointment-header {
            display: flex;
            justify-content: space-between;
            margin-bottom: 15px;
        }
        
        .appointment-header h3 {
            margin: 0;
            color: #333;
        }
        
        .appointment-details {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            margin-bottom: 15px;
        }
        
        .appointment-detail {
            margin-bottom: 5px;
        }
        
        .appointment-detail label {
            font-weight: bold;
            color: #666;
            margin-right: 5px;
        }
        
        .status-badge {
            display: inline-block;
            padding: 3px 8px;
            border-radius: 3px;
            font-size: 0.8rem;
            font-weight: bold;
            text-transform: uppercase;
        }
        
        .status-scheduled {
            background-color: #3498db;
            color: #fff;
        }
        
        .status-completed {
            background-color: #2ecc71;
            color: #fff;
        }
        
        .status-cancelled {
            background-color: #e74c3c;
            color: #fff;
        }
        
        .donor-info {
            background-color: #f8f9fa;
            border-radius: 5px;
            padding: 15px;
            margin-top: 15px;
        }
        
        .donor-info h4 {
            margin-top: 0;
            color: #333;
        }
        
        .request-info {
            background-color: #f0f0f0;
            border-radius: 5px;
            padding: 15px;
            margin-bottom: 20px;
        }
        
        .request-info h3 {
            margin-top: 0;
            color: #333;
        }
        
        .no-appointments {
            text-align: center;
            padding: 30px;
            background-color: #f8f9fa;
            border-radius: 5px;
            margin-top: 20px;
        }
        
        .no-appointments i {
            font-size: 48px;
            color: #95a5a6;
            margin-bottom: 15px;
            display: block;
        }
        
        .no-appointments p {
            color: #7f8c8d;
            font-size: 18px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <jsp:include page="../common/header.jsp" />
    
    <div class="container">
        <h1>Appointments for Blood Request</h1>
        
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
            List<Appointment> appointments = (List<Appointment>) request.getAttribute("appointments");
            Map<Integer, User> donorUserMap = (Map<Integer, User>) request.getAttribute("donorUserMap");
            
            if(bloodRequest != null) {
        %>
        <div class="request-info">
            <h3>Blood Request #<%= bloodRequest.getId() %></h3>
            <div class="appointment-details">
                <div class="appointment-detail">
                    <label>Blood Group:</label>
                    <span><%= bloodRequest.getBloodGroup() %></span>
                </div>
                <div class="appointment-detail">
                    <label>Quantity:</label>
                    <span><%= bloodRequest.getQuantity() %> units</span>
                </div>
                <div class="appointment-detail">
                    <label>Status:</label>
                    <span class="status-badge status-<%= bloodRequest.getStatus().toLowerCase() %>">
                        <%= bloodRequest.getStatus() %>
                    </span>
                </div>
                <div class="appointment-detail">
                    <label>Patient:</label>
                    <span><%= bloodRequest.getPatientName() %></span>
                </div>
                <div class="appointment-detail">
                    <label>Hospital:</label>
                    <span><%= bloodRequest.getHospitalName() %></span>
                </div>
                <div class="appointment-detail">
                    <label>Required By:</label>
                    <span><%= bloodRequest.getRequiredDate() %></span>
                </div>
            </div>
        </div>
        
        <div class="action-buttons" style="margin-bottom: 20px;">
            <a href="${pageContext.request.contextPath}/user/my-requests" class="btn btn-secondary">Back to My Requests</a>
        </div>
        
        <h2>Scheduled Appointments</h2>
        
        <% 
            if(appointments != null && !appointments.isEmpty()) {
                for(Appointment appointment : appointments) {
        %>
        <div class="appointment-card <%= appointment.getStatus().toLowerCase() %>">
            <div class="appointment-header">
                <div>
                    <h3>Appointment #<%= appointment.getId() %></h3>
                    <span class="status-badge status-<%= appointment.getStatus().toLowerCase() %>">
                        <%= appointment.getStatus() %>
                    </span>
                </div>
            </div>
            
            <div class="appointment-details">
                <div class="appointment-detail">
                    <label>Date:</label>
                    <span><%= appointment.getAppointmentDate() %></span>
                </div>
                <div class="appointment-detail">
                    <label>Time:</label>
                    <span><%= appointment.getAppointmentTime() %></span>
                </div>
                <% if(appointment.getCreatedAt() != null) { %>
                <div class="appointment-detail">
                    <label>Created:</label>
                    <span><%= appointment.getCreatedAt() %></span>
                </div>
                <% } %>
                <% if(appointment.getNotes() != null && !appointment.getNotes().isEmpty()) { 
                    // Remove the blood request ID tag from notes before displaying
                    String displayNotes = appointment.getNotes().replaceAll("\\[BloodRequestID:\\d+\\]", "").trim();
                    if(!displayNotes.isEmpty()) {
                %>
                <div class="appointment-detail">
                    <label>Notes:</label>
                    <span><%= displayNotes %></span>
                </div>
                <% } } %>
            </div>
            
            <% 
                // Display donor information if available
                User donorUser = donorUserMap != null ? donorUserMap.get(appointment.getDonorId()) : null;
                if(donorUser != null) {
            %>
            <div class="donor-info">
                <h4>Donor Information</h4>
                <div class="appointment-details">
                    <div class="appointment-detail">
                        <label>Name:</label>
                        <span><%= donorUser.getName() %></span>
                    </div>
                    <div class="appointment-detail">
                        <label>Contact:</label>
                        <span><%= donorUser.getPhone() %></span>
                    </div>
                    <div class="appointment-detail">
                        <label>Email:</label>
                        <span><%= donorUser.getEmail() %></span>
                    </div>
                </div>
            </div>
            <% } %>
        </div>
        <% 
                }
            } else {
        %>
        <div class="no-appointments">
            <i class="fa fa-calendar-times-o"></i>
            <p>No appointments have been scheduled for this blood request yet.</p>
            <p>When donors respond to your request, their appointments will appear here.</p>
        </div>
        <% } %>
        
        <% } else { %>
        <div class="alert alert-danger">
            Blood request not found.
        </div>
        <% } %>
    </div>
    
    <jsp:include page="../common/footer.jsp" />
</body>
</html>
