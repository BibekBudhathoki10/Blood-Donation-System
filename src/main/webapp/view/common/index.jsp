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
            height: 600px; /* Fixed height for better image display */
        }
        
        .hero-content {
            position: relative;
            z-index: 3;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.8); /* Enhanced text shadow for better readability */
            padding: 0 20px;
        }
        
        .hero h1 {
            font-size: 3.5rem;
            margin-bottom: 20px;
            font-weight: 700;
        }
        
        .hero p {
            font-size: 1.3rem;
            max-width: 800px;
            margin: 0 auto 30px;
            line-height: 1.6;
        }
        
        .hero-buttons {
            display: flex;
            justify-content: center;
            gap: 20px;
            flex-wrap: wrap;
        }
        
        .hero-buttons .btn {
            padding: 12px 30px;
            font-size: 1.1rem;
            font-weight: 600;
            border-radius: 50px;
            transition: all 0.3s ease;
        }
        
        .hero-buttons .btn:hover {
            transform: translateY(-3px);
            box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
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
        
        /* Dark overlay to ensure text readability */
        .hero::after {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5); /* Slightly darker overlay for better contrast */
            z-index: 2;
        }
        
        /* Image selector styles */
        .image-selector {
            position: absolute;
            bottom: 20px;
            left: 50%;
            transform: translateX(-50%);
            display: flex;
            gap: 10px;
            z-index: 4;
        }
        
        .image-selector button {
            width: 12px;
            height: 12px;
            border-radius: 50%;
            border: 2px solid white;
            background: transparent;
            cursor: pointer;
            padding: 0;
            transition: all 0.3s ease;
        }
        
        .image-selector button.active {
            background: white;
            transform: scale(1.2);
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
        
        /* Responsive adjustments */
        @media (max-width: 768px) {
            .hero {
                padding: 80px 0;
                height: 500px;
            }
            
            .hero h1 {
                font-size: 2.5rem;
            }
            
            .hero p {
                font-size: 1.1rem;
            }
        }

        /* Contact Section Styles */
        .contact-section {
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 30px;
            margin-bottom: 50px;
        }
        
        .contact-section h2 {
            text-align: center;
            margin-bottom: 20px;
            color: #e74c3c;
        }
        
        .contact-intro {
            text-align: center;
            max-width: 800px;
            margin: 0 auto 30px;
            color: #666;
        }
        
        .contact-container {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 30px;
        }
        
        .contact-info {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }
        
        .contact-item {
            padding: 15px;
            border: 1px solid #eee;
            border-radius: 5px;
        }
        
        .contact-item h3 {
            display: flex;
            align-items: center;
            margin-bottom: 10px;
            color: #e74c3c;
            font-size: 1.1rem;
        }
        
        .contact-icon {
            margin-right: 10px;
            font-style: normal;
        }
        
        .contact-item p {
            margin: 5px 0;
            color: #666;
        }
        
        .contact-form-container {
            padding: 20px;
            border: 1px solid #eee;
            border-radius: 5px;
            background-color: #f9f9f9;
        }
        
        .contact-form .form-group {
            margin-bottom: 15px;
        }
        
        .contact-form label {
            display: block;
            margin-bottom: 5px;
            font-weight: 600;
            color: #333;
        }
        
        .contact-form input,
        .contact-form textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-family: inherit;
        }
        
        .contact-form textarea {
            resize: vertical;
        }
        
        .contact-form button {
            width: 100%;
            padding: 12px;
            margin-top: 10px;
        }
        
        /* Responsive adjustments for contact section */
        @media (max-width: 992px) {
            .contact-container {
                grid-template-columns: 1fr;
            }
        }
        
        @media (max-width: 768px) {
            .contact-info {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <jsp:include page="header.jsp" />
    
    <section class="hero">
        <!-- Hero image -->
        <img src="${pageContext.request.contextPath}/assets/images/hero-donation-3.jpg" alt="Blood Donation" class="hero-image" id="heroImage">
        
        <div class="hero-content">
            <h1>Donate Blood, Save Lives</h1>
            <p>Your donation can make a difference. Join our community of donors and help save lives by donating blood. Every drop counts!</p>
            <div class="hero-buttons">
                <a href="${pageContext.request.contextPath}/auth/register" class="btn btn-primary">Register as Donor or User</a>
                <a href="${pageContext.request.contextPath}/auth/login" class="btn btn-secondary">Login</a>
            </div>
        </div>
        
        <!-- Image selector dots -->
        <div class="image-selector">
            <button class="active" data-image="hero-donation-1.jpg"></button>
            <button data-image="hero-donation-3.jpg"></button>
            <button data-image="hero-donation-3.jpg"></button>
            <button data-image="hero-donation-3.jpg"></button>
            <button data-image="hero-donation-3.jpg"></button>
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

    <section class="container" id="contact-us">
        <div class="contact-section">
            <h2>Contact Us</h2>
            <p class="contact-intro">Have questions about blood donation or need assistance? Reach out to us!</p>
            
            <div class="contact-container">
                <div class="contact-info">
                    <div class="contact-item">
                        <h3><i class="contact-icon">📍</i> Our Location</h3>
                        <p>123 Blood Donor Venue</p>
                        <p>Jhapa District, Damak City </p>
                    </div>
                    
                    <div class="contact-item">
                        <h3><i class="contact-icon">📞</i> Phone Numbers</h3>
                        <p>Main Office: +977 9812345678</p>
                        <p>Emergency: +977  9712345678</p>
                    </div>
                    
                    <div class="contact-item">
                        <h3><i class="contact-icon">✉️</i> Email Addresses</h3>
                        <p>General Inquiries: info@blooddonation.org</p>
                        <p>Support: support@blooddonation.org</p>
                    </div>
                    
                    <div class="contact-item">
                        <h3><i class="contact-icon">⏰</i> Operating Hours</h3>
                        <p>Monday - Friday: 8:00 AM - 6:00 PM</p>
                        <p>Saturday: 9:00 AM - 2:00 PM</p>
                        <p>Sunday: Closed</p>
                    </div>
                </div>
                
                <div class="contact-form-container">
                    <form id="contactForm" class="contact-form">
                        <div class="form-group">
                            <label for="name">Your Name</label>
                            <input type="text" id="name" name="name" required>
                        </div>
                        
                        <div class="form-group">
                            <label for="email">Your Email</label>
                            <input type="email" id="email" name="email" required>
                        </div>
                        
                        <div class="form-group">
                            <label for="subject">Subject</label>
                            <input type="text" id="subject" name="subject" required>
                        </div>
                        
                        <div class="form-group">
                            <label for="message">Message</label>
                            <textarea id="message" name="message" rows="5" required></textarea>
                        </div>
                        
                        <button type="submit" class="btn btn-primary">Send Message</button>
                    </form>
                </div>
            </div>
        </div>
    </section>
    
    <jsp:include page="footer.jsp" />
    
    <script>
        // Simple image carousel for hero section
        document.addEventListener('DOMContentLoaded', function() {
            const heroImage = document.getElementById('heroImage');
            const buttons = document.querySelectorAll('.image-selector button');
            const basePath = '${pageContext.request.contextPath}/assets/images/';
            
            buttons.forEach(button => {
                button.addEventListener('click', function() {
                    // Update image
                    const imageName = this.getAttribute('data-image');
                    heroImage.src = basePath + imageName;
                    
                    // Update active button
                    buttons.forEach(btn => btn.classList.remove('active'));
                    this.classList.add('active');
                });
            });
            
            // Optional: Auto-rotate images
            let currentIndex = 0;
            setInterval(() => {
                currentIndex = (currentIndex + 1) % buttons.length;
                buttons[currentIndex].click();
            }, 5000); // Change image every 5 seconds
        });

            // Contact form validation and submission
            const contactForm = document.getElementById('contactForm');
            if (contactForm) {
                contactForm.addEventListener('submit', function(e) {
                    e.preventDefault();
                    
                    // Get form values
                    const name = document.getElementById('name').value;
                    const email = document.getElementById('email').value;
                    const subject = document.getElementById('subject').value;
                    const message = document.getElementById('message').value;
                    
                    // Simple validation
                    if (!name || !email || !subject || !message) {
                        alert('Please fill in all fields');
                        return;
                    }
                    
                    // Email validation
                    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                    if (!emailRegex.test(email)) {
                        alert('Please enter a valid email address');
                        return;
                    }
                    
                    // In a real application, you would send this data to the server
                    // For now, we'll just show a success message
                    alert('Thank you for your message! We will get back to you soon.');
                    contactForm.reset();
                });
            }
    </script>
</body>
</html>
