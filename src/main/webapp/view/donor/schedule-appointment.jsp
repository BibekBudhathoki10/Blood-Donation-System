<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.time.LocalDate" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Schedule Donation Appointment - Blood Donation System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .appointment-form {
            max-width: 800px;
            margin: 0 auto;
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 30px;
        }
        
        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-bottom: 20px;
        }
        
        .eligibility-checklist {
            background-color: #f8f9fa;
            border-radius: 5px;
            padding: 20px;
            margin-bottom: 30px;
        }
        
        .eligibility-checklist h3 {
            margin-top: 0;
            color: #e74c3c;
            margin-bottom: 15px;
        }
        
        .eligibility-checklist ul {
            padding-left: 20px;
        }
        
        .eligibility-checklist li {
            margin-bottom: 10px;
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
        <h1>Schedule a Blood Donation Appointment</h1>
        
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
        
        <div class="eligibility-checklist">
            <h3>Eligibility Checklist</h3>
            <p>Please ensure you meet the following criteria before scheduling an appointment:</p>
            <ul>
                <li>You are between 18 and 65 years old</li>
                <li>You weigh at least 50 kg (110 lbs)</li>
                <li>You are in good health</li>
                <li>You have not donated blood in the last 3 months</li>
                <li>You have not had any recent surgeries or major medical procedures</li>
                <li>You are not currently taking antibiotics</li>
                <li>You have not had any tattoos or piercings in the last 6 months</li>
                <li>You have not traveled to areas with high risk of malaria in the last year</li>
            </ul>
            <p>If you have any questions about your eligibility, please contact us before scheduling an appointment.</p>
        </div>
        
        <div class="appointment-form">
            <form action="${pageContext.request.contextPath}/donor/schedule-appointment" method="post" id="appointmentForm">
                <div class="form-row">
                    <div class="form-group">
                        <label for="appointmentDate">Appointment Date:</label>
                        <input type="date" id="appointmentDate" name="appointmentDate" 
                               min="<%= LocalDate.now().plusDays(1) %>" 
                               max="<%= LocalDate.now().plusMonths(1) %>" 
                               value="${appointmentDate}" 
                               required>
                        <% if(request.getAttribute("dateError") != null) { %>
                            <span class="error"><%= request.getAttribute("dateError") %></span>
                        <% } %>
                    </div>
                    
                    <div class="form-group">
                        <label>Appointment Time:</label>
                        <input type="hidden" id="appointmentTime" name="appointmentTime" value="${appointmentTime}" required>
                        <div class="time-slots">
                            <div class="time-slot" data-time="09:00">9:00 AM</div>
                            <div class="time-slot" data-time="10:00">10:00 AM</div>
                            <div class="time-slot" data-time="11:00">11:00 AM</div>
                            <div class="time-slot" data-time="12:00">12:00 PM</div>
                            <div class="time-slot" data-time="13:00">1:00 PM</div>
                            <div class="time-slot" data-time="14:00">2:00 PM</div>
                            <div class="time-slot" data-time="15:00">3:00 PM</div>
                            <div class="time-slot" data-time="16:00">4:00 PM</div>
                        </div>
                        <% if(request.getAttribute("timeError") != null) { %>
                            <span class="error"><%= request.getAttribute("timeError") %></span>
                        <% } %>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="notes">Additional Notes (Optional):</label>
                    <textarea id="notes" name="notes" rows="3">${notes}</textarea>
                </div>
                
                <div class="form-group">
                    <label>
                        <input type="checkbox" id="eligibilityConfirmation" required>
                        I confirm that I meet all the eligibility criteria for blood donation
                    </label>
                </div>
                
                <div class="form-group">
                    <button type="submit" class="btn btn-primary">Schedule Appointment</button>
                </div>
            </form>
        </div>
    </div>
    
    <jsp:include page="../common/footer.jsp" />
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Handle time slot selection
            const timeSlots = document.querySelectorAll('.time-slot');
            const timeInput = document.getElementById('appointmentTime');
            
            // Set initial selected time if available
            if (timeInput.value) {
                timeSlots.forEach(slot => {
                    if (slot.dataset.time === timeInput.value) {
                        slot.classList.add('selected');
                    }
                });
            }
            
            timeSlots.forEach(slot => {
                slot.addEventListener('click', function() {
                    if (!this.classList.contains('disabled')) {
                        // Remove selected class from all slots
                        timeSlots.forEach(s => s.classList.remove('selected'));
                        
                        // Add selected class to clicked slot
                        this.classList.add('selected');
                        
                        // Update hidden input value
                        timeInput.value = this.dataset.time;
                    }
                });
            });
            
            // Form validation
            const appointmentForm = document.getElementById('appointmentForm');
            appointmentForm.addEventListener('submit', function(event) {
                if (!timeInput.value) {
                    event.preventDefault();
                    alert('Please select an appointment time');
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

