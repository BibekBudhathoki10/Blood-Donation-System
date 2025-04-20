<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Appointment, model.Donor, java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Appointments - Blood Donation System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <jsp:include page="../common/header.jsp" />
    
    <div class="container">
        <h1>Manage Appointments</h1>
        
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
            <form action="${pageContext.request.contextPath}/admin/manage-appointments" method="get">
                <div class="form-group">
                    <label for="date">Filter by Date:</label>
                    <input type="date" id="date" name="date" value="${param.date}">
                </div>
                
                <div class="form-group">
                    <label for="status">Filter by Status:</label>
                    <select id="status" name="status">
                        <option value="">All</option>
                        <option value="scheduled" ${param.status == 'scheduled' ? 'selected' : ''}>Scheduled</option>
                        <option value="completed" ${param.status == 'completed' ? 'selected' : ''}>Completed</option>
                        <option value="cancelled" ${param.status == 'cancelled' ? 'selected' : ''}>Cancelled</option>
                        <option value="no-show" ${param.status == 'no-show' ? 'selected' : ''}>No-Show</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <button type="submit" class="btn btn-primary">Filter</button>
                    <a href="${pageContext.request.contextPath}/admin/manage-appointments" class="btn btn-secondary">Reset</a>
                </div>
            </form>
        </div>
        
        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Donor</th>
                        <th>Blood Group</th>
                        <th>Date</th>
                        <th>Time</th>
                        <th>Status</th>
                        <th>Notes</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        List<Appointment> appointments = (List<Appointment>) request.getAttribute("appointments");
                        if(appointments != null && !appointments.isEmpty()) {
                            for(Appointment appointment : appointments) {
                                Donor donor = (Donor) request.getAttribute("donor_" + appointment.getId());
                    %>
                    <tr>
                        <td><%= appointment.getId() %></td>
                        <td>
                            <% if(donor != null) { %>
                                <a href="${pageContext.request.contextPath}/admin/view-donor?id=<%= donor.getId() %>">
                                    Donor #<%= donor.getId() %>
                                </a>
                            <% } else { %>
                                N/A
                            <% } %>
                        </td>
                        <td><%= donor != null ? donor.getBloodGroup() : "N/A" %></td>
                        <td><%= appointment.getAppointmentDate() %></td>
                        <td><%= appointment.getAppointmentTime() %></td>
                        <td>
                            <span class="status-<%= appointment.getStatus().toLowerCase() %>">
                                <%= appointment.getStatus() %>
                            </span>
                        </td>
                        <td><%= appointment.getNotes() != null ? appointment.getNotes() : "" %></td>
                        <td class="table-actions">
                            <% if("scheduled".equals(appointment.getStatus())) { %>
                                <a href="${pageContext.request.contextPath}/admin/edit-appointment?id=<%= appointment.getId() %>" class="action-edit">Edit</a>
                                
                                <form action="${pageContext.request.contextPath}/appointment/complete" method="post" style="display: inline;">
                                    <input type="hidden" name="id" value="<%= appointment.getId() %>">
                                    <button type="button" class="action-view" onclick="showCompleteForm(<%= appointment.getId() %>)">Complete</button>
                                </form>
                                
                                <form action="${pageContext.request.contextPath}/appointment/cancel" method="post" style="display: inline;">
                                    <input type="hidden" name="id" value="<%= appointment.getId() %>">
                                    <button type="submit" class="action-delete" onclick="return confirm('Are you sure you want to cancel this appointment?')">Cancel</button>
                                </form>
                            <% } else { %>
                                <a href="${pageContext.request.contextPath}/admin/view-appointment?id=<%= appointment.getId() %>" class="action-view">View</a>
                            <% } %>
                        </td>
                    </tr>
                    <% 
                            }
                        } else {
                    %>
                    <tr>
                        <td colspan="8" class="text-center">No appointments found</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
        
        <!-- Complete Appointment Modal -->
        <div id="completeAppointmentModal" class="modal">
            <div class="modal-content">
                <span class="close" onclick="closeModal()">&times;</span>
                <h2>Complete Appointment</h2>
                <form id="completeAppointmentForm" action="${pageContext.request.contextPath}/appointment/complete" method="post">
                    <input type="hidden" id="appointmentId" name="id" value="">
                    
                    <div class="form-group">
                        <label for="bloodGroup">Blood Group:</label>
                        <select id="bloodGroup" name="bloodGroup" required>
                            <option value="">Select Blood Group</option>
                            <option value="A+">A+</option>
                            <option value="A-">A-</option>
                            <option value="B+">B+</option>
                            <option value="B-">B-</option>
                            <option value="AB+">AB+</option>
                            <option value="AB-">AB-</option>
                            <option value="O+">O+</option>
                            <option value="O-">O-</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="quantity">Quantity (units):</label>
                        <input type="number" id="quantity" name="quantity" min="1" max="5" value="1" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="notes">Notes:</label>
                        <textarea id="notes" name="notes" rows="3"></textarea>
                    </div>
                    
                    <div class="form-group">
                        <button type="submit" class="btn btn-primary">Complete Donation</button>
                        <button type="button" class="btn btn-secondary" onclick="closeModal()">Cancel</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    
    <jsp:include page="../common/footer.jsp" />
    
    <script>
        function showCompleteForm(appointmentId) {
            document.getElementById('appointmentId').value = appointmentId;
            document.getElementById('completeAppointmentModal').style.display = 'block';
        }
        
        function closeModal() {
            document.getElementById('completeAppointmentModal').style.display = 'none';
        }
        
        // Close the modal when clicking outside of it
        window.onclick = function(event) {
            var modal = document.getElementById('completeAppointmentModal');
            if (event.target == modal) {
                closeModal();
            }
        }
    </script>
</body>
</html>

