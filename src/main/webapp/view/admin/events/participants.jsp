<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.EventParticipantDTO, java.util.List, java.text.SimpleDateFormat" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Event Participants - Blood Donation System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <jsp:include page="../../common/header.jsp" />
    
    <div class="container">
        <h1>Event Participants</h1>
        
        <div class="event-details">
            <h2>${event.title}</h2>
            <p><strong>Date:</strong> ${event.eventDate}</p>
            <p><strong>Time:</strong> ${event.startTime} - ${event.endTime}</p>
            <p><strong>Location:</strong> ${event.location}</p>
        </div>
        
        <div class="back-link">
            <a href="${pageContext.request.contextPath}/events/view?id=${event.id}">Back to Event</a>
        </div>
        
        <c:if test="${not empty success}">
            <div class="success-message">${success}</div>
        </c:if>
        
        <c:if test="${not empty error}">
            <div class="error-message">${error}</div>
        </c:if>
        
        <div class="table-container">
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
                    <c:forEach var="participant" items="${participants}">
                        <tr>
                            <td>${participant.name}</td>
                            <td>${participant.email}</td>
                            <td>${participant.phone}</td>
                            <td>${participant.registrationDate}</td>
                            <td>${participant.status}</td>
                            <td>
                                <form action="${pageContext.request.contextPath}/events/remove-participant" method="post" style="display: inline;">
                                    <input type="hidden" name="participantId" value="${participant.id}">
                                    <input type="hidden" name="eventId" value="${event.id}">
                                    <button type="submit" class="btn-delete" onclick="return confirm('Are you sure you want to remove this participant?')">Remove</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
    
    <jsp:include page="../../common/footer.jsp" />
</body>
</html>
