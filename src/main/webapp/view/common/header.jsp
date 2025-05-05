<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.User" %>
<%
    // Get user information from session attributes
    Integer userId = (Integer) session.getAttribute("userId");
    String userRole = (String) session.getAttribute("userRole");
    String userName = (String) session.getAttribute("userName");
    boolean isLoggedIn = (userId != null);
%>
<header>
    <div class="header-container">
        <div class="logo">
            <a href="${pageContext.request.contextPath}/">
                <img src="${pageContext.request.contextPath}/assets/images/blooddonation.png" alt="Blood Donation System">
                <span>Blood Donation System</span>
            </a>
        </div>
        
        <nav>
            <ul>
                <li><a href="${pageContext.request.contextPath}/">Home</a></li>
                
                <% if (!isLoggedIn) { %>
                    <li><a href="${pageContext.request.contextPath}/auth/login">Login</a></li>
                    <li><a href="${pageContext.request.contextPath}/auth/register">Register</a></li>
                <% } else { %>
                    <% if ("admin".equals(userRole)) { %>
                        <li><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
                        <li><a href="${pageContext.request.contextPath}/admin/manage-donors">Donors</a></li>
                        <li><a href="${pageContext.request.contextPath}/admin/manage-requests">Requests</a></li>
                        <li><a href="${pageContext.request.contextPath}/admin/inventory">Inventory</a></li>
                        <li><a href="${pageContext.request.contextPath}/admin/events">Events</a></li>
                    <% } else if ("donor".equals(userRole)) { %>
                        <li><a href="${pageContext.request.contextPath}/donor/dashboard">Dashboard</a></li>
                        <li><a href="${pageContext.request.contextPath}/donor/schedule-appointment">Donate</a></li>
                        <li><a href="${pageContext.request.contextPath}/donor/donation-history">History</a></li>
                        <li><a href="${pageContext.request.contextPath}/donor/events">Events</a></li>
                    <% } else if ("general".equals(userRole)) { %>
                        <li><a href="${pageContext.request.contextPath}/user/dashboard">Dashboard</a></li>
                        <li><a href="${pageContext.request.contextPath}/user/request-blood">Request Blood</a></li>
                        <li><a href="${pageContext.request.contextPath}/user/my-requests">My Requests</a></li>
                        <li><a href="${pageContext.request.contextPath}/user/events">Events</a></li>
                    <% } %>
                    <li><a href="${pageContext.request.contextPath}/auth/logout">Logout</a></li>
                    <li><span class="user-greeting">Hello, <%= userName %></span></li>
                <% } %>
            </ul>
        </nav>
    </div>
</header>
