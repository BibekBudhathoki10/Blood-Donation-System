package controller;

import model.*;
import util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/user/*")
public class GeneralUserController extends HttpServlet {
    private UserDAO userDAO;
    private BloodRequestDAO bloodRequestDAO;
    private AppointmentDAO appointmentDAO;
    private DonorDAO donorDAO;
    private DonationEventDAO donationEventDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        bloodRequestDAO = new BloodRequestDAO();
        appointmentDAO = new AppointmentDAO();
        donorDAO = new DonorDAO();
        donationEventDAO = new DonationEventDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/user/dashboard");
            return;
        }
        
        switch (pathInfo) {
            case "/dashboard":
                showDashboard(request, response);
                break;
            case "/profile":
                showProfile(request, response);
                break;
            case "/request-blood":
                showRequestBlood(request, response);
                break;
            case "/my-requests":
                showMyRequests(request, response);
                break;
            case "/edit-request":
                showEditRequest(request, response);
                break;
            case "/scheduled-donations":
                showScheduledDonations(request, response);
                break;
            case "/view-appointment":
                viewAppointment(request, response);
                break;
            case "/events":
                showEvents(request, response);
                break;
            case "/events/view":
                viewEvent(request, response);
                break;
            case "/view-request-appointments":
                viewRequestAppointments(request, response);
                break;
            case "/schedule-appointment":
                showScheduleAppointment(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        switch (pathInfo) {
            case "/profile":
                updateProfile(request, response);
                break;
            case "/request-blood":
                requestBlood(request, response);
                break;
            case "/edit-request":
                updateRequest(request, response);
                break;
            case "/cancel-request":
                cancelRequest(request, response);
                break;
            case "/cancel-appointment":
                cancelAppointment(request, response);
                break;
            case "/reschedule-appointment":
                rescheduleAppointment(request, response);
                break;
            case "/events/register":
                registerForEvent(request, response);
                break;
            case "/schedule-appointment":
                scheduleAppointment(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    private void showScheduleAppointment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get blood request ID if available
        String bloodRequestIdStr = request.getParameter("requestId");
        
        if (bloodRequestIdStr != null && ValidationUtil.isPositiveNumeric(bloodRequestIdStr)) {
            int bloodRequestId = Integer.parseInt(bloodRequestIdStr);
            BloodRequest bloodRequest = bloodRequestDAO.getBloodRequestById(bloodRequestId);
            
            if (bloodRequest != null) {
                request.setAttribute("bloodRequest", bloodRequest);
            }
        }
        
        // Forward to the schedule appointment form
        request.getRequestDispatcher("/view/user/schedule-appointment.jsp").forward(request, response);
    }

    private void scheduleAppointment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");
        
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        try {
            System.out.println("Starting appointment scheduling process...");
            
            // Get form parameters
            String appointmentDateStr = request.getParameter("appointmentDate");
            String appointmentTimeStr = request.getParameter("appointmentTime");
            String notes = request.getParameter("notes");
            String bloodRequestIdStr = request.getParameter("bloodRequestId");
            
            System.out.println("Form data received - Date: " + appointmentDateStr + ", Time: " + appointmentTimeStr);
            
            // Validate required fields
            if (!ValidationUtil.isNotEmpty(appointmentDateStr) || !ValidationUtil.isNotEmpty(appointmentTimeStr)) {
                request.setAttribute("error", "Date and time are required");
                request.getRequestDispatcher("/view/user/schedule-appointment.jsp").forward(request, response);
                return;
            }
            
            // Parse date
            Date appointmentDate;
            try {
                appointmentDate = Date.valueOf(appointmentDateStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid date format: " + appointmentDateStr);
                request.setAttribute("error", "Invalid date format. Please use YYYY-MM-DD format.");
                request.getRequestDispatcher("/view/user/schedule-appointment.jsp").forward(request, response);
                return;
            }
            
            // Parse time - ensure it's in the correct format (HH:MM:SS)
            Time appointmentTime;
            try {
                // Check if the time string already has seconds
                if (!appointmentTimeStr.matches("\\d{2}:\\d{2}:\\d{2}")) {
                    // If not, add ":00" for seconds
                    if (appointmentTimeStr.matches("\\d{2}:\\d{2}")) {
                        appointmentTimeStr += ":00";
                    } else {
                        throw new IllegalArgumentException("Time format must be HH:MM or HH:MM:SS");
                    }
                }
                appointmentTime = Time.valueOf(appointmentTimeStr);
                System.out.println("Parsed time: " + appointmentTime);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid time format: " + appointmentTimeStr + " - " + e.getMessage());
                request.setAttribute("error", "Invalid time format. Please use HH:MM format.");
                request.getRequestDispatcher("/view/user/schedule-appointment.jsp").forward(request, response);
                return;
            }
            
            // Create appointment object
            Appointment appointment = new Appointment();
            
            // Get donor information
            Donor donor = donorDAO.getDonorByUserId(userId);
            if (donor == null) {
                System.out.println("No donor found for user ID: " + userId);
                // Create a new donor record if one doesn't exist
                donor = new Donor();
                donor.setUserId(userId);
                donor.setAvailable(true);
                
                // Get user information to set donor details
                User user = userDAO.getUserById(userId);
                if (user != null) {
                    // Set default values for required fields
                    donor.setBloodGroup("Unknown"); // This should be updated by the user later
                    donor.setLocation(user.getAddress());
                    donor.setMedicalHistory("");
                    donor.setDonationCount(0);
                }
                
                boolean donorAdded = donorDAO.addDonor(donor);
                if (!donorAdded) {
                    request.setAttribute("error", "Failed to create donor profile. Please complete your donor profile first.");
                    request.getRequestDispatcher("/view/user/schedule-appointment.jsp").forward(request, response);
                    return;
                }
                
                System.out.println("Created new donor with ID: " + donor.getId());
            }
            
            // Set donor ID
            appointment.setDonorId(donor.getId());
            System.out.println("Setting donor ID: " + donor.getId());
            
            // Set appointment details
            appointment.setAppointmentDate(appointmentDate);
            appointment.setAppointmentTime(appointmentTime);
            appointment.setStatus("scheduled");
            appointment.setNotes(notes);
            
            // Set blood request ID if available
            if (ValidationUtil.isPositiveNumeric(bloodRequestIdStr)) {
                int bloodRequestId = Integer.parseInt(bloodRequestIdStr);
                BloodRequest bloodRequest = bloodRequestDAO.getBloodRequestById(bloodRequestId);
                
                if (bloodRequest != null) {
                    appointment.setBloodRequestId(bloodRequestId);
                    System.out.println("Setting blood request ID: " + bloodRequestId);
                    
                    // Update blood request status if it's pending
                    if ("pending".equals(bloodRequest.getStatus())) {
                        bloodRequestDAO.updateBloodRequestStatus(bloodRequestId, "in-progress");
                    }
                }
            }
            
            // Check if appointments table exists
            if (!appointmentDAO.checkTableExists()) {
                System.out.println("Appointments table does not exist!");
                request.setAttribute("error", "Database error: Appointments table does not exist. Please contact the administrator.");
                request.getRequestDispatcher("/view/user/schedule-appointment.jsp").forward(request, response);
                return;
            }
            
            // Save appointment with detailed error tracking
            StringBuilder errorDetails = new StringBuilder();
            System.out.println("Attempting to save appointment...");
            boolean success = appointmentDAO.addAppointment(appointment, errorDetails);

            if (success) {
                System.out.println("Appointment saved successfully with ID: " + appointment.getId());
                
                // Update donor availability
                donorDAO.updateDonorAvailability(donor.getId(), false);
                
                // Redirect based on user role
                if ("donor".equals(userRole)) {
                    response.sendRedirect(request.getContextPath() + "/donor/dashboard?success=Appointment scheduled successfully");
                } else {
                    response.sendRedirect(request.getContextPath() + "/user/dashboard?success=Appointment scheduled successfully");
                }
            } else {
                System.out.println("Failed to save appointment. Error details: " + errorDetails.toString());
                request.setAttribute("error", "Failed to schedule appointment. Please try again later.");
                request.setAttribute("errorDetails", errorDetails.toString());
                request.getRequestDispatcher("/view/user/schedule-appointment.jsp").forward(request, response);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date/time format: " + e.getMessage());
            request.setAttribute("error", "Invalid date or time format: " + e.getMessage());
            request.getRequestDispatcher("/view/user/schedule-appointment.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error scheduling appointment: " + e.getMessage());
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/view/user/schedule-appointment.jsp").forward(request, response);
        }
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            int userId = (int) session.getAttribute("userId");
        
            // Get user information
            User user = userDAO.getUserById(userId);
        
            if (user == null) {
                session.invalidate();
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }
        
            // Get blood requests
            List<BloodRequest> bloodRequests = bloodRequestDAO.getBloodRequestsByUserId(userId);
            int pendingRequests = 0;
            int approvedRequests = 0;
            int completedRequests = 0;
        
            if (bloodRequests != null) {
                for (BloodRequest req : bloodRequests) {
                    switch (req.getStatus()) {
                        case "pending":
                            pendingRequests++;
                            break;
                        case "approved":
                        case "in-progress":
                            approvedRequests++;
                            break;
                        case "completed":
                            completedRequests++;
                            break;
                    }
                }
            }
            // Get upcoming events
            List<DonationEvent> upcomingEvents = donationEventDAO.getUpcomingDonationEvents();
            if (upcomingEvents == null) {
                upcomingEvents = new ArrayList<>();
            }
        
            // For scheduled donations, we'll just count all scheduled appointments
            // since we can't filter by blood request ID
            int scheduledDonations = 0;
            List<Appointment> allAppointments = appointmentDAO.getAppointmentsByStatus("scheduled");
            if (allAppointments != null) {
                scheduledDonations = allAppointments.size();
            }
        
            // Set attributes for the dashboard
            request.setAttribute("user", user);
            request.setAttribute("pendingRequests", pendingRequests);
            request.setAttribute("approvedRequests", approvedRequests);
            request.setAttribute("completedRequests", completedRequests);
            request.setAttribute("scheduledDonations", scheduledDonations);
            request.setAttribute("upcomingEvents", upcomingEvents);
        
            // Forward to the dashboard page
            request.getRequestDispatcher("/view/user/dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred while loading the dashboard. Please try again later.");
            request.getRequestDispatcher("/view/user/dashboard.jsp").forward(request, response);
        }
    }
    
    // Enhanced method to show user's blood requests with appointment details
    private void showMyRequests(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            int userId = (int) session.getAttribute("userId");
            
            System.out.println("Fetching blood requests for user ID: " + userId);
            
            // Get blood requests for this user
            List<BloodRequest> requests = bloodRequestDAO.getBloodRequestsByUserId(userId);
            System.out.println("Found " + (requests != null ? requests.size() : 0) + " blood requests for user ID: " + userId);
            
            // Create a map to store appointments for each blood request
            Map<Integer, List<Appointment>> requestAppointmentsMap = new HashMap<>();
            Map<Integer, User> donorUserMap = new HashMap<>();
            
            // For each blood request, get related appointments
            if (requests != null && !requests.isEmpty()) {
                for (BloodRequest bloodRequest : requests) {
                    if (bloodRequest != null) {
                        try {
                            System.out.println("Getting appointments for blood request ID: " + bloodRequest.getId());
                            
                            // Get all appointments
                            List<Appointment> allAppointments = appointmentDAO.getAllAppointments();
                            List<Appointment> matchingAppointments = new ArrayList<>();
                            
                            if (allAppointments != null) {
                                for (Appointment app : allAppointments) {
                                    // Check if this appointment is for the current blood request
                                    // Either by direct blood request ID or by notes containing the ID
                                    if (app != null && 
                                        ((app.getBloodRequestId() != null && app.getBloodRequestId() == bloodRequest.getId()) ||
                                         (app.getNotes() != null && app.getNotes().contains("[BloodRequestID:" + bloodRequest.getId() + "]")))) {
                                        
                                        // Ensure the blood request ID is set
                                        app.setBloodRequestId(bloodRequest.getId());
                                        matchingAppointments.add(app);
                                        System.out.println("Found matching appointment ID: " + app.getId() + " for blood request ID: " + bloodRequest.getId());
                                    }
                                }
                            }
                            
                            if (!matchingAppointments.isEmpty()) {
                                System.out.println("Found " + matchingAppointments.size() + " appointments for blood request ID: " + bloodRequest.getId());
                                requestAppointmentsMap.put(bloodRequest.getId(), matchingAppointments);
                                
                                // Get donor user information for each appointment
                                for (Appointment appointment : matchingAppointments) {
                                    if (appointment != null) {
                                        int donorId = appointment.getDonorId();
                                        System.out.println("Getting donor user info for donor ID: " + donorId);
                                        
                                        if (!donorUserMap.containsKey(donorId)) {
                                            Donor donor = donorDAO.getDonorById(donorId);
                                            if (donor != null) {
                                                User donorUser = userDAO.getUserById(donor.getUserId());
                                                if (donorUser != null) {
                                                    donorUserMap.put(donorId, donorUser);
                                                    System.out.println("Added donor user to map: " + donorUser.getName());
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                System.out.println("No appointments found for blood request ID: " + bloodRequest.getId());
                                // Initialize with empty list to avoid null pointer exceptions
                                requestAppointmentsMap.put(bloodRequest.getId(), new ArrayList<>());
                            }
                        } catch (Exception e) {
                            // Log the error but continue processing other requests
                            e.printStackTrace();
                            System.out.println("Error getting appointments for blood request ID: " + bloodRequest.getId() + " - " + e.getMessage());
                            // Initialize with empty list to avoid null pointer exceptions
                            requestAppointmentsMap.put(bloodRequest.getId(), new ArrayList<>());
                        }
                    }
                }
            }
            
            // Check for success message
            String success = request.getParameter("success");
            if ("true".equals(success)) {
                request.setAttribute("success", "Blood request created successfully");
            }
            
            System.out.println("Request Appointments Map size: " + requestAppointmentsMap.size());
            for (Map.Entry<Integer, List<Appointment>> entry : requestAppointmentsMap.entrySet()) {
                System.out.println("Blood Request ID: " + entry.getKey() + ", Number of appointments: " + entry.getValue().size());
            }
            
            request.setAttribute("requests", requests);
            request.setAttribute("requestAppointmentsMap", requestAppointmentsMap);
            request.setAttribute("donorUserMap", donorUserMap);
            
            // Forward to the my requests page
            request.getRequestDispatcher("/view/user/my-requests.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred while retrieving your blood requests: " + e.getMessage());
            request.getRequestDispatcher("/view/user/my-requests.jsp").forward(request, response);
        }
    }
    
    private void showProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");
        
        // Get user information
        User user = userDAO.getUserById(userId);
        
        // Set attributes for the profile page
        request.setAttribute("user", user);
        
        // Forward to the profile page
        request.getRequestDispatcher("/view/user/profile.jsp").forward(request, response);
    }
    
    private void updateProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");
        
        // Get form data
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Validate input
        boolean hasError = false;
        
        if (!ValidationUtil.isNotEmpty(name)) {
            request.setAttribute("nameError", "Name is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isValidEmail(email)) {
            request.setAttribute("emailError", "Invalid email format");
            hasError = true;
        } else {
            // Check if email is already in use by another user
            User existingUser = userDAO.getUserByEmail(email);
            if (existingUser != null && existingUser.getId() != userId) {
                request.setAttribute("emailError", "Email already in use");
                hasError = true;
            }
        }
        
        if (!ValidationUtil.isValidPhone(phone)) {
            request.setAttribute("phoneError", "Invalid phone number");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(address)) {
            request.setAttribute("addressError", "Address is required");
            hasError = true;
        }
        
        // Check if password change is requested
        boolean changePassword = ValidationUtil.isNotEmpty(currentPassword) && ValidationUtil.isNotEmpty(newPassword);
        
        if (changePassword) {
            // Get current user to verify password
            User user = userDAO.getUserById(userId);
            
            if (!user.getPassword().equals(currentPassword)) {
                request.setAttribute("currentPasswordError", "Current password is incorrect");
                hasError = true;
            }
            
            if (!ValidationUtil.isValidPassword(newPassword)) {
                request.setAttribute("newPasswordError", "Password must be at least 8 characters and contain letters and numbers");
                hasError = true;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                request.setAttribute("confirmPasswordError", "Passwords do not match");
                hasError = true;
            }
        }
        
        if (hasError) {
            // Get user information
            User user = userDAO.getUserById(userId);
            
            // Set attributes for the profile page
            request.setAttribute("user", user);
            // Preserve form data
            request.setAttribute("name", name);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.setAttribute("address", address);
            
            // Forward back to the profile page with errors
            request.getRequestDispatcher("/view/user/profile.jsp").forward(request, response);
            return;
        }
        
        // Update user information
        User user = userDAO.getUserById(userId);
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);
        
        if (changePassword) {
            user.setPassword(newPassword);
        }
        
        boolean userUpdated = userDAO.updateUser(user);
        
        if (userUpdated) {
            request.setAttribute("success", "Profile updated successfully");
        } else {
            request.setAttribute("error", "Failed to update profile");
        }
        
        // Get updated user information
        user = userDAO.getUserById(userId);
        
        // Set attributes for the profile page
        request.setAttribute("user", user);
        
        // Forward to the profile page
        request.getRequestDispatcher("/view/user/profile.jsp").forward(request, response);
    }
    
    private void showRequestBlood(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Forward to the request blood page
        request.getRequestDispatcher("/view/user/request-blood.jsp").forward(request, response);
    }
    
    private void requestBlood(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");
        
        // Get form data
        String patientName = request.getParameter("patientName");
        String bloodGroup = request.getParameter("bloodGroup");
        String quantityStr = request.getParameter("quantity");
        String urgency = request.getParameter("urgency");
        String hospitalName = request.getParameter("hospitalName");
        String hospitalAddress = request.getParameter("hospitalAddress");
        String contactPerson = request.getParameter("contactPerson");
        String contactPhone = request.getParameter("contactPhone");
        String reason = request.getParameter("reason");
        String requiredDateStr = request.getParameter("requiredDate");
        
        // Validate input
        boolean hasError = false;
        
        if (!ValidationUtil.isNotEmpty(patientName)) {
            request.setAttribute("patientNameError", "Patient name is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isValidBloodGroup(bloodGroup)) {
            request.setAttribute("bloodGroupError", "Invalid blood group");
            hasError = true;
        }
        
        int quantity = 0;
        if (!ValidationUtil.isPositiveNumeric(quantityStr)) {
            request.setAttribute("quantityError", "Quantity must be a positive number");
            hasError = true;
        } else {
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                request.setAttribute("quantityError", "Quantity must be greater than 0");
                hasError = true;
            }
        }
        
        if (!ValidationUtil.isNotEmpty(urgency)) {
            request.setAttribute("urgencyError", "Urgency is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(hospitalName)) {
            request.setAttribute("hospitalNameError", "Hospital name is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(hospitalAddress)) {
            request.setAttribute("hospitalAddressError", "Hospital address is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(contactPerson)) {
            request.setAttribute("contactPersonError", "Contact person is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(contactPhone)) {
            request.setAttribute("contactPhoneError", "Contact phone is required");
            hasError = true;
        }
        
        Date requiredDate = null;
        if (!ValidationUtil.isNotEmpty(requiredDateStr)) {
            request.setAttribute("requiredDateError", "Required date is required");
            hasError = true;
        } else {
            try {
                requiredDate = Date.valueOf(requiredDateStr);
                
                // Check if required date is in the future
                Date today = new Date(System.currentTimeMillis());
                if (requiredDate.before(today)) {
                    request.setAttribute("requiredDateError", "Required date must be in the future");
                    hasError = true;
                }
            } catch (IllegalArgumentException e) {
                request.setAttribute("requiredDateError", "Invalid date format");
                hasError = true;
            }
        }
        if (hasError) {
            // Preserve form data
            request.setAttribute("patientName", patientName);
            request.setAttribute("bloodGroup", bloodGroup);
            request.setAttribute("quantity", quantityStr);
            request.setAttribute("urgency", urgency);
            request.setAttribute("hospitalName", hospitalName);
            request.setAttribute("hospitalAddress", hospitalAddress);
            request.setAttribute("contactPerson", contactPerson);
            request.setAttribute("contactPhone", contactPhone);
            request.setAttribute("reason", reason);
            request.setAttribute("requiredDate", requiredDateStr);
            
            // Forward back to the request blood page with errors
            request.getRequestDispatcher("/view/user/request-blood.jsp").forward(request, response);
            return;
        }
        
        // Create blood request
        BloodRequest bloodRequest = new BloodRequest();
        bloodRequest.setUserId(userId);
        bloodRequest.setPatientName(patientName);
        bloodRequest.setBloodGroup(bloodGroup);
        bloodRequest.setQuantity(quantity);
        bloodRequest.setUrgency(urgency);
        bloodRequest.setHospitalName(hospitalName);
        bloodRequest.setHospitalAddress(hospitalAddress);
        bloodRequest.setContactPerson(contactPerson);
        bloodRequest.setContactPhone(contactPhone);
        bloodRequest.setReason(reason);
        bloodRequest.setRequiredDate(requiredDate);
        bloodRequest.setStatus("pending");
        bloodRequest.setRequestDate(new Timestamp(System.currentTimeMillis()));
        
        boolean requestCreated = bloodRequestDAO.addBloodRequest(bloodRequest);
        
        if (requestCreated) {
            // Redirect to my requests page with success message
            response.sendRedirect(request.getContextPath() + "/user/my-requests?success=true");
        } else {
            request.setAttribute("error", "Failed to create blood request");
            request.getRequestDispatcher("/view/user/request-blood.jsp").forward(request, response);
        }
    }
    
    private void showEditRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestIdStr = request.getParameter("id");
        
        if (!ValidationUtil.isPositiveNumeric(requestIdStr)) {
            response.sendRedirect(request.getContextPath() + "/user/my-requests");
            return;
        }
        
        int requestId = Integer.parseInt(requestIdStr);
        
        // Get blood request
        BloodRequest bloodRequest = bloodRequestDAO.getBloodRequestById(requestId);
        
        if (bloodRequest == null) {
            response.sendRedirect(request.getContextPath() + "/user/my-requests");
            return;
        }
        
        // Check if the blood request belongs to the user
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");
        
        if (bloodRequest.getUserId() != userId) {
            response.sendRedirect(request.getContextPath() + "/user/my-requests");
            return;
        }
        
        // Check if the blood request can be edited (only pending requests can be edited)
        if (!"pending".equals(bloodRequest.getStatus())) {
            request.setAttribute("error", "Only pending requests can be edited");
            showMyRequests(request, response);
            return;
        }
        
        request.setAttribute("bloodRequest", bloodRequest);
        
        // Forward to the edit request page
        request.getRequestDispatcher("/view/user/edit-request.jsp").forward(request, response);
    }
    
    private void updateRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestIdStr = request.getParameter("id");
        
        if (!ValidationUtil.isPositiveNumeric(requestIdStr)) {
            response.sendRedirect(request.getContextPath() + "/user/my-requests");
            return;
        }
        
        int requestId = Integer.parseInt(requestIdStr);
        
        // Get blood request
        BloodRequest bloodRequest = bloodRequestDAO.getBloodRequestById(requestId);
        
        if (bloodRequest == null) {
            response.sendRedirect(request.getContextPath() + "/user/my-requests");
            return;
        }
        
        // Check if the blood request belongs to the user
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");
        
        if (bloodRequest.getUserId() != userId) {
            response.sendRedirect(request.getContextPath() + "/user/my-requests");
            return;
        }
        
        // Check if the blood request can be edited (only pending requests can be edited)
        if (!"pending".equals(bloodRequest.getStatus())) {
            request.setAttribute("error", "Only pending requests can be edited");
            showMyRequests(request, response);
            return;
        }
        
        // Get form data
        String patientName = request.getParameter("patientName");
        String bloodGroup = request.getParameter("bloodGroup");
        String quantityStr = request.getParameter("quantity");
        String urgency = request.getParameter("urgency");
        String hospitalName = request.getParameter("hospitalName");
        String hospitalAddress = request.getParameter("hospitalAddress");
        String contactPerson = request.getParameter("contactPerson");
        String contactPhone = request.getParameter("contactPhone");
        String reason = request.getParameter("reason");
        String requiredDateStr = request.getParameter("requiredDate");
        
        // Validate input
        boolean hasError = false;
        
        if (!ValidationUtil.isNotEmpty(patientName)) {
            request.setAttribute("patientNameError", "Patient name is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isValidBloodGroup(bloodGroup)) {
            request.setAttribute("bloodGroupError", "Invalid blood group");
            hasError = true;
        }
        
        int quantity = 0;
        if (!ValidationUtil.isPositiveNumeric(quantityStr)) {
            request.setAttribute("quantityError", "Quantity must be a positive number");
            hasError = true;
        } else {
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                request.setAttribute("quantityError", "Quantity must be greater than 0");
                hasError = true;
            }
        }
        
        if (!ValidationUtil.isNotEmpty(urgency)) {
            request.setAttribute("urgencyError", "Urgency is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(hospitalName)) {
            request.setAttribute("hospitalNameError", "Hospital name is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(hospitalAddress)) {
            request.setAttribute("hospitalAddressError", "Hospital address is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(contactPerson)) {
            request.setAttribute("contactPersonError", "Contact person is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(contactPhone)) {
            request.setAttribute("contactPhoneError", "Contact phone is required");
            hasError = true;
        }
        
        Date requiredDate = null;
        if (!ValidationUtil.isNotEmpty(requiredDateStr)) {
            request.setAttribute("requiredDateError", "Required date is required");
            hasError = true;
        } else {
            try {
                requiredDate = Date.valueOf(requiredDateStr);
                
                // Check if required date is in the future
                Date today = new Date(System.currentTimeMillis());
                if (requiredDate.before(today)) {
                    request.setAttribute("requiredDateError", "Required date must be in the future");
                    hasError = true;
                }
            } catch (IllegalArgumentException e) {
                request.setAttribute("requiredDateError", "Invalid date format");
                hasError = true;
            }
        }
        
        if (hasError) {
            // Preserve form data
            request.setAttribute("bloodRequest", bloodRequest);
            request.setAttribute("patientName", patientName);
            request.setAttribute("bloodGroup", bloodGroup);
            request.setAttribute("quantity", quantityStr);
            request.setAttribute("urgency", urgency);
            request.setAttribute("hospitalName", hospitalName);
            request.setAttribute("hospitalAddress", hospitalAddress);
            request.setAttribute("contactPerson", contactPerson);
            request.setAttribute("contactPhone", contactPhone);
            request.setAttribute("reason", reason);
            request.setAttribute("requiredDate", requiredDateStr);
            
            // Forward back to the edit request page with errors
            request.getRequestDispatcher("/view/user/edit-request.jsp").forward(request, response);
            return;
        }
        
        // Update blood request
        bloodRequest.setPatientName(patientName);
        bloodRequest.setBloodGroup(bloodGroup);
        bloodRequest.setQuantity(quantity);
        bloodRequest.setUrgency(urgency);
        bloodRequest.setHospitalName(hospitalName);
        bloodRequest.setHospitalAddress(hospitalAddress);
        bloodRequest.setContactPerson(contactPerson);
        bloodRequest.setContactPhone(contactPhone);
        bloodRequest.setReason(reason);
        bloodRequest.setRequiredDate(requiredDate);
        
        boolean requestUpdated = bloodRequestDAO.updateBloodRequest(bloodRequest);
        
        if (requestUpdated) {
            // Redirect to my requests page with success message
            response.sendRedirect(request.getContextPath() + "/user/my-requests?success=true");
        } else {
            request.setAttribute("error", "Failed to update blood request");
            request.setAttribute("bloodRequest", bloodRequest);
            request.getRequestDispatcher("/view/user/edit-request.jsp").forward(request, response);
        }
    }
    
    private void cancelRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestIdStr = request.getParameter("id");
        
        if (!ValidationUtil.isPositiveNumeric(requestIdStr)) {
            response.sendRedirect(request.getContextPath() + "/user/my-requests");
            return;
        }
        
        int requestId = Integer.parseInt(requestIdStr);
        
        // Get blood request
        BloodRequest bloodRequest = bloodRequestDAO.getBloodRequestById(requestId);
        
        if (bloodRequest == null) {
            response.sendRedirect(request.getContextPath() + "/user/my-requests");
            return;
        }
        
        // Check if the blood request belongs to the user
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");
        
        if (bloodRequest.getUserId() != userId) {
            response.sendRedirect(request.getContextPath() + "/user/my-requests");
            return;
        }
        
        // Check if the blood request can be cancelled (only pending and approved requests can be cancelled)
        if (!("pending".equals(bloodRequest.getStatus()) || "approved".equals(bloodRequest.getStatus()))) {
            request.setAttribute("error", "Only pending and approved requests can be cancelled");
            showMyRequests(request, response);
            return;
        }
        
        // Cancel blood request
        bloodRequest.setStatus("cancelled");
        boolean requestCancelled = bloodRequestDAO.updateBloodRequest(bloodRequest);
        
        if (requestCancelled) {
            request.setAttribute("success", "Blood request cancelled successfully");
        } else {
            request.setAttribute("error", "Failed to cancel blood request");
        }
        
        showMyRequests(request, response);
    }
    
    private void showEvents(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get upcoming events
        List<DonationEvent> upcomingEvents = donationEventDAO.getUpcomingDonationEvents();
        
        request.setAttribute("events", upcomingEvents);
        
        // Forward to the events page
        request.getRequestDispatcher("/view/user/events/index.jsp").forward(request, response);
    }
    
    private void viewEvent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String eventIdStr = request.getParameter("id");
        
        if (!ValidationUtil.isPositiveNumeric(eventIdStr)) {
            response.sendRedirect(request.getContextPath() + "/user/events");
            return;
        }
        
        int eventId = Integer.parseInt(eventIdStr);
        
        // Get event
        DonationEvent event = donationEventDAO.getDonationEventById(eventId);
        
        if (event == null) {
            response.sendRedirect(request.getContextPath() + "/user/events");
            return;
        }
        
        // Get participant count
        int participantCount = donationEventDAO.getParticipantCount(eventId);
        
        request.setAttribute("event", event);
        request.setAttribute("participantCount", participantCount);
        
        // Forward to the event view page
        request.getRequestDispatcher("/view/user/events/view.jsp").forward(request, response);
    }
    
    private void registerForEvent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Implementation for registering for an event
        String eventIdStr = request.getParameter("id");
        
        if (!ValidationUtil.isPositiveNumeric(eventIdStr)) {
            response.sendRedirect(request.getContextPath() + "/user/events");
            return;
        }
        
        int eventId = Integer.parseInt(eventIdStr);
        
        // Get event
        DonationEvent event = donationEventDAO.getDonationEventById(eventId);
        
        if (event == null) {
            response.sendRedirect(request.getContextPath() + "/user/events");
            return;
        }
        
        // Register user for event
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");
        
        // Redirect back to the event page
        response.sendRedirect(request.getContextPath() + "/user/events/view?id=" + eventId);
    }
    
    // Methods for handling scheduled appointments
    
    private void showScheduledDonations(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            Integer userId = (Integer) session.getAttribute("userId");
        
            if (userId == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }
            
            // Get all blood requests for this user
            List<BloodRequest> userRequests = bloodRequestDAO.getBloodRequestsByUserId(userId);
            
            // Create a list to store all appointments related to user's blood requests
            List<Appointment> userAppointments = new ArrayList<>();
            Map<Integer, BloodRequest> requestMap = new HashMap<>();
            Map<Integer, User> donorMap = new HashMap<>();
            
            // For each blood request, get related appointments
            if (userRequests != null && !userRequests.isEmpty()) {
                for (BloodRequest bloodRequest : userRequests) {
                    // Get appointments for this blood request
                    if (bloodRequest != null) {
                        // Get all appointments
                        List<Appointment> allAppointments = appointmentDAO.getAllAppointments();
                        
                        if (allAppointments != null) {
                            for (Appointment appointment : allAppointments) {
                                // Check if this appointment is for the current blood request
                                if (appointment != null && appointment.getBloodRequestId() != null && 
                                    appointment.getBloodRequestId() == bloodRequest.getId()) {
                                    userAppointments.add(appointment);
                                    requestMap.put(appointment.getId(), bloodRequest);
                                    
                                    // Get donor information
                                    int donorId = appointment.getDonorId();
                                    if (!donorMap.containsKey(donorId)) {
                                        Donor donor = donorDAO.getDonorById(donorId);
                                        if (donor != null) {
                                            User donorUser = userDAO.getUserById(donor.getUserId());
                                            if (donorUser != null) {
                                                donorMap.put(donorId, donorUser);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // If no appointments were found through blood requests, get all scheduled appointments
            // This is a fallback in case the blood request ID is not properly set
            if (userAppointments.isEmpty()) {
                List<Appointment> allAppointments = appointmentDAO.getAppointmentsByStatus("scheduled");
                
                if (allAppointments != null) {
                    for (Appointment appointment : allAppointments) {
                        if (appointment != null) {
                            userAppointments.add(appointment);
                            
                            // If this appointment has a blood request ID, get the blood request
                            if (appointment.getBloodRequestId() != null) {
                                BloodRequest bloodRequest = bloodRequestDAO.getBloodRequestById(appointment.getBloodRequestId());
                                if (bloodRequest != null) {
                                    requestMap.put(appointment.getId(), bloodRequest);
                                }
                            }
                            
                            // Get donor information
                            int donorId = appointment.getDonorId();
                            if (!donorMap.containsKey(donorId)) {
                                Donor donor = donorDAO.getDonorById(donorId);
                                if (donor != null) {
                                    User donorUser = userDAO.getUserById(donor.getUserId());
                                    if (donorUser != null) {
                                        donorMap.put(donorId, donorUser);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            request.setAttribute("appointments", userAppointments);
            request.setAttribute("requestMap", requestMap);
            request.setAttribute("donorMap", donorMap);
            
            request.getRequestDispatcher("/view/user/scheduled-donations.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred while retrieving scheduled donations.");
            request.getRequestDispatcher("/view/user/scheduled-donations.jsp").forward(request, response);
        }
    }
    
    private void viewAppointment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String appointmentIdStr = request.getParameter("id");
        
        if (!ValidationUtil.isPositiveNumeric(appointmentIdStr)) {
            response.sendRedirect(request.getContextPath() + "/user/scheduled-donations");
            return;
        }
        
        int appointmentId = Integer.parseInt(appointmentIdStr);
        
        // Get appointment
        Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);
        
        if (appointment == null) {
            response.sendRedirect(request.getContextPath() + "/user/scheduled-donations");
            return;
        }
        
        // Get donor information
        Donor donor = donorDAO.getDonorById(appointment.getDonorId());
        User donorUser = null;
        if (donor != null) {
            donorUser = userDAO.getUserById(donor.getUserId());
        }
        
        // Get blood request information if available
        BloodRequest bloodRequest = null;
        if (appointment.getBloodRequestId() != null) {
            bloodRequest = bloodRequestDAO.getBloodRequestById(appointment.getBloodRequestId());
        }
        
        request.setAttribute("appointment", appointment);
        request.setAttribute("donor", donor);
        request.setAttribute("donorUser", donorUser);
        request.setAttribute("bloodRequest", bloodRequest);
        
        request.getRequestDispatcher("/view/user/view-appointment.jsp").forward(request, response);
    }
    
    private void cancelAppointment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String appointmentIdStr = request.getParameter("id");
        
        if (!ValidationUtil.isPositiveNumeric(appointmentIdStr)) {
            response.sendRedirect(request.getContextPath() + "/user/scheduled-donations");
            return;
        }
        
        int appointmentId = Integer.parseInt(appointmentIdStr);
        
        // Get appointment
        Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);
        
        if (appointment == null) {
            response.sendRedirect(request.getContextPath() + "/user/scheduled-donations");
            return;
        }
        
        // Check if appointment can be cancelled (only scheduled appointments can be cancelled)
        if (!"scheduled".equals(appointment.getStatus())) {
            request.setAttribute("error", "Only scheduled appointments can be cancelled");
            response.sendRedirect(request.getContextPath() + "/user/scheduled-donations");
            return;
        }
        
        // Cancel appointment
        appointment.setStatus("cancelled");
        boolean appointmentUpdated = appointmentDAO.updateAppointment(appointment);
        
        if (appointmentUpdated) {
            request.setAttribute("success", "Appointment cancelled successfully");
        } else {
            request.setAttribute("error", "Failed to cancel appointment");
        }
        
        response.sendRedirect(request.getContextPath() + "/user/scheduled-donations");
    }
    
    private void rescheduleAppointment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String appointmentIdStr = request.getParameter("id");
        
        if (!ValidationUtil.isPositiveNumeric(appointmentIdStr)) {
            response.sendRedirect(request.getContextPath() + "/user/scheduled-donations");
            return;
        }
        
        int appointmentId = Integer.parseInt(appointmentIdStr);
        
        // Get appointment
        Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);
        
        if (appointment == null) {
            response.sendRedirect(request.getContextPath() + "/user/scheduled-donations");
            return;
        }
        
        // Get form data
        String appointmentDateStr = request.getParameter("appointmentDate");
        String appointmentTimeStr = request.getParameter("appointmentTime");
        String notes = request.getParameter("notes");
        
        // Validate input
        boolean hasError = false;
        
        if (!ValidationUtil.isNotEmpty(appointmentDateStr)) {
            request.setAttribute("dateError", "Date is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(appointmentTimeStr)) {
            request.setAttribute("timeError", "Time is required");
            hasError = true;
        }
        
        if (hasError) {
            // Preserve form data
            request.setAttribute("appointment", appointment);
            request.setAttribute("appointmentDate", appointmentDateStr);
            request.setAttribute("appointmentTime", appointmentTimeStr);
            request.setAttribute("notes", notes);
        
            // Forward back to the reschedule appointment page with errors
            request.getRequestDispatcher("/view/user/reschedule-appointment.jsp").forward(request, response);
            return;
        }
        try {
            // Parse data
            Date appointmentDate = Date.valueOf(appointmentDateStr);
            Time appointmentTime = Time.valueOf(appointmentTimeStr + ":00");
        
            // Update appointment
            appointment.setAppointmentDate(appointmentDate);
            appointment.setAppointmentTime(appointmentTime);
            if (notes != null && !notes.isEmpty()) {
                appointment.setNotes(notes);
            }
        
            boolean appointmentUpdated = appointmentDAO.updateAppointment(appointment);
        
            if (appointmentUpdated) {
                request.setAttribute("success", "Appointment rescheduled successfully");
                response.sendRedirect(request.getContextPath() + "/user/scheduled-donations?success=true");
            } else {
                request.setAttribute("error", "Failed to reschedule appointment");
                request.setAttribute("appointment", appointment);
                request.getRequestDispatcher("/view/user/reschedule-appointment.jsp").forward(request, response);
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Invalid date or time format");
            request.setAttribute("appointment", appointment);
            request.getRequestDispatcher("/view/user/reschedule-appointment.jsp").forward(request, response);
        }
    }
    
    // Method to view appointments for a specific blood request
    private void viewRequestAppointments(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String requestIdStr = request.getParameter("id");
            
            if (!ValidationUtil.isPositiveNumeric(requestIdStr)) {
                response.sendRedirect(request.getContextPath() + "/user/my-requests");
                return;
            }
            
            int requestId = Integer.parseInt(requestIdStr);
            
            // Get blood request
            BloodRequest bloodRequest = bloodRequestDAO.getBloodRequestById(requestId);
            
            if (bloodRequest == null) {
                response.sendRedirect(request.getContextPath() + "/user/my-requests");
                return;
            }
            
            // Check if the blood request belongs to the user
            HttpSession session = request.getSession();
            int userId = (int) session.getAttribute("userId");
            
            if (bloodRequest.getUserId() != userId) {
                response.sendRedirect(request.getContextPath() + "/user/my-requests");
                return;
            }
            
            // Get appointments for this blood request
            List<Appointment> appointments = null;
            try {
                appointments = appointmentDAO.getAppointmentsByBloodRequestId(requestId);
            } catch (Exception e) {
                e.printStackTrace();
                appointments = new ArrayList<>(); // Use empty list if there's an error
            }
            Map<Integer, User> donorUserMap = new HashMap<>();
            
            // Get donor user information for each appointment
            if (appointments != null) {
                for (Appointment appointment : appointments) {
                    int donorId = appointment.getDonorId();
                    if (!donorUserMap.containsKey(donorId)) {
                        Donor donor = donorDAO.getDonorById(donorId);
                        if (donor != null) {
                            User donorUser = userDAO.getUserById(donor.getUserId());
                            if (donorUser != null) {
                                donorUserMap.put(donorId, donorUser);
                            }
                        }
                    }
                }
            }
            
            request.setAttribute("bloodRequest", bloodRequest);
            request.setAttribute("appointments", appointments);
            request.setAttribute("donorUserMap", donorUserMap);
            
            // Forward to the view appointments page
            request.getRequestDispatcher("/view/user/view-request-appointments.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred while retrieving appointments.");
            request.getRequestDispatcher("/view/user/my-requests.jsp").forward(request, response);
        }
    }
}
