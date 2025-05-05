package controller;

import model.*;
import util.ValidationUtil;
import util.DBConnection;

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
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

@WebServlet("/donor/*")
public class DonorController extends HttpServlet {
  private DonorDAO donorDAO;
  private UserDAO userDAO;
  private AppointmentDAO appointmentDAO;
  private BloodRequestDAO bloodRequestDAO;
  private DonationEventDAO donationEventDAO;
  private EventParticipantDAO eventParticipantDAO;
   private Connection connection;
   
   @Override
   public void init() throws ServletException {
       try {
           // Test database connection
           boolean dbConnected = DBConnection.testConnection();
           if (!dbConnected) {
               System.err.println("WARNING: Database connection test failed during servlet initialization");
               throw new ServletException("Database connection failed");
           }
           
           // Print database diagnostic information
           System.out.println("Database connection status: " + (DBConnection.testConnection() ? "Connected" : "Not connected"));
           
           connection = DBConnection.getConnection();
           donorDAO = new DonorDAO();
           userDAO = new UserDAO();
           appointmentDAO = new AppointmentDAO();
           
           // Test if appointmentDAO can access the database
           boolean dbAccessOk = appointmentDAO.testDatabaseAccess();
           if (!dbAccessOk) {
               System.err.println("WARNING: AppointmentDAO database access test failed");
           }
           
           // Check if appointments table exists
           boolean tableExists = appointmentDAO.checkTableExists();
           if (!tableExists) {
               System.err.println("WARNING: appointments table does not exist");
           } else {
               System.out.println("appointments table structure:\n" + appointmentDAO.describeTable());
           }
           
           bloodRequestDAO = new BloodRequestDAO();
           donationEventDAO = new DonationEventDAO();
           eventParticipantDAO = new EventParticipantDAO();
       } catch (Exception e) {
           System.err.println("Error during servlet initialization: " + e.getMessage());
           e.printStackTrace();
           throw new ServletException("Failed to initialize servlet: " + e.getMessage(), e);
       }
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
          case "/respond-to-request":
              showRespondToRequest(request, response);
              break;
          case "/db-test":
              testDatabase(request, response);
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
          case "/respond-to-request":
              respondToRequest(request, response);
              break;
          default:
              response.sendError(HttpServletResponse.SC_NOT_FOUND);
              break;
      }
  }

  // Special diagnostic endpoint to test database connection
  private void testDatabase(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      StringBuilder result = new StringBuilder();
      
      // Test database connection
      result.append("<h2>Database Connection Test</h2>");
      try {
          boolean connectionOk = DBConnection.testConnection();
          result.append("<p>Connection test: ").append(connectionOk ? "SUCCESS" : "FAILED").append("</p>");
          
          // Get diagnostic report
          result.append("<h2>Database Diagnostic Report</h2>");
          result.append("<p>Connection status: ").append(connectionOk ? "Connected" : "Not connected").append("</p>");
          
          // List tables in the database
          List<String> tables = appointmentDAO.listAllTables();
          result.append("<h3>Tables in Database</h3>");
          result.append("<ul>");
          for (String table : tables) {
              result.append("<li>").append(table).append("</li>");
          }
          result.append("</ul>");
          
          // Test appointments table
          result.append("<h2>Appointments Table Test</h2>");
          boolean tableExists = appointmentDAO.checkTableExists();
          result.append("<p>Appointments table exists: ").append(tableExists ? "YES" : "NO").append("</p>");
          
          if (tableExists) {
              result.append("<h3>Table Structure</h3>");
              result.append("<pre>").append(appointmentDAO.describeTable()).append("</pre>");
          }
          
      } catch (Exception e) {
          result.append("<h2>Error</h2>");
          result.append("<p>").append(e.getMessage()).append("</p>");
          result.append("<pre>");
          for (StackTraceElement element : e.getStackTrace()) {
              result.append(element.toString()).append("\n");
          }
          result.append("</pre>");
      }
      
      response.setContentType("text/html");
      response.getWriter().write("<html><body>" + result.toString() + "</body></html>");
  }

  private void showDashboard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      try {
          HttpSession session = request.getSession();
          Integer donorId = (Integer) session.getAttribute("donorId");
          Integer userId = (Integer) session.getAttribute("userId");
          
          if (donorId == null || userId == null) {
              // If session attributes are missing, redirect to login
              response.sendRedirect(request.getContextPath() + "/auth/login");
              return;
          }
          
          // Get donor information
          Donor donor = donorDAO.getDonorById(donorId);
          User user = userDAO.getUserById(userId);
          
          if (donor == null || user == null) {
              // If donor or user not found, redirect to login
              session.invalidate(); // Clear invalid session
              response.sendRedirect(request.getContextPath() + "/auth/login");
              return;
          }
          
          // Get upcoming appointments
          List<Appointment> upcomingAppointments = appointmentDAO.getUpcomingAppointmentsForDonor(donorId);
          if (upcomingAppointments == null) {
              upcomingAppointments = new ArrayList<>();
          }
          
          // Get donation history count
          Integer donationCount = donor.getDonationCount();
          if (donationCount == null) {
              donationCount = 0; // Default to 0 if null
          }
          
          // Get upcoming events
          List<DonationEvent> upcomingEvents = donationEventDAO.getUpcomingDonationEvents();
          if (upcomingEvents == null) {
              upcomingEvents = new ArrayList<>();
          }
          
          // Set attributes for the dashboard
          request.setAttribute("donor", donor);
          request.setAttribute("user", user);
          request.setAttribute("upcomingAppointments", upcomingAppointments);
          request.setAttribute("donationCount", donationCount);
          request.setAttribute("upcomingEvents", upcomingEvents);
          
          // Forward to the dashboard page
          request.getRequestDispatcher("/view/donor/dashboard.jsp").forward(request, response);
      } catch (Exception e) {
          // Log the exception
          e.printStackTrace();
          
          // Set error message
          request.setAttribute("error", "An error occurred while loading the dashboard. Please try again later.");
          
          // Forward to error page or dashboard with error
          request.getRequestDispatcher("/view/donor/dashboard.jsp").forward(request, response);
      }
  }

  private void showProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      HttpSession session = request.getSession();
      int donorId = (int) session.getAttribute("donorId");
      int userId = (int) session.getAttribute("userId");
      
      Donor donor = donorDAO.getDonorById(donorId);
      User user = userDAO.getUserById(userId);
      
      request.setAttribute("donor", donor);
      request.setAttribute("user", user);
      
      request.getRequestDispatcher("/view/donor/profile.jsp").forward(request, response);
  }

  // Updated updateProfile method to allow donors to edit their profiles
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
      
      // Get availability status - default to true if not specified
      String availableStr = request.getParameter("available");
      boolean available = (availableStr != null); // Checkbox is only sent when checked
      
      // Always set available to true for donors
      available = true;
      
      System.out.println("Updating donor profile - Name: " + name + ", Email: " + email + 
                         ", Available: " + available + ", Blood Group: " + bloodGroup);
      
      // Validate input
      boolean hasError = false;
      
      if (!ValidationUtil.isNotEmpty(name)) {
          request.setAttribute("nameError", "Name is required");
          hasError = true;
      }
      
      if (!ValidationUtil.isValidEmail(email)) {
          request.setAttribute("emailError", "Valid email is required");
          hasError = true;
      }
      
      if (!ValidationUtil.isValidPhone(phone)) {
          request.setAttribute("phoneError", "Valid phone number is required");
          hasError = true;
      }
      
      if (!ValidationUtil.isNotEmpty(address)) {
          request.setAttribute("addressError", "Address is required");
          hasError = true;
      }
      
      if (!ValidationUtil.isValidBloodGroup(bloodGroup)) {
          request.setAttribute("bloodGroupError", "Valid blood group is required");
          hasError = true;
      }
      
      if (hasError) {
          // Get current donor and user data
          Donor donor = donorDAO.getDonorById(donorId);
          User user = userDAO.getUserById(userId);
          
          // Set attributes for the form
          request.setAttribute("donor", donor);
          request.setAttribute("user", user);
          
          // Forward back to the profile page with errors
          request.getRequestDispatcher("/view/donor/profile.jsp").forward(request, response);
          return;
      }
      
      try {
          // Update user information
          User user = userDAO.getUserById(userId);
          user.setName(name);
          user.setEmail(email);
          user.setPhone(phone);
          user.setAddress(address);
          
          boolean userUpdated = userDAO.updateUser(user);
          
          // Update donor information
          Donor donor = donorDAO.getDonorById(donorId);
          donor.setBloodGroup(bloodGroup);
          donor.setLocation(address);
          
          if (medicalHistory != null) {
              donor.setMedicalHistory(medicalHistory);
          }
          
          // Always set donor as available
          donor.setAvailable(true);
          
          boolean donorUpdated = donorDAO.updateDonor(donor);
          
          if (userUpdated && donorUpdated) {
              request.setAttribute("success", "Profile updated successfully");
              System.out.println("Profile updated successfully for donor ID: " + donorId);
          } else {
              request.setAttribute("error", "Failed to update profile");
              System.err.println("Failed to update profile for donor ID: " + donorId);
          }
          
          // Refresh donor and user data
          donor = donorDAO.getDonorById(donorId);
          user = userDAO.getUserById(userId);
          
          request.setAttribute("donor", donor);
          request.setAttribute("user", user);
          
          // Forward to the profile page
          request.getRequestDispatcher("/view/donor/profile.jsp").forward(request, response);
          
      } catch (Exception e) {
          e.printStackTrace();
          request.setAttribute("error", "An error occurred: " + e.getMessage());
          
          // Get current donor and user data
          Donor donor = donorDAO.getDonorById(donorId);
          User user = userDAO.getUserById(userId);
          
          request.setAttribute("donor", donor);
          request.setAttribute("user", user);
          
          request.getRequestDispatcher("/view/donor/profile.jsp").forward(request, response);
      }
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
      
      System.out.println("Scheduling appointment - Date: " + appointmentDateStr + ", Time: " + appointmentTimeStr);
      
      // Validate input
      boolean hasError = false;
      
      if (appointmentDateStr == null || appointmentDateStr.trim().isEmpty()) {
          request.setAttribute("dateError", "Appointment date is required");
          hasError = true;
      }
      
      if (appointmentTimeStr == null || appointmentTimeStr.trim().isEmpty()) {
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
          
          // Make sure the time is in the correct format (HH:MM:SS)
          if (!appointmentTimeStr.contains(":")) {
              appointmentTimeStr += ":00";
          } else if (appointmentTimeStr.split(":").length < 3) {
              // If it has one colon but not seconds (HH:MM), add seconds
              appointmentTimeStr += ":00";
          }
          
          System.out.println("Formatted time: " + appointmentTimeStr);
          Time appointmentTime = null;
          
          try {
              appointmentTime = Time.valueOf(appointmentTimeStr);
          } catch (IllegalArgumentException e) {
              System.err.println("Invalid time format: " + appointmentTimeStr);
              System.err.println("Error details: " + e.getMessage());
              request.setAttribute("timeError", "Invalid time format. Please use HH:MM format.");
              request.setAttribute("appointmentDate", appointmentDateStr);
              request.setAttribute("notes", notes);
              request.getRequestDispatcher("/view/donor/schedule-appointment.jsp").forward(request, response);
              return;
          }
          
          System.out.println("Creating appointment with date: " + appointmentDate + ", time: " + appointmentTime);
          
          // Create appointment
          Appointment appointment = new Appointment();
          appointment.setDonorId(donorId);
          appointment.setAppointmentDate(appointmentDate);
          appointment.setAppointmentTime(appointmentTime);
          appointment.setStatus("scheduled");
          appointment.setNotes(notes);
          
          // Add diagnostic information to the request
          StringBuilder diagnosticInfo = new StringBuilder();
          try {
              String connectionStatus = DBConnection.testConnection() ? "Connected" : "Not connected";
              List<String> tables = appointmentDAO.listAllTables();
              
              diagnosticInfo.append("Connection status: ").append(connectionStatus).append("\n");
              diagnosticInfo.append("Tables in database: ").append(String.join(", ", tables)).append("\n");
              
              request.setAttribute("connectionStatus", connectionStatus);
              request.setAttribute("tablesList", String.join(", ", tables));
              
              System.out.println("Database connection status: " + connectionStatus);
              System.out.println("Database tables: " + String.join(", ", tables));
          } catch (Exception e) {
              System.err.println("Error getting diagnostic information: " + e.getMessage());
              diagnosticInfo.append("Error getting diagnostic info: ").append(e.getMessage()).append("\n");
              request.setAttribute("connectionStatus", "Error: " + e.getMessage());
          }
          
          // Check database connection before attempting to save
          try {
              if (appointmentDAO == null) {
                  System.err.println("Error: appointmentDAO is null");
                  diagnosticInfo.append("Error: appointmentDAO is null\n");
                  request.setAttribute("error", "System error: Database access not available");
                  request.setAttribute("diagnosticInfo", diagnosticInfo.toString());
                  request.getRequestDispatcher("/view/donor/schedule-appointment.jsp").forward(request, response);
                  return;
              }
              
              // Test database connection
              boolean dbConnected = DBConnection.testConnection();
              if (!dbConnected) {
                  System.err.println("Database connection test failed before saving appointment");
                  diagnosticInfo.append("Database connection test failed\n");
                  request.setAttribute("error", "Database connection error. Please try again later.");
                  request.setAttribute("diagnosticInfo", diagnosticInfo.toString());
                  request.getRequestDispatcher("/view/donor/schedule-appointment.jsp").forward(request, response);
                  return;
              }
              
              // Test if appointments table exists
              boolean tableExists = appointmentDAO.checkTableExists();
              if (!tableExists) {
                  System.err.println("Appointments table does not exist");
                  diagnosticInfo.append("Appointments table does not exist\n");
                  request.setAttribute("error", "Database schema error: Appointments table does not exist");
                  request.setAttribute("diagnosticInfo", diagnosticInfo.toString());
                  request.getRequestDispatcher("/view/donor/schedule-appointment.jsp").forward(request, response);
                  return;
              }
              
              // Test direct database access
              boolean dbAccessOk = appointmentDAO.testDatabaseAccess();
              if (!dbAccessOk) {
                  System.err.println("Database access test failed");
                  diagnosticInfo.append("Database access test failed\n");
                  request.setAttribute("error", "Database access error. Please try again later.");
                  request.setAttribute("diagnosticInfo", diagnosticInfo.toString());
                  request.getRequestDispatcher("/view/donor/schedule-appointment.jsp").forward(request, response);
                  return;
              }
              
              // Check if donor exists
              Donor donor = donorDAO.getDonorById(donorId);
              if (donor == null) {
                  System.err.println("Donor not found with ID: " + donorId);
                  diagnosticInfo.append("Donor not found with ID: ").append(donorId).append("\n");
                  request.setAttribute("error", "Invalid donor information. Please try again later.");
                  request.setAttribute("diagnosticInfo", diagnosticInfo.toString());
                  request.getRequestDispatcher("/view/donor/schedule-appointment.jsp").forward(request, response);
                  return;
              }
              
              diagnosticInfo.append("All pre-checks passed. Attempting to save appointment...\n");
          } catch (Exception e) {
              System.err.println("Error checking database connection: " + e.getMessage());
              e.printStackTrace();
              diagnosticInfo.append("Error checking database: ").append(e.getMessage()).append("\n");
              request.setAttribute("error", "Error checking database connection: " + e.getMessage());
              request.setAttribute("diagnosticInfo", diagnosticInfo.toString());
              request.getRequestDispatcher("/view/donor/schedule-appointment.jsp").forward(request, response);
              return;
          }
      
          System.out.println("Attempting to save appointment...");
          String errorDetails = "";
          StringBuilder errorBuilder = new StringBuilder();
          boolean appointmentCreated = appointmentDAO.addAppointment(appointment, errorBuilder);
          
          if (appointmentCreated) {
              System.out.println("Appointment created successfully with ID: " + appointment.getId());
              
              // Update donor availability if needed
              try {
                  donorDAO.updateDonorAvailability(donorId, false);
              } catch (Exception e) {
                  System.out.println("Note: Could not update donor availability: " + e.getMessage());
                  // Continue even if this fails
              }
              
              request.setAttribute("success", "Appointment scheduled successfully");
          } else {
              System.err.println("Failed to create appointment");
              if (errorBuilder.length() > 0) {
                  System.err.println("Error details: " + errorBuilder.toString());
                  diagnosticInfo.append("Error details: ").append(errorBuilder.toString()).append("\n");
              }
              request.setAttribute("error", "Failed to schedule appointment. Please try again later.");
              request.setAttribute("errorDetails", errorBuilder.toString());
          }
          
          // Add diagnostic info to request
          request.setAttribute("diagnosticInfo", diagnosticInfo.toString());
          
          // Forward to the schedule appointment page
          request.getRequestDispatcher("/view/donor/schedule-appointment.jsp").forward(request, response);
          
      } catch (IllegalArgumentException e) {
          System.err.println("Invalid date/time format: " + e.getMessage());
          e.printStackTrace();
          request.setAttribute("error", "Invalid date or time format: " + e.getMessage());
          request.getRequestDispatcher("/view/donor/schedule-appointment.jsp").forward(request, response);
      } catch (Exception e) {
          System.err.println("Error scheduling appointment: " + e.getMessage());
          e.printStackTrace();
          request.setAttribute("error", "An error occurred: " + e.getMessage());
          request.setAttribute("stackTrace", getStackTraceAsString(e));
          request.getRequestDispatcher("/view/donor/schedule-appointment.jsp").forward(request, response);
      }
  }
  
  // Helper method to get stack trace as string
  private String getStackTraceAsString(Exception e) {
      StringBuilder sb = new StringBuilder();
      sb.append(e.getMessage()).append("\n");
      for (StackTraceElement element : e.getStackTrace()) {
          sb.append(element.toString()).append("\n");
      }
      return sb.toString();
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
      
      // Check if the appointment belongs to the donor
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
      
      // Check if the appointment belongs to the donor
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
      
      if (appointmentDateStr == null || appointmentDateStr.trim().isEmpty()) {
          request.setAttribute("dateError", "Appointment date is required");
          hasError = true;
      }
      
      if (appointmentTimeStr == null || appointmentTimeStr.trim().isEmpty()) {
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
          
          // Make sure the time is in the correct format (HH:MM:SS)
          if (!appointmentTimeStr.contains(":")) {
              appointmentTimeStr += ":00";
          } else if (appointmentTimeStr.split(":").length < 3) {
              // If it has one colon but not seconds (HH:MM), add seconds
              appointmentTimeStr += ":00";
          }
          
          Time appointmentTime = Time.valueOf(appointmentTimeStr);
          
          // Update appointment
          appointment.setAppointmentDate(appointmentDate);
          appointment.setAppointmentTime(appointmentTime);
          appointment.setNotes(notes);
          
          boolean appointmentUpdated = appointmentDAO.updateAppointment(appointment);
          
          if (appointmentUpdated) {
              response.sendRedirect(request.getContextPath() + "/donor/dashboard?success=true");
          } else {
              request.setAttribute("error", "Failed to reschedule appointment");
              request.setAttribute("appointment", appointment);
              request.getRequestDispatcher("/view/donor/reschedule-appointment.jsp").forward(request, response);
          }
          
      } catch (IllegalArgumentException e) {
          request.setAttribute("error", "Invalid date or time format: " + e.getMessage());
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
      
      // Check if the appointment belongs to the donor
      HttpSession session = request.getSession();
      int donorId = (int) session.getAttribute("donorId");
      
      if (appointment.getDonorId() != donorId) {
          response.sendRedirect(request.getContextPath() + "/donor/dashboard");
          return;
      }
      
      // Update appointment status to cancelled
      appointment.setStatus("cancelled");
      
      boolean appointmentUpdated = appointmentDAO.updateAppointment(appointment);
      
      if (appointmentUpdated) {
          // If the appointment was for a blood request, update the blood request status back to pending
          Integer bloodRequestId = appointment.getBloodRequestId();
          if (bloodRequestId != null) {
              BloodRequest bloodRequest = bloodRequestDAO.getBloodRequestById(bloodRequestId);
              if (bloodRequest != null && "in-progress".equals(bloodRequest.getStatus())) {
                  bloodRequest.setStatus("pending");
                  bloodRequestDAO.updateBloodRequest(bloodRequest);
              }
          }
          
          response.sendRedirect(request.getContextPath() + "/donor/dashboard?success=true");
      } else {
          response.sendRedirect(request.getContextPath() + "/donor/dashboard?error=true");
      }
  }

  private void showDonationHistory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      HttpSession session = request.getSession();
      int donorId = (int) session.getAttribute("donorId");
      
      // Get all appointments for this donor, not just completed ones
      List<Appointment> allAppointments = appointmentDAO.getAppointmentsByDonorId(donorId);
      
      // If no appointments were found, initialize an empty list to avoid null pointer exceptions
      if (allAppointments == null) {
          allAppointments = new ArrayList<>();
      }
      
      request.setAttribute("appointments", allAppointments);
      
      // Forward to the donation history page
      request.getRequestDispatcher("/view/donor/donation-history.jsp").forward(request, response);
  }

  private void showEvents(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      // Get all upcoming donation events
      List<DonationEvent> events = donationEventDAO.getUpcomingDonationEvents();
      
      // Get donor's registered events
      HttpSession session = request.getSession();
      int userId = (int) session.getAttribute("userId");
      
      List<Integer> registeredEventIds = new ArrayList<>();
      List<EventParticipant> participations = eventParticipantDAO.getParticipantsByUserId(userId);
      
      for (EventParticipant participation : participations) {
          registeredEventIds.add(participation.getEventId());
      }
      
      request.setAttribute("events", events);
      request.setAttribute("registeredEventIds", registeredEventIds);
      
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
      
      // Check if donor is registered for this event
      HttpSession session = request.getSession();
      int userId = (int) session.getAttribute("userId");
      
      boolean isRegistered = eventParticipantDAO.isUserRegisteredForEvent(userId, eventId);
      
      request.setAttribute("event", event);
      request.setAttribute("isRegistered", isRegistered);
      
      // Forward to the event view page
      request.getRequestDispatcher("/view/donor/events/view.jsp").forward(request, response);
  }

  private void registerForEvent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
      
      // Register donor for the event
      HttpSession session = request.getSession();
      int userId = (int) session.getAttribute("userId");
      
      EventParticipant participant = new EventParticipant();
      participant.setEventId(eventId);
      participant.setUserId(userId);
      participant.setRegistrationDate(new Timestamp(System.currentTimeMillis()));
      participant.setStatus("registered");
      
      boolean registered = eventParticipantDAO.addParticipant(participant);
      
      if (registered) {
          response.sendRedirect(request.getContextPath() + "/donor/events/view?id=" + eventId + "&success=true");
      } else {
          response.sendRedirect(request.getContextPath() + "/donor/events/view?id=" + eventId + "&error=true");
      }
  }

  private void cancelEventRegistration(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
      
      // Cancel donor's registration for the event
      HttpSession session = request.getSession();
      int userId = (int) session.getAttribute("userId");
      
      boolean cancelled = eventParticipantDAO.cancelEventParticipation(userId, eventId);
      
      if (cancelled) {
          response.sendRedirect(request.getContextPath() + "/donor/events/view?id=" + eventId + "&success=true");
      } else {
          response.sendRedirect(request.getContextPath() + "/donor/events/view?id=" + eventId + "&error=true");
      }
  }

  // Method to show the respond to request form
  private void showRespondToRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String requestIdStr = request.getParameter("id");
      
      if (!ValidationUtil.isPositiveNumeric(requestIdStr)) {
          response.sendRedirect(request.getContextPath() + "/donor/view-requests");
          return;
      }
      
      int requestId = Integer.parseInt(requestIdStr);
      
      // Get blood request
      BloodRequest bloodRequest = bloodRequestDAO.getBloodRequestById(requestId);
      
      if (bloodRequest == null) {
          response.sendRedirect(request.getContextPath() + "/donor/view-requests");
          return;
      }
      
      // Check if the blood group matches the donor's blood group
      HttpSession session = request.getSession();
      int donorId = (int) session.getAttribute("donorId");
      
      Donor donor = donorDAO.getDonorById(donorId);
      
      if (!bloodRequest.getBloodGroup().equals(donor.getBloodGroup())) {
          response.sendRedirect(request.getContextPath() + "/donor/view-requests");
          return;
      }
      
      // Set blood request attribute
      request.setAttribute("bloodRequest", bloodRequest);
      
      // Get requester information
      User requester = userDAO.getUserById(bloodRequest.getUserId());
      request.setAttribute("requester", requester);
      
      // Forward to the respond to request page
      request.getRequestDispatcher("/view/donor/respond-to-request.jsp").forward(request, response);
  }
  
  // Method to handle responding to a blood request
  private void respondToRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       try {
           HttpSession session = request.getSession();
           int userId = (int) session.getAttribute("userId");
           
           // Get donor information
           Donor donor = donorDAO.getDonorByUserId(userId);
           if (donor == null) {
               request.setAttribute("error", "Donor profile not found. Please complete your profile first.");
               showViewRequests(request, response);
               return;
           }
           
           // Get request ID
           String requestIdStr = request.getParameter("requestId");
           if (!ValidationUtil.isPositiveNumeric(requestIdStr)) {
               request.setAttribute("error", "Invalid request ID.");
               showViewRequests(request, response);
               return;
           }
           
           int requestId = Integer.parseInt(requestIdStr);
           
           // Get blood request
           BloodRequest bloodRequest = bloodRequestDAO.getBloodRequestById(requestId);
           if (bloodRequest == null) {
               request.setAttribute("error", "Blood request not found.");
               showViewRequests(request, response);
               return;
           }
           
           // Check if donor's blood group matches the request
           if (!bloodRequest.getBloodGroup().equals(donor.getBloodGroup())) {
               request.setAttribute("error", "Your blood group is not compatible with this request.");
               showViewRequests(request, response);
               return;
           }
           
           // Get form data
           String appointmentDateStr = request.getParameter("appointmentDate");
           String appointmentTimeStr = request.getParameter("appointmentTime");
           String notes = request.getParameter("notes");
           
           // Validate required fields
           if (!ValidationUtil.isNotEmpty(appointmentDateStr) || !ValidationUtil.isNotEmpty(appointmentTimeStr)) {
               request.setAttribute("error", "Date and time are required.");
               request.setAttribute("bloodRequest", bloodRequest);
               request.getRequestDispatcher("/view/donor/respond-to-request.jsp").forward(request, response);
               return;
           }
           
           // Parse date and time
           Date appointmentDate;
           Time appointmentTime;
           
           try {
               appointmentDate = Date.valueOf(appointmentDateStr);
               
               // Ensure time is in the correct format (HH:MM:SS)
               if (!appointmentTimeStr.matches("\\d{2}:\\d{2}:\\d{2}")) {
                   if (appointmentTimeStr.matches("\\d{2}:\\d{2}")) {
                       appointmentTimeStr += ":00";
                   } else {
                       throw new IllegalArgumentException("Time format must be HH:MM or HH:MM:SS");
                   }
               }
               
               appointmentTime = Time.valueOf(appointmentTimeStr);
           } catch (IllegalArgumentException e) {
               request.setAttribute("error", "Invalid date or time format: " + e.getMessage());
               request.setAttribute("bloodRequest", bloodRequest);
               request.getRequestDispatcher("/view/donor/respond-to-request.jsp").forward(request, response);
               return;
           }
           
           // Create appointment
           Appointment appointment = new Appointment();
           appointment.setDonorId(donor.getId());
           appointment.setAppointmentDate(appointmentDate);
           appointment.setAppointmentTime(appointmentTime);
           appointment.setStatus("scheduled");
           
           // Explicitly set the blood request ID
           appointment.setBloodRequestId(requestId);
           
           // Add blood request ID to notes as well for backward compatibility
           String bloodRequestTag = "[BloodRequestID:" + requestId + "]";
           if (notes == null || notes.isEmpty()) {
               notes = bloodRequestTag;
           } else {
               notes = bloodRequestTag + " " + notes;
           }
           appointment.setNotes(notes);
           
           System.out.println("Creating appointment for donor ID: " + donor.getId() + 
                             ", blood request ID: " + requestId + 
                             ", date: " + appointmentDate + 
                             ", time: " + appointmentTime);
           
           // Save appointment with detailed error tracking
           StringBuilder errorDetails = new StringBuilder();
           boolean success = appointmentDAO.addAppointment(appointment, errorDetails);
           
           if (success) {
               System.out.println("Appointment created successfully with ID: " + appointment.getId());
               
               // Update blood request status if it's pending
               if ("pending".equals(bloodRequest.getStatus())) {
                   bloodRequestDAO.updateBloodRequestStatus(requestId, "in-progress");
                   System.out.println("Updated blood request status to in-progress");
               }
               
               // Update donor availability
               donorDAO.updateDonorAvailability(donor.getId(), false);
               
               // Redirect to view requests page after successful response
               response.sendRedirect(request.getContextPath() + "/donor/view-requests?success=true");
           } else {
               System.out.println("Failed to create appointment. Error details: " + errorDetails.toString());
               request.setAttribute("error", "Failed to schedule appointment: " + errorDetails.toString());
               request.setAttribute("bloodRequest", bloodRequest);
               request.getRequestDispatcher("/view/donor/respond-to-request.jsp").forward(request, response);
           }
       } catch (Exception e) {
           e.printStackTrace();
           request.setAttribute("error", "An error occurred: " + e.getMessage());
           showViewRequests(request, response);
       }
   }

   private void showViewRequests(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       viewRequests(request, response);
   }
}
