<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.DonationEvent, model.EventParticipantDTO, java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Event Details - Blood Donation System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .event-container {
            display: grid;
            grid-template-columns: 2fr 1fr;
            gap: 30px;
        }
        
        .event-details {
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 30px;
        }
        
        .event-details h2 {
            margin-top: 0;
            color: #333;
            margin-bottom: 20px;
        }
        
        .event-meta {
            margin-bottom: 20px;
        }
        
        .event-meta-item {
            display: flex;
            margin-bottom: 10px;
        }
        
        .event-meta-label {
            font-weight: bold;
            color: #666;
            width: 120px;
        }
        
        .event-description {
            margin-top: 30px;
            line-height: 1.6;
        }
        
        .event-sidebar {
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 30px;
        }
        
        .event-sidebar h3 {
            margin-top: 0;
            color: #e74c3c;
            margin-bottom: 20px;
        }
        
        .event-status {
            background-color: #f8f9fa;
            border-radius: 5px;
            padding: 20px;
            margin-bottom: 20px;
        }
        
        .event-status p {
            margin: 0;
            font-size: 1.1rem;
        }
        
        .event-status .count {
            font-size: 2rem;
            font-weight: bold;
            color: #e74c3c;
            margin: 10px 0;
        }
        
        .event-actions {
            margin-top: 30px;
        }
        
        .participant-list {
            margin-top: 30px;
        }
        
        .participant-list h3 {
            margin-bottom: 15px;
        }
        
        .participant-table {
            width: 100%;
            border-collapse: collapse;
        }
        
        .participant-table th, .participant-table td {
            padding: 10px;
            text-align: left;
            border-bottom: 1px solid #eee;
        }
        
        .participant-table th {
            background-color: #f8f9fa;
            font-weight: bold;
            color: #333;
        }
        
        @media (max-width: 768px) {
            .event-container {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <jsp:include page="../../common/header.jsp" />
    
    <div class="container">
        <% 
            DonationEvent event = (DonationEvent) request.getAttribute("event");
            List<EventParticipantDTO> participants = (List<EventParticipantDTO>) request.getAttribute("participants");
            Integer participantCount = participants != null ? participants.size() : 0;
            
            if(event != null) {
        %>
        
        <div class="event-container">
            <div class="event-details">
                <h2><%= event.getTitle() %></h2>
                
                <div class="event-meta">
                    <div class="event-meta-item">
                        <div class="event-meta-label">Date:</div>
                        <div><%= event.getEventDate() %></div>
                    </div>
                    
                    <div class="event-meta-item">
                        <div class="event-meta-label">Time:</div>
                        <div><%= event.getStartTime() %> - <%= event.getEndTime() %></div>
                    </div>
                    
                    <div class="event-meta-item">
                        <div class="event-meta-label">Location:</div>
                        <div><%= event.getLocation() %></div>
                    </div>
                    
                    <div class="event-meta-item">
                        <div class="event-meta-label">Organizer:</div>
                        <div><%= event.getOrganizer() %></div>
                    </div>
                    
                    <div class="event-meta-item">
                        <div class="event-meta-label">Contact:</div>
                        <div><%= event.getContactEmail() != null ? event.getContactEmail() : "N/A" %> | <%= event.getContactPhone() != null ? event.getContactPhone() : "N/A" %></div>
                    </div>
                </div>
                
                <div class="event-description">
                    <h3>Event Description</h3>
                    <p><%= event.getDescription() != null ? event.getDescription() : "No description available." %></p>
                </div>
                
                <div class="participant-list">
                    <h3>Registered Participants (<%= participantCount %>)</h3>
                    
                    <% if(participants != null && !participants.isEmpty()) { %>
                        <table class="participant-table">
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Email</th>
                                    <th>Phone</th>
                                    <th>Registration Date</th>
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
                                        <td>
                                            <form action="${pageContext.request.contextPath}/events/remove-participant" method="post" style="display: inline;">
                                                <input type="hidden" name="eventId" value="<%= event.getId() %>">
                                                <input type="hidden" name="participantId" value="<%= participant.getId() %>">
                                                <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure you want to remove this participant?')">Remove</button>
                                            </form>
                                        </td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    <% } else { %>
                        <p>No participants registered for this event yet.</p>
                    <% } %>
                </div>
            </div>
            
            <div class="event-sidebar">
                <h3>Event Status</h3>
                
                <div class="event-status">
                    <p>Participants</p>
                    <div class="count"><%= participantCount %> / <%= event.getMaxParticipants() %></div>
                    <p><%= participantCount >= event.getMaxParticipants() ? "Event is full" : "Spots available" %></p>
                </div>
                
                <div class="event-actions">
                    <a href="${pageContext.request.contextPath}/admin/events/edit?id=<%= event.getId() %>" class="btn btn-primary">Edit Event</a>
                    <form action="${pageContext.request.contextPath}/events/delete" method="post" style="margin-top: 10px;">
                        <input type="hidden" name="id" value="<%= event.getId() %>">
                        <button type="submit" class="btn btn-danger" onclick="return confirm('Are you sure you want to delete this event? This action cannot be undone.')">Delete Event</button>
                    </form>
                </div>
                
                <div style="margin-top: 30px;">
                    <h3>Export Options</h3>
                    <a href="${pageContext.request.contextPath}/events/export-participants?id=<%= event.getId() %>&format=csv" class="btn btn-secondary">Export Participants (CSV)</a>
                    <a href="${pageContext.request.contextPath}/events/export-participants?id=<%= event.getId() %>&format=pdf" class="btn btn-secondary" style="margin-top: 10px;">Export Participants (PDF)</a>
                </div>
                
                <div style="margin-top: 30px;">
                    <h3>Quick Actions</h3>
                    <a href="${pageContext.request.contextPath}/admin/events/send-reminder?id=<%= event.getId() %>" class="btn btn-secondary">Send Reminder to Participants</a>
                    <a href="${pageContext.request.contextPath}/admin/events/duplicate?id=<%= event.getId() %>" class="btn btn-secondary" style="margin-top: 10px;">Duplicate Event</a>
                </div>
            </div>
        </div>
        
        <% } else { %>
            <div class="alert alert-danger">
                Event not found.
            </div>
            <p><a href="${pageContext.request.contextPath}/admin/events" class="btn btn-primary">Back to Events</a></p>
        <% } %>
    </div>
    
    <jsp:include page="../../common/footer.jsp" />
</body>
</html>
