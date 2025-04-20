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
import java.util.List;

@WebServlet("/admin/*")
public class AdminController extends HttpServlet {
  private UserDAO userDAO;
  private DonorDAO donorDAO;
  private BloodInventoryDAO bloodInventoryDAO;
  private BloodRequestDAO bloodRequestDAO;
  private AppointmentDAO appointmentDAO;
  private DonationEventDAO donationEventDAO;

  @Override
  public void init() throws ServletException {
      userDAO = new UserDAO();
      donorDAO = new DonorDAO();
      bloodInventoryDAO = new BloodInventoryDAO();
      bloodRequestDAO = new BloodRequestDAO();
      appointmentDAO = new AppointmentDAO();
      donationEventDAO = new DonationEventDAO();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String pathInfo = request.getPathInfo();
      
      if (pathInfo == null || pathInfo.equals("/")) {
          response.sendRedirect(request.getContextPath() + "/admin/dashboard");
          return;
      }
      
      switch (pathInfo) {
          case "/dashboard":
              showDashboard(request, response);
              break;
          case "/manage-donors":
              manageDonors(request, response);
              break;
          case "/manage-requests":
              manageRequests(request, response);
              break;
          case "/manage-appointments":
              manageAppointments(request, response);
              break;
          case "/inventory":
              showInventory(request, response);
              break;
          case "/inventory/add":
              showAddInventory(request, response);
              break;
          case "/inventory/edit":
              showEditInventory(request, response);
              break;
          case "/events":
              showEvents(request, response);
              break;
          case "/events/add":
              showAddEvent(request, response);
              break;
          case "/events/edit":
              showEditEvent(request, response);
              break;
          case "/events/view":
              viewEvent(request, response);
              break;
          case "/reports":
              showReports(request, response);
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
          case "/manage-donors/update-status":
              updateDonorStatus(request, response);
              break;
          case "/manage-requests/update-status":
              updateRequestStatus(request, response);
              break;
          case "/manage-appointments/update-status":
              updateAppointmentStatus(request, response);
              break;
          case "/inventory/add":
              addInventory(request, response);
              break;
          case "/inventory/edit":
              updateInventory(request, response);
              break;
          case "/inventory/delete":
              deleteInventory(request, response);
              break;
          case "/events/add":
              addEvent(request, response);
              break;
          case "/events/edit":
              updateEvent(request, response);
              break;
          case "/events/delete":
              deleteEvent(request, response);
              break;
          default:
              response.sendError(HttpServletResponse.SC_NOT_FOUND);
              break;
      }
  }

  private void showDashboard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get counts for dashboard
        int donorCount = donorDAO.getAllDonors().size();
        int activeRequestsCount = bloodRequestDAO.getBloodRequestsByStatus("pending").size();
        int upcomingAppointmentsCount = appointmentDAO.getUpcomingAppointments().size();
        int upcomingEventsCount = donationEventDAO.getUpcomingDonationEvents().size();
        
        // Get blood inventory statistics
        int aPositiveCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("A+");
        int aNegativeCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("A-");
        int bPositiveCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("B+");
        int bNegativeCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("B-");
        int abPositiveCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("AB+");
        int abNegativeCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("AB-");
        int oPositiveCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("O+");
        int oNegativeCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("O-");
        
        // Set attributes for the dashboard
        request.setAttribute("donorCount", donorCount);
        request.setAttribute("activeRequestsCount", activeRequestsCount);
        request.setAttribute("upcomingAppointmentsCount", upcomingAppointmentsCount);
        request.setAttribute("upcomingEventsCount", upcomingEventsCount);
        
        request.setAttribute("aPositiveCount", aPositiveCount);
        request.setAttribute("aNegativeCount", aNegativeCount);
        request.setAttribute("bPositiveCount", bPositiveCount);
        request.setAttribute("bNegativeCount", bNegativeCount);
        request.setAttribute("abPositiveCount", abPositiveCount);
        request.setAttribute("abNegativeCount", abNegativeCount);
        request.setAttribute("oPositiveCount", oPositiveCount);
        request.setAttribute("oNegativeCount", oNegativeCount);
        
        // Get recent requests and appointments
        List<BloodRequest> recentRequests = bloodRequestDAO.getAllBloodRequests();
        if (recentRequests.size() > 5) {
            recentRequests = recentRequests.subList(0, 5);
        }
        
        List<Appointment> upcomingAppointments = appointmentDAO.getUpcomingAppointments();
        if (upcomingAppointments.size() > 5) {
            upcomingAppointments = upcomingAppointments.subList(0, 5);
        }
        
        request.setAttribute("recentRequests", recentRequests);
        request.setAttribute("upcomingAppointments", upcomingAppointments);
        
        // Forward to the dashboard page
        request.getRequestDispatcher("/view/admin/dashboard.jsp").forward(request, response);
    }
  
  private void manageDonors(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get all donors
        List<Donor> donors = donorDAO.getAllDonors();
        
        // For each donor, get the user information
        for (Donor donor : donors) {
            User user = userDAO.getUserById(donor.getUserId());
            request.setAttribute("user_" + donor.getId(), user);
        }
        
        request.setAttribute("donors", donors);
        
        // Forward to the manage donors page
        request.getRequestDispatcher("/view/admin/manage-donors.jsp").forward(request, response);
    }
  
  private void updateDonorStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String donorIdStr = request.getParameter("id");
        String availableStr = request.getParameter("available");
        
        if (!ValidationUtil.isPositiveNumeric(donorIdStr)) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-donors");
            return;
        }
        
        int donorId = Integer.parseInt(donorIdStr);
        boolean available = "true".equals(availableStr);
        
        // Update donor availability
        boolean updated = donorDAO.updateDonorAvailability(donorId, available);
        
        if (updated) {
            request.setAttribute("success", "Donor status updated successfully");
        } else {
            request.setAttribute("error", "Failed to update donor status");
        }
        
        // Redirect back to manage donors
        response.sendRedirect(request.getContextPath() + "/admin/manage-donors");
    }
  
  // Adding the missing methods
  private void manageRequests(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      // Get all blood requests
      List<BloodRequest> requests = bloodRequestDAO.getAllBloodRequests();
      
      // For each request, get the user information
      for (BloodRequest bloodRequest : requests) {
          User user = userDAO.getUserById(bloodRequest.getUserId());
          request.setAttribute("user_" + bloodRequest.getId(), user);
      }
      
      request.setAttribute("requests", requests);
      
      // Forward to the manage requests page
      request.getRequestDispatcher("/view/admin/manage-requests.jsp").forward(request, response);
  }
  
  private void updateRequestStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String requestIdStr = request.getParameter("id");
      String status = request.getParameter("status");
      
      if (!ValidationUtil.isPositiveNumeric(requestIdStr) || status == null || status.isEmpty()) {
          response.sendRedirect(request.getContextPath() + "/admin/manage-requests");
          return;
      }
      
      int requestId = Integer.parseInt(requestIdStr);
      
      // Update request status
      boolean updated = bloodRequestDAO.updateBloodRequestStatus(requestId, status);
      
      if (updated) {
          request.setAttribute("success", "Request status updated successfully");
      } else {
          request.setAttribute("error", "Failed to update request status");
      }
      
      // Redirect back to manage requests
      response.sendRedirect(request.getContextPath() + "/admin/manage-requests");
  }
  
  private void manageAppointments(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      // Get all appointments
      List<Appointment> appointments = appointmentDAO.getAllAppointments();
      
      // For each appointment, get the donor and user information
      for (Appointment appointment : appointments) {
          Donor donor = donorDAO.getDonorById(appointment.getDonorId());
          if (donor != null) {
              User user = userDAO.getUserById(donor.getUserId());
              request.setAttribute("user_" + appointment.getId(), user);
              request.setAttribute("donor_" + appointment.getId(), donor);
          }
      }
      
      request.setAttribute("appointments", appointments);
      
      // Forward to the manage appointments page
      request.getRequestDispatcher("/view/admin/manage-appointments.jsp").forward(request, response);
  }
  
  private void updateAppointmentStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String appointmentIdStr = request.getParameter("id");
      String status = request.getParameter("status");
      
      if (!ValidationUtil.isPositiveNumeric(appointmentIdStr) || status == null || status.isEmpty()) {
          response.sendRedirect(request.getContextPath() + "/admin/manage-appointments");
          return;
      }
      
      int appointmentId = Integer.parseInt(appointmentIdStr);
      
      // Update appointment status
      boolean updated = appointmentDAO.updateAppointmentStatus(appointmentId, status);
      
      if (updated) {
          request.setAttribute("success", "Appointment status updated successfully");
      } else {
          request.setAttribute("error", "Failed to update appointment status");
      }
      
      // Redirect back to manage appointments
      response.sendRedirect(request.getContextPath() + "/admin/manage-appointments");
  }
  
  private void showInventory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      // Get all blood inventory items
      List<BloodInventory> inventoryItems = bloodInventoryDAO.getAllBloodInventory();
      
      request.setAttribute("inventoryItems", inventoryItems);
      
      // Forward to the inventory page
      request.getRequestDispatcher("/view/admin/inventory/index.jsp").forward(request, response);
  }
  
  private void showAddInventory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      // Get all donors for dropdown
      List<Donor> donors = donorDAO.getAllDonors();
      request.setAttribute("donors", donors);
      
      // Forward to the add inventory page
      request.getRequestDispatcher("/view/admin/inventory/add.jsp").forward(request, response);
  }
  
  private void showEditInventory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String inventoryIdStr = request.getParameter("id");
      
      if (!ValidationUtil.isPositiveNumeric(inventoryIdStr)) {
          response.sendRedirect(request.getContextPath() + "/admin/inventory");
          return;
      }
      
      int inventoryId = Integer.parseInt(inventoryIdStr);
      
      // Get the inventory item
      BloodInventory inventoryItem = bloodInventoryDAO.getBloodInventoryById(inventoryId);
      
      if (inventoryItem == null) {
          response.sendRedirect(request.getContextPath() + "/admin/inventory");
          return;
      }
      
      request.setAttribute("inventoryItem", inventoryItem);
      
      // Get all donors for dropdown
      List<Donor> donors = donorDAO.getAllDonors();
      request.setAttribute("donors", donors);
      
      // Forward to the edit inventory page
      request.getRequestDispatcher("/view/admin/inventory/edit.jsp").forward(request, response);
  }
  
  private void addInventory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String bloodGroup = request.getParameter("bloodGroup");
      String quantityStr = request.getParameter("quantity");
      String collectionDate = request.getParameter("collectionDate");
      String expiryDate = request.getParameter("expiryDate");
      String status = request.getParameter("status");
      String donorIdStr = request.getParameter("donorId");
      String location = request.getParameter("location");
      
      // Validate input
      if (bloodGroup == null || quantityStr == null || !ValidationUtil.isPositiveNumeric(quantityStr) ||
          collectionDate == null || expiryDate == null || status == null) {
          request.setAttribute("error", "Invalid input data");
          showAddInventory(request, response);
          return;
      }
      
      int quantity = Integer.parseInt(quantityStr);
      int donorId = 0;
      if (ValidationUtil.isPositiveNumeric(donorIdStr)) {
          donorId = Integer.parseInt(donorIdStr);
      }
      
      // Create new inventory item
      BloodInventory inventoryItem = new BloodInventory();
      inventoryItem.setBloodGroup(bloodGroup);
      inventoryItem.setQuantity(quantity);
      inventoryItem.setCollectionDate(java.sql.Date.valueOf(collectionDate));
      inventoryItem.setExpiryDate(java.sql.Date.valueOf(expiryDate));
      inventoryItem.setStatus(status);
      if (donorId > 0) {
          inventoryItem.setDonorId(donorId);
      }
      inventoryItem.setLocation(location);
      
      // Add to database
      boolean added = bloodInventoryDAO.addBloodInventory(inventoryItem);
      
      if (added) {
          request.setAttribute("success", "Blood inventory added successfully");
      } else {
          request.setAttribute("error", "Failed to add blood inventory");
      }
      
      // Redirect to inventory list
      response.sendRedirect(request.getContextPath() + "/admin/inventory");
  }
  
  private void updateInventory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String inventoryIdStr = request.getParameter("id");
      String bloodGroup = request.getParameter("bloodGroup");
      String quantityStr = request.getParameter("quantity");
      String collectionDate = request.getParameter("collectionDate");
      String expiryDate = request.getParameter("expiryDate");
      String status = request.getParameter("status");
      String donorIdStr = request.getParameter("donorId");
      String location = request.getParameter("location");
      
      // Validate input
      if (!ValidationUtil.isPositiveNumeric(inventoryIdStr) || bloodGroup == null || 
          quantityStr == null || !ValidationUtil.isPositiveNumeric(quantityStr) ||
          collectionDate == null || expiryDate == null || status == null) {
          request.setAttribute("error", "Invalid input data");
          response.sendRedirect(request.getContextPath() + "/admin/inventory");
          return;
      }
      
      int inventoryId = Integer.parseInt(inventoryIdStr);
      int quantity = Integer.parseInt(quantityStr);
      int donorId = 0;
      if (ValidationUtil.isPositiveNumeric(donorIdStr)) {
          donorId = Integer.parseInt(donorIdStr);
      }
      
      // Create inventory item with updated values
      BloodInventory inventoryItem = new BloodInventory();
      inventoryItem.setId(inventoryId);
      inventoryItem.setBloodGroup(bloodGroup);
      inventoryItem.setQuantity(quantity);
      inventoryItem.setCollectionDate(java.sql.Date.valueOf(collectionDate));
      inventoryItem.setExpiryDate(java.sql.Date.valueOf(expiryDate));
      inventoryItem.setStatus(status);
      if (donorId > 0) {
          inventoryItem.setDonorId(donorId);
      }
      inventoryItem.setLocation(location);
      
      // Update in database
      boolean updated = bloodInventoryDAO.updateBloodInventory(inventoryItem);
      
      if (updated) {
          request.setAttribute("success", "Blood inventory updated successfully");
      } else {
          request.setAttribute("error", "Failed to update blood inventory");
      }
      
      // Redirect to inventory list
      response.sendRedirect(request.getContextPath() + "/admin/inventory");
  }
  
  private void deleteInventory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String inventoryIdStr = request.getParameter("id");
      
      if (!ValidationUtil.isPositiveNumeric(inventoryIdStr)) {
          response.sendRedirect(request.getContextPath() + "/admin/inventory");
          return;
      }
      
      int inventoryId = Integer.parseInt(inventoryIdStr);
      
      // Delete from database
      boolean deleted = bloodInventoryDAO.deleteBloodInventory(inventoryId);
      
      if (deleted) {
          request.setAttribute("success", "Blood inventory deleted successfully");
      } else {
          request.setAttribute("error", "Failed to delete blood inventory");
      }
      
      // Redirect to inventory list
      response.sendRedirect(request.getContextPath() + "/admin/inventory");
  }
  
  private void showEvents(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      // Get all donation events
      List<DonationEvent> events = donationEventDAO.getAllDonationEvents();
      
      request.setAttribute("events", events);
      
      // Forward to the events page
      request.getRequestDispatcher("/view/admin/events/index.jsp").forward(request, response);
  }
  
  private void showAddEvent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      // Forward to the add event page
      request.getRequestDispatcher("/view/admin/events/add.jsp").forward(request, response);
  }
  
  private void showEditEvent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String eventIdStr = request.getParameter("id");
      
      if (!ValidationUtil.isPositiveNumeric(eventIdStr)) {
          response.sendRedirect(request.getContextPath() + "/admin/events");
          return;
      }
      
      int eventId = Integer.parseInt(eventIdStr);
      
      // Get the event
      DonationEvent event = donationEventDAO.getDonationEventById(eventId);
      
      if (event == null) {
          response.sendRedirect(request.getContextPath() + "/admin/events");
          return;
      }
      
      request.setAttribute("event", event);
      
      // Forward to the edit event page
      request.getRequestDispatcher("/view/admin/events/edit.jsp").forward(request, response);
  }
  
  private void viewEvent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String eventIdStr = request.getParameter("id");
      
      if (!ValidationUtil.isPositiveNumeric(eventIdStr)) {
          response.sendRedirect(request.getContextPath() + "/admin/events");
          return;
      }
      
      int eventId = Integer.parseInt(eventIdStr);
      
      // Get the event
      DonationEvent event = donationEventDAO.getDonationEventById(eventId);
      
      if (event == null) {
          response.sendRedirect(request.getContextPath() + "/admin/events");
          return;
      }
      
      request.setAttribute("event", event);
      
      // Get participants for this event
      List<EventParticipant> participants = new EventParticipantDAO().getParticipantsByEventId(eventId);
      request.setAttribute("participants", participants);
      
      // For each participant, get the user information
      for (EventParticipant participant : participants) {
          User user = userDAO.getUserById(participant.getUserId());
          request.setAttribute("user_" + participant.getId(), user);
      }
      
      // Forward to the view event page
      request.getRequestDispatcher("/view/admin/events/view.jsp").forward(request, response);
  }
  
  private void addEvent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String title = request.getParameter("title");
      String description = request.getParameter("description");
      String eventDate = request.getParameter("eventDate");
      String startTime = request.getParameter("startTime");
      String endTime = request.getParameter("endTime");
      String location = request.getParameter("location");
      String organizer = request.getParameter("organizer");
      String contactEmail = request.getParameter("contactEmail");
      String contactPhone = request.getParameter("contactPhone");
      String maxParticipantsStr = request.getParameter("maxParticipants");
      
      // Validate input
      if (title == null || eventDate == null || startTime == null || endTime == null || 
          location == null || organizer == null || maxParticipantsStr == null || 
          !ValidationUtil.isPositiveNumeric(maxParticipantsStr)) {
          request.setAttribute("error", "Invalid input data");
          showAddEvent(request, response);
          return;
      }
      
      int maxParticipants = Integer.parseInt(maxParticipantsStr);
      
      // Create new event
      DonationEvent event = new DonationEvent();
      event.setTitle(title);
      event.setDescription(description);
      event.setEventDate(java.sql.Date.valueOf(eventDate));
      //event.setEndTime(java.sql.Time.valueOf(startTime + ":00"));
      //event.setStartTime(java.sql.Time.valueOf(endTime + ":00"));
      event.setLocation(location);
      event.setOrganizer(organizer);
      event.setContactEmail(contactEmail);
      event.setContactPhone(contactPhone);
      event.setMaxParticipants(maxParticipants);
      
      // Add to database
      boolean added = donationEventDAO.addDonationEvent(event);
      
      if (added) {
          request.setAttribute("success", "Donation event added successfully");
      } else {
          request.setAttribute("error", "Failed to add donation event");
      }
      
      // Redirect to events list
      response.sendRedirect(request.getContextPath() + "/admin/events");
  }
  
  private void updateEvent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String eventIdStr = request.getParameter("id");
      String title = request.getParameter("title");
      String description = request.getParameter("description");
      String eventDate = request.getParameter("eventDate");
      String startTime = request.getParameter("startTime");
      String endTime = request.getParameter("endTime");
      String location = request.getParameter("location");
      String organizer = request.getParameter("organizer");
      String contactEmail = request.getParameter("contactEmail");
      String contactPhone = request.getParameter("contactPhone");
      String maxParticipantsStr = request.getParameter("maxParticipants");
      
      // Validate input
      if (!ValidationUtil.isPositiveNumeric(eventIdStr) || title == null || eventDate == null || 
          startTime == null || endTime == null || location == null || organizer == null || 
          maxParticipantsStr == null || !ValidationUtil.isPositiveNumeric(maxParticipantsStr)) {
          request.setAttribute("error", "Invalid input data");
          response.sendRedirect(request.getContextPath() + "/admin/events");
          return;
      }
      
      int eventId = Integer.parseInt(eventIdStr);
      int maxParticipants = Integer.parseInt(maxParticipantsStr);
      
      // Create event with updated values
      DonationEvent event = new DonationEvent();
      event.setId(eventId);
      event.setTitle(title);
      event.setDescription(description);
      event.setEventDate(java.sql.Date.valueOf(eventDate));
      //event.setStartTime(java.sql.Time.valueOf(startTime + ":00"));
     // event.setEndTime(java.sql.Time.valueOf(endTime + ":00"));
      event.setLocation(location);
      event.setOrganizer(organizer);
      event.setContactEmail(contactEmail);
      event.setContactPhone(contactPhone);
      event.setMaxParticipants(maxParticipants);
      
      // Update in database
      boolean updated = donationEventDAO.updateDonationEvent(event);
      
      if (updated) {
          request.setAttribute("success", "Donation event updated successfully");
      } else {
          request.setAttribute("error", "Failed to update donation event");
      }
      
      // Redirect to events list
      response.sendRedirect(request.getContextPath() + "/admin/events");
  }
  
  private void deleteEvent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String eventIdStr = request.getParameter("id");
      
      if (!ValidationUtil.isPositiveNumeric(eventIdStr)) {
          response.sendRedirect(request.getContextPath() + "/admin/events");
          return;
      }
      
      int eventId = Integer.parseInt(eventIdStr);
      
      // Delete from database
      boolean deleted = donationEventDAO.deleteDonationEvent(eventId);
      
      if (deleted) {
          request.setAttribute("success", "Donation event deleted successfully");
      } else {
          request.setAttribute("error", "Failed to delete donation event");
      }
      
      // Redirect to events list
      response.sendRedirect(request.getContextPath() + "/admin/events");
  }
  
  private void showReports(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      // Get statistics for reports
      int totalDonors = donorDAO.getAllDonors().size();
      int totalRequests = bloodRequestDAO.getAllBloodRequests().size();
      int totalAppointments = appointmentDAO.getAllAppointments().size();
      int totalEvents = donationEventDAO.getAllDonationEvents().size();
      
      // Get blood inventory statistics
      int totalBloodUnits = bloodInventoryDAO.getTotalAvailableQuantity();
      
      // Blood group distribution
      int aPositiveCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("A+");
      int aNegativeCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("A-");
      int bPositiveCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("B+");
      int bNegativeCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("B-");
      int abPositiveCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("AB+");
      int abNegativeCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("AB-");
      int oPositiveCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("O+");
      int oNegativeCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("O-");
      
      // Set attributes for the reports page
      request.setAttribute("totalDonors", totalDonors);
      request.setAttribute("totalRequests", totalRequests);
      request.setAttribute("totalAppointments", totalAppointments);
      request.setAttribute("totalEvents", totalEvents);
      request.setAttribute("totalBloodUnits", totalBloodUnits);
      
      request.setAttribute("aPositiveCount", aPositiveCount);
      request.setAttribute("aNegativeCount", aNegativeCount);
      request.setAttribute("bPositiveCount", bPositiveCount);
      request.setAttribute("bNegativeCount", bNegativeCount);
      request.setAttribute("abPositiveCount", abPositiveCount);
      request.setAttribute("abNegativeCount", abNegativeCount);
      request.setAttribute("oPositiveCount", oPositiveCount);
      request.setAttribute("oNegativeCount", oNegativeCount);
      
      // Forward to the reports page
      request.getRequestDispatcher("/view/admin/reports.jsp").forward(request, response);
  }
}
