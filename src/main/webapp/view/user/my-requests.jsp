<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.BloodRequest, model.Appointment, model.User, java.util.List, java.util.Map, java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Blood Requests - Blood Donation System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .request-container {
            margin-top: 30px;
        }
        
        .request-card {
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            padding: 20px;
            margin-bottom: 20px;
        }
        
        .request-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
            border-bottom: 1px solid #eee;
            padding-bottom: 10px;
        }
        
        .request-title {
            font-size: 1.2rem;
            font-weight: bold;
            color: #e74c3c;
            margin: 0;
        }
        
        .request-date {
            background-color: #f8f9fa;
            padding: 5px 10px;
            border-radius: 4px;
            font-size: 0.9rem;
            color: #666;
        }
        
        .request-details {
            margin-bottom: 15px;
        }
        
        .request-details p {
            margin: 5px 0;
            color: #555;
        }
        
        .request-details strong {
            color: #333;
        }
        
        .request-status {
            display: inline-block;
            padding: 3px 8px;
            border-radius: 3px;
            font-size: 0.8rem;
            font-weight: bold;
            text-transform: uppercase;
        }
        
        .status-pending {
            background-color: #f39c12;
            color: white;
        }
        
        .status-approved, .status-in-progress {
            background-color: #3498db;
            color: white;
        }
        
        .status-completed {
            background-color: #2ecc71;
            color: white;
        }
        
        .status-cancelled, .status-rejected {
            background-color: #e74c3c;
            color: white;
        }
        
        .no-requests {
            text-align: center;
            padding: 30px;
            background-color: #f8f9fa;
            border-radius: 8px;
            color: #666;
        }
        
        .filter-container {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        
        .filter-group {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .filter-label {
            font-weight: bold;
            color: #555;
        }
        
        .filter-select {
            padding: 8px 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            background-color: #fff;
        }
        
        .action-buttons {
            margin-top: 15px;
            display: flex;
            gap: 10px;
        }
        
        .appointment-section {
            margin-top: 15px;
            padding-top: 15px;
            border-top: 1px dashed #ddd;
        }
        
        .appointment-section h4 {
            color: #3498db;
            margin-bottom: 10px;
        }
        
        .appointment-list {
            background-color: #f8f9fa;
            border-radius: 4px;
            padding: 10px;
        }
        
        .appointment-item {
            padding: 8px;
            margin-bottom: 8px;
            border-bottom: 1px solid #eee;
        }
        
        .appointment-item:last-child {
            border-bottom: none;
            margin-bottom: 0;
        }
        
        .donor-info {
            font-style: italic;
            color: #666;
        }
    </style>
</head>
<body>
    <jsp:include page="../common/header.jsp" />
    
    <div class="container">
        <h1>My Blood Requests</h1>
        
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
        
        <div class="filter-container">
            <div class="filter-group">
                <span class="filter-label">Filter by Status:</span>
                <select id="statusFilter" class="filter-select" onchange="filterRequests()">
                    <option value="all">All</option>
                    <option value="pending">Pending</option>
                    <option value="approved">Approved</option>
                    <option value="in-progress">In Progress</option>
                    <option value="completed">Completed</option>
                    <option value="cancelled">Cancelled</option>
                    <option value="rejected">Rejected</option>
                </select>
            </div>
            
            <div class="filter-group">
                <span class="filter-label">Sort by:</span>
                <select id="sortBy" class="filter-select" onchange="sortRequests()">
                    <option value="date-asc">Date (Oldest First)</option>
                    <option value="date-desc" selected>Date (Newest First)</option>
                </select>
            </div>
        </div>
        
        <div class="request-container">
            <% 
                List<BloodRequest> requests = (List<BloodRequest>) request.getAttribute("requests");
                Map<Integer, List<Appointment>> requestAppointmentsMap = (Map<Integer, List<Appointment>>) request.getAttribute("requestAppointmentsMap");
                Map<Integer, User> donorUserMap = (Map<Integer, User>) request.getAttribute("donorUserMap");
                
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
                SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
                
                if(requests != null && !requests.isEmpty()) {
                    for(BloodRequest bloodRequest : requests) {
            %>
            <div class="request-card" data-status="<%= bloodRequest.getStatus().toLowerCase() %>">
                <div class="request-header">
                    <h3 class="request-title">Blood Request #<%= bloodRequest.getId() %></h3>
                    <span class="request-date"><%= dateFormat.format(bloodRequest.getRequestDate()) %></span>
                </div>
                <div class="request-details">
                    <p><strong>Patient:</strong> <%= bloodRequest.getPatientName() %></p>
                    <p><strong>Blood Group:</strong> <%= bloodRequest.getBloodGroup() %></p>
                    <p><strong>Quantity:</strong> <%= bloodRequest.getQuantity() %> units</p>
                    <p><strong>Urgency:</strong> <%= bloodRequest.getUrgency() %></p>
                    <p><strong>Hospital:</strong> <%= bloodRequest.getHospitalName() %></p>
                    <p><strong>Required Date:</strong> <%= dateFormat.format(bloodRequest.getRequiredDate()) %></p>
                    <p><strong>Status:</strong> <span class="request-status status-<%= bloodRequest.getStatus().toLowerCase() %>"><%= bloodRequest.getStatus() %></span></p>
                </div>
                
                <% 
                    // Display appointments for this blood request
                    List<Appointment> appointments = requestAppointmentsMap.get(bloodRequest.getId());
                    if(appointments != null && !appointments.isEmpty()) {
                %>
                <div class="appointment-section">
                    <h4>Donor Appointments (<%= appointments.size() %>)</h4>
                    <div class="appointment-list">
                        <% for(Appointment appointment : appointments) { %>
                            <div class="appointment-item">
                                <p><strong>Date:</strong> <%= dateFormat.format(appointment.getAppointmentDate()) %> at <%= timeFormat.format(appointment.getAppointmentTime()) %></p>
                                <p><strong>Status:</strong> <span class="request-status status-<%= appointment.getStatus().toLowerCase() %>"><%= appointment.getStatus() %></span></p>
                                <% 
                                    User donorUser = donorUserMap.get(appointment.getDonorId());
                                    if(donorUser != null) {
                                %>
                                <p class="donor-info">Donor: <%= donorUser.getName() %> (<%= donorUser.getPhone() %>)</p>
                                <% } %>
                            </div>
                        <% } %>
                    </div>
                </div>
                <% } %>
                
                <div class="action-buttons">
                    <% if("pending".equals(bloodRequest.getStatus())) { %>
                        <a href="${pageContext.request.contextPath}/user/edit-request?id=<%= bloodRequest.getId() %>" class="btn btn-secondary">Edit Request</a>
                    <% } %>
                    
                    <% if("pending".equals(bloodRequest.getStatus()) || "approved".equals(bloodRequest.getStatus())) { %>
                        <a href="${pageContext.request.contextPath}/user/cancel-request?id=<%= bloodRequest.getId() %>" class="btn btn-danger" onclick="return confirm('Are you sure you want to cancel this request?')">Cancel Request</a>
                    <% } %>
                    
                    <a href="${pageContext.request.contextPath}/user/view-request-appointments?id=<%= bloodRequest.getId() %>" class="btn btn-primary">View Appointments</a>
                </div>
            </div>
            <% 
                    }
                } else {
            %>
            <div class="no-requests">
                <h3>No blood requests found</h3>
                <p>You haven't made any blood requests yet.</p>
            </div>
            <% } %>
        </div>
        
        <div class="text-center" style="margin-top: 30px;">
            <a href="${pageContext.request.contextPath}/user/request-blood" class="btn btn-primary">Request Blood</a>
            <a href="${pageContext.request.contextPath}/user/dashboard" class="btn btn-secondary">Back to Dashboard</a>
        </div>
    </div>
    
    <jsp:include page="../common/footer.jsp" />
    
    <script>
        function filterRequests() {
            const status = document.getElementById('statusFilter').value;
            const cards = document.querySelectorAll('.request-card');
            
            cards.forEach(card => {
                if (status === 'all' || card.dataset.status === status) {
                    card.style.display = 'block';
                } else {
                    card.style.display = 'none';
                }
            });
            
            // Show "no requests" message if all cards are hidden
            const visibleCards = document.querySelectorAll('.request-card[style="display: block;"]');
            const noRequests = document.querySelector('.no-requests');
            
            if (visibleCards.length === 0 && cards.length > 0) {
                if (!noRequests) {
                    const container = document.querySelector('.request-container');
                    const noRequestsDiv = document.createElement('div');
                    noRequestsDiv.className = 'no-requests';
                    noRequestsDiv.innerHTML = `
                        <h3>No ${status} requests found</h3>
                        <p>You don't have any blood requests with status "${status}" at the moment.</p>
                    `;
                    container.appendChild(noRequestsDiv);
                }
            } else if (noRequests && visibleCards.length > 0) {
                noRequests.style.display = 'none';
            }
        }
        
        function sortRequests() {
            const sortBy = document.getElementById('sortBy').value;
            const container = document.querySelector('.request-container');
            const cards = Array.from(document.querySelectorAll('.request-card'));
            
            cards.sort((a, b) => {
                const dateA = new Date(a.querySelector('.request-date').textContent);
                const dateB = new Date(b.querySelector('.request-date').textContent);
                
                if (sortBy === 'date-asc') {
                    return dateA - dateB;
                } else {
                    return dateB - dateA;
                }
            });
            
            // Remove all cards
            cards.forEach(card => card.remove());
            
            // Add sorted cards back
            cards.forEach(card => container.appendChild(card));
        }
    </script>
</body>
</html>
