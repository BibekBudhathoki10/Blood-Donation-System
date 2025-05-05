<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Date, java.util.Calendar, java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Schedule Blood Donation - Blood Donation System</title>
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
        
        .eligibility-check {
            background-color: #f8f9fa;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 30px;
        }
        
        .eligibility-check h3 {
            margin-top: 0;
            color: #555;
        }
        
        .eligibility-questions {
            margin-top: 15px;
        }
        
        .eligibility-question {
            margin-bottom: 10px;
        }
        
        .eligibility-question label {
            display: flex;
            align-items: center;
        }
        
        .eligibility-question input[type="checkbox"] {
            margin-right: 10px;
        }
        
        .error-details {
            background-color: #f8d7da;
            color: #721c24;
            padding: 15px;
            border-radius: 4px;
            margin-top: 10px;
            margin-bottom: 20px;
            border: 1px solid #f5c6cb;
            white-space: pre-wrap;
            word-break: break-word;
        }
    </style>
</head>
<body>
    <jsp:include page="../common/header.jsp" />
    
    <div class="container">
        <h1>Schedule Blood Donation</h1>
        
        <% if(request.getAttribute("success") != null) { %>
            <div class="alert alert-success">
                <%= request.getAttribute("success") %>
            </div>
        <% } %>
        
        <% if(request.getAttribute("error") != null) { %>
            <div class="alert alert-danger">
                <%= request.getAttribute("error") %>
            </div>
            <% if(request.getAttribute("errorDetails") != null) { %>
                <div class="error-details">
                    <strong>Error Details:</strong>
                    <pre><%= request.getAttribute("errorDetails") %></pre>
                </div>
            <% } %>
        <% } %>
        
        <div class="eligibility-check">
            <h3>Blood Donation Eligibility</h3>
            <p>Before scheduling your appointment, please confirm that you meet the following eligibility criteria:</p>
            
            <div class="eligibility-questions">
                <div class="eligibility-question">
                    <label>
                        <input type="checkbox" id="age" required>
                        I am between 18 and 65 years old
                    </label>
                </div>
                
                <div class="eligibility-question">
                    <label>
                        <input type="checkbox" id="weight" required>
                        I weigh at least 50 kg (110 lbs)
                    </label>
                </div>
                
                <div class="eligibility-question">
                    <label>
                        <input type="checkbox" id="health" required>
                        I am in good health and feeling well
                    </label>
                </div>
                
                <div class="eligibility-question">
                    <label>
                        <input type="checkbox" id="lastDonation" required>
                        It has been at least 3 months since my last blood donation
                    </label>
                </div>
                
                <div class="eligibility-question">
                    <label>
                        <input type="checkbox" id="medication" required>
                        I am not taking any medication that would prevent me from donating blood
                    </label>
                </div>
            </div>
        </div>
        
        <div class="form-container">
            <h2 class="form-title">Appointment Details</h2>
            
            <form action="${pageContext.request.contextPath}/user/schedule-appointment" method="post" id="appointmentForm">
                <div class="form-group">
                    <label for="appointmentDate" class="form-label">Appointment Date</label>
                    <input type="date" id="appointmentDate" name="appointmentDate" class="form-control" required 
                           min="<%= new SimpleDateFormat("yyyy-MM-dd").format(new Date()) %>"
                           value="${param.appointmentDate}">
                    <small class="form-text">Select a date for your blood donation appointment.</small>
                    <% if(request.getAttribute("appointmentDateError") != null) { %>
                        <div class="error-text">${appointmentDateError}</div>
                    <% } %>
                </div>
                
                <div class="form-group">
                    <label class="form-label">Appointment Time</label>
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
                    <textarea id="notes" name="notes" class="form-control" rows="3">${param.notes}</textarea>
                    <small class="form-text">Any additional information you'd like to provide about your appointment.</small>
                </div>
                
                <div class="form-group">
                    <button type="submit" class="btn btn-primary" id="scheduleBtn" disabled>Schedule Appointment</button>
                    <a href="${pageContext.request.contextPath}/user/dashboard" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </div>
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
            
            // Check if form can be submitted
            checkFormValidity();
        }
        
        // Function to check eligibility
        function checkEligibility() {
            const age = document.getElementById('age').checked;
            const weight = document.getElementById('weight').checked;
            const health = document.getElementById('health').checked;
            const lastDonation = document.getElementById('lastDonation').checked;
            const medication = document.getElementById('medication').checked;
            
            return age && weight && health && lastDonation && medication;
        }
        
        // Function to check form validity
        function checkFormValidity() {
            const date = document.getElementById('appointmentDate').value;
            const time = document.getElementById('appointmentTime').value;
            const eligible = checkEligibility();
            
            document.getElementById('scheduleBtn').disabled = !(date && time && eligible);
        }
        
        // Add event listeners
        document.getElementById('appointmentDate').addEventListener('change', checkFormValidity);
        
        document.querySelectorAll('.eligibility-question input').forEach(checkbox => {
            checkbox.addEventListener('change', checkFormValidity);
        });
        
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
            
            checkFormValidity();
        });
        
        // Initialize form
        window.onload = function() {
            // If there's a previously selected time, mark it
            const selectedTime = document.getElementById('appointmentTime').value;
            if (selectedTime) {
                document.querySelector(`.time-slot[data-time="${selectedTime}"]`)?.classList.add('selected');
            }
            
            checkFormValidity();
        };
    </script>
</body>
</html>
