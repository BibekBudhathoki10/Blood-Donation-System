<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Map, java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reports - Blood Donation System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        .reports-container {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }
        
        .report-card {
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 20px;
        }
        
        .report-card h3 {
            margin-top: 0;
            color: #333;
            margin-bottom: 15px;
            border-bottom: 1px solid #eee;
            padding-bottom: 10px;
        }
        
        .chart-container {
            position: relative;
            height: 250px;
            width: 100%;
        }
        
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 15px;
        }
        
        .stat-item {
            background-color: #f8f9fa;
            border-radius: 5px;
            padding: 15px;
            text-align: center;
        }
        
        .stat-value {
            font-size: 2rem;
            font-weight: bold;
            color: #e74c3c;
            margin: 10px 0;
        }
        
        .stat-label {
            color: #666;
            font-size: 0.9rem;
        }
        
        .filter-section {
            margin-bottom: 30px;
            display: flex;
            gap: 15px;
            align-items: flex-end;
        }
        
        .filter-section .form-group {
            margin-bottom: 0;
        }
        
        .table-responsive {
            overflow-x: auto;
        }
        
        .data-table {
            width: 100%;
            border-collapse: collapse;
        }
        
        .data-table th, .data-table td {
            padding: 10px;
            text-align: left;
            border-bottom: 1px solid #eee;
        }
        
        .data-table th {
            background-color: #f8f9fa;
            font-weight: bold;
            color: #333;
        }
        
        .export-options {
            margin-top: 20px;
            text-align: right;
        }
    </style>
</head>
<body>
    <jsp:include page="../common/header.jsp" />
    
    <div class="container">
        <h1>Reports Dashboard</h1>
        
        <div class="filter-section">
            <form action="${pageContext.request.contextPath}/admin/reports" method="get" id="reportForm">
                <div class="form-group">
                    <label for="reportType">Report Type:</label>
                    <select id="reportType" name="reportType" onchange="this.form.submit()">
                        <option value="donations" ${reportType == 'donations' ? 'selected' : ''}>Donations</option>
                        <option value="inventory" ${reportType == 'inventory' ? 'selected' : ''}>Blood Inventory</option>
                        <option value="requests" ${reportType == 'requests' ? 'selected' : ''}>Blood Requests</option>
                        <option value="events" ${reportType == 'events' ? 'selected' : ''}>Donation Events</option>
                        <option value="donors" ${reportType == 'donors' ? 'selected' : ''}>Donor Statistics</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="timeRange">Time Range:</label>
                    <select id="timeRange" name="timeRange" onchange="this.form.submit()">
                        <option value="7days" ${timeRange == '7days' ? 'selected' : ''}>Last 7 Days</option>
                        <option value="30days" ${timeRange == '30days' ? 'selected' : ''}>Last 30 Days</option>
                        <option value="90days" ${timeRange == '90days' ? 'selected' : ''}>Last 90 Days</option>
                        <option value="1year" ${timeRange == '1year' ? 'selected' : ''}>Last Year</option>
                        <option value="all" ${timeRange == 'all' ? 'selected' : ''}>All Time</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <button type="submit" class="btn btn-primary">Generate Report</button>
                </div>
            </form>
        </div>
        
        <div class="reports-container">
            <!-- Summary Statistics Card -->
            <div class="report-card">
                <h3>Summary Statistics</h3>
                <div class="stats-grid">
                    <div class="stat-item">
                        <div class="stat-value">${totalDonations}</div>
                        <div class="stat-label">Total Donations</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-value">${totalRequests}</div>
                        <div class="stat-label">Blood Requests</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-value">${totalDonors}</div>
                        <div class="stat-label">Registered Donors</div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-value">${totalEvents}</div>
                        <div class="stat-label">Donation Events</div>
                    </div>
                </div>
            </div>
            
            <!-- Primary Chart Card -->
            <div class="report-card">
                <h3>
                    <% if(request.getAttribute("reportType") != null) { %>
                        <% if(request.getAttribute("reportType").equals("donations")) { %>
                            Donations Over Time
                        <% } else if(request.getAttribute("reportType").equals("inventory")) { %>
                            Blood Inventory Levels
                        <% } else if(request.getAttribute("reportType").equals("requests")) { %>
                            Blood Requests Over Time
                        <% } else if(request.getAttribute("reportType").equals("events")) { %>
                            Donation Events Participation
                        <% } else if(request.getAttribute("reportType").equals("donors")) { %>
                            Donor Registration Trends
                        <% } %>
                    <% } else { %>
                        Donations Over Time
                    <% } %>
                </h3>
                <div class="chart-container">
                    <canvas id="primaryChart"></canvas>
                </div>
            </div>
            
            <!-- Secondary Chart Card -->
            <div class="report-card">
                <h3>
                    <% if(request.getAttribute("reportType") != null) { %>
                        <% if(request.getAttribute("reportType").equals("donations")) { %>
                            Donations by Blood Type
                        <% } else if(request.getAttribute("reportType").equals("inventory")) { %>
                            Current Inventory by Blood Type
                        <% } else if(request.getAttribute("reportType").equals("requests")) { %>
                            Requests by Blood Type
                        <% } else if(request.getAttribute("reportType").equals("events")) { %>
                            Events by Location
                        <% } else if(request.getAttribute("reportType").equals("donors")) { %>
                            Donors by Age Group
                        <% } %>
                    <% } else { %>
                        Donations by Blood Type
                    <% } %>
                </h3>
                <div class="chart-container">
                    <canvas id="secondaryChart"></canvas>
                </div>
            </div>
        </div>
        
        <!-- Detailed Data Table -->
        <div class="report-card" style="margin-top: 20px;">
            <h3>
                <% if(request.getAttribute("reportType") != null) { %>
                    <% if(request.getAttribute("reportType").equals("donations")) { %>
                        Recent Donations
                    <% } else if(request.getAttribute("reportType").equals("inventory")) { %>
                        Current Inventory Status
                    <% } else if(request.getAttribute("reportType").equals("requests")) { %>
                        Recent Blood Requests
                    <% } else if(request.getAttribute("reportType").equals("events")) { %>
                        Recent Donation Events
                    <% } else if(request.getAttribute("reportType").equals("donors")) { %>
                        Recently Registered Donors
                    <% } %>
                <% } else { %>
                    Recent Donations
                <% } %>
            </h3>
            
            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <% if(request.getAttribute("reportType") == null || request.getAttribute("reportType").equals("donations")) { %>
                                <th>Donor Name</th>
                                <th>Blood Type</th>
                                <th>Donation Date</th>
                                <th>Quantity (ml)</th>
                                <th>Status</th>
                            <% } else if(request.getAttribute("reportType").equals("inventory")) { %>
                                <th>Blood Type</th>
                                <th>Quantity Available (ml)</th>
                                <th>Last Updated</th>
                                <th>Status</th>
                            <% } else if(request.getAttribute("reportType").equals("requests")) { %>
                                <th>Requester</th>
                                <th>Blood Type</th>
                                <th>Request Date</th>
                                <th>Quantity (ml)</th>
                                <th>Status</th>
                            <% } else if(request.getAttribute("reportType").equals("events")) { %>
                                <th>Event Name</th>
                                <th>Date</th>
                                <th>Location</th>
                                <th>Participants</th>
                                <th>Status</th>
                            <% } else if(request.getAttribute("reportType").equals("donors")) { %>
                                <th>Donor Name</th>
                                <th>Blood Type</th>
                                <th>Registration Date</th>
                                <th>Total Donations</th>
                                <th>Status</th>
                            <% } %>
                        </tr>
                    </thead>
                    <tbody>
                        <!-- This would be populated with actual data from the server -->
                        <tr>
                            <td colspan="5" class="text-center">No data available for the selected criteria.</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            
            <div class="export-options">
                <a href="${pageContext.request.contextPath}/admin/reports/export?type=${reportType}&range=${timeRange}&format=csv" class="btn btn-secondary">Export as CSV</a>
                <a href="${pageContext.request.contextPath}/admin/reports/export?type=${reportType}&range=${timeRange}&format=pdf" class="btn btn-secondary">Export as PDF</a>
                <a href="${pageContext.request.contextPath}/admin/reports/export?type=${reportType}&range=${timeRange}&format=excel" class="btn btn-secondary">Export as Excel</a>
            </div>
        </div>
    </div>
    
    <jsp:include page="../common/footer.jsp" />
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Sample data for charts - in a real application, this would come from the server
            const timeLabels = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'];
            const primaryData = [65, 59, 80, 81, 56, 55, 40];
            
            const categoryLabels = ['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'];
            const secondaryData = [12, 19, 3, 5, 2, 3, 20, 3];
            
            // Primary Chart
            const primaryCtx = document.getElementById('primaryChart').getContext('2d');
            const primaryChart = new Chart(primaryCtx, {
                type: 'line',
                data: {
                    labels: timeLabels,
                    datasets: [{
                        label: 'Count',
                        data: primaryData,
                        backgroundColor: 'rgba(231, 76, 60, 0.2)',
                        borderColor: 'rgba(231, 76, 60, 1)',
                        borderWidth: 2,
                        tension: 0.1
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                        y: {
                            beginAtZero: true
                        }
                    }
                }
            });
            
            // Secondary Chart
            const secondaryCtx = document.getElementById('secondaryChart').getContext('2d');
            const secondaryChart = new Chart(secondaryCtx, {
                type: 'doughnut',
                data: {
                    labels: categoryLabels,
                    datasets: [{
                        label: 'Count',
                        data: secondaryData,
                        backgroundColor: [
                            'rgba(231, 76, 60, 0.7)',
                            'rgba(241, 196, 15, 0.7)',
                            'rgba(46, 204, 113, 0.7)',
                            'rgba(52, 152, 219, 0.7)',
                            'rgba(155, 89, 182, 0.7)',
                            'rgba(52, 73, 94, 0.7)',
                            'rgba(230, 126, 34, 0.7)',
                            'rgba(149, 165, 166, 0.7)'
                        ],
                        borderColor: [
                            'rgba(231, 76, 60, 1)',
                            'rgba(241, 196, 15, 1)',
                            'rgba(46, 204, 113, 1)',
                            'rgba(52, 152, 219, 1)',
                            'rgba(155, 89, 182, 1)',
                            'rgba(52, 73, 94, 1)',
                            'rgba(230, 126, 34, 1)',
                            'rgba(149, 165, 166, 1)'
                        ],
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            position: 'right',
                        }
                    }
                }
            });
        });
    </script>
</body>
</html>

