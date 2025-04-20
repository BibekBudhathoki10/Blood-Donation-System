<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.BloodInventory, java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Blood Inventory - Blood Donation System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .blood-stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            gap: 15px;
            margin-bottom: 30px;
        }
        
        .blood-stat-card {
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
            padding: 15px;
            text-align: center;
        }
        
        .blood-stat-card h3 {
            font-size: 1.8rem;
            margin-bottom: 5px;
            color: #e74c3c;
        }
        
        .blood-stat-card p {
            margin: 0;
            color: #666;
        }
        
        .action-buttons {
            margin-bottom: 20px;
            text-align: right;
        }
    </style>
</head>
<body>
    <jsp:include page="../../common/header.jsp" />
    
    <div class="container">
        <h1>Blood Inventory</h1>
        
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
        
        <div class="blood-stats">
            <div class="blood-stat-card">
                <h3>A+</h3>
                <p><%= request.getAttribute("aPositiveCount") %> units</p>
            </div>
            <div class="blood-stat-card">
                <h3>A-</h3>
                <p><%= request.getAttribute("aNegativeCount") %> units</p>
            </div>
            <div class="blood-stat-card">
                <h3>B+</h3>
                <p><%= request.getAttribute("bPositiveCount") %> units</p>
            </div>
            <div class="blood-stat-card">
                <h3>B-</h3>
                <p><%= request.getAttribute("bNegativeCount") %> units</p>
            </div>
            <div class="blood-stat-card">
                <h3>AB+</h3>
                <p><%= request.getAttribute("abPositiveCount") %> units</p>
            </div>
            <div class="blood-stat-card">
                <h3>AB-</h3>
                <p><%= request.getAttribute("abNegativeCount") %> units</p>
            </div>
            <div class="blood-stat-card">
                <h3>O+</h3>
                <p><%= request.getAttribute("oPositiveCount") %> units</p>
            </div>
            <div class="blood-stat-card">
                <h3>O-</h3>
                <p><%= request.getAttribute("oNegativeCount") %> units</p>
            </div>
        </div>
        
        <div class="action-buttons">
            <a href="${pageContext.request.contextPath}/inventory/add" class="btn btn-primary">Add Blood Unit</a>
            <button onclick="updateExpiredBlood()" class="btn btn-secondary">Update Expired Units</button>
        </div>
        
        <div class="filter-section">
            <form action="${pageContext.request.contextPath}/inventory/list" method="get">
                <div class="form-group">
                    <label for="bloodGroup">Filter by Blood Group:</label>
                    <select id="bloodGroup" name="bloodGroup">
                        <option value="">All Blood Groups</option>
                        <option value="A+" ${bloodGroup == 'A+' ? 'selected' : ''}>A+</option>
                        <option value="A-" ${bloodGroup == 'A-' ? 'selected' : ''}>A-</option>
                        <option value="B+" ${bloodGroup == 'B+' ? 'selected' : ''}>B+</option>
                        <option value="B-" ${bloodGroup == 'B-' ? 'selected' : ''}>B-</option>
                        <option value="AB+" ${bloodGroup == 'AB+' ? 'selected' : ''}>AB+</option>
                        <option value="AB-" ${bloodGroup == 'AB-' ? 'selected' : ''}>AB-</option>
                        <option value="O+" ${bloodGroup == 'O+' ? 'selected' : ''}>O+</option>
                        <option value="O-" ${bloodGroup == 'O-' ? 'selected' : ''}>O-</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="status">Filter by Status:</label>
                    <select id="status" name="status">
                        <option value="">All</option>
                        <option value="available" ${status == 'available' ? 'selected' : ''}>Available</option>
                        <option value="reserved" ${status == 'reserved' ? 'selected' : ''}>Reserved</option>
                        <option value="used" ${status == 'used' ? 'selected' : ''}>Used</option>
                        <option value="expired" ${status == 'expired' ? 'selected' : ''}>Expired</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <button type="submit" class="btn btn-primary">Filter</button>
                    <a href="${pageContext.request.contextPath}/inventory/list" class="btn btn-secondary">Reset</a>
                </div>
            </form>
        </div>
        
        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Blood Group</th>
                        <th>Quantity</th>
                        <th>Collection Date</th>
                        <th>Expiry Date</th>
                        <th>Status</th>
                        <th>Donor ID</th>
                        <th>Location</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        List<BloodInventory> inventoryList = (List<BloodInventory>) request.getAttribute("inventoryList");
                        if(inventoryList != null && !inventoryList.isEmpty()) {
                            for(BloodInventory inventory : inventoryList) {
                    %>
                    <tr>
                        <td><%= inventory.getId() %></td>
                        <td><%= inventory.getBloodGroup() %></td>
                        <td><%= inventory.getQuantity() %></td>
                        <td><%= inventory.getCollectionDate() %></td>
                        <td><%= inventory.getExpiryDate() %></td>
                        <td>
                            <span class="status-<%= inventory.getStatus().toLowerCase() %>">
                                <%= inventory.getStatus() %>
                            </span>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/admin/view-donor?id=<%= inventory.getDonorId() %>">
                                Donor #<%= inventory.getDonorId() %>
                            </a>
                        </td>
                        <td><%= inventory.getLocation() %></td>
                        <td class="table-actions">
                            <a href="${pageContext.request.contextPath}/inventory/view?id=<%= inventory.getId() %>" class="action-view">View</a>
                            
                            <% if("available".equals(inventory.getStatus())) { %>
                                <a href="${pageContext.request.contextPath}/inventory/edit?id=<%= inventory.getId() %>" class="action-edit">Edit</a>
                                
                                <form action="${pageContext.request.contextPath}/inventory/update-status" method="post" style="display: inline;">
                                    <input type="hidden" name="id" value="<%= inventory.getId() %>">
                                    <input type="hidden" name="status" value="reserved">
                                    <button type="submit" class="action-edit">Reserve</button>
                                </form>
                            <% } else if("reserved".equals(inventory.getStatus())) { %>
                                <form action="${pageContext.request.contextPath}/inventory/update-status" method="post" style="display: inline;">
                                    <input type="hidden" name="id" value="<%= inventory.getId() %>">
                                    <input type="hidden" name="status" value="used">
                                    <button type="submit" class="action-edit">Mark Used</button>
                                </form>
                                
                                <form action="${pageContext.request.contextPath}/inventory/update-status" method="post" style="display: inline;">
                                    <input type="hidden" name="id" value="<%= inventory.getId() %>">
                                    <input type="hidden" name="status" value="available">
                                    <button type="submit" class="action-edit">Unreserve</button>
                                </form>
                            <% } %>
                            
                            <form action="${pageContext.request.contextPath}/inventory/delete" method="post" style="display: inline;">
                                <input type="hidden" name="id" value="<%= inventory.getId() %>">
                                <button type="submit" class="action-delete" onclick="return confirm('Are you sure you want to delete this blood unit?')">Delete</button>
                            </form>
                        </td>
                    </tr>
                    <% 
                            }
                        } else {
                    %>
                    <tr>
                        <td colspan="9" class="text-center">No blood inventory found</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
    
    <jsp:include page="../../common/footer.jsp" />
    
    <script>
        function updateExpiredBlood() {
            if (confirm('Are you sure you want to update expired blood units?')) {
                fetch('${pageContext.request.contextPath}/inventory/update-expired', {
                    method: 'POST'
                })
                .then(response => {
                    if (response.ok) {
                        alert('Expired blood units updated successfully');
                        location.reload();
                    } else {
                        alert('Failed to update expired blood units');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('An error occurred while updating expired blood units');
                });
            }
        }
    </script>
</body>
</html>

