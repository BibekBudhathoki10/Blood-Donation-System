<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.BloodRequest, java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>View Blood Requests - Blood Donation System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .request-card {
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 20px;
            margin-bottom: 20px;
            position: relative;
            overflow: hidden;
        }
        
        .request-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 5px;
            height: 100%;
        }
        
        .request-card.urgent::before {
            background-color: #e74c3c;
        }
        
        .request-card.critical::before {
            background-color: #c0392b;
        }
        
        .request-card.normal::before {
            background-color: #3498db;
        }
        
        .request-header {
            display: flex;
            justify-content: space-between;
            margin-bottom: 15px;
        }
        
        .request-header h3 {
            margin: 0;
            color: #333;
        }
        
        .request-header .blood-group {
            font-size: 1.2rem;
            font-weight: bold;
            color: #e74c3c;
        }
        
        .request-details {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            margin-bottom: 15px;
        }
        
        .request-detail {
            margin-bottom: 5px;
        }
        
        .request-detail label {
            font-weight: bold;
            color: #666;
            margin-right: 5px;
        }
        
        .request-actions {
            display: flex;
            justify-content: flex-end;
            gap: 10px;
            margin-top: 15px;
        }
        
        .urgency-badge {
            display: inline-block;
            padding: 3px 8px;
            border-radius: 3px;
            font-size: 0.8rem;
            font-weight: bold;
            text-transform: uppercase;
        }
        
        .urgency-normal {
            background-color: #3498db;
            color: #fff;
        }
        
        .urgency-urgent {
            background-color: #e74c3c;
            color: #fff;
        }
        
        .urgency-critical {
            background-color: #c0392b;
            color: #fff;
        }
        
        .filter-section {
            margin-bottom: 30px;
        }
        
        .filter-section form {
            display: flex;
            flex-wrap: wrap;
            gap: 15px;
            align-items: flex-end;
        }
        
        .filter-section .form-group {
            margin-bottom: 0;
        }
    </style>
</head>
<body>
    <jsp:include page="../common/header.jsp" />
    
    <div class="container">
        <h1>Blood Requests Matching Your Blood Group</h1>
        
        <div class="filter-section">
            <form action="${pageContext.request.contextPath}/donor/view-requests" method="get">
                <div class="form-group">
                    <label for="urgency">Filter by Urgency:</label>
                    <select id="urgency" name="urgency">
                        <option value="">All</option>
                        <option value="normal" ${param.urgency == 'normal' ? 'selected' : ''}>Normal</option>
                        <option value="urgent" ${param.urgency == 'urgent' ? 'selected' : ''}>Urgent</option>
                        <option value="critical" ${param.urgency == 'critical' ? 'selected' : ''}>Critical</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="location">Filter by Location:</label>
                    <input type="text" id="location" name="location" value="${param.location}" placeholder="Enter location">
                </div>
                
                <div class="form-group">
                    <button type="submit" class="btn btn-primary">Filter</button>
                    <a href="${pageContext.request.contextPath}/donor/view-requests" class="btn btn-secondary">Reset</a>
                </div>
            </form>
        </div>
        
        <% 
            List<BloodRequest> requests = (List<BloodRequest>) request.getAttribute("requests");
            if(requests != null && !requests.isEmpty()) {
                for(BloodRequest bloodRequest : requests) {
        %>
        <div class="request-card <%= bloodRequest.getUrgency().toLowerCase() %>">
            <div class="request-header">
                <div>
                    <h3>Blood Request #<%= bloodRequest.getId() %></h3>
                    <span class="urgency-badge urgency-<%= bloodRequest.getUrgency().toLowerCase() %>">
                        <%= bloodRequest.getUrgency() %>
                    </span>
                </div>
                <div class="blood-group">
                    <%= bloodRequest.getBloodGroup() %> <span>(<%= bloodRequest.getQuantity() %> units)</span>
                </div>
            </div>
            
            <div class="request-details">
                <div>
                    <div class="request-detail">
                        <label>Hospital:</label>
                        <span><%= bloodRequest.getHospitalName() %></span>
                    </div>
                    <div class="request-detail">
                        <label>Location:</label>
                        <span><%= bloodRequest.getHospitalAddress() %></span>
                    </div>
                    <div class="request-detail">
                        <label>Patient:</label>
                        <span><%= bloodRequest.getPatientName() %></span>
                    </div>
                </div>
                
                <div>
                    <div class="request-detail">
                        <label>Contact Person:</label>
                        <span><%= bloodRequest.getContactPerson() %></span>
                    </div>
                    <div class="request-detail">
                        <label>Contact Phone:</label>
                        <span><%= bloodRequest.getContactPhone() %></span>
                    </div>
                    <div class="request-detail">
                        <label>Required By:</label>
                        <span><%= bloodRequest.getRequiredDate() %></span>
                    </div>
                </div>
            </div>
            
            <% if(bloodRequest.getReason() != null && !bloodRequest.getReason().isEmpty()) { %>
            <div class="request-detail">
                <label>Reason:</label>
                <p><%= bloodRequest.getReason() %></p>
            </div>
            <% } %>
            
            <div class="request-actions">
                <a href="${pageContext.request.contextPath}/donor/schedule-appointment" class="btn btn-primary">Schedule Donation</a>
                <a href="tel:<%= bloodRequest.getContactPhone() %>" class="btn btn-secondary">Contact</a>
            </div>
        </div>
        <% 
                }
            } else {
        %>
        <div class="alert alert-info">
            No blood requests matching your blood group at the moment.
        </div>
        <% } %>
    </div>
    
    <jsp:include page="../common/footer.jsp" />
</body>
</html>

