<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<footer>
    <div class="footer-container">
        <div class="footer-content">
            <div class="footer-section">
                <h3>Blood Donation System</h3>
                <p>Connecting donors with those in need. Save lives by donating blood.</p>
            </div>
            
            <div class="footer-section">
                <h3>Quick Links</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/">Home</a></li>
                    <li><a href="${pageContext.request.contextPath}/auth/login">Login</a></li>
                    <li><a href="${pageContext.request.contextPath}/auth/register">Register</a></li>
                </ul>
            </div>
            
            <div class="footer-section">
                <h3>Contact Us</h3>
                <p>Email: bds@blooddonationnepal.com</p>
                <p>Phone: + (977) 9812345678</p>
                <p>Address: 123 Red Cross, Damak, Street-4</p>
            </div>
        </div>
        
        <div class="footer-bottom">
            <p>&copy; <%= new java.util.Date().getYear() + 1900 %> Blood Donation System. All rights reserved.</p>
        </div>
    </div>
</footer>

