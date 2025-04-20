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

@WebServlet("/events/*")
public class DonationEventController extends HttpServlet {
    private DonationEventDAO donationEventDAO;
    private EventParticipantDAO eventParticipantDAO;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        donationEventDAO = new DonationEventDAO();
        eventParticipantDAO = new EventParticipantDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/events/list");
            return;
        }
        
        switch (pathInfo) {
            case "/list":
                listEvents(request, response);
                break;
            case "/view":
                viewEvent(request, response);
                break;
            case "/add":
                showAddEvent(request, response);
                break;
            case "/edit":
                showEditEvent(request, response);
                break;
            case "/participants":
                listParticipants(request, response);
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
            case "/add":
                addEvent(request, response);
                break;
            case "/edit":
                updateEvent(request, response);
                break;
            case "/delete":
                deleteEvent(request, response);
                break;
            case "/register":
                registerForEvent(request, response);
                break;
            case "/cancel-registration":
                cancelRegistration(request, response);
                break;
            case "/remove-participant":
                removeParticipant(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    private void listEvents(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        List<DonationEvent> events;
        
        // Get filter parameter
        String filter = request.getParameter("filter");
        
        if ("past".equals(filter)) {
            events = donationEventDAO.getPastDonationEvents();
        } else {
            events = donationEventDAO.getUpcomingDonationEvents();
        }
        
        request.setAttribute("events", events);
        request.setAttribute("filter", filter);
        
        // Forward to the appropriate view based on user role
        if ("admin".equals(userRole)) {
            request.getRequestDispatcher("/view/admin/events/index.jsp").forward(request, response);
        } else if ("donor".equals(userRole)) {
            request.getRequestDispatcher("/view/donor/events/index.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/view/user/events/index.jsp").forward(request, response);
        }
    }

    private void viewEvent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String eventIdStr = request.getParameter("id");
        
        if (!ValidationUtil.isPositiveNumeric(eventIdStr)) {
            response.sendRedirect(request.getContextPath() + "/events/list");
            return;
        }
        
        int eventId = Integer.parseInt(eventIdStr);
        
        // Get event
        DonationEvent event = donationEventDAO.getDonationEventById(eventId);
        
        if (event == null) {
            response.sendRedirect(request.getContextPath() + "/events/list");
            return;
        }
        
        // Get participant count
        int participantCount = donationEventDAO.getParticipantCount(eventId);
        
        request.setAttribute("event", event);
        request.setAttribute("participantCount", participantCount);
        
        // Check if user is registered for this event
        HttpSession session = request.getSession();
        if (session.getAttribute("userId") != null) {
            int userId = (int) session.getAttribute("userId");
            boolean isRegistered = eventParticipantDAO.isUserRegisteredForEvent(userId, eventId);
            request.setAttribute("isRegistered", isRegistered);
        }

        // Get participants with user details
        List<EventParticipantDTO> participants = eventParticipantDAO.getParticipantsWithUserDetailsByEventId(eventId);
        
        request.setAttribute("participants", participants);
        
        // Forward to the appropriate view based on user role
        String userRole = (String) session.getAttribute("userRole");
        
        if ("admin".equals(userRole)) {
            request.getRequestDispatcher("/view/admin/events/view.jsp").forward(request, response);
        } else if ("donor".equals(userRole)) {
            request.getRequestDispatcher("/view/donor/events/view.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/view/user/events/view.jsp").forward(request, response);
        }
    }

    private void showAddEvent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is admin
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        if (!"admin".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        request.getRequestDispatcher("/view/admin/events/add.jsp").forward(request, response);
    }

    private void addEvent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is admin
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        if (!"admin".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        // Get form data
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String eventDateStr = request.getParameter("eventDate");
        String startTimeStr = request.getParameter("startTime");
        String endTimeStr = request.getParameter("endTime");
        String location = request.getParameter("location");
        String organizer = request.getParameter("organizer");
        String contactPerson = request.getParameter("contactPerson");
        String contactEmail = request.getParameter("contactEmail");
        String contactPhone = request.getParameter("contactPhone");
        String maxParticipantsStr = request.getParameter("maxParticipants");
        
        // Validate input
        boolean hasError = false;
        
        if (!ValidationUtil.isNotEmpty(title)) {
            request.setAttribute("titleError", "Title is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(description)) {
            request.setAttribute("descriptionError", "Description is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(eventDateStr)) {
            request.setAttribute("eventDateError", "Event date is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(startTimeStr)) {
            request.setAttribute("startTimeError", "Start time is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(endTimeStr)) {
            request.setAttribute("endTimeError", "End time is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(location)) {
            request.setAttribute("locationError", "Location is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(organizer)) {
            request.setAttribute("organizerError", "Organizer is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isValidEmail(contactEmail)) {
            request.setAttribute("contactEmailError", "Invalid email format");
            hasError = true;
        }
        
        if (!ValidationUtil.isValidPhone(contactPhone)) {
            request.setAttribute("contactPhoneError", "Invalid phone number");
            hasError = true;
        }
        
        if (!ValidationUtil.isPositiveNumeric(maxParticipantsStr)) {
            request.setAttribute("maxParticipantsError", "Max participants must be a positive number");
            hasError = true;
        }
        
        if (hasError) {
            // Preserve form data
            request.setAttribute("title", title);
            request.setAttribute("description", description);
            request.setAttribute("eventDate", eventDateStr);
            request.setAttribute("startTime", startTimeStr);
            request.setAttribute("endTime", endTimeStr);
            request.setAttribute("location", location);
            request.setAttribute("organizer", organizer);
            request.setAttribute("contactEmail", contactEmail);
            request.setAttribute("contactPhone", contactPhone);
            request.setAttribute("maxParticipants", maxParticipantsStr);
            
            request.getRequestDispatcher("/view/admin/events/add.jsp").forward(request, response);
            return;
        }
        
        try {
            // Parse data
            Date eventDate = Date.valueOf(eventDateStr);
            int maxParticipants = Integer.parseInt(maxParticipantsStr);
            
            // Create event
            DonationEvent event = new DonationEvent();
            event.setTitle(title);
            event.setDescription(description);
            event.setEventDate(eventDate);
            event.setStartTime(startTimeStr);
            event.setEndTime(endTimeStr);
            event.setLocation(location);
            event.setOrganizer(organizer);
            event.setContactPerson(contactPerson);
            event.setContactEmail(contactEmail);
            event.setContactPhone(contactPhone);
            event.setMaxParticipants(maxParticipants);
            event.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            
            boolean eventAdded = donationEventDAO.addDonationEvent(event);
            
            if (eventAdded) {
                request.setAttribute("success", "Event added successfully");
                response.sendRedirect(request.getContextPath() + "/events/list");
            } else {
                request.setAttribute("error", "Failed to add event");
                request.getRequestDispatcher("/view/admin/events/add.jsp").forward(request, response);
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Invalid date or time format");
            request.getRequestDispatcher("/view/admin/events/add.jsp").forward(request, response);
        }
    }

    private void showEditEvent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is admin
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        if (!"admin".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        String eventIdStr = request.getParameter("id");
        
        if (!ValidationUtil.isPositiveNumeric(eventIdStr)) {
            response.sendRedirect(request.getContextPath() + "/events/list");
            return;
        }
        
        int eventId = Integer.parseInt(eventIdStr);
        
        // Get event
        DonationEvent event = donationEventDAO.getDonationEventById(eventId);
        
        if (event == null) {
            response.sendRedirect(request.getContextPath() + "/events/list");
            return;
        }
        
        request.setAttribute("event", event);
        
        request.getRequestDispatcher("/view/admin/events/edit.jsp").forward(request, response);
    }

    private void updateEvent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is admin
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        if (!"admin".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        String eventIdStr = request.getParameter("id");
        
        if (!ValidationUtil.isPositiveNumeric(eventIdStr)) {
            response.sendRedirect(request.getContextPath() + "/events/list");
            return;
        }
        
        int eventId = Integer.parseInt(eventIdStr);
        
        // Get event
        DonationEvent event = donationEventDAO.getDonationEventById(eventId);
        
        if (event == null) {
            response.sendRedirect(request.getContextPath() + "/events/list");
            return;
        }
        
        // Get form data
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String eventDateStr = request.getParameter("eventDate");
        String startTimeStr = request.getParameter("startTime");
        String endTimeStr = request.getParameter("endTime");
        String location = request.getParameter("location");
        String organizer = request.getParameter("organizer");
        String contactPerson = request.getParameter("contactPerson");
        String contactEmail = request.getParameter("contactEmail");
        String contactPhone = request.getParameter("contactPhone");
        String maxParticipantsStr = request.getParameter("maxParticipants");
        
        // Validate input
        boolean hasError = false;
        
        if (!ValidationUtil.isNotEmpty(title)) {
            request.setAttribute("titleError", "Title is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(description)) {
            request.setAttribute("descriptionError", "Description is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(eventDateStr)) {
            request.setAttribute("eventDateError", "Event date is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(startTimeStr)) {
            request.setAttribute("startTimeError", "Start time is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(endTimeStr)) {
            request.setAttribute("endTimeError", "End time is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(location)) {
            request.setAttribute("locationError", "Location is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(organizer)) {
            request.setAttribute("organizerError", "Organizer is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isValidEmail(contactEmail)) {
            request.setAttribute("contactEmailError", "Invalid email format");
            hasError = true;
        }
        
        if (!ValidationUtil.isValidPhone(contactPhone)) {
            request.setAttribute("contactPhoneError", "Invalid phone number");
            hasError = true;
        }
        
        if (!ValidationUtil.isPositiveNumeric(maxParticipantsStr)) {
            request.setAttribute("maxParticipantsError", "Max participants must be a positive number");
            hasError = true;
        }
        
        if (hasError) {
            // Preserve form data
            request.setAttribute("event", event);
            request.setAttribute("title", title);
            request.setAttribute("description", description);
            request.setAttribute("eventDate", eventDateStr);
            request.setAttribute("startTime", startTimeStr);
            request.setAttribute("endTime", endTimeStr);
            request.setAttribute("location", location);
            request.setAttribute("organizer", organizer);
            request.setAttribute("contactEmail", contactEmail);
            request.setAttribute("contactPhone", contactPhone);
            request.setAttribute("maxParticipants", maxParticipantsStr);
            
            request.getRequestDispatcher("/view/admin/events/edit.jsp").forward(request, response);
            return;
        }
        
        try {
            // Parse data
            Date eventDate = Date.valueOf(eventDateStr);
            int maxParticipants = Integer.parseInt(maxParticipantsStr);
            
            // Update event
            event.setTitle(title);
            event.setDescription(description);
            event.setEventDate(eventDate);
            event.setStartTime(startTimeStr);
            event.setEndTime(endTimeStr);
            event.setLocation(location);
            event.setOrganizer(organizer);
            event.setContactPerson(contactPerson);
            event.setContactEmail(contactEmail);
            event.setContactPhone(contactPhone);
            event.setMaxParticipants(maxParticipants);
            event.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            
            boolean eventUpdated = donationEventDAO.updateDonationEvent(event);
            
            if (eventUpdated) {
                request.setAttribute("success", "Event updated successfully");
                response.sendRedirect(request.getContextPath() + "/events/view?id=" + eventId);
            } else {
                request.setAttribute("error", "Failed to update event");
                request.setAttribute("event", event);
                request.getRequestDispatcher("/view/admin/events/edit.jsp").forward(request, response);
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Invalid date or time format");
            request.setAttribute("event", event);
            request.getRequestDispatcher("/view/admin/events/edit.jsp").forward(request, response);
        }
    }

    private void deleteEvent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is admin
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        if (!"admin".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        String eventIdStr = request.getParameter("id");
        
        if (!ValidationUtil.isPositiveNumeric(eventIdStr)) {
            response.sendRedirect(request.getContextPath() + "/events/list");
            return;
        }
        
        int eventId = Integer.parseInt(eventIdStr);
        
        // Delete event
        boolean eventDeleted = donationEventDAO.deleteDonationEvent(eventId);
        
        if (eventDeleted) {
            request.setAttribute("success", "Event deleted successfully");
        } else {
            request.setAttribute("error", "Failed to delete event");
        }
        
        response.sendRedirect(request.getContextPath() + "/events/list");
    }

    private void listParticipants(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is admin
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        if (!"admin".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        String eventIdStr = request.getParameter("id");
        
        if (!ValidationUtil.isPositiveNumeric(eventIdStr)) {
            response.sendRedirect(request.getContextPath() + "/events/list");
            return;
        }
        
        int eventId = Integer.parseInt(eventIdStr);
        
        // Get event
        DonationEvent event = donationEventDAO.getDonationEventById(eventId);
        
        if (event == null) {
            response.sendRedirect(request.getContextPath() + "/events/list");
            return;
        }
        
        // Get participants with user details
        List<EventParticipantDTO> participants = eventParticipantDAO.getParticipantsWithUserDetailsByEventId(eventId);
        
        request.setAttribute("event", event);
        request.setAttribute("participants", participants);
        
        request.getRequestDispatcher("/view/admin/events/participants.jsp").forward(request, response);
    }

    private void registerForEvent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        int userId = (int) session.getAttribute("userId");
        
        String eventIdStr = request.getParameter("id");
        
        if (!ValidationUtil.isPositiveNumeric(eventIdStr)) {
            response.sendRedirect(request.getContextPath() + "/events/list");
            return;
        }
        
        int eventId = Integer.parseInt(eventIdStr);
        
        // Get event
        DonationEvent event = donationEventDAO.getDonationEventById(eventId);
        
        if (event == null) {
            response.sendRedirect(request.getContextPath() + "/events/list");
            return;
        }
        
        // Check if event is in the future
        Date currentDate = new Date(System.currentTimeMillis());
        if (event.getEventDate().before(currentDate)) {
            request.setAttribute("error", "Cannot register for past events");
            response.sendRedirect(request.getContextPath() + "/events/view?id=" + eventId);
            return;
        }
        
        // Check if user is already registered
        if (eventParticipantDAO.isUserRegisteredForEvent(userId, eventId)) {
            request.setAttribute("error", "You are already registered for this event");
            response.sendRedirect(request.getContextPath() + "/events/view?id=" + eventId);
            return;
        }
        
        // Check if event is full
        int participantCount = donationEventDAO.getParticipantCount(eventId);
        if (participantCount >= event.getMaxParticipants()) {
            request.setAttribute("error", "Event is full");
            response.sendRedirect(request.getContextPath() + "/events/view?id=" + eventId);
            return;
        }
        
        // Register user for event
        EventParticipant participant = new EventParticipant();
        participant.setEventId(eventId);
        participant.setUserId(userId);
        participant.setRegistrationDate(new Timestamp(System.currentTimeMillis()));
        participant.setStatus("registered");
        
        // Changed from addEventParticipant to addParticipant to match the DAO method name
        boolean registered = eventParticipantDAO.addParticipant(participant);
        
        if (registered) {
            request.setAttribute("success", "Successfully registered for event");
        } else {
            request.setAttribute("error", "Failed to register for event");
        }
        
        response.sendRedirect(request.getContextPath() + "/events/view?id=" + eventId);
    }

    private void cancelRegistration(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        int userId = (int) session.getAttribute("userId");
        
        String eventIdStr = request.getParameter("id");
        
        if (!ValidationUtil.isPositiveNumeric(eventIdStr)) {
            response.sendRedirect(request.getContextPath() + "/events/list");
            return;
        }
        
        int eventId = Integer.parseInt(eventIdStr);
        
        // Get event
        DonationEvent event = donationEventDAO.getDonationEventById(eventId);
        
        if (event == null) {
            response.sendRedirect(request.getContextPath() + "/events/list");
            return;
        }
        
        // Check if event is in the future
        Date currentDate = new Date(System.currentTimeMillis());
        if (event.getEventDate().before(currentDate)) {
            request.setAttribute("error", "Cannot cancel registration for past events");
            response.sendRedirect(request.getContextPath() + "/events/view?id=" + eventId);
            return;
        }
        
        // Check if user is registered
        if (!eventParticipantDAO.isUserRegisteredForEvent(userId, eventId)) {
            request.setAttribute("error", "You are not registered for this event");
            response.sendRedirect(request.getContextPath() + "/events/view?id=" + eventId);
            return;
        }
        
        // Cancel registration
        boolean cancelled = eventParticipantDAO.cancelEventParticipation(userId, eventId);
        
        if (cancelled) {
            request.setAttribute("success", "Registration cancelled successfully");
        } else {
            request.setAttribute("error", "Failed to cancel registration");
        }
        
        response.sendRedirect(request.getContextPath() + "/events/view?id=" + eventId);
    }

    private void removeParticipant(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is admin
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        if (!"admin".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        String participantIdStr = request.getParameter("participantId");
        String eventIdStr = request.getParameter("eventId");
        
        if (!ValidationUtil.isPositiveNumeric(participantIdStr) || !ValidationUtil.isPositiveNumeric(eventIdStr)) {
            response.sendRedirect(request.getContextPath() + "/events/list");
            return;
        }
        
        int participantId = Integer.parseInt(participantIdStr);
        int eventId = Integer.parseInt(eventIdStr);
        
        // Remove participant
        // Changed from deleteEventParticipant to removeParticipant to match the DAO method name
        boolean removed = eventParticipantDAO.removeParticipant(participantId);
        
        if (removed) {
            request.setAttribute("success", "Participant removed successfully");
        } else {
            request.setAttribute("error", "Failed to remove participant");
        }
        
        response.sendRedirect(request.getContextPath() + "/events/participants?id=" + eventId);
    }

    @Override
    public void destroy() {
        try {
            if (donationEventDAO != null) {
                // Close connection if needed
            }
            if (eventParticipantDAO != null) {
                // Close connection if needed
            }
            if (userDAO != null) {
                // Close connection if needed
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
