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
import java.util.List;

@WebServlet("/donor/*")
public class DonorController extends HttpServlet {
  private DonorDAO donorDAO;
  private UserDAO userDAO;
  private AppointmentDAO appointmentDAO;
  private BloodRequestDAO bloodRequestDAO;
  private DonationEventDAO donationEventDAO;

  @Override
  public void init() throws ServletException {
      donorDAO = new DonorDAO();
      userDAO = new UserDAO();
      appointmentDAO = new AppointmentDAO();
      bloodRequestDAO = new BloodRequestDAO();
      donationEventDAO = new DonationEventDAO();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String pathInfo = request.getPathInfo();
      
      if (pathInfo == null || pathInfo.equals("/")) {
          response.sendRedirect(request.getContextPath() + "/donor/dashboard");
          return;
      }
      
      switch (pathInfo) {
          case "/dashboard":
              showDashboard(request, response);
              break;
          case "/profile":
              showProfile(request, response);
              break;
          case "/view-requests":
              viewRequests(request, response);
              break;
          case "/schedule-appointment":
              showScheduleAppointment(request, response);
              break;
          case "/reschedule-appointment":
              showRescheduleAppointment(request, response);
              break;
          case "/donation-history":
              showDonationHistory(request, response);
              break;
          case "/events":
              showEvents(request, response);
              break;
          case "/events/view":
              viewEvent(request, response);
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
          case "/schedule-appointment":
              scheduleAppointment(request, response);
              break;
          case "/reschedule-appointment":
              rescheduleAppointment(request, response);
              break;
          case "/cancel-appointment":
              cancelAppointment(request, response);
              break;
          case "/events/register":
              registerForEvent(request, response);
              break;
          case "/events/cancel":
              cancelEventRegistration(request, response);
              break;
          default:
              response.sendError(HttpServletResponse.SC_NOT_FOUND);
              break;
      }
  }

  private void showDashboard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        int donorId = (int) session.getAttribute("donorId");
        int userId = (int) session.getAttribute("userId");
        
        // Get donor information
        Donor donor = donorDAO.getDonorById(donorId);
        User user = userDAO.getUserById(userId);
        
        // Get upcoming appointments
        List<Appointment> upcomingAppointments = appointmentDAO.getUpcomingAppointmentsForDonor(donorId);
        
        // Get donation history count
        int donationCount = donor.getDonationCount();
        
        // Get upcoming events
        List<DonationEvent> upcomingEvents = donationEventDAO.getUpcomingDonationEvents();
        
        // Set attributes for the dashboard
        request.setAttribute("donor", donor);
        request.setAttribute("user", user);
        request.setAttribute("upcomingAppointments", upcomingAppointments);
        request.setAttribute("donationCount", donationCount);
        request.setAttribute("upcomingEvents", upcomingEvents);
        
        // Forward to the dashboard page
        request.getRequestDispatcher("/view/donor/dashboard.jsp").forward(request, response);
    }
    
    private void showProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        int donorId = (int) session.getAttribute("donorId");
        int userId = (int) session.getAttribute("userId");
        
        // Get donor and user information
        Donor donor = donorDAO.getDonorById(donorId);
        User user = userDAO.getUserById(userId);
        
        // Set attributes for the profile page
        request.setAttribute("donor", donor);
        request.setAttribute("user", user);
        
        // Forward to the profile page
        request.getRequestDispatcher("/view/donor/profile.jsp").forward(request, response);
    }
    
    private void updateProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        int donorId = (int) session.getAttribute("donorId");
        int userId = (int) session.getAttribute("userId");
        
        // Get form data
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String bloodGroup = request.getParameter("bloodGroup");
        String medicalHistory = request.getParameter("medicalHistory");
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        String availableStr = request.getParameter("available");
        boolean available = "on".equals(availableStr) || "true".equals(availableStr);
        
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
        
        if (!ValidationUtil.isValidBloodGroup(bloodGroup)) {
            request.setAttribute("bloodGroupError", "Invalid blood group");
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
            // Get donor and user information
            Donor donor = donorDAO.getDonorById(donorId);
            User user = userDAO.getUserById(userId);
            
            // Set attributes for the profile page
            request.setAttribute("donor", donor);
            request.setAttribute("user", user);
            
            // Preserve form data
            request.setAttribute("name", name);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.setAttribute("address", address);
            request.setAttribute("bloodGroup", bloodGroup);
            request.setAttribute("medicalHistory", medicalHistory);
            request.setAttribute("available", available);
            
            // Forward back to the profile page with errors
            request.getRequestDispatcher("/view/donor/profile.jsp").forward(request, response);
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
        
        // Update donor information
        Donor donor = donorDAO.getDonorById(donorId);
        donor.setBloodGroup(bloodGroup);
        donor.setMedicalHistory(medicalHistory);
        donor.setAvailable(available);
        donor.setLocation(address);
        
        boolean donorUpdated = donorDAO.updateDonor(donor);
        
        if (userUpdated && donorUpdated) {
            request.setAttribute("success", "Profile updated successfully");
        } else {
            request.setAttribute("error", "Failed to update profile");
        }
        
        // Get updated donor and user information
        donor = donorDAO.getDonorById(donorId);
        user = userDAO.getUserById(userId);
        
        // Set attributes for the profile page
        request.setAttribute("donor", donor);
        request.setAttribute("user", user);
        
        // Forward to the profile page
        request.getRequestDispatcher("/view/donor/profile.jsp").forward(request, response);
    }
    
    private void viewRequests(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get blood requests that match the donor's blood group
        HttpSession session = request.getSession();
        int donorId = (int) session.getAttribute("donorId");
        
        Donor donor = donorDAO.getDonorById(donorId);
        String bloodGroup = donor.getBloodGroup();
        
        List<BloodRequest> requests = bloodRequestDAO.getBloodRequestsByBloodGroup(bloodGroup);
        
        // Filter requests to show only pending and approved ones
        requests.removeIf(request1 -> !("pending".equals(request1.getStatus()) || "approved".equals(request1.getStatus())));
        
        request.setAttribute("requests", requests);
        
        // Forward to the view requests page
        request.getRequestDispatcher("/view/donor/view-requests.jsp").forward(request, response);
    }
    
    private void showScheduleAppointment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Forward to the schedule appointment page
        request.getRequestDispatcher("/view/donor/schedule-appointment.jsp").forward(request, response);
    }
    
    private void scheduleAppointment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        int donorId = (int) session.getAttribute("donorId");
        
        // Get form data
        String appointmentDateStr = request.getParameter("appointmentDate");
        String appointmentTimeStr = request.getParameter("appointmentTime");
        String notes = request.getParameter("notes");
        
        // Validate input
        boolean hasError = false;
        
        if (!ValidationUtil.isNotEmpty(appointmentDateStr)) {
            request.setAttribute("dateError", "Appointment date is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(appointmentTimeStr)) {
            request.setAttribute("timeError", "Appointment time is required");
            hasError = true;
        }
        
        if (hasError) {
            // Preserve form data
            request.setAttribute("appointmentDate", appointmentDateStr);
            request.setAttribute("appointmentTime", appointmentTimeStr);
            request.setAttribute("notes", notes);
            
            // Forward back to the schedule appointment page with errors
            request.getRequestDispatcher("/view/donor/schedule-appointment.jsp").forward(request, response);
            return;
        }
        
        try {
            // Parse date and time
            Date appointmentDate = Date.valueOf(appointmentDateStr);
            Time appointmentTime = Time.valueOf(appointmentTimeStr + ":00");
            
            // Create appointment
            Appointment appointment = new Appointment();
            appointment.setDonorId(donorId);
            appointment.setAppointmentDate(appointmentDate);
            appointment.setAppointmentTime(appointmentTime);
            appointment.setStatus("scheduled");
            appointment.setNotes(notes);
            
            boolean appointmentCreated = appointmentDAO.addAppointment(appointment);
            
            if (appointmentCreated) {
                request.setAttribute("success", "Appointment scheduled successfully");
            } else {
                request.setAttribute("error", "Failed to schedule appointment");
            }
            
            // Forward to the schedule appointment page
            request.getRequestDispatcher("/view/donor/schedule-appointment.jsp").forward(request, response);
            
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Invalid date or time format");
            request.getRequestDispatcher("/view/donor/schedule-appointment.jsp").forward(request, response);
        }
    }
    
    private void showRescheduleAppointment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String appointmentIdStr = request.getParameter("id");
        
        if (!ValidationUtil.isPositiveNumeric(appointmentIdStr)) {
            response.sendRedirect(request.getContextPath() + "/donor/dashboard");
            return;
        }
        
        int appointmentId = Integer.parseInt(appointmentIdStr);
        
        // Get appointment
        Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);
        
        if (appointment == null) {
            response.sendRedirect(request.getContextPath() + "/donor/dashboard");
            return;
        }
        
        // Check if appointment belongs to the donor
        HttpSession session = request.getSession();
        int donorId = (int) session.getAttribute("donorId");
        
        if (appointment.getDonorId() != donorId) {
            response.sendRedirect(request.getContextPath() + "/donor/dashboard");
            return;
        }
        
        // Set appointment attribute
        request.setAttribute("appointment", appointment);
        
        // Forward to the reschedule appointment page
        request.getRequestDispatcher("/view/donor/reschedule-appointment.jsp").forward(request, response);
    }
    
    private void rescheduleAppointment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String appointmentIdStr = request.getParameter("id");
        
        if (!ValidationUtil.isPositiveNumeric(appointmentIdStr)) {
            response.sendRedirect(request.getContextPath() + "/donor/dashboard");
            return;
        }
        
        int appointmentId = Integer.parseInt(appointmentIdStr);
        
        // Get appointment
        Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);
        
        if (appointment == null) {
            response.sendRedirect(request.getContextPath() + "/donor/dashboard");
            return;
        }
        
        // Check if appointment belongs to the donor
        HttpSession session = request.getSession();
        int donorId = (int) session.getAttribute("donorId");
        
        if (appointment.getDonorId() != donorId) {
            response.sendRedirect(request.getContextPath() + "/donor/dashboard");
            return;
        }
        
        // Get form data
        String appointmentDateStr = request.getParameter("appointmentDate");
        String appointmentTimeStr = request.getParameter("appointmentTime");
        String notes = request.getParameter("notes");
        
        // Validate input
        boolean hasError = false;
        
        if (!ValidationUtil.isNotEmpty(appointmentDateStr)) {
            request.setAttribute("dateError", "Appointment date is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(appointmentTimeStr)) {
            request.setAttribute("timeError", "Appointment time is required");
            hasError = true;
        }
        
        if (hasError) {
            // Preserve form data
            request.setAttribute("appointmentDate", appointmentDateStr);
            request.setAttribute("appointmentTime", appointmentTimeStr);
            request.setAttribute("notes", notes);
            request.setAttribute("appointment", appointment);
            
            // Forward back to the reschedule appointment page with errors
            request.getRequestDispatcher("/view/donor/reschedule-appointment.jsp").forward(request, response);
            return;
        }
        
        try {
            // Parse date and time
            Date appointmentDate = Date.valueOf(appointmentDateStr);
            Time appointmentTime = Time.valueOf(appointmentTimeStr + ":00");
            
            // Update appointment
            appointment.setAppointmentDate(appointmentDate);
            appointment.setAppointmentTime(appointmentTime);
            appointment.setNotes(notes);
            
            boolean appointmentUpdated = appointmentDAO.updateAppointment(appointment);
            
            if (appointmentUpdated) {
                request.setAttribute("success", "Appointment rescheduled successfully");
            } else {
                request.setAttribute("error", "Failed to reschedule appointment");
            }
            
            // Get updated appointment
            appointment = appointmentDAO.getAppointmentById(appointmentId);
            request.setAttribute("appointment", appointment);
            
            // Forward to the reschedule appointment page
            request.getRequestDispatcher("/view/donor/reschedule-appointment.jsp").forward(request, response);
            
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Invalid date or time format");
            request.setAttribute("appointment", appointment);
            request.getRequestDispatcher("/view/donor/reschedule-appointment.jsp").forward(request, response);
        }
    }
    
    private void cancelAppointment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String appointmentIdStr = request.getParameter("id");
        
        if (!ValidationUtil.isPositiveNumeric(appointmentIdStr)) {
            response.sendRedirect(request.getContextPath() + "/donor/dashboard");
            return;
        }
        
        int appointmentId = Integer.parseInt(appointmentIdStr);
        
        // Get appointment
        Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);
        
        if (appointment == null) {
            response.sendRedirect(request.getContextPath() + "/donor/dashboard");
            return;
        }
        
        // Check if appointment belongs to the donor
        HttpSession session = request.getSession();
        int donorId = (int) session.getAttribute("donorId");
        
        if (appointment.getDonorId() != donorId) {
            response.sendRedirect(request.getContextPath() + "/donor/dashboard");
            return;
        }
        
        // Cancel appointment
        boolean appointmentCancelled = appointmentDAO.updateAppointmentStatus(appointmentId, "cancelled");
        
        if (appointmentCancelled) {
            request.setAttribute("success", "Appointment cancelled successfully");
        } else {
            request.setAttribute("error", "Failed to cancel appointment");
        }
        
        // Redirect to dashboard
        response.sendRedirect(request.getContextPath() + "/donor/dashboard");
    }
    
    private void showDonationHistory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        int donorId = (int) session.getAttribute("donorId");
        
        // Get all appointments for the donor
        List<Appointment> appointments = appointmentDAO.getAppointmentsByDonorId(donorId);
        
        request.setAttribute("appointments", appointments);
        
        // Forward to the donation history page
        request.getRequestDispatcher("/view/donor/donation-history.jsp").forward(request, response);
    }
    
    private void showEvents(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get upcoming events
        List<DonationEvent> upcomingEvents = donationEventDAO.getUpcomingDonationEvents();
        
        request.setAttribute("events", upcomingEvents);
        
        // Forward to the events page
        request.getRequestDispatcher("/view/donor/events/index.jsp").forward(request, response);
    }
    
    private void viewEvent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String eventIdStr = request.getParameter("id");
        
        if (!ValidationUtil.isPositiveNumeric(eventIdStr)) {
            response.sendRedirect(request.getContextPath() + "/donor/events");
            return;
        }
        
        int eventId = Integer.parseInt(eventIdStr);
        
        // Get event
        DonationEvent event = donationEventDAO.getDonationEventById(eventId);
        
        if (event == null) {
            response.sendRedirect(request.getContextPath() + "/donor/events");
            return;
        }
        
        // Get participant count
        int participantCount = donationEventDAO.getParticipantCount(eventId);
        
        request.setAttribute("event", event);
        request.setAttribute("participantCount", participantCount);
        
        // Forward to the event view page
        request.getRequestDispatcher("/view/donor/events/view.jsp").forward(request, response);
    }
    
    private void registerForEvent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Implementation for registering for an event
        // This would involve creating an EventParticipant record
        
        // Redirect back to the event page
        String eventId = request.getParameter("id");
        response.sendRedirect(request.getContextPath() + "/donor/events/view?id=" + eventId);
    }
    
    private void cancelEventRegistration(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Implementation for cancelling event registration
        // This would involve updating the EventParticipant status
        
        // Redirect back to the event page
        String eventId = request.getParameter("id");
        response.sendRedirect(request.getContextPath() + "/donor/events/view?id=" + eventId);
    }
}

