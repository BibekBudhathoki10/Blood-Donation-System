<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Appointment, model.Donor, model.User, model.BloodRequest, java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>View Appointment - Blood Donation System</title>
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
            font-weight: bold;
            color: #e74c3c;
            margin: 0;
        }
        
        .appointment-status {
            display: inline-block;
            padding: 5px 12px;
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
        
        .appointment-section {
            margin-bottom: 25px;
        }
        
        .section-title {
            font-size: 1.2rem;
            font-weight: bold;
            margin-bottom: 15px;
            color: #333;
            border-bottom: 1px solid #eee;
            padding-bottom: 5px;
        }
        
        .info-row {
            display: flex;
            margin-bottom: 10px;
        }
        
        .info-label {
            width: 150px;
            font-weight: bold;
            color: #555;
        }
        
        .info-value {
            flex: 1;
            color: #333;
        }
        
        .blood-request-info {
            background-color: #f8f9fa;
            border-left: 3px solid #e74c3c;
            padding: 15px;
            margin-top: 10px;
            border-radius: 4px;
        }
        
        .action-buttons {
            margin-top: 30px;
            display: flex;
            gap: 10px;
        }
    </style>
</head>
<body>
    <jsp:include page="../common/header.jsp" />
    
    <div class="container">
        <% 
            Appointment appointment = (Appointment) request.getAttribute("appointment");
            Donor donor = (Donor) request.getAttribute("donor");
            User donorUser = (User) request.getAttribute("donorUser");
            BloodRequest bloodRequest = (BloodRequest) request.getAttribute("bloodRequest");
            
            if(appointment != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
                SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
                SimpleDateFormat fullDateFormat = new SimpleDateFormat("MMMM d, yyyy h:mm a");
        %>
        <div class="appointment-container">
            <div class="appointment-header">
                <h1 class="appointment-title">Appointment #<%= appointment.getId() %></h1>
                <span class="appointment-status status-<%= appointment.getStatus().toLowerCase() %>"><%= appointment.getStatus() %></span>
            </div>
            
            <div class="appointment-section">
                <h2 class="section-title">Appointment Details</h2>
                <div class="info-row">
                    <div class="info-label">Date:</div>
                    <div class="info-value"><%= dateFormat.format(appointment.getAppointmentDate()) %></div>
                </div>
                <div class="info-row">
                    <div class="info-label">Time:</div>
                    <div class="info-value"><%= timeFormat.format(appointment.getAppointmentTime()) %></div>
                </div>
                <div class="info-row">
                    <div class="info-label">Created:</div>
                    <div class="info-value"><%= fullDateFormat.format(appointment.getCreatedAt()) %></div>
                </div>
                <% if(appointment.getNotes() != null && !appointment.getNotes().isEmpty()) { %>
                <div class="info-row">
                    <div class="info-label">Notes:</div>
                    <div class="info-value"><%= appointment.getNotes() %></div>
                </div>
                <% } %>
            </div>
            
            <% if(donor != null && donorUser != null) { %>
            <div class="appointment-section">
                <h2 class="section-title">Donor Information</h2>
                <div class="info-row">
                    <div class="info-label">Name:</div>
                    <div class="info-value"><%= donorUser.getName() %></div>
                </div>
                <div class="info-row">
                    <div class="info-label">Blood Group:</div>
                    <div class="info-value"><%= donor.getBloodGroup() %></div>
                </div>
                <div class="info-row">
                    <div class="info-label">Contact:</div>
                    <div class="info-value"><%= donorUser.getPhone() %></div>
                </div>
                <div class="info-row">
                    <div class="info-label">Email:</div>
                    <div class="info-value"><%= donorUser.getEmail() %></div>
                </div>
            </div>
            <% } %>
            
            <% if(bloodRequest != null) { %>
            <div class="appointment-section">
                <h2 class="section-title">Blood Request Information</h2>
                <div class="blood-request-info">
                    <div class="info-row">
                        <div class="info-label">Request ID:</div>
                        <div class="info-value">#<%= bloodRequest.getId() %></div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Patient Name:</div>
                        <div class="info-value"><%= bloodRequest.getPatientName() %></div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Blood Group:</div>
                        <div class="info-value"><%= bloodRequest.getBloodGroup() %></div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Quantity:</div>
                        <div class="info-value"><%= bloodRequest.getQuantity() %> units</div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Hospital:</div>
                        <div class="info-value"><%= bloodRequest.getHospitalName() %></div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Required Date:</div>
                        <div class="info-value"><%= dateFormat.format(bloodRequest.getRequiredDate()) %></div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Status:</div>
                        <div class="info-value"><%= bloodRequest.getStatus() %></div>
                    </div>
                </div>
            </div>
            <% } %>
            
            <div class="action-buttons">
                <% if("scheduled".equalsIgnoreCase(appointment.getStatus())) { %>
                    <a href="${pageContext.request.contextPath}/user/cancel-appointment?id=<%= appointment.getId() %>" class="btn btn-danger" onclick="return confirm('Are you sure you want to cancel this appointment?')">Cancel Appointment</a>
                    <a href="${pageContext.request.contextPath}/user/reschedule-appointment?id=<%= appointment.getId() %>" class="btn btn-secondary">Reschedule</a>
                <% } %>
                <a href="${pageContext.request.contextPath}/user/scheduled-donations" class="btn btn-primary">Back to Scheduled Donations</a>
            </div>
        </div>
        <% } else { %>
        <div class="alert alert-danger">
            Appointment not found.
        </div>
        <div class="text-center" style="margin-top: 30px;">
            <a href="${pageContext.request.contextPath}/user/scheduled-donations" class="btn btn-primary">Back to Scheduled Donations</a>
        </div>
        <% } %>
    </div>
    
    <jsp:include page="../common/footer.jsp" />
</body>
</html>
