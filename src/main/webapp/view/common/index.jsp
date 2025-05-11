<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Blood Donation System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .hero {
            background: none; 
            color: #fff;
            padding: 100px 0;
            text-align: center;
            margin-bottom: 50px;
            position: relative;
            overflow: hidden;
        }
        
        .hero-content {
            position: relative;
            z-index: 2;
            text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.8); /* Added text shadow for better readability */
        }
        
        .hero h1 {
            font-size: 3rem;
            margin-bottom: 20px;
        }
        
        .hero p {
            font-size: 1.2rem;
            max-width: 800px;
            margin: 0 auto 30px;
        }
        
        .hero-buttons {
            display: flex;
            justify-content: center;
            gap: 20px;
        }
        
        .hero-image {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            object-fit: cover;
            z-index: 1;
        }
        
        /* Optional dark overlay to ensure text readability */
        .hero::after {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.4); /* Dark overlay instead of gray background */
            z-index: 1;
        }
        
        .features {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 30px;
            margin-bottom: 50px;
        }
        
        .feature-card {
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 30px;
            text-align: center;
        }
        
        .feature-card h3 {
            margin-bottom: 15px;
            color: #e74c3c;
        }
        
        .feature-card p {
            color: #666;
        }
        
        .blood-types {
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 30px;
            margin-bottom: 50px;
        }
        
        .blood-types h2 {
            text-align: center;
            margin-bottom: 30px;
            color: #e74c3c;
        }
        
        .blood-types-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
        }
        
        .blood-type {
            text-align: center;
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 5px;
        }
        
        .blood-type h3 {
            font-size: 2rem;
            margin-bottom: 10px;
            color: #e74c3c;
        }
        
        .blood-type p {
            color: #666;
        }
    </style>
</head>
<body>
    <jsp:include page="header.jsp" />
    
    <section class="hero">
        <!-- Hero image without gray background -->
        <img src="${pageContext.request.contextPath}/assets/images/hero-blood-donation-1.png" alt="Blood Donation" class="hero-image">
        
        <div class="hero-content">
            <h1>Donate Blood, Save Lives</h1>
            <p>Your donation can make a difference. Join our community of donors and help to save lives by donating blood. Every drop counts!</p>
            <div class="hero-buttons">
                <a href="${pageContext.request.contextPath}/auth/register" class="btn btn-primary">Register as Donor or User</a>
                <a href="${pageContext.request.contextPath}/auth/login" class="btn btn-secondary">Login</a>
            </div>
        </div>
    </section>
    
    <section class="container">
        <div class="features">
            <div class="feature-card">
                <h3>Donate Blood</h3>
                <p>Register as a donor and schedule appointments to donate blood. Your donation can save up to three lives!</p>
            </div>
            
            <div class="feature-card">
                <h3>Request Blood</h3>
                <p>Need blood for a medical emergency? Submit a request and we'll connect you with available donors.</p>
            </div>
            
            <div class="feature-card">
                <h3>Join Events</h3>
                <p>Participate in blood donation events organized in your area. Help us create awareness about blood donation.</p>
            </div>
        </div>
        
        <div class="blood-types">
            <h2>Blood Types</h2>
            <div class="blood-types-grid">
                <div class="blood-type">
                    <h3>A+</h3>
                    <p>Can receive from: A+, A-, O+, O-</p>
                    <p>Can donate to: A+, AB+</p>
                </div>
                
                <div class="blood-type">
                    <h3>A-</h3>
                    <p>Can receive from: A-, O-</p>
                    <p>Can donate to: A+, A-, AB+, AB-</p>
                </div>
                
                <div class="blood-type">
                    <h3>B+</h3>
                    <p>Can receive from: B+, B-, O+, O-</p>
                    <p>Can donate to: B+, AB+</p>
                </div>
                
                <div class="blood-type">
                    <h3>B-</h3>
                    <p>Can receive from: B-, O-</p>
                    <p>Can donate to: B+, B-, AB+, AB-</p>
                </div>
                
                <div class="blood-type">
                    <h3>AB+</h3>
                    <p>Can receive from: All blood types</p>
                    <p>Can donate to: AB+</p>
                </div>
                
                <div class="blood-type">
                    <h3>AB-</h3>
                    <p>Can receive from: A-, B-, AB-, O-</p>
                    <p>Can donate to: AB+, AB-</p>
                </div>
                
                <div class="blood-type">
                    <h3>O+</h3>
                    <p>Can receive from: O+, O-</p>
                    <p>Can donate to: A+, B+, AB+, O+</p>
                </div>
                
                <div class="blood-type">
                    <h3>O-</h3>
                    <p>Can receive from: O-</p>
                    <p>Can donate to: All blood types</p>
                </div>
            </div>
        </div>
    </section>
    
    <jsp:include page="footer.jsp" />
</body>
</html>
