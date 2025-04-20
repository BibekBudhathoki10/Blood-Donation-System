<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.DonationEvent, java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Donation Events - Blood Donation System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .events-container {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 20px;
            margin-top: 30px;
        }
        
        .event-card {
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            overflow: hidden;
            transition: transform 0.3s;
        }
        
        .event-card:hover {
            transform: translateY(-5px);
        }
        
        .event-image {
            height: 150px;
            background-color: #e74c3c;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            font-size: 1.5rem;
            font-weight: bold;
        }
        
        .event-content {
            padding: 20px;
        }
        
        .event-title {
            margin-top: 0;
            margin-bottom: 10px;
            color: #333;
        }
        
        .event-date {
            color: #e74c3c;
            font-weight: bold;
            margin-bottom: 10px;
        }
        
        .event-location {
            color: #666;
            margin-bottom: 15px;
        }
        
        .event-description {
            color: #666;
            margin-bottom: 15px;
            display: -webkit-box;
            -webkit-line-clamp: 3;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
        
        .event-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-top: 1px solid #eee;
            padding-top: 15px;
        }
        
        .event-participants {
            color: #666;
            font-size: 0.9rem;
        }
        
        .filter-section {
            margin-bottom: 30px;
        }
    </style>
</head>
<body>
    <jsp:include page="../../common/header.jsp" />
    
    <div class="container">
        <h1>Blood Donation Events</h1>
        
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
        
        <div class="filter-section">
            <form action="${pageContext.request.contextPath}/donor/events" method="get">
                <div class="form-group">
                    <label for="filter">Filter Events:</label>
                    <select id="filter" name="filter" onchange="this.form.submit()">
                        <option value="upcoming" ${filter == null || filter == 'upcoming' ? 'selected' : ''}>Upcoming Events</option>
                        <option value="past" ${filter == 'past' ? 'selected' : ''}>Past Events</option>
                    </select>
                </div>
            </form>
        </div>
        
        <div class="events-container">
            <% 
                List<DonationEvent> events = (List<DonationEvent>) request.getAttribute("events");
                if(events != null && !events.isEmpty()) {
                    for(DonationEvent event : events) {
            %>
            <div class="event-card">
                <div class="event-image">
                    Blood Donation Drive
                </div>
                <div class="event-content">
                    <h3 class="event-title"><%= event.getTitle() %></h3>
                    <div class="event-date">
                        <%= event.getEventDate() %>, <%= event.getStartTime() %> - <%= event.getEndTime() %>
                    </div>
                    <div class="event-location">
                        <i class="fas fa-map-marker-alt"></i> <%= event.getLocation() %>
                    </div>
                    <div class="event-description">
                        <%= event.getDescription() != null ? event.getDescription() : "Join us for this blood donation event and help save lives!" %>
                    </div>
                    <div class="event-footer">
                        <div class="event-participants">
                            <% 
                                Integer participantCount = (Integer) request.getAttribute("participantCount_" + event.getId());
                                if(participantCount == null) participantCount = 0;
                            %>
                            <%= participantCount %> / <%= event.getMaxParticipants() %> participants
                        </div>
                        <a href="${pageContext.request.contextPath}/donor/events/view?id=<%= event.getId() %>" class="btn btn-primary">View Details</a>
                    </div>
                </div>
            </div>
            <% 
                    }
                } else {
            %>
            <div class="alert alert-info" style="grid-column: 1 / -1;">
                No events found.
            </div>
            <% } %>
        </div>
    </div>
    
    <jsp:include page="../../common/footer.jsp" />
</body>
</html>

