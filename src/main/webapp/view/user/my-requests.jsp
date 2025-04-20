<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.BloodRequest, java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Blood Requests - Blood Donation System</title>
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
        
        .request-card.pending::before {
            background-color: #3498db;
        }
        
        .request-card.approved::before {
            background-color: #2ecc71;
        }
        
        .request-card.fulfilled::before {
            background-color: #27ae60;
        }
        
        .request-card.rejected::before {
            background-color: #e74c3c;
        }
        
        .request-card.cancelled::before {
            background-color: #95a5a6;
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
        
        .status-badge {
            display: inline-block;
            padding: 3px 8px;
            border-radius: 3px;
            font-size: 0.8rem;
            font-weight: bold;
            text-transform: uppercase;
        }
        
        .status-pending {
            background-color: #3498db;
            color: #fff;
        }
        
        .status-approved {
            background-color: #2ecc71;
            color: #fff;
        }
        
        .status-fulfilled {
            background-color: #27ae60;
            color: #fff;
        }
        
        .status-rejected {
            background-color: #e74c3c;
            color: #fff;
        }
        
        .status-cancelled {
            background-color: #95a5a6;
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
        <h1>My Blood Requests</h1>
        
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
            <form action="${pageContext.request.contextPath}/user/my-requests" method="get">
                <div class="form-group">
                    <label for="status">Filter by Status:</label>
                    <select id="status" name="status">
                        <option value="">All</option>
                        <option value="pending" ${param.status == 'pending' ? 'selected' : ''}>Pending</option>
                        <option value="approved" ${param.status == 'approved' ? 'selected' : ''}>Approved</option>
                        <option value="fulfilled" ${param.status == 'fulfilled' ? 'selected' : ''}>Fulfilled</option>
                        <option value="rejected" ${param.status == 'rejected' ? 'selected' : ''}>Rejected</option>
                        <option value="cancelled" ${param.status == 'cancelled' ? 'selected' : ''}>Cancelled</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <button type="submit" class="btn btn-primary">Filter</button>
                    <a href="${pageContext.request.contextPath}/user/my-requests" class="btn btn-secondary">Reset</a>
                </div>
            </form>
        </div>
        
        <div class="action-buttons" style="margin-bottom: 20px;">
            <a href="${pageContext.request.contextPath}/user/request-blood" class="btn btn-primary">New Blood Request</a>
        </div>
        
        <% 
            List<BloodRequest> requests = (List<BloodRequest>) request.getAttribute("requests");
            if(requests != null && !requests.isEmpty()) {
                for(BloodRequest bloodRequest : requests) {
        %>
        <div class="request-card <%= bloodRequest.getStatus().toLowerCase() %>">
            <div class="request-header">
                <div>
                    <h3>Request #<%= bloodRequest.getId() %></h3>
                    <span class="status-badge status-<%= bloodRequest.getStatus().toLowerCase() %>">
                        <%= bloodRequest.getStatus() %>
                    </span>
                </div>
                <div class="blood-group">
                    <%= bloodRequest.getBloodGroup() %> <span>(<%= bloodRequest.getQuantity() %> units)</span>
                </div>
            </div>
            
            <div class="request-details">
                <div>
                    <div class="request-detail">
                        <label>Patient:</label>
                        <span><%= bloodRequest.getPatientName() %></span>
                    </div>
                    <div class="request-detail">
                        <label>Hospital:</label>
                        <span><%= bloodRequest.getHospitalName() %></span>
                    </div>
                    <div class="request-detail">
                        <label>Urgency:</label>
                        <span><%= bloodRequest.getUrgency() %></span>
                    </div>
                </div>
                
                <div>
                    <div class="request-detail">
                        <label>Required By:</label>
                        <span><%= bloodRequest.getRequiredDate() %></span>
                    </div>
                    <div class="request-detail">
                        <label>Requested On:</label>
                        <span><%= bloodRequest.getRequestDate() %></span>
                    </div>
                    <div class="request-detail">
                        <label>Contact:</label>
                        <span><%= bloodRequest.getContactPerson() %> (<%= bloodRequest.getContactPhone() %>)</span>
                    </div>
                </div>
            </div>
            
            <div class="request-actions">
                <% if("pending".equals(bloodRequest.getStatus())) { %>
                    <a href="${pageContext.request.contextPath}/user/edit-request?id=<%= bloodRequest.getId() %>" class="btn btn-secondary">Edit</a>
                    <a href="${pageContext.request.contextPath}/user/cancel-request?id=<%= bloodRequest.getId() %>" class="btn btn-danger" onclick="return confirm('Are you sure you want to cancel this request?')">Cancel</a>
                <% } else if("approved".equals(bloodRequest.getStatus())) { %>
                    <a href="${pageContext.request.contextPath}/user/cancel-request?id=<%= bloodRequest.getId() %>" class="btn btn-danger" onclick="return confirm('Are you sure you want to cancel this request?')">Cancel</a>
                <% } %>
            </div>
        </div>
        <% 
                }
            } else {
        %>
        <div class="alert alert-info">
            You haven't made any blood requests yet. <a href="${pageContext.request.contextPath}/user/request-blood">Request blood now</a>.
        </div>
        <% } %>
    </div>
    
    <jsp:include page="../common/footer.jsp" />
</body>
</html>

