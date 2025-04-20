<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.BloodInventory, model.Appointment, java.util.List, java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Scheduled Donations - Blood Donation System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .donations-container {
            margin-top: 30px;
        }
        
        .donation-card {
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            padding: 20px;
            margin-bottom: 20px;
        }
        
        .donation-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
            border-bottom: 1px solid #eee;
            padding-bottom: 10px;
        }
        
        .donation-title {
            font-size: 1.2rem;
            font-weight: bold;
            color: #e74c3c;
            margin: 0;
        }
        
        .donation-date {
            background-color: #f8f9fa;
            padding: 5px 10px;
            border-radius: 4px;
            font-size: 0.9rem;
            color: #666;
        }
        
        .donation-details {
            margin-bottom: 15px;
        }
        
        .donation-details p {
            margin: 5px 0;
            color: #555;
        }
        
        .donation-details strong {
            color: #333;
        }
        
        .donation-status {
            display: inline-block;
            padding: 3px 8px;
            border-radius: 3px;
            font-size: 0.8rem;
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
        
        .no-donations {
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
    </style>
</head>
<body>
    <jsp:include page="../common/header.jsp" />
    
    <div class="container">
        <h1>Scheduled Blood Donations</h1>
        
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
                <select id="statusFilter" class="filter-select" onchange="filterDonations()">
                    <option value="all">All</option>
                    <option value="scheduled">Scheduled</option>
                    <option value="completed">Completed</option>
                    <option value="cancelled">Cancelled</option>
                    <option value="no-show">No Show</option>
                </select>
            </div>
            
            <div class="filter-group">
                <span class="filter-label">Sort by:</span>
                <select id="sortBy" class="filter-select" onchange="sortDonations()">
                    <option value="date-asc">Date (Oldest First)</option>
                    <option value="date-desc" selected>Date (Newest First)</option>
                </select>
            </div>
        </div>
        
        <div class="donations-container">
            <% 
                List<Appointment> appointments = (List<Appointment>) request.getAttribute("appointments");
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
                SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
                
                if(appointments != null && !appointments.isEmpty()) {
                    for(Appointment appointment : appointments) {
            %>
            <div class="donation-card" data-status="<%= appointment.getStatus().toLowerCase() %>">
                <div class="donation-header">
                    <h3 class="donation-title">Appointment #<%= appointment.getId() %></h3>
                    <span class="donation-date"><%= dateFormat.format(appointment.getAppointmentDate()) %></span>
                </div>
                <div class="donation-details">
                    <p><strong>Time:</strong> <%= timeFormat.format(appointment.getAppointmentTime()) %></p>
                    <p><strong>Status:</strong> <span class="donation-status status-<%= appointment.getStatus().toLowerCase() %>"><%= appointment.getStatus() %></span></p>
                    <% if(appointment.getNotes() != null && !appointment.getNotes().isEmpty()) { %>
                        <p><strong>Notes:</strong> <%= appointment.getNotes() %></p>
                    <% } %>
                </div>
                <div class="action-buttons">
                    <% if("scheduled".equalsIgnoreCase(appointment.getStatus())) { %>
                        <a href="${pageContext.request.contextPath}/user/cancel-appointment?id=<%= appointment.getId() %>" class="btn btn-danger" onclick="return confirm('Are you sure you want to cancel this appointment?')">Cancel Appointment</a>
                        <a href="${pageContext.request.contextPath}/user/reschedule-appointment?id=<%= appointment.getId() %>" class="btn btn-secondary">Reschedule</a>
                    <% } %>
                    <a href="${pageContext.request.contextPath}/user/view-appointment?id=<%= appointment.getId() %>" class="btn btn-primary">View Details</a>
                </div>
            </div>
            <% 
                    }
                } else {
            %>
            <div class="no-donations">
                <h3>No scheduled donations found</h3>
                <p>You don't have any blood donation appointments scheduled at the moment.</p>
                <a href="${pageContext.request.contextPath}/user/schedule-appointment" class="btn btn-primary">Schedule a Donation</a>
            </div>
            <% } %>
        </div>
        
        <div class="text-center" style="margin-top: 30px;">
            <a href="${pageContext.request.contextPath}/user/schedule-appointment" class="btn btn-primary">Schedule New Donation</a>
            <a href="${pageContext.request.contextPath}/user/dashboard" class="btn btn-secondary">Back to Dashboard</a>
        </div>
    </div>
    
    <jsp:include page="../common/footer.jsp" />
    
    <script>
        function filterDonations() {
            const status = document.getElementById('statusFilter').value;
            const cards = document.querySelectorAll('.donation-card');
            
            cards.forEach(card => {
                if (status === 'all' || card.dataset.status === status) {
                    card.style.display = 'block';
                } else {
                    card.style.display = 'none';
                }
            });
            
            // Show "no donations" message if all cards are hidden
            const visibleCards = document.querySelectorAll('.donation-card[style="display: block;"]');
            const nodonations = document.querySelector('.no-donations');
            
            if (visibleCards.length === 0 && cards.length > 0) {
                if (!nodonations) {
                    const container = document.querySelector('.donations-container');
                    const nodonationsDiv = document.createElement('div');
                    nodonationsDiv.className = 'no-donations';
                    nodonationsDiv.innerHTML = `
                        <h3>No ${status} donations found</h3>
                        <p>You don't have any blood donation appointments with status "${status}" at the moment.</p>
                    `;
                    container.appendChild(nodonationsDiv);
                }
            } else if (nodonations && visibleCards.length > 0) {
                nodonations.style.display = 'none';
            }
        }
        
        function sortDonations() {
            const sortBy = document.getElementById('sortBy').value;
            const container = document.querySelector('.donations-container');
            const cards = Array.from(document.querySelectorAll('.donation-card'));
            
            cards.sort((a, b) => {
                const dateA = new Date(a.querySelector('.donation-date').textContent);
                const dateB = new Date(b.querySelector('.donation-date').textContent);
                
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
