<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Donor, model.User, java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Donors - Blood Donation System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <jsp:include page="../common/header.jsp" />
    
    <div class="container">
        <h1>Manage Donors</h1>
        
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
            <form action="${pageContext.request.contextPath}/admin/manage-donors" method="get">
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
                    <label for="availability">Filter by Availability:</label>
                    <select id="availability" name="availability">
                        <option value="">All</option>
                        <option value="true">Available</option>
                        <option value="false">Not Available</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <button type="submit" class="btn btn-primary">Filter</button>
                    <a href="${pageContext.request.contextPath}/admin/manage-donors" class="btn btn-secondary">Reset</a>
                </div>
            </form>
        </div>
        
        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Phone</th>
                        <th>Blood Group</th>
                        <th>Last Donation</th>
                        <th>Donation Count</th>
                        <th>Available</th>
                        <th>Location</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        List<Donor> donors = (List<Donor>) request.getAttribute("donors");
                        if(donors != null && !donors.isEmpty()) {
                            for(Donor donor : donors) {
                                User user = (User) request.getAttribute("user_" + donor.getId());
                    %>
                    <tr>
                        <td><%= donor.getId() %></td>
                        <td><%= user != null ? user.getName() : "N/A" %></td>
                        <td><%= user != null ? user.getEmail() : "N/A" %></td>
                        <td><%= user != null ? user.getPhone() : "N/A" %></td>
                        <td><%= donor.getBloodGroup() %></td>
                        <td><%= donor.getLastDonationDate() != null ? donor.getLastDonationDate() : "Never" %></td>
                        <td><%= donor.getDonationCount() %></td>
                        <td>
                            <span class="<%= donor.isAvailable() ? "status-available" : "status-unavailable" %>">
                                <%= donor.isAvailable() ? "Available" : "Not Available" %>
                            </span>
                        </td>
                        <td><%= donor.getLocation() %></td>
                        <td class="table-actions">
                            <a href="${pageContext.request.contextPath}/admin/view-donor?id=<%= donor.getId() %>" class="action-view">View</a>
                            <form action="${pageContext.request.contextPath}/admin/manage-donors/update-status" method="post" style="display: inline;">
                                <input type="hidden" name="id" value="<%= donor.getId() %>">
                                <input type="hidden" name="available" value="<%= !donor.isAvailable() %>">
                                <button type="submit" class="<%= donor.isAvailable() ? "action-delete" : "action-edit" %>">
                                    <%= donor.isAvailable() ? "Mark Unavailable" : "Mark Available" %>
                                </button>
                            </form>
                        </td>
                    </tr>
                    <% 
                            }
                        } else {
                    %>
                    <tr>
                        <td colspan="10" class="text-center">No donors found</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
    
    <jsp:include page="../common/footer.jsp" />
</body>
</html>

