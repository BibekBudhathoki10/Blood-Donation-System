<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.BloodRequest, model.Appointment, java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Blood Donation System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <jsp:include page="../common/header.jsp" />
    
    <div class="container">
        <h1>Admin Dashboard</h1>
        
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
        
        <div class="dashboard">
            <div class="dashboard-card">
                <h3>Donors</h3>
                <div class="count"><%= request.getAttribute("donorCount") %></div>
                <p class="description">Registered Donors</p>
                <p><a href="${pageContext.request.contextPath}/admin/manage-donors" class="btn btn-primary">Manage Donors</a></p>
            </div>
            
            <div class="dashboard-card">
                <h3>Blood Requests</h3>
                <div class="count"><%= request.getAttribute("activeRequestsCount") %></div>
                <p class="description">Active Requests</p>
                <p><a href="${pageContext.request.contextPath}/admin/manage-requests" class="btn btn-primary">Manage Requests</a></p>
            </div>
            
            <div class="dashboard-card">
                <h3>Appointments</h3>
                <div class="count"><%= request.getAttribute("upcomingAppointmentsCount") %></div>
                <p class="description">Upcoming Appointments</p>
                <p><a href="${pageContext.request.contextPath}/admin/manage-appointments" class="btn btn-primary">Manage Appointments</a></p>
            </div>
            
            <div class="dashboard-card">
                <h3>Events</h3>
                <div class="count"><%= request.getAttribute("upcomingEventsCount") %></div>
                <p class="description">Upcoming Events</p>
                <p><a href="${pageContext.request.contextPath}/admin/events" class="btn btn-primary">Manage Events</a></p>
            </div>
        </div>
        
        <div class="section">
            <h2>Blood Inventory</h2>
            <div class="blood-types-grid">
                <div class="blood-type">
                    <h3>A+</h3>
                    <p><%= request.getAttribute("aPositiveCount") %> units</p>
                </div>
                <div class="blood-type">
                    <h3>A-</h3>
                    <p><%= request.getAttribute("aNegativeCount") %> units</p>
                </div>
                <div class="blood-type">
                    <h3>B+</h3>
                    <p><%= request.getAttribute("bPositiveCount") %> units</p>
                </div>
                <div class="blood-type">
                    <h3>B-</h3>
                    <p><%= request.getAttribute("bNegativeCount") %> units</p>
                </div>
                <div class="blood-type">
                    <h3>AB+</h3>
                    <p><%= request.getAttribute("abPositiveCount") %> units</p>
                </div>
                <div class="blood-type">
                    <h3>AB-</h3>
                    <p><%= request.getAttribute("abNegativeCount") %> units</p>
                </div>
                <div class="blood-type">
                    <h3>O+</h3>
                    <p><%= request.getAttribute("oPositiveCount") %> units</p>
                </div>
                <div class="blood-type">
                    <h3>O-</h3>
                    <p><%= request.getAttribute("oNegativeCount") %> units</p>
                </div>
            </div>
            <p class="text-center">
                <a href="${pageContext.request.contextPath}/inventory/list" class="btn btn-primary">Manage Inventory</a>
            </p>
        </div>
        
        <div class="section">
            <h2>Recent Blood Requests</h2>
            <% 
                List<BloodRequest> recentRequests = (List<BloodRequest>) request.getAttribute("recentRequests");
                if(recentRequests != null && !recentRequests.isEmpty()) { 
            %>
                <div class="table-container">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Blood Group</th>
                                <th>Quantity</th>
                                <th>Urgency</th>
                                <th>Status</th>
                                <th>Required Date</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for(BloodRequest bloodRequest : recentRequests) { %>
                                <tr>
                                    <td><%= bloodRequest.getId() %></td>
                                    <td><%= bloodRequest.getBloodGroup() %></td>
                                    <td><%= bloodRequest.getQuantity() %></td>
                                    <td><%= bloodRequest.getUrgency() %></td>
                                    <td><%= bloodRequest.getStatus() %></td>
                                    <td><%= bloodRequest.getRequiredDate() %></td>
                                    <td class="table-actions">
                                        <a href="${pageContext.request.contextPath}/admin/manage-requests?id=<%= bloodRequest.getId() %>" class="action-view">View</a>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } else { %>
                <p>No recent blood requests.</p>
            <% } %>
        </div>
        
        <div class="section">
            <h2>Upcoming Appointments</h2>
            <% 
                List<Appointment> upcomingAppointments = (List<Appointment>) request.getAttribute("upcomingAppointments");
                if(upcomingAppointments != null && !upcomingAppointments.isEmpty()) { 
            %>
                <div class="table-container">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Donor ID</th>
                                <th>Date</th>
                                <th>Time</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for(Appointment appointment : upcomingAppointments) { %>
                                <tr>
                                    <td><%= appointment.getId() %></td>
                                    <td><%= appointment.getDonorId() %></td>
                                    <td><%= appointment.getAppointmentDate() %></td>
                                    <td><%= appointment.getAppointmentTime() %></td>
                                    <td><%= appointment.getStatus() %></td>
                                    <td class="table-actions">
                                        <a href="${pageContext.request.contextPath}/admin/manage-appointments?id=<%= appointment.getId() %>" class="action-view">View</a>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } else { %>
                <p>No upcoming appointments.</p>
            <% } %>
        </div>
    </div>
    
    <jsp:include page="../common/footer.jsp" />
</body>
</html>

