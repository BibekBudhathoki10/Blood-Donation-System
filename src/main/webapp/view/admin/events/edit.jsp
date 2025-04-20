<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.DonationEvent" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Donation Event - Blood Donation System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <jsp:include page="../../common/header.jsp" />
    
    <div class="container">
        <h1>Edit Donation Event</h1>
        
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
            DonationEvent event = (DonationEvent) request.getAttribute("event");
            if(event != null) {
        %>
        
        <div class="auth-form">
            <form action="${pageContext.request.contextPath}/events/edit" method="post">
                <input type="hidden" name="id" value="<%= event.getId() %>">
                
                <div class="form-group">
                    <label for="title">Event Title:</label>
                    <input type="text" id="title" name="title" value="<%= event.getTitle() %>" required>
                    <% if(request.getAttribute("titleError") != null) { %>
                        <span class="error"><%= request.getAttribute("titleError") %></span>
                    <% } %>
                </div>
                
                <div class="form-group">
                    <label for="description">Event Description:</label>
                    <textarea id="description" name="description" rows="4" required><%= event.getDescription() != null ? event.getDescription() : "" %></textarea>
                    <% if(request.getAttribute("descriptionError") != null) { %>
                        <span class="error"><%= request.getAttribute("descriptionError") %></span>
                    <% } %>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="eventDate">Event Date:</label>
                        <input type="date" id="eventDate" name="eventDate" value="<%= event.getEventDate() %>" required>
                        <% if(request.getAttribute("eventDateError") != null) { %>
                            <span class="error"><%= request.getAttribute("eventDateError") %></span>
                        <% } %>
                    </div>
                    
                    <div class="form-group">
                        <label for="location">Location:</label>
                        <input type="text" id="location" name="location" value="<%= event.getLocation() %>" required>
                        <% if(request.getAttribute("locationError") != null) { %>
                            <span class="error"><%= request.getAttribute("locationError") %></span>
                        <% } %>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="startTime">Start Time:</label>
                        <input type="time" id="startTime" name="startTime" value="<%= event.getStartTime() %>" required>
                        <% if(request.getAttribute("startTimeError") != null) { %>
                            <span class="error"><%= request.getAttribute("startTimeError") %></span>
                        <% } %>
                    </div>
                    
                    <div class="form-group">
                        <label for="endTime">End Time:</label>
                        <input type="time" id="endTime" name="endTime" value="<%= event.getEndTime() %>" required>
                        <% if(request.getAttribute("endTimeError") != null) { %>
                            <span class="error"><%= request.getAttribute("endTimeError") %></span>
                        <% } %>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="organizer">Organizer:</label>
                        <input type="text" id="organizer" name="organizer" value="<%= event.getOrganizer() %>" required>
                        <% if(request.getAttribute("organizerError") != null) { %>
                            <span class="error"><%= request.getAttribute("organizerError") %></span>
                        <% } %>
                    </div>
                    
                    <div class="form-group">
                        <label for="maxParticipants">Maximum Participants:</label>
                        <input type="number" id="maxParticipants" name="maxParticipants" min="1" max="500" value="<%= event.getMaxParticipants() %>" required>
                        <% if(request.getAttribute("maxParticipantsError") != null) { %>
                            <span class="error"><%= request.getAttribute("maxParticipantsError") %></span>
                        <% } %>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="contactEmail">Contact Email:</label>
                        <input type="email" id="contactEmail" name="contactEmail" value="<%= event.getContactEmail() %>" required>
                        <% if(request.getAttribute("contactEmailError") != null) { %>
                            <span class="error"><%= request.getAttribute("contactEmailError") %></span>
                        <% } %>
                    </div>
                    
                    <div class="form-group">
                        <label for="contactPhone">Contact Phone:</label>
                        <input type="text" id="contactPhone" name="contactPhone" value="<%= event.getContactPhone() %>" required>
                        <% if(request.getAttribute("contactPhoneError") != null) { %>
                            <span class="error"><%= request.getAttribute("contactPhoneError") %></span>
                        <% } %>
                    </div>
                </div>
                
                <div class="form-group">
                    <button type="submit" class="btn btn-primary">Update Event</button>
                    <a href="${pageContext.request.contextPath}/events/view?id=<%= event.getId() %>" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>
        
        <% } else { %>
            <div class="alert alert-danger">
                Event not found.
            </div>
            <p><a href="${pageContext.request.contextPath}/events/list" class="btn btn-primary">Back to Events</a></p>
        <% } %>
    </div>
    
    <jsp:include page="../../common/footer.jsp" />
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Validate end time is after start time
            const form = document.querySelector('form');
            const startTimeInput = document.getElementById('startTime');
            const endTimeInput = document.getElementById('endTime');
            
            form.addEventListener('submit', function(event) {
                const startTime = startTimeInput.value;
                const endTime = endTimeInput.value;
                
                if (startTime >= endTime) {
                    event.preventDefault();
                    alert('End time must be after start time');
                    endTimeInput.focus();
                }
            });
        });
    </script>
</body>
</html>

