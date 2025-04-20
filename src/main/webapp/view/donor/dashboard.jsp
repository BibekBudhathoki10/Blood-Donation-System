<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Donor, model.User, model.Appointment, model.DonationEvent, java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Donor Dashboard - Blood Donation System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <jsp:include page="../common/header.jsp" />
    
    <div class="container">
        <h1>Donor Dashboard</h1>
        
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
            List<Appointment> upcomingAppointments = (List<Appointment>) request.getAttribute("upcomingAppointments");
            int donationCount = (int) request.getAttribute("donationCount");
            List<DonationEvent> upcomingEvents = (List<DonationEvent>) request.getAttribute("upcomingEvents");
        %>
        
        <div class="dashboard">
            <div class="dashboard-card">
                <h3>Welcome, <%= user.getName() %></h3>
                <p>Blood Group: <strong><%= donor.getBloodGroup() %></strong></p>
                <p>Status: <strong><%= donor.isAvailable() ? "Available" : "Not Available" %></strong></p>
                <p>Last Donation: <strong><%= donor.getLastDonationDate() != null ? donor.getLastDonationDate() : "Never" %></strong></p>
                <p><a href="${pageContext.request.contextPath}/donor/profile" class="btn btn-primary">Edit Profile</a></p>
            </div>
            
            <div class="dashboard-card">
                <h3>Donation Statistics</h3>
                <div class="count"><%= donationCount %></div>
                <p class="description">Total Donations</p>
                <p><a href="${pageContext.request.contextPath}/donor/donation-history" class="btn btn-secondary">View History</a></p>
            </div>
            
            <div class="dashboard-card">
                <h3>Quick Actions</h3>
                <p><a href="${pageContext.request.contextPath}/donor/schedule-appointment" class="btn btn-primary">Schedule Donation</a></p>
                <p><a href="${pageContext.request.contextPath}/donor/view-requests" class="btn btn-secondary">View Blood Requests</a></p>
                <p><a href="${pageContext.request.contextPath}/donor/events" class="btn btn-secondary">View Donation Events</a></p>
            </div>
        </div>  class="btn btn-secondary">View Donation Events</a></p>
            </div>
        </div>
        
        <div class="section">
            <h2>Upcoming Appointments</h2>
            <% if(upcomingAppointments != null && !upcomingAppointments.isEmpty()) { %>
                <div class="table-container">
                    <table>
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Time</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for(Appointment appointment : upcomingAppointments) { %>
                                <tr>
                                    <td><%= appointment.getAppointmentDate() %></td>
                                    <td><%= appointment.getAppointmentTime() %></td>
                                    <td><%= appointment.getStatus() %></td>
                                    <td class="table-actions">
                                        <a href="${pageContext.request.contextPath}/donor/reschedule-appointment?id=<%= appointment.getId() %>" class="action-edit">Reschedule</a>
                                        <a href="${pageContext.request.contextPath}/donor/cancel-appointment?id=<%= appointment.getId() %>" class="action-delete" onclick="return confirm('Are you sure you want to cancel this appointment?')">Cancel</a>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } else { %>
                <p>No upcoming appointments. <a href="${pageContext.request.contextPath}/donor/schedule-appointment">Schedule a donation</a>.</p>
            <% } %>
        </div>
        
        <div class="section">
            <h2>Upcoming Donation Events</h2>
            <% if(upcomingEvents != null && !upcomingEvents.isEmpty()) { %>
                <div class="table-container">
                    <table>
                        <thead>
                            <tr>
                                <th>Event</th>
                                <th>Date</th>
                                <th>Location</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for(DonationEvent event : upcomingEvents) { %>
                                <tr>
                                    <td><%= event.getTitle() %></td>
                                    <td><%= event.getEventDate() %></td>
                                    <td><%= event.getLocation() %></td>
                                    <td class="table-actions">
                                        <a href="${pageContext.request.contextPath}/donor/events/view?id=<%= event.getId() %>" class="action-view">View Details</a>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } else { %>
                <p>No upcoming events.</p>
            <% } %>
        </div>
    </div>
    
    <jsp:include page="../common/footer.jsp" />
</body>
</html>

