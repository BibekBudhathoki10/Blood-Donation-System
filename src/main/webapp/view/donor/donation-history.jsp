<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Appointment, java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Donation History - Blood Donation System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .donation-stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .stat-card {
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 20px;
            text-align: center;
        }
        
        .stat-card .count {
            font-size: 2.5rem;
            font-weight: bold;
            color: #e74c3c;
            margin-bottom: 10px;
        }
        
        .stat-card .label {
            color: #666;
            font-size: 1rem;
        }
        
        .timeline {
            position: relative;
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px 0;
        }
        
        .timeline::after {
            content: '';
            position: absolute;
            width: 6px;
            background-color: #e74c3c;
            top: 0;
            bottom: 0;
            left: 50%;
            margin-left: -3px;
        }
        
        .timeline-item {
            padding: 10px 40px;
            position: relative;
            width: 50%;
            box-sizing: border-box;
        }
        
        .timeline-item::after {
            content: '';
            position: absolute;
            width: 20px;
            height: 20px;
            background-color: #fff;
            border: 4px solid #e74c3c;
            border-radius: 50%;
            top: 15px;
            z-index: 1;
        }
        
        .timeline-item.left {
            left: 0;
        }
        
        .timeline-item.right {
            left: 50%;
        }
        
        .timeline-item.left::after {
            right: -10px;
        }
        
        .timeline-item.right::after {
            left: -10px;
        }
        
        .timeline-content {
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 20px;
        }
        
        .timeline-content h3 {
            margin-top: 0;
            color: #333;
        }
        
        .timeline-content .date {
            color: #e74c3c;
            font-weight: bold;
        }
        
        .timeline-content .status {
            display: inline-block;
            padding: 3px 8px;
            border-radius: 3px;
            font-size: 0.8rem;
            font-weight: bold;
            text-transform: uppercase;
            margin-left: 10px;
        }
        
        .status-completed {
            background-color: #2ecc71;
            color: #fff;
        }
        
        .status-scheduled {
            background-color: #3498db;
            color: #fff;
        }
        
        .status-cancelled {
            background-color: #e74c3c;
            color: #fff;
        }
        
        .status-no-show {
            background-color: #f39c12;
            color: #fff;
        }
        
        .timeline-actions {
            margin-top: 15px;
            display: flex;
            gap: 10px;
        }
        
        @media screen and (max-width: 768px) {
            .timeline::after {
                left: 31px;
            }
            
            .timeline-item {
                width: 100%;
                padding-left: 70px;
                padding-right: 25px;
            }
            
            .timeline-item.right {
                left: 0;
            }
            
            .timeline-item.left::after,
            .timeline-item.right::after {
                left: 21px;
            }
        }
    </style>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
    <jsp:include page="../common/header.jsp" />
    
    <div class="container">
        <h1>My Donation History</h1>
        
        <% 
            List<Appointment> appointments = (List<Appointment>) request.getAttribute("appointments");
            int completedCount = 0;
            int scheduledCount = 0;
            int cancelledCount = 0;
            
            if(appointments != null) {
                for(Appointment appointment : appointments) {
                    if("completed".equals(appointment.getStatus())) {
                        completedCount++;
                    } else if("scheduled".equals(appointment.getStatus())) {
                        scheduledCount++;
                    } else if("cancelled".equals(appointment.getStatus())) {
                        cancelledCount++;
                    }
                }
            }
        %>
        
        <div class="donation-stats">
            <div class="stat-card">
                <div class="count"><%= completedCount %></div>
                <div class="label">Completed Donations</div>
            </div>
            <div class="stat-card">
                <div class="count"><%= scheduledCount %></div>
                <div class="label">Upcoming Appointments</div>
            </div>
            <div class="stat-card">
                <div class="count"><%= appointments != null ? appointments.size() : 0 %></div>
                <div class="label">Total Appointments</div>
            </div>
        </div>
        
        <% if(appointments != null && !appointments.isEmpty()) { %>
            <div class="timeline">
                <% 
                    boolean isLeft = true;
                    for(Appointment appointment : appointments) {
                %>
                    <div class="timeline-item <%= isLeft ? "left" : "right" %>">
                        <div class="timeline-content">
                            <h3>
                                <span class="date"><%= appointment.getAppointmentDate() %> at <%= appointment.getAppointmentTime() %></span>
                                <span class="status status-<%= appointment.getStatus() %>"><%= appointment.getStatus() %></span>
                            </h3>
                            
                            <% if(appointment.getNotes() != null && !appointment.getNotes().isEmpty()) { %>
                                <p><%= appointment.getNotes() %></p>
                            <% } %>
                            
                            <% if("scheduled".equals(appointment.getStatus())) { %>
                                <div class="timeline-actions">
                                    <a href="${pageContext.request.contextPath}/donor/reschedule-appointment?id=<%= appointment.getId() %>" class="btn btn-secondary">Reschedule</a>
                                    <form action="${pageContext.request.contextPath}/donor/cancel-appointment" method="post" style="display: inline;">
                                        <input type="hidden" name="id" value="<%= appointment.getId() %>">
                                        <button type="submit" class="btn btn-danger" onclick="return confirm('Are you sure you want to cancel this appointment?')">
                                            <i class="fas fa-times"></i> Cancel
                                        </button>
                                    </form>
                                </div>
                            <% } %>
                        </div>
                    </div>
                <% 
                        isLeft = !isLeft;
                    }
                %>
            </div>
        <% } else { %>
            <div class="alert alert-info">
                You haven't made any donation appointments yet. <a href="${pageContext.request.contextPath}/donor/schedule-appointment">Schedule your first donation</a>.
            </div>
        <% } %>
    </div>
    
    <jsp:include page="../common/footer.jsp" />
</body>
</html>
