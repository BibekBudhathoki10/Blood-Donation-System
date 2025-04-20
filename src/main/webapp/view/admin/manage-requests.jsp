<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.BloodRequest, model.User, java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Blood Requests - Blood Donation System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <jsp:include page="../common/header.jsp" />
    
    <div class="container">
        <h1>Manage Blood Requests</h1>
        
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
            <form action="${pageContext.request.contextPath}/admin/manage-requests" method="get">
                <div class="form-group">
                    <label for="bloodGroup">Filter by Blood Group:</label>
                    <select id="bloodGroup" name="bloodGroup">
                        <option value="">All Blood Groups</option>
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
                    <label for="status">Filter by Status:</label>
                    <select id="status" name="status">
                        <option value="">All</option>
                        <option value="pending">Pending</option>
                        <option value="approved">Approved</option>
                        <option value="fulfilled">Fulfilled</option>
                        <option value="rejected">Rejected</option>
                        <option value="cancelled">Cancelled</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="urgency">Filter by Urgency:</label>
                    <select id="urgency" name="urgency">
                        <option value="">All</option>
                        <option value="normal">Normal</option>
                        <option value="urgent">Urgent</option>
                        <option value="critical">Critical</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <button type="submit" class="btn btn-primary">Filter</button>
                    <a href="${pageContext.request.contextPath}/admin/manage-requests" class="btn btn-secondary">Reset</a>
                </div>
            </form>
        </div>
        
        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Requester</th>
                        <th>Blood Group</th>
                        <th>Quantity</th>
                        <th>Urgency</th>
                        <th>Status</th>
                        <th>Hospital</th>
                        <th>Required Date</th>
                        <th>Request Date</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        List<BloodRequest> requests = (List<BloodRequest>) request.getAttribute("requests");
                        if(requests != null && !requests.isEmpty()) {
                            for(BloodRequest bloodRequest : requests) {
                                User user = (User) request.getAttribute("user_" + bloodRequest.getUserId());
                    %>
                    <tr>
                        <td><%= bloodRequest.getId() %></td>
                        <td><%= user != null ? user.getName() : "N/A" %></td>
                        <td><%= bloodRequest.getBloodGroup() %></td>
                        <td><%= bloodRequest.getQuantity() %></td>
                        <td>
                            <span class="urgency-<%= bloodRequest.getUrgency().toLowerCase() %>">
                                <%= bloodRequest.getUrgency() %>
                            </span>
                        </td>
                        <td>
                            <span class="status-<%= bloodRequest.getStatus().toLowerCase() %>">
                                <%= bloodRequest.getStatus() %>
                            </span>
                        </td>
                        <td><%= bloodRequest.getHospitalName() %></td>
                        <td><%= bloodRequest.getRequiredDate() %></td>
                        <td><%= bloodRequest.getRequestDate() %></td>
                        <td class="table-actions">
                            <a href="${pageContext.request.contextPath}/admin/view-request?id=<%= bloodRequest.getId() %>" class="action-view">View</a>
                            
                            <% if("pending".equals(bloodRequest.getStatus())) { %>
                                <form action="${pageContext.request.contextPath}/admin/manage-requests/update-status" method="post" style="display: inline;">
                                    <input type="hidden" name="id" value="<%= bloodRequest.getId() %>">
                                    <input type="hidden" name="status" value="approved">
                                    <button type="submit" class="action-edit">Approve</button>
                                </form>
                                
                                <form action="${pageContext.request.contextPath}/admin/manage-requests/update-status" method="post" style="display: inline;">
                                    <input type="hidden" name="id" value="<%= bloodRequest.getId() %>">
                                    <input type="hidden" name="status" value="rejected">
                                    <button type="submit" class="action-delete">Reject</button>
                                </form>
                            <% } else if("approved".equals(bloodRequest.getStatus())) { %>
                                <form action="${pageContext.request.contextPath}/admin/manage-requests/update-status" method="post" style="display: inline;">
                                    <input type="hidden" name="id" value="<%= bloodRequest.getId() %>">
                                    <input type="hidden" name="status" value="fulfilled">
                                    <button type="submit" class="action-edit">Mark Fulfilled</button>
                                </form>
                            <% } %>
                        </td>
                    </tr>
                    <% 
                            }
                        } else {
                    %>
                    <tr>
                        <td colspan="10" class="text-center">No blood requests found</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
    
    <jsp:include page="../common/footer.jsp" />
</body>
</html>

