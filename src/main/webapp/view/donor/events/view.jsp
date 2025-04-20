<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.DonationEvent" %>
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
        
        .event-map {
            margin-top: 30px;
            height: 200px;
            background-color: #f8f9fa;
            border-radius: 5px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #666;
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
            Integer participantCount = (Integer) request.getAttribute("participantCount");
            Boolean isRegistered = (Boolean) request.getAttribute("isRegistered");
            
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
                        <div><%= event.getContactEmail() %> | <%= event.getContactPhone() %></div>
                    </div>
                </div>
                
                <div class="event-description">
                    <h3>About This Event</h3>
                    <p><%= event.getDescription() != null ? event.getDescription() : "Join us for this blood donation event and help save lives! Your donation can make a significant difference in someone's life." %></p>
                    
                    <h3>What to Expect</h3>
                    <ul>
                        <li>The donation process takes about 10-15 minutes</li>
                        <li>Please bring a valid ID</li>
                        <li>Eat a healthy meal and stay hydrated before donating</li>
                        <li>Avoid alcohol consumption 24 hours before donation</li>
                        <li>Refreshments will be provided after donation</li>
                    </ul>
                    
                    <h3>Eligibility Criteria</h3>
                    <ul>
                        <li>Age between 18-65 years</li>
                        <li>Weight at least 50 kg</li>
                        <li>Good health condition</li>
                        <li>No recent surgeries or major medical procedures</li>
                        <li>No tattoos or piercings in the last 6 months</li>
                    </ul>
                </div>
                
                <div class="event-map">
                    <p>Map location will be displayed here</p>
                </div>
            </div>
            
            <div class="event-sidebar">
                <h3>Event Status</h3>
                
                <div class="event-status">
                    <p>Participants</p>
                    <div class="count"><%= participantCount != null ? participantCount : 0 %> / <%= event.getMaxParticipants() %></div>
                    <p><%= (participantCount != null && participantCount >= event.getMaxParticipants()) ? "Event is full" : "Spots available" %></p>
                </div>
                
                <div class="event-actions">
                    <% if(isRegistered != null && isRegistered) { %>
                        <p>You are registered for this event!</p>
                        <form action="${pageContext.request.contextPath}/events/cancel-registration" method="post">
                            <input type="hidden" name="id" value="<%= event.getId() %>">
                            <button type="submit" class="btn btn-danger" onclick="return confirm('Are you sure you want to cancel your registration?')">Cancel Registration</button>
                        </form>
                    <% } else if(participantCount != null && participantCount >= event.getMaxParticipants()) { %>
                        <p>This event is full. Please check other events.</p>
                        <a href="${pageContext.request.contextPath}/donor/events" class="btn btn-secondary">View Other Events</a>
                    <% } else { %>
                        <form action="${pageContext.request.contextPath}/events/register" method="post">
                            <input type="hidden" name="id" value="<%= event.getId() %>">
                            <button type="submit" class="btn btn-primary">Register for Event</button>
                        </form>
                    <% } %>
                </div>
                
                <div style="margin-top: 30px;">
                    <h3>Share This Event</h3>
                    <div class="social-share">
                        <a href="#" class="btn btn-secondary">Share on Facebook</a>
                        <a href="#" class="btn btn-secondary" style="margin-top: 10px;">Share on Twitter</a>
                    </div>
                </div>
            </div>
        </div>
        
        <% } else { %>
            <div class="alert alert-danger">
                Event not found.
            </div>
            <p><a href="${pageContext.request.contextPath}/donor/events" class="btn btn-primary">Back to Events</a></p>
        <% } %>
    </div>
    
    <jsp:include page="../../common/footer.jsp" />
</body>
</html>

