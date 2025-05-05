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
import java.time.LocalDateTime;
import java.util.List;
import java.sql.SQLException;

@WebServlet("/appointment/*")
public class AppointmentController extends HttpServlet {
  private AppointmentDAO appointmentDAO;
  private DonorDAO donorDAO;
  private BloodInventoryDAO bloodInventoryDAO;
  private BloodRequestDAO bloodRequestDAO;

  @Override
  public void init() throws ServletException {
      appointmentDAO = new AppointmentDAO();
      donorDAO = new DonorDAO();
      bloodInventoryDAO = new BloodInventoryDAO();
      bloodRequestDAO = new BloodRequestDAO();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String pathInfo = request.getPathInfo();
      
      if (pathInfo == null || pathInfo.equals("/")) {
          response.sendError(HttpServletResponse.SC_BAD_REQUEST);
          return;
      }
      
      switch (pathInfo) {
          case "/view":
              viewAppointment(request, response);
              break;
          case "/list":
              listAppointments(request, response);
              break;
          case "/schedule":
              showScheduleForm(request, response);
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
          case "/complete":
              completeAppointment(request, response);
              break;
          case "/cancel":
              cancelAppointment(request, response);
              break;
          case "/reschedule":
              rescheduleAppointment(request, response);
              break;
          case "/schedule":
              scheduleAppointment(request, response);
              break;
          default:
              response.sendError(HttpServletResponse.SC_NOT_FOUND);
              break;
      }
  }

  private void showScheduleForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
        // Get form parameters
        String appointmentDateStr = request.getParameter("appointmentDate");
        String appointmentTimeStr = request.getParameter("appointmentTime");
        String notes = request.getParameter("notes");
        String bloodRequestIdStr = request.getParameter("bloodRequestId");
        
        System.out.println("Scheduling appointment with date: " + appointmentDateStr + ", time: " + appointmentTimeStr);
        
        // Validate required fields
        if (!ValidationUtil.isNotEmpty(appointmentDateStr) || !ValidationUtil.isNotEmpty(appointmentTimeStr)) {
            request.setAttribute("error", "Date and time are required");
            request.getRequestDispatcher("/view/user/schedule-appointment.jsp").forward(request, response);
            return;
        }
        
        // Parse date and time
        Date appointmentDate = Date.valueOf(appointmentDateStr);
        
        // Ensure time is in proper format (HH:MM:SS)
        if (!appointmentTimeStr.matches("\\d{1,2}:\\d{2}:\\d{2}")) {
            if (appointmentTimeStr.matches("\\d{1,2}:\\d{2}")) {
                appointmentTimeStr += ":00";
            } else {
                System.err.println("Invalid time format: " + appointmentTimeStr);
                request.setAttribute("error", "Invalid time format. Please use HH:MM:SS format.");
                request.getRequestDispatcher("/view/user/schedule-appointment.jsp").forward(request, response);
                return;
            }
        }
        
        Time appointmentTime = Time.valueOf(appointmentTimeStr);
        System.out.println("Parsed time: " + appointmentTime);
        
        // Create appointment object
        Appointment appointment = new Appointment();
        
        // Set donor ID based on user role
        if ("donor".equals(userRole)) {
            Integer donorId = (Integer) session.getAttribute("donorId");
            if (donorId == null) {
                // Try to get donor by user ID
                Donor donor = donorDAO.getDonorByUserId(userId);
                if (donor != null) {
                    donorId = donor.getId();
                    session.setAttribute("donorId", donorId);
                } else {
                    request.setAttribute("error", "Donor profile not found. Please complete your donor profile first.");
                    request.getRequestDispatcher("/view/user/schedule-appointment.jsp").forward(request, response);
                    return;
                }
            }
            appointment.setDonorId(donorId);
            System.out.println("Setting donor ID: " + donorId);
        } else {
            // For regular users, they need to be a donor to schedule
            Donor donor = donorDAO.getDonorByUserId(userId);
            if (donor == null) {
                request.setAttribute("error", "You need to register as a donor before scheduling appointments.");
                request.getRequestDispatcher("/view/user/schedule-appointment.jsp").forward(request, response);
                return;
            }
            appointment.setDonorId(donor.getId());
            System.out.println("Setting donor ID from user: " + donor.getId());
        }
        
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
                
                // Update blood request status if it's pending
                if ("pending".equals(bloodRequest.getStatus())) {
                    bloodRequestDAO.updateBloodRequestStatus(bloodRequestId, "in-progress");
                }
            }
        }
        
        // Test database connection before saving
        boolean connectionOk = appointmentDAO.testDatabaseAccess();
        if (!connectionOk) {
            System.err.println("Database connection test failed");
            request.setAttribute("error", "Database connection error. Please try again later.");
            request.getRequestDispatcher("/view/user/schedule-appointment.jsp").forward(request, response);
            return;
        }
        
        // Save appointment
        System.out.println("Saving appointment: " + appointment);
        boolean success = appointmentDAO.addAppointment(appointment);
        
        if (success) {
            System.out.println("Appointment scheduled successfully with ID: " + appointment.getId());
            
            // If donor, update availability
            if ("donor".equals(userRole)) {
                Integer donorId = (Integer) session.getAttribute("donorId");
                if (donorId != null) {
                    donorDAO.updateDonorAvailability(donorId, false);
                }
            }
            
            request.setAttribute("success", "Appointment scheduled successfully");
            
            // Redirect based on user role
            if ("donor".equals(userRole)) {
                response.sendRedirect(request.getContextPath() + "/donor/dashboard?success=Appointment scheduled successfully");
            } else if ("admin".equals(userRole)) {
                response.sendRedirect(request.getContextPath() + "/admin/manage-appointments?success=Appointment scheduled successfully");
            } else {
                response.sendRedirect(request.getContextPath() + "/user/dashboard?success=Appointment scheduled successfully");
            }
        } else {
            System.err.println("Failed to schedule appointment");
            request.setAttribute("error", "Failed to schedule appointment. Please try again later.");
            request.getRequestDispatcher("/view/user/schedule-appointment.jsp").forward(request, response);
        }
    } catch (IllegalArgumentException e) {
        System.err.println("Invalid date or time format: " + e.getMessage());
        e.printStackTrace();
        request.setAttribute("error", "Invalid date or time format: " + e.getMessage());
        request.getRequestDispatcher("/view/user/schedule-appointment.jsp").forward(request, response);
    } catch (Exception e) {
        System.err.println("Unexpected error: " + e.getMessage());
        e.printStackTrace();
        request.setAttribute("error", "An unexpected error occurred: " + e.getMessage());
        request.getRequestDispatcher("/view/user/schedule-appointment.jsp").forward(request, response);
    }
}

  private void viewAppointment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String appointmentIdStr = request.getParameter("id");
        
        if (!ValidationUtil.isPositiveNumeric(appointmentIdStr)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        int appointmentId = Integer.parseInt(appointmentIdStr);
        
        // Get appointment
        Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);
        
        if (appointment == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // Get donor information
        Donor donor = donorDAO.getDonorById(appointment.getDonorId());
        
        // Get blood request information if available
        BloodRequest bloodRequest = null;
        if (appointment.getBloodRequestId() != null) {
            bloodRequest = bloodRequestDAO.getBloodRequestById(appointment.getBloodRequestId());
        }
        
        request.setAttribute("appointment", appointment);
        request.setAttribute("donor", donor);
        request.setAttribute("bloodRequest", bloodRequest);
        
        // Determine the appropriate view based on user role
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        if ("admin".equals(userRole)) {
            request.getRequestDispatcher("/view/admin/manage-appointments.jsp").forward(request, response);
        } else if ("donor".equals(userRole)) {
            int donorId = (int) session.getAttribute("donorId");
            
            // Check if the appointment belongs to the donor
            if (appointment.getDonorId() != donorId) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            
            request.getRequestDispatcher("/view/donor/reschedule-appointment.jsp").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    private void listAppointments(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        if ("admin".equals(userRole)) {
            // Get all appointments for admin
            List<Appointment> appointments = appointmentDAO.getAllAppointments();
            request.setAttribute("appointments", appointments);
            
            // For each appointment, get the donor information
            for (Appointment appointment : appointments) {
                Donor donor = donorDAO.getDonorById(appointment.getDonorId());
                request.setAttribute("donor_" + appointment.getId(), donor);
            }
            
            request.getRequestDispatcher("/view/admin/manage-appointments.jsp").forward(request, response);
        } else if ("donor".equals(userRole)) {
            int donorId = (int) session.getAttribute("donorId");
            
            // Get appointments for the donor
            List<Appointment> appointments = appointmentDAO.getAppointmentsByDonorId(donorId);
            request.setAttribute("appointments", appointments);
            
            request.getRequestDispatcher("/view/donor/donation-history.jsp").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    private void completeAppointment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        if (!"admin".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        String appointmentIdStr = request.getParameter("id");
        String bloodGroupStr = request.getParameter("bloodGroup");
        String quantityStr = request.getParameter("quantity");
        
        if (!ValidationUtil.isPositiveNumeric(appointmentIdStr) || 
            !ValidationUtil.isValidBloodGroup(bloodGroupStr) || 
            !ValidationUtil.isPositiveNumeric(quantityStr)) {
            request.setAttribute("error", "Invalid input parameters");
            request.getRequestDispatcher("/view/admin/manage-appointments.jsp").forward(request, response);
            return;
        }
        
        int appointmentId = Integer.parseInt(appointmentIdStr);
        int quantity = Integer.parseInt(quantityStr);
        
        // Get appointment
        Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);
        
        if (appointment == null) {
            request.setAttribute("error", "Appointment not found");
            request.getRequestDispatcher("/view/admin/manage-appointments.jsp").forward(request, response);
            return;
        }
        
        // Update appointment status
        boolean appointmentUpdated = appointmentDAO.updateAppointmentStatus(appointmentId, "completed");
        
        if (!appointmentUpdated) {
            request.setAttribute("error", "Failed to update appointment status");
            request.getRequestDispatcher("/view/admin/manage-appointments.jsp").forward(request, response);
            return;
        }
        
        // Get donor
        Donor donor = donorDAO.getDonorById(appointment.getDonorId());
        
        // Update donor's last donation date and increment donation count
        boolean donorUpdated = donorDAO.incrementDonationCount(donor.getId());
        
        if (!donorUpdated) {
            request.setAttribute("error", "Failed to update donor information");
            request.getRequestDispatcher("/view/admin/manage-appointments.jsp").forward(request, response);
            return;
        }
        
        // Add blood to inventory
        BloodInventory inventory = new BloodInventory();
        inventory.setBloodGroup(bloodGroupStr);
        inventory.setQuantity(quantity);
        inventory.setCollectionDate(new Date(System.currentTimeMillis()));
        
        // Set expiry date to 42 days from now (typical shelf life of blood)
        long expiryMillis = System.currentTimeMillis() + (42L * 24 * 60 * 60 * 1000);
        inventory.setExpiryDate(new Date(expiryMillis));
        
        inventory.setStatus("available");
        inventory.setDonorId(donor.getId());
        inventory.setLocation(donor.getLocation());
        
        boolean inventoryAdded = bloodInventoryDAO.addBloodInventory(inventory);
        
        if (!inventoryAdded) {
            request.setAttribute("error", "Failed to add blood to inventory");
            request.getRequestDispatcher("/view/admin/manage-appointments.jsp").forward(request, response);
            return;
        }
        
        request.setAttribute("success", "Appointment completed successfully and blood added to inventory");
        response.sendRedirect(request.getContextPath() + "/admin/manage-appointments");
    }

    private void cancelAppointment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String appointmentIdStr = request.getParameter("id");
        
        if (!ValidationUtil.isPositiveNumeric(appointmentIdStr)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        int appointmentId = Integer.parseInt(appointmentIdStr);
        
        // Get appointment
        Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);
        
        if (appointment == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // Check authorization
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        if ("admin".equals(userRole)) {
            // Admin can cancel any appointment
            boolean appointmentCancelled = appointmentDAO.updateAppointmentStatus(appointmentId, "cancelled");
            
            if (appointmentCancelled) {
                request.setAttribute("success", "Appointment cancelled successfully");
            } else {
                request.setAttribute("error", "Failed to cancel appointment");
            }
            
            response.sendRedirect(request.getContextPath() + "/admin/manage-appointments");
        } else if ("donor".equals(userRole)) {
            int donorId = (int) session.getAttribute("donorId");
            
            // Check if the appointment belongs to the donor
            if (appointment.getDonorId() != donorId) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            
            boolean appointmentCancelled = appointmentDAO.updateAppointmentStatus(appointmentId, "cancelled");
            
            if (appointmentCancelled) {
                request.setAttribute("success", "Appointment cancelled successfully");
            } else {
                request.setAttribute("error", "Failed to cancel appointment");
            }
            
            response.sendRedirect(request.getContextPath() + "/donor/dashboard");
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    private void rescheduleAppointment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String appointmentIdStr = request.getParameter("id");
        String appointmentDateStr = request.getParameter("appointmentDate");
        String appointmentTimeStr = request.getParameter("appointmentTime");
        String notes = request.getParameter("notes");
        
        if (!ValidationUtil.isPositiveNumeric(appointmentIdStr) || 
            !ValidationUtil.isNotEmpty(appointmentDateStr) || 
            !ValidationUtil.isNotEmpty(appointmentTimeStr)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        int appointmentId = Integer.parseInt(appointmentIdStr);
        
        // Get appointment
        Appointment appointment = appointmentDAO.getAppointmentById(appointmentId);
        
        if (appointment == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // Check authorization
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        if ("admin".equals(userRole)) {
            // Admin can reschedule any appointment
            try {
                Date appointmentDate = Date.valueOf(appointmentDateStr);
                Time appointmentTime = Time.valueOf(appointmentTimeStr + ":00");
                
                appointment.setAppointmentDate(appointmentDate);
                appointment.setAppointmentTime(appointmentTime);
                appointment.setNotes(notes);
                
                boolean appointmentUpdated = appointmentDAO.updateAppointment(appointment);
                
                if (appointmentUpdated) {
                    request.setAttribute("success", "Appointment rescheduled successfully");
                } else {
                    request.setAttribute("error", "Failed to reschedule appointment");
                }
                
                response.sendRedirect(request.getContextPath() + "/admin/manage-appointments");
            } catch (IllegalArgumentException e) {
                request.setAttribute("error", "Invalid date or time format");
                request.getRequestDispatcher("/view/admin/manage-appointments.jsp").forward(request, response);
            }
        } else if ("donor".equals(userRole)) {
            int donorId = (int) session.getAttribute("donorId");
            
            // Check if the appointment belongs to the donor
            if (appointment.getDonorId() != donorId) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            
            try {
                Date appointmentDate = Date.valueOf(appointmentDateStr);
                Time appointmentTime = Time.valueOf(appointmentTimeStr + ":00");
                
                appointment.setAppointmentDate(appointmentDate);
                appointment.setAppointmentTime(appointmentTime);
                appointment.setNotes(notes);
                
                boolean appointmentUpdated = appointmentDAO.updateAppointment(appointment);
                
                if (appointmentUpdated) {
                    request.setAttribute("success", "Appointment rescheduled successfully");
                } else {
                    request.setAttribute("error", "Failed to reschedule appointment");
                }
                
                response.sendRedirect(request.getContextPath() + "/donor/dashboard");
            } catch (IllegalArgumentException e) {
                request.setAttribute("error", "Invalid date or time format");
                request.getRequestDispatcher("/view/donor/reschedule-appointment.jsp").forward(request, response);
            }
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
