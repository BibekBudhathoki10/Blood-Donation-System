<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Events</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <jsp:include page="/view/common/header.jsp" />
    
    <div class="container">
        <h1>Admin Events</h1>

        <a href="${pageContext.request.contextPath}/admin/events/add" class="btn btn-primary">Add New Event</a>

        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Title</th>
                        <th>Date</th>
                        <th>Location</th>
                        <th>Organizer</th>
                        <th>Description</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="event" items="${events}">
                        <tr>
                            <td>${event.id}</td>
                            <td>${event.title}</td>
                            <td><fmt:formatDate value="${event.eventDate}" pattern="yyyy-MM-dd HH:mm" /></td>
                            <td>${event.location}</td>
                            <td>
                                <span>${event.organizer} (${event.contactPhone != null ? event.contactPhone : "N/A"})</span>
                            </td>
                            <td>${event.description}</td>
                            <td class="table-actions">
                                <a href="${pageContext.request.contextPath}/admin/events/edit?id=${event.id}" class="action-edit">Edit</a>
                                <a href="${pageContext.request.contextPath}/admin/events/delete?id=${event.id}" class="action-delete" onclick="return confirm('Are you sure you want to delete this event?')">Delete</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
    
    <jsp:include page="/view/common/footer.jsp" />
</body>
</html>
