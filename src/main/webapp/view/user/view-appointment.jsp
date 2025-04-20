<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Appointment, java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Appointment Details - Blood Donation System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .appointment-container {
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            padding: 30px;
            margin-top: 30px;
        }
        
        .appointment-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            border-bottom: 1px solid #eee;
            padding-bottom: 15px;
        }
        
        .appointment-title {
            font-size: 1.5rem;
            color: #e74c3c;
            margin: 0;
        }
        
        .appointment-status {
            display: inline-block;
            padding: 5px 10px;
            border-radius: 4px;
            font-size: 0.9rem;
            font-weight: bold;
            text-transform: uppercase;
        }
        
        .status-scheduled {
            background-color: #3498db;
            color: white;
        }
        
        .status-completed {
            background-color: #2ecc71;
            color: white;
        }
        
        .status-cancelled {
            background-color: #e74c3c;
            color: white;
        }
        
        .status-no-show {
            background-color: #f39c12;
            color: white;
        }
        
        .appointment-details {
            margin-bottom: 30px;
        }
        
        .detail-row {
            display: flex;
            margin-bottom: 15px;
        }
        
        .detail-label {
            width: 200px;
            font-weight: bold;
            color: #555;
        }
        
        .detail-value {
            flex: 1;
            color: #333;
        }
        
        .appointment-notes {
            background-color: #f8f9fa;
            padding: 15px;
            border-radius: 4px;
            margin-bottom: 30px;
        }
        
        .appointment-notes h3 {
            margin-top: 0;
            color: #555;
            font-size: 1.1rem;
        }
        
        .appointment-notes p {
            margin-bottom: 0;
            color: #666;
        }
        
        .action-buttons {
            display: flex;
            gap: 15px;
            margin-top: 20px;
        }
    </style>
</head>
<body>
    <jsp:include page="../common/header.jsp" />
    
    <div class="container">
        <h1>Appointment Details</h1>
        
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
            Appointment appointment = (Appointment) request.getAttribute("appointment");
            if(appointment != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
                SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
                SimpleDateFormat fullDateFormat = new SimpleDateFormat("MMMM d, yyyy h:mm a");
        %>
        <div class="appointment-container">
            <div class="appointment-header">
                <h2 class="appointment-title">Appointment #<%= appointment.getId() %></h2>
                <span class="appointment-status status-<%= appointment.getStatus().toLowerCase() %>">
                    <%= appointment.getStatus() %>
                </span>
            </div>
            
            <div class="appointment-details">
                <div class="detail-row">
                    <div class="detail-label">Appointment Date:</div>
                    <div class="detail-value"><%= dateFormat.format(appointment.getAppointmentDate()) %></div>
                </div>
                
                <div class="detail-row">
                    <div class="detail-label">Appointment Time:</div>
                    <div class="detail-value"><%= timeFormat.format(appointment.getAppointmentTime()) %></div>
                </div>
                
                <div class="detail-row">
                    <div class="detail-label">Created On:</div>
                    <div class="detail-value"><%= fullDateFormat.format(appointment.getCreatedAt()) %></div>
                </div>
            </div>
            
            <% if(appointment.getNotes() != null && !appointment.getNotes().isEmpty()) { %>
            <div class="appointment-notes">
                <h3>Notes</h3>
                <p><%= appointment.getNotes() %></p>
            </div>
            <% } %>
            
            <div class="action-buttons">
                <% if("scheduled".equalsIgnoreCase(appointment.getStatus())) { %>
                    <a href="${pageContext.request.contextPath}/user/cancel-appointment?id=<%= appointment.getId() %>" class="btn btn-danger" onclick="return confirm('Are you sure you want to cancel this appointment?')">Cancel Appointment</a>
                    <a href="${pageContext.request.contextPath}/user/reschedule-appointment?id=<%= appointment.getId() %>" class="btn btn-secondary">Reschedule</a>
                <% } %>
                <a href="${pageContext.request.contextPath}/user/scheduled-donations" class="btn btn-primary">Back to Appointments</a>
            </div>
        </div>
        <% } else { %>
        <div class="alert alert-danger">
            Appointment not found.
        </div>
        <div class="text-center" style="margin-top: 30px;">
            <a href="${pageContext.request.contextPath}/user/scheduled-donations" class="btn btn-primary">Back to Appointments</a>
        </div>
        <% } %>
    </div>
    
    <jsp:include page="../common/footer.jsp" />
</body>
</html>
