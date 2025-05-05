<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.User, model.BloodRequest, java.util.List, java.text.SimpleDateFormat, java.util.Date" %>
<%@ page import="model.DonationEvent" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Dashboard - Blood Donation System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .dashboard-container {
            display: flex;
            flex-wrap: wrap;
            gap: 20px;
            margin-top: 20px;
        }
        
        .dashboard-card {
            flex: 1;
            min-width: 300px;
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            padding: 20px;
            margin-bottom: 20px;
        }
        
        .dashboard-card h2 {
            color: #e74c3c;
            margin-top: 0;
            border-bottom: 1px solid #eee;
            padding-bottom: 10px;
        }
        
        .dashboard-card .btn {
            margin-top: 15px;
        }
        
        .request-item {
            border: 1px solid #eee;
            border-radius: 5px;
            padding: 15px;
            margin-bottom: 15px;
            background-color: #f9f9f9;
        }
        
        .request-header {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
        }
        
        .blood-type {
            background-color: #e74c3c;
            color: white;
            padding: 5px 10px;
            border-radius: 3px;
            font-weight: bold;
        }
        
        .request-status {
            padding: 3px 8px;
            border-radius: 3px;
            font-size: 0.9em;
        }
        
        .status-pending {
            background-color: #f39c12;
            color: white;
        }
        
        .status-approved {
            background-color: #2ecc71;
            color: white;
        }
        
        .status-rejected {
            background-color: #e74c3c;
            color: white;
        }
        
        .status-fulfilled {
            background-color: #3498db;
            color: white;
        }
        
        .request-details {
            margin-top: 10px;
            font-size: 0.9em;
        }
        
        .request-actions {
            margin-top: 15px;
            display: flex;
            gap: 10px;
        }
        
        .stats-container {
            display: flex;
            justify-content: space-between;
            margin-bottom: 20px;
        }
        
        .stat-box {
            flex: 1;
            background-color: #f8f9fa;
            border-radius: 5px;
            padding: 15px;
            text-align: center;
            margin: 0 5px;
        }
        
        .stat-box h3 {
            margin: 0;
            color: #6c757d;
            font-size: 0.9em;
        }
        
        .stat-box p {
            margin: 10px 0 0;
            font-size: 1.5em;
            font-weight: bold;
            color: #e74c3c;
        }
        
        .upcoming-events {
            margin-top: 20px;
        }
        
        .event-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 10px 0;
            border-bottom: 1px solid #eee;
        }
        
        .event-date {
            background-color: #3498db;
            color: white;
            padding: 5px 10px;
            border-radius: 3px;
            font-size: 0.9em;
        }
    </style>
</head>
<body>
  <jsp:include page="../common/header.jsp" />
  
  <div class="container">
      <h1>Welcome, 
      <% 
          // Get user from session instead of request attribute
          User user = (User)session.getAttribute("user");
          if(user != null) {
              out.print(user.getName());
          } else {
              out.print("User");
          }
      %>
      </h1>
      
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
      
      <div class="stats-container">
          <div class="stat-box">
              <h3>Total Requests</h3>
              <p><%= request.getAttribute("totalRequests") != null ? request.getAttribute("totalRequests") : 0 %></p>
          </div>
          <div class="stat-box">
              <h3>Pending Requests</h3>
              <p><%= request.getAttribute("pendingRequests") != null ? request.getAttribute("pendingRequests") : 0 %></p>
          </div>
          <div class="stat-box">
              <h3>Fulfilled Requests</h3>
              <p><%= request.getAttribute("fulfilledRequests") != null ? request.getAttribute("fulfilledRequests") : 0 %></p>
          </div>
      </div>
      
      <div class="dashboard-container">
          <div class="dashboard-card">
              <h2>Quick Actions</h2>
              <p>What would you like to do today?</p>
              <a href="${pageContext.request.contextPath}/user/request-blood" class="btn btn-primary">Request Blood</a>
              <a href="${pageContext.request.contextPath}/user/my-requests" class="btn btn-secondary">View My Requests</a>
              <a href="${pageContext.request.contextPath}/user/profile" class="btn btn-secondary">Edit Profile</a>
              <a href="${pageContext.request.contextPath}/user/events" class="btn btn-secondary">View Donation Events</a>
          </div>
          
          <div class="dashboard-card">
              <h2>Recent Blood Requests</h2>
              <% 
                  List<BloodRequest> requests = (List<BloodRequest>) request.getAttribute("requests");
                  if(requests != null && !requests.isEmpty()) {
                      for(int i = 0; i < Math.min(3, requests.size()); i++) {
                          BloodRequest bloodRequest = requests.get(i);
              %>
              <div class="request-item">
                  <div class="request-header">
                      <span class="blood-type"><%= bloodRequest.getBloodGroup() %> (<%= bloodRequest.getQuantity() %> units)</span>
                      <span class="request-status status-<%= bloodRequest.getStatus().toLowerCase() %>"><%= bloodRequest.getStatus() %></span>
                  </div>
                  <div class="request-details">
                      <p>Patient: <%= bloodRequest.getPatientName() %> | Hospital: <%= bloodRequest.getHospitalName() %></p>
                      <p>Required by: <%= bloodRequest.getRequiredDate() %></p>
                  </div>
                  <div class="request-actions">
                      <% if("pending".equals(bloodRequest.getStatus())) { %>
                          <a href="${pageContext.request.contextPath}/user/edit-request?id=<%= bloodRequest.getId() %>" class="btn btn-sm btn-secondary">Edit</a>
                          <a href="${pageContext.request.contextPath}/user/cancel-request?id=<%= bloodRequest.getId() %>" class="btn btn-sm btn-danger" onclick="return confirm('Are you sure you want to cancel this request?')">Cancel</a>
                      <% } %>
                      <a href="${pageContext.request.contextPath}/user/view-request?id=<%= bloodRequest.getId() %>" class="btn btn-sm btn-primary">View Details</a>
                  </div>
              </div>
              <% 
                      }
                      if(requests.size() > 3) {
              %>
                  <a href="${pageContext.request.contextPath}/user/my-requests" class="btn btn-sm btn-link">View all requests</a>
              <%
                      }
                  } else {
              %>
                  <p>You haven't made any blood requests yet.</p>
                  <a href="${pageContext.request.contextPath}/user/request-blood" class="btn btn-primary">Make a Request</a>
              <% } %>
          </div>
      </div>
      
      <div class="dashboard-card">
          <h2>Upcoming Donation Events</h2>
          <% 
              List<DonationEvent> events = (List<DonationEvent>) request.getAttribute("upcomingEvents");
              if(events != null && !events.isEmpty()) {
                  for(DonationEvent event : events) {
          %>
          <div class="event-item">
              <div>
                  <h4><%= event.getTitle() %></h4>
                  <p><%= event.getLocation() %></p>
              </div>
              <span class="event-date"><%= event.getEventDate() %></span>
              <a href="${pageContext.request.contextPath}/user/events/view?id=<%= event.getId() %>" class="btn btn-sm btn-primary">View Details</a>
          </div>
          <% 
                  }
              } else {
          %>
              <p>No upcoming donation events.</p>
          <% } %>
          <a href="${pageContext.request.contextPath}/user/events" class="btn btn-secondary">View All Events</a>
      </div>
  </div>
  
  <jsp:include page="../common/footer.jsp" />
</body>
</html>
