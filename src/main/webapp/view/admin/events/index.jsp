<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.DonationEvent, model.EventParticipantDTO, java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Donation Events - Blood Donation System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .event-card {
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 20px;
            margin-bottom: 20px;
        }
        
        .event-header {
            display: flex;
            justify-content: space-between;
            margin-bottom: 15px;
        }
        
        .event-header h3 {
            margin: 0;
            color: #333;
        }
        
        .event-details {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            margin-bottom: 15px;
        }
        
        .event-detail {
            margin-bottom: 5px;
        }
        
        .event-detail label {
            font-weight: bold;
            color: #666;
            margin-right: 5px;
        }
        
        .event-actions {
            display: flex;
            justify-content: flex-end;
            gap: 10px;
            margin-top: 15px;
        }
        
        .action-buttons {
            margin-bottom: 20px;
            text-align: right;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        
        table th, table td {
            padding: 10px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        
        table th {
            background-color: #f5f5f5;
            font-weight: bold;
        }
    </style>
</head>
<body>
    <jsp:include page="../../common/header.jsp" />
    
    <div class="container">
        <h1>Manage Donation Events</h1>
        
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
        
        <div class="action-buttons">
            <a href="${pageContext.request.contextPath}/admin/events/add" class="btn btn-primary">Add New Event</a>
        </div>
        
        <div class="filter-section">
            <form action="${pageContext.request.contextPath}/events/list" method="get">
                <div class="form-group">
                    <label for="filter">Filter Events:</label>
                    <select id="filter" name="filter" onchange="this.form.submit()">
                        <option value="upcoming" ${filter == null || filter == 'upcoming' ? 'selected' : ''}>Upcoming Events</option>
                        <option value="past" ${filter == 'past' ? 'selected' : ''}>Past Events</option>
                    </select>
                </div>
            </form>
        </div>
        
        <% 
            List<DonationEvent> events = (List<DonationEvent>) request.getAttribute("events");
            if(events != null && !events.isEmpty()) {
                for(DonationEvent event : events) {
        %>
        <div class="event-card">
            <div class="event-header">
                <h3><%= event.getTitle() %></h3>
                <span><%= event.getEventDate() %></span>
            </div>
            
            <div class="event-details">
                <div>
                    <div class="event-detail">
                        <label>Time:</label>
                        <span><%= event.getStartTime() %> - <%= event.getEndTime() %></span>
                    </div>
                    <div class="event-detail">
                        <label>Location:</label>
                        <span><%= event.getLocation() %></span>
                    </div>
                    <div class="event-detail">
                        <label>Organizer:</label>
                        <span><%= event.getOrganizer() %></span>
                    </div>
                </div>
                
                <div>
                    <div class="event-detail">
                        <label>Contact:</label>
                        <span><%= event.getContactPerson() %> (<%= event.getContactPhone() != null ? event.getContactPhone() : "N/A" %>)</span>
                    </div>
                    <div class="event-detail">
                        <label>Max Participants:</label>
                        <span><%= event.getMaxParticipants() %></span>
                    </div>
                    <div class="event-detail">
                        <label>Current Participants:</label>
                        <span><%= request.getAttribute("participantCount_" + event.getId()) != null ? request.getAttribute("participantCount_" + event.getId()) : "0" %></span>
                    </div>
                </div>
            </div>
            
            <div class="event-actions">
                <a href="${pageContext.request.contextPath}/events/view?id=<%= event.getId() %>" class="btn btn-secondary">View Details</a>
                <a href="${pageContext.request.contextPath}/events/participants?id=<%= event.getId() %>" class="btn btn-secondary">View Participants</a>
                <a href="${pageContext.request.contextPath}/admin/events/edit?id=<%= event.getId() %>" class="btn btn-primary">Edit</a>
                <form action="${pageContext.request.contextPath}/events/delete" method="post" style="display: inline;">
                    <input type="hidden" name="id" value="<%= event.getId() %>">
                    <button type="submit" class="btn btn-danger" onclick="return confirm('Are you sure you want to delete this event?')">Delete</button>
                </form>
            </div>
        </div>
        <% 
                }
            } else {
        %>
        <div class="alert alert-info">
            No events found.
        </div>
        <% } %>
        
        <% 
            // Display participants if available
            List<EventParticipantDTO> participants = (List<EventParticipantDTO>) request.getAttribute("participants");
            if(participants != null && !participants.isEmpty()) {
        %>
        <h2>Event Participants</h2>
        <table>
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Registration Date</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <% for(EventParticipantDTO participant : participants) { %>
                    <tr>
                        <td><%= participant.getName() %></td>
                        <td><%= participant.getEmail() %></td>
                        <td><%= participant.getPhone() %></td>
                        <td><%= participant.getRegistrationDate() %></td>
                        <td><%= participant.getStatus() %></td>
                        <td>
                            <form action="${pageContext.request.contextPath}/events/remove-participant" method="post" style="display: inline;">
                                <input type="hidden" name="participantId" value="<%= participant.getId() %>">
                                <input type="hidden" name="eventId" value="<%= participant.getEventId() %>">
                                <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure you want to remove this participant?')">Remove</button>
                            </form>
                        </td>
                    </tr>
                <% } %>
            </tbody>
        </table>
        <% } %>
    </div>
    
    <jsp:include page="../../common/footer.jsp" />
</body>
</html>
