<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.BloodRequest, model.User, java.time.LocalDate" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Respond to Blood Request - Blood Donation System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .request-details {
            background-color: #f8f9fa;
            border-radius: 5px;
            padding: 20px;
            margin-bottom: 30px;
        }
        
        .request-details h3 {
            margin-top: 0;
            color: #e74c3c;
            margin-bottom: 15px;
        }
        
        .detail-row {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            margin-bottom: 15px;
        }
        
        .detail-item {
            margin-bottom: 10px;
        }
        
        .detail-item label {
            font-weight: bold;
            color: #666;
            margin-right: 5px;
        }
        
        .response-form {
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 20px;
        }
        
        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-bottom: 20px;
        }
        
        .time-slots {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
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
        }
        
        .time-slot.selected {
            background-color: #e74c3c;
            color: #fff;
            border-color: #e74c3c;
        }
        
        .time-slot.disabled {
            background-color: #f8f9fa;
            color: #aaa;
            cursor: not-allowed;
        }
        
        .action-buttons {
            display: flex;
            justify-content: space-between;
            margin-top: 20px;
        }
        
        @media (max-width: 768px) {
            .form-row {
                grid-template-columns: 1fr;
                gap: 10px;
            }
        }
    </style>
</head>
<body>
    <jsp:include page="../common/header.jsp" />
    
    <div class="container">
        <h1>Respond to Blood Request</h1>
        
        <% if(request.getAttribute("error") != null) { %>
            <div class="alert alert-danger">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <% 
            BloodRequest bloodRequest = (BloodRequest) request.getAttribute("bloodRequest");
            User requester = (User) request.getAttribute("requester");
            
            if(bloodRequest != null && requester != null) {
        %>
        <div class="request-details">
            <h3>Request Details</h3>
            <div class="detail-row">
                <div class="detail-item">
                    <label>Request ID:</label>
                    <span><%= bloodRequest.getId() %></span>
                </div>
                <div class="detail-item">
                    <label>Blood Group:</label>
                    <span><%= bloodRequest.getBloodGroup() %></span>
                </div>
                <div class="detail-item">
                    <label>Quantity:</label>
                    <span><%= bloodRequest.getQuantity() %> units</span>
                </div>
                <div class="detail-item">
                    <label>Urgency:</label>
                    <span><%= bloodRequest.getUrgency() %></span>
                </div>
            </div>
            
            <div class="detail-row">
                <div class="detail-item">
                    <label>Patient Name:</label>
                    <span><%= bloodRequest.getPatientName() %></span>
                </div>
                <div class="detail-item">
                    <label>Hospital:</label>
                    <span><%= bloodRequest.getHospitalName() %></span>
                </div>
                <div class="detail-item">
                    <label>Hospital Address:</label>
                    <span><%= bloodRequest.getHospitalAddress() %></span>
                </div>
                <div class="detail-item">
                    <label>Required By:</label>
                    <span><%= bloodRequest.getRequiredDate() %></span>
                </div>
            </div>
            
            <div class="detail-row">
                <div class="detail-item">
                    <label>Requester:</label>
                    <span><%= requester.getName() %></span>
                </div>
                <div class="detail-item">
                    <label>Contact Person:</label>
                    <span><%= bloodRequest.getContactPerson() %></span>
                </div>
                <div class="detail-item">
                    <label>Contact Phone:</label>
                    <span><%= bloodRequest.getContactPhone() %></span>
                </div>
                <div class="detail-item">
                    <label>Request Date:</label>
                    <span><%= bloodRequest.getRequestDate() %></span>
                </div>
            </div>
            
            <% if(bloodRequest.getReason() != null && !bloodRequest.getReason().isEmpty()) { %>
                <div class="detail-item">
                    <label>Reason:</label>
                    <p><%= bloodRequest.getReason() %></p>
                </div>
            <% } %>
        </div>
        
        <div class="response-form">
            <h3>Schedule Donation Appointment</h3>
            <form action="${pageContext.request.contextPath}/donor/respond-to-request" method="post">
                <input type="hidden" name="id" value="<%= bloodRequest.getId() %>">
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="appointmentDate">Appointment Date:</label>
                        <input type="date" id="appointmentDate" name="appointmentDate" 
                               min="<%= LocalDate.now().plusDays(1) %>" 
                               max="<%= LocalDate.now().plusMonths(1) %>" 
                               required>
                    </div>
                    
                    <div class="form-group">
                        <label>Appointment Time:</label>
                        <input type="hidden" id="appointmentTime" name="appointmentTime" required>
                        <div class="time-slots">
                            <div class="time-slot" data-time="09:00:00">9:00 AM</div>
                            <div class="time-slot" data-time="10:00:00">10:00 AM</div>
                            <div class="time-slot" data-time="11:00:00">11:00 AM</div>
                            <div class="time-slot" data-time="12:00:00">12:00 PM</div>
                            <div class="time-slot" data-time="13:00:00">1:00 PM</div>
                            <div class="time-slot" data-time="14:00:00">2:00 PM</div>
                            <div class="time-slot" data-time="15:00:00">3:00 PM</div>
                            <div class="time-slot" data-time="16:00:00">4:00 PM</div>
                        </div>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="notes">Additional Notes (Optional):</label>
                    <textarea id="notes" name="notes" rows="3"></textarea>
                </div>
                
                <div class="action-buttons">
                    <button type="submit" name="action" value="accept" class="btn btn-primary">Accept & Schedule</button>
                    <button type="submit" name="action" value="reject" class="btn btn-danger">Reject Request</button>
                    <a href="${pageContext.request.contextPath}/donor/view-requests" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>
        <% } else { %>
            <div class="alert alert-danger">
                Blood request not found or has been removed.
            </div>
            <a href="${pageContext.request.contextPath}/donor/view-requests" class="btn btn-primary">Back to Requests</a>
        <% } %>
    </div>
    
    <jsp:include page="../common/footer.jsp" />
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Handle time slot selection
            const timeSlots = document.querySelectorAll('.time-slot');
            const timeInput = document.getElementById('appointmentTime');
            
            timeSlots.forEach(slot => {
                slot.addEventListener('click', function() {
                    if (!this.classList.contains('disabled')) {
                        // Remove selected class from all slots
                        timeSlots.forEach(s => s.classList.remove('selected'));
                        
                        // Add selected class to clicked slot
                        this.classList.add('selected');
                        
                        // Update hidden input with selected time
                        timeInput.value = this.dataset.time;
                    }
                });
            });
            
            // Form validation
            const appointmentForm = document.querySelector('form');
            appointmentForm.addEventListener('submit', function(event) {
                // Only validate if accepting the request
                if (event.submitter.value === 'accept') {
                    // Check if time is selected
                    if (!timeInput.value) {
                        event.preventDefault();
                        alert('Please select an appointment time');
                    }
                }
            });
            
            // Date change handler - in a real application, this would check availability
            const dateInput = document.getElementById('appointmentDate');
            dateInput.addEventListener('change', function() {
                // Reset time selection when date changes
                timeSlots.forEach(slot => slot.classList.remove('selected'));
                timeInput.value = '';
                
                // Simulate checking availability (would be an AJAX call in a real app)
                // For demo purposes, randomly disable some time slots
                timeSlots.forEach(slot => {
                    slot.classList.remove('disabled');
                    if (Math.random() > 0.7) {
                        slot.classList.add('disabled');
                    }
                });
            });
        });
    </script>
</body>
</html>
