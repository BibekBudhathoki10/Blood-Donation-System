<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Appointment, java.util.Date, java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reschedule Appointment - Blood Donation System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .form-container {
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            padding: 30px;
            margin-top: 30px;
        }
        
        .form-title {
            color: #e74c3c;
            margin-top: 0;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 1px solid #eee;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        .form-label {
            display: block;
            margin-bottom: 8px;
            font-weight: bold;
            color: #555;
        }
        
        .form-control {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 16px;
        }
        
        .form-control:focus {
            border-color: #e74c3c;
            outline: none;
            box-shadow: 0 0 0 2px rgba(231, 76, 60, 0.25);
        }
        
        .form-text {
            display: block;
            margin-top: 5px;
            font-size: 0.9rem;
            color: #6c757d;
        }
        
        .error-text {
            color: #e74c3c;
            font-size: 0.9rem;
            margin-top: 5px;
        }
        
        .time-slots {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
            gap: 10px;
            margin-top: 10px;
        }
        
        .time-slot {
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            text-align: center;
            cursor: pointer;
            transition: all 0.2s;
        }
        
        .time-slot:hover {
            background-color: #f8f9fa;
            border-color: #e74c3c;
        }
        
        .time-slot.selected {
            background-color: #e74c3c;
            color: white;
            border-color: #e74c3c;
        }
        
        .time-slot.disabled {
            background-color: #f8f9fa;
            color: #adb5bd;
            cursor: not-allowed;
            border-color: #ddd;
        }
        
        .current-appointment {
            background-color: #f8f9fa;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 30px;
        }
        
        .current-appointment h3 {
            margin-top: 0;
            color: #555;
        }
        
        .current-details {
            display: flex;
            justify-content: space-between;
            margin-top: 15px;
        }
        
        .current-detail {
            background-color: #fff;
            border-radius: 4px;
            padding: 10px 15px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        
        .current-detail strong {
            display: block;
            color: #e74c3c;
            margin-bottom: 5px;
        }
    </style>
</head>
<body>
    <jsp:include page="../common/header.jsp" />
    
    <div class="container">
        <h1>Reschedule Appointment</h1>
        
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
            Appointment appointment = (Appointment) request.getAttribute("appointment");
            if(appointment != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
                SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        %>
        <div class="current-appointment">
            <h3>Current Appointment Details</h3>
            <div class="current-details">
                <div class="current-detail">
                    <strong>Appointment ID</strong>
                    <span>#<%= appointment.getId() %></span>
                </div>
                <div class="current-detail">
                    <strong>Current Date</strong>
                    <span><%= dateFormat.format(appointment.getAppointmentDate()) %></span>
                </div>
                <div class="current-detail">
                    <strong>Current Time</strong>
                    <span><%= timeFormat.format(appointment.getAppointmentTime()) %></span>
                </div>
                <div class="current-detail">
                    <strong>Status</strong>
                    <span><%= appointment.getStatus() %></span>
                </div>
            </div>
        </div>
        
        <div class="form-container">
            <h2 class="form-title">New Appointment Details</h2>
            
            <form action="${pageContext.request.contextPath}/user/reschedule-appointment" method="post" id="appointmentForm">
                <input type="hidden" name="id" value="<%= appointment.getId() %>">
                
                <div class="form-group">
                    <label for="appointmentDate" class="form-label">New Appointment Date</label>
                    <input type="date" id="appointmentDate" name="appointmentDate" class="form-control" required 
                           min="<%= new SimpleDateFormat("yyyy-MM-dd").format(new Date()) %>"
                           value="${param.appointmentDate}">
                    <small class="form-text">Select a new date for your blood donation appointment.</small>
                    <% if(request.getAttribute("appointmentDateError") != null) { %>
                        <div class="error-text">${appointmentDateError}</div>
                    <% } %>
                </div>
                
                <div class="form-group">
                    <label class="form-label">New Appointment Time</label>
                    <input type="hidden" id="appointmentTime" name="appointmentTime" value="${param.appointmentTime}" required>
                    <div class="time-slots">
                        <div class="time-slot" data-time="09:00:00" onclick="selectTimeSlot(this)">9:00 AM</div>
                        <div class="time-slot" data-time="09:30:00" onclick="selectTimeSlot(this)">9:30 AM</div>
                        <div class="time-slot" data-time="10:00:00" onclick="selectTimeSlot(this)">10:00 AM</div>
                        <div class="time-slot" data-time="10:30:00" onclick="selectTimeSlot(this)">10:30 AM</div>
                        <div class="time-slot" data-time="11:00:00" onclick="selectTimeSlot(this)">11:00 AM</div>
                        <div class="time-slot" data-time="11:30:00" onclick="selectTimeSlot(this)">11:30 AM</div>
                        <div class="time-slot" data-time="13:00:00" onclick="selectTimeSlot(this)">1:00 PM</div>
                        <div class="time-slot" data-time="13:30:00" onclick="selectTimeSlot(this)">1:30 PM</div>
                        <div class="time-slot" data-time="14:00:00" onclick="selectTimeSlot(this)">2:00 PM</div>
                        <div class="time-slot" data-time="14:30:00" onclick="selectTimeSlot(this)">2:30 PM</div>
                        <div class="time-slot" data-time="15:00:00" onclick="selectTimeSlot(this)">3:00 PM</div>
                        <div class="time-slot" data-time="15:30:00" onclick="selectTimeSlot(this)">3:30 PM</div>
                    </div>
                    <small class="form-text">Select an available time slot for your appointment.</small>
                    <% if(request.getAttribute("appointmentTimeError") != null) { %>
                        <div class="error-text">${appointmentTimeError}</div>
                    <% } %>
                </div>
                
                <div class="form-group">
                    <label for="notes" class="form-label">Additional Notes (Optional)</label>
                    <textarea id="notes" name="notes" class="form-control" rows="3">${param.notes != null ? param.notes : appointment.getNotes()}</textarea>
                    <small class="form-text">Any additional information you'd like to provide about your appointment.</small>
                </div>
                
                <div class="form-group">
                    <button type="submit" class="btn btn-primary" id="rescheduleBtn">Reschedule Appointment</button>
                    <a href="${pageContext.request.contextPath}/user/scheduled-donations" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>
        <% } else { %>
        <div class="alert alert-danger">
            Appointment not found.
        </div>
        <div class="text-center" style="margin-top: 30px;">
            <a href="${pageContext.request.contextPath}/user/scheduled-donations" class="btn btn-primary">Back to Appointments</a>
        </div>
        <% } %>
    </div>
    
    <jsp:include page="../common/footer.jsp" />
    
    <script>
        // Function to select time slot
        function selectTimeSlot(element) {
            if (element.classList.contains('disabled')) {
                return;
            }
            
            // Remove selected class from all time slots
            document.querySelectorAll('.time-slot').forEach(slot => {
                slot.classList.remove('selected');
            });
            
            // Add selected class to clicked time slot
            element.classList.add('selected');
            
            // Set the hidden input value
            document.getElementById('appointmentTime').value = element.dataset.time;
            
            // Enable the reschedule button
            document.getElementById('rescheduleBtn').disabled = false;
        }
        
        // Check for available time slots when date changes
        document.getElementById('appointmentDate').addEventListener('change', function() {
            const date = this.value;
            
            // This would normally be an AJAX call to check availability
            // For demo purposes, we'll just disable random slots
            document.querySelectorAll('.time-slot').forEach(slot => {
                slot.classList.remove('disabled');
                
                // Randomly disable some slots for demonstration
                if (Math.random() > 0.7) {
                    slot.classList.add('disabled');
                    if (slot.classList.contains('selected')) {
                        slot.classList.remove('selected');
                        document.getElementById('appointmentTime').value = '';
                    }
                }
            });
        });
        
        // Initialize form
        window.onload = function() {
            // If there's a previously selected time, mark it
            const selectedTime = document.getElementById('appointmentTime').value;
            if (selectedTime) {
                document.querySelector(`.time-slot[data-time="${selectedTime}"]`)?.classList.add('selected');
            }
        };
    </script>
</body>
</html>
