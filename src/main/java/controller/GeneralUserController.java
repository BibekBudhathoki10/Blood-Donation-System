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
import java.sql.Timestamp;
import java.util.List;

@WebServlet("/user/*")
public class GeneralUserController extends HttpServlet {
  private UserDAO userDAO;
  private BloodRequestDAO bloodRequestDAO;
  private BloodInventoryDAO bloodInventoryDAO;
  private DonationEventDAO donationEventDAO;

  @Override
  public void init() throws ServletException {
      userDAO = new UserDAO();
      bloodRequestDAO = new BloodRequestDAO();
      bloodInventoryDAO = new BloodInventoryDAO();
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
          case "/request-blood":
              requestBlood(request, response);
              break;
          case "/edit-request":
              updateRequest(request, response);
              break;
          case "/cancel-request":
              cancelRequest(request, response);
              break;
          case "/events/register":
              registerForEvent(request, response);
              break;
          default:
              response.sendError(HttpServletResponse.SC_NOT_FOUND);
              break;
      }
  }

  private void showDashboard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");
        
        // Get user information
        User user = userDAO.getUserById(userId);
        
        // Get user's blood requests
        List<BloodRequest> requests = bloodRequestDAO.getBloodRequestsByUserId(userId);
        
        // Get blood inventory statistics
        int aPositiveCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("A+");
        int aNegativeCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("A-");
        int bPositiveCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("B+");
        int bNegativeCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("B-");
        int abPositiveCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("AB+");
        int abNegativeCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("AB-");
        int oPositiveCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("O+");
        int oNegativeCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("O-");
        
        // Get upcoming events
        List<DonationEvent> upcomingEvents = donationEventDAO.getUpcomingDonationEvents();
        if (upcomingEvents.size() > 3) {
            upcomingEvents = upcomingEvents.subList(0, 3);
        }
        
        // Set attributes for the dashboard
        request.setAttribute("user", user);
        request.setAttribute("requests", requests);
        
        request.setAttribute("aPositiveCount", aPositiveCount);
        request.setAttribute("aNegativeCount", aNegativeCount);
        request.setAttribute("bPositiveCount", bPositiveCount);
        request.setAttribute("bNegativeCount", bNegativeCount);
        request.setAttribute("abPositiveCount", abPositiveCount);
        request.setAttribute("abNegativeCount", abNegativeCount);
        request.setAttribute("oPositiveCount", oPositiveCount);
        request.setAttribute("oNegativeCount", oNegativeCount);
        
        request.setAttribute("upcomingEvents", upcomingEvents);
        
        // Forward to the dashboard page
        request.getRequestDispatcher("/view/user/dashboard.jsp").forward(request, response);
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
        String bloodGroup = request.getParameter("bloodGroup");
        String quantityStr = request.getParameter("quantity");
        String urgency = request.getParameter("urgency");
        String hospitalName = request.getParameter("hospitalName");
        String hospitalAddress = request.getParameter("hospitalAddress");
        String patientName = request.getParameter("patientName");
        String contactPerson = request.getParameter("contactPerson");
        String contactPhone = request.getParameter("contactPhone");
        String reason = request.getParameter("reason");
        String requiredDateStr = request.getParameter("requiredDate");
        
        // Validate input
        boolean hasError = false;
        
        if (!ValidationUtil.isValidBloodGroup(bloodGroup)) {
            request.setAttribute("bloodGroupError", "Invalid blood group");
            hasError = true;
        }
        
        if (!ValidationUtil.isPositiveNumeric(quantityStr)) {
            request.setAttribute("quantityError", "Quantity must be a positive number");
            hasError = true;
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
        
        if (!ValidationUtil.isNotEmpty(patientName)) {
            request.setAttribute("patientNameError", "Patient name is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(contactPerson)) {
            request.setAttribute("contactPersonError", "Contact person is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isValidPhone(contactPhone)) {
            request.setAttribute("contactPhoneError", "Invalid contact phone");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(requiredDateStr)) {
            request.setAttribute("requiredDateError", "Required date is required");
            hasError = true;
        }
        
        if (hasError) {
            // Preserve form data
            request.setAttribute("bloodGroup", bloodGroup);
            request.setAttribute("quantity", quantityStr);
            request.setAttribute("urgency", urgency);
            request.setAttribute("hospitalName", hospitalName);
            request.setAttribute("hospitalAddress", hospitalAddress);
            request.setAttribute("patientName", patientName);
            request.setAttribute("contactPerson", contactPerson);
            request.setAttribute("contactPhone", contactPhone);
            request.setAttribute("reason", reason);
            request.setAttribute("requiredDate", requiredDateStr);
            
            // Forward back to the request blood page with errors
            request.getRequestDispatcher("/view/user/request-blood.jsp").forward(request, response);
            return;
        }
        
        try {
            // Parse data
            int quantity = Integer.parseInt(quantityStr);
            Date requiredDate = Date.valueOf(requiredDateStr);
            
            // Create blood request
            BloodRequest bloodRequest = new BloodRequest();
            bloodRequest.setUserId(userId);
            bloodRequest.setBloodGroup(bloodGroup);
            bloodRequest.setQuantity(quantity);
            bloodRequest.setUrgency(urgency);
            bloodRequest.setStatus("pending");
            bloodRequest.setHospitalName(hospitalName);
            bloodRequest.setHospitalAddress(hospitalAddress);
            bloodRequest.setPatientName(patientName);
            bloodRequest.setContactPerson(contactPerson);
            bloodRequest.setContactPhone(contactPhone);
            bloodRequest.setReason(reason);
            bloodRequest.setRequestDate(new Timestamp(System.currentTimeMillis()));
            bloodRequest.setRequiredDate(requiredDate);
            
            boolean requestCreated = bloodRequestDAO.addBloodRequest(bloodRequest);
            
            if (requestCreated) {
                request.setAttribute("success", "Blood request submitted successfully");
                response.sendRedirect(request.getContextPath() + "/user/my-requests");
            } else {
                request.setAttribute("error", "Failed to submit blood request");
                request.getRequestDispatcher("/view/user/request-blood.jsp").forward(request, response);
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Invalid date format");
            request.getRequestDispatcher("/view/user/request-blood.jsp").forward(request, response);
        }
    }
    
    private void showMyRequests(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");
        
        // Get user's blood requests
        List<BloodRequest> requests = bloodRequestDAO.getBloodRequestsByUserId(userId);
        
        request.setAttribute("requests", requests);
        
        // Forward to the my requests page
        request.getRequestDispatcher("/view/user/my-requests.jsp").forward(request, response);
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
        
        // Check if request belongs to the user
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");
        
        if (bloodRequest.getUserId() != userId) {
            response.sendRedirect(request.getContextPath() + "/user/my-requests");
            return;
        }
        
        // Check if request can be edited (only pending requests can be edited)
        if (!"pending".equals(bloodRequest.getStatus())) {
            request.setAttribute("error", "Only pending requests can be edited");
            response.sendRedirect(request.getContextPath() + "/user/my-requests");
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
        
        // Check if request belongs to the user
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");
        
        if (bloodRequest.getUserId() != userId) {
            response.sendRedirect(request.getContextPath() + "/user/my-requests");
            return;
        }
        
        // Check if request can be edited (only pending requests can be edited)
        if (!"pending".equals(bloodRequest.getStatus())) {
            request.setAttribute("error", "Only pending requests can be edited");
            response.sendRedirect(request.getContextPath() + "/user/my-requests");
            return;
        }
        
        // Get form data
        String bloodGroup = request.getParameter("bloodGroup");
        String quantityStr = request.getParameter("quantity");
        String urgency = request.getParameter("urgency");
        String hospitalName = request.getParameter("hospitalName");
        String hospitalAddress = request.getParameter("hospitalAddress");
        String patientName = request.getParameter("patientName");
        String contactPerson = request.getParameter("contactPerson");
        String contactPhone = request.getParameter("contactPhone");
        String reason = request.getParameter("reason");
        String requiredDateStr = request.getParameter("requiredDate");
        
        // Validate input
        boolean hasError = false;
        
        if (!ValidationUtil.isValidBloodGroup(bloodGroup)) {
            request.setAttribute("bloodGroupError", "Invalid blood group");
            hasError = true;
        }
        
        if (!ValidationUtil.isPositiveNumeric(quantityStr)) {
            request.setAttribute("quantityError", "Quantity must be a positive number");
            hasError = true;
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
        
        if (!ValidationUtil.isNotEmpty(patientName)) {
            request.setAttribute("patientNameError", "Patient name is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(contactPerson)) {
            request.setAttribute("contactPersonError", "Contact person is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isValidPhone(contactPhone)) {
            request.setAttribute("contactPhoneError", "Invalid contact phone");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(requiredDateStr)) {
            request.setAttribute("requiredDateError", "Required date is required");
            hasError = true;
        }
        
        if (hasError) {
            // Preserve form data
            request.setAttribute("bloodRequest", bloodRequest);
            request.setAttribute("bloodGroup", bloodGroup);
            request.setAttribute("quantity", quantityStr);
            request.setAttribute("urgency", urgency);
            request.setAttribute("hospitalName", hospitalName);
            request.setAttribute("hospitalAddress", hospitalAddress);
            request.setAttribute("patientName", patientName);
            request.setAttribute("contactPerson", contactPerson);
            request.setAttribute("contactPhone", contactPhone);
            request.setAttribute("reason", reason);
            request.setAttribute("requiredDate", requiredDateStr);
            
            // Forward back to the edit request page with errors
            request.getRequestDispatcher("/view/user/edit-request.jsp").forward(request, response);
            return;
        }
        
        try {
            // Parse data
            int quantity = Integer.parseInt(quantityStr);
            Date requiredDate = Date.valueOf(requiredDateStr);
            
            // Update blood request
            bloodRequest.setBloodGroup(bloodGroup);
            bloodRequest.setQuantity(quantity);
            bloodRequest.setUrgency(urgency);
            bloodRequest.setHospitalName(hospitalName);
            bloodRequest.setHospitalAddress(hospitalAddress);
            bloodRequest.setPatientName(patientName);
            bloodRequest.setContactPerson(contactPerson);
            bloodRequest.setContactPhone(contactPhone);
            bloodRequest.setReason(reason);
            bloodRequest.setRequiredDate(requiredDate);
            
            boolean requestUpdated = bloodRequestDAO.updateBloodRequest(bloodRequest);
            
            if (requestUpdated) {
                request.setAttribute("success", "Blood request updated successfully");
                response.sendRedirect(request.getContextPath() + "/user/my-requests");
            } else {
                request.setAttribute("error", "Failed to update blood request");
                request.setAttribute("bloodRequest", bloodRequest);
                request.getRequestDispatcher("/view/user/edit-request.jsp").forward(request, response);
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Invalid date format");
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
        
        // Check if request belongs to the user
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");
        
        if (bloodRequest.getUserId() != userId) {
            response.sendRedirect(request.getContextPath() + "/user/my-requests");
            return;
        }
        
        // Check if request can be cancelled (only pending and approved requests can be cancelled)
        if (!"pending".equals(bloodRequest.getStatus()) && !"approved".equals(bloodRequest.getStatus())) {
            request.setAttribute("error", "Only pending and approved requests can be cancelled");
            response.sendRedirect(request.getContextPath() + "/user/my-requests");
            return;
        }
        
        // Cancel request
        boolean requestCancelled = bloodRequestDAO.updateBloodRequestStatus(requestId, "cancelled");
        
        if (requestCancelled) {
            request.setAttribute("success", "Blood request cancelled successfully");
        } else {
            request.setAttribute("error", "Failed to cancel blood request");
        }
        
        response.sendRedirect(request.getContextPath() + "/user/my-requests");
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
        // This would involve creating an EventParticipant record
        
        // Redirect back to the event page
        String eventId = request.getParameter("id");
        response.sendRedirect(request.getContextPath() + "/user/events/view?id=" + eventId);
    }
}

