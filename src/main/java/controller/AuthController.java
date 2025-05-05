package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Donor;
import model.DonorDAO;
import model.User;
import model.UserDAO;
import util.ValidationUtil;

import java.io.IOException;

@WebServlet(urlPatterns = {"/auth/login", "/auth/register", "/auth/logout"})
public class AuthController extends HttpServlet {
    private UserDAO userDAO;
    private DonorDAO donorDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        donorDAO = new DonorDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getServletPath();
        
        if (pathInfo.equals("/auth/logout")) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        // Check if user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            // Use userId instead of user object to avoid serialization issues
            String userRole = (String) session.getAttribute("userRole");
            if (userRole != null) {
                // Only redirect if we're on login or register page
                if (pathInfo.equals("/auth/login") || pathInfo.equals("/auth/register")) {
                    redirectBasedOnRole(response, userRole, request.getContextPath());
                    return;
                }
            }
        }

        if (pathInfo.equals("/auth/login")) {
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
        } else if (pathInfo.equals("/auth/register")) {
            request.getRequestDispatcher("/view/auth/register.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getServletPath();

        if (pathInfo.equals("/auth/login")) {
            handleLogin(request, response);
        } else if (pathInfo.equals("/auth/register")) {
            handleRegistration(request, response);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String contextPath = request.getContextPath();

        // Validate input
        if (!ValidationUtil.isValidEmail(email) || password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Invalid email or password format");
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
            return;
        }

        // Authenticate user - password verification happens in the DAO
        User user = userDAO.authenticate(email, password);
        if (user == null) {
            request.setAttribute("error", "Invalid email or password");
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
            return;
        }

        // Create session with user ID and role
        HttpSession session = request.getSession();
        session.setAttribute("userId", user.getId());
        session.setAttribute("userRole", user.getRole());
        session.setAttribute("userName", user.getName());
        
        // For donor, also store donor ID
        if ("donor".equals(user.getRole())) {
            Donor donor = donorDAO.getDonorByUserId(user.getId());
            if (donor != null) {
                session.setAttribute("donorId", donor.getId());
            }
        }
        
        // Log the session attributes for debugging
        System.out.println("Session created with userId: " + session.getAttribute("userId"));
        System.out.println("Session created with userRole: " + session.getAttribute("userRole"));
        
        // Redirect based on role
        redirectBasedOnRole(response, user.getRole(), contextPath);
    }

    private void handleRegistration(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String role = request.getParameter("role");
        String bloodGroup = request.getParameter("bloodGroup");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        // Validate input
        if (!ValidationUtil.isNotEmpty(name) || !ValidationUtil.isValidEmail(email) || 
            !ValidationUtil.isValidPassword(password) || !password.equals(confirmPassword)) {
            request.setAttribute("error", "Invalid input data");
            request.getRequestDispatcher("/view/auth/register.jsp").forward(request, response);
            return;
        }

        // Check if email already exists
        if (userDAO.getUserByEmail(email) != null) {
            request.setAttribute("error", "Email already registered");
            request.getRequestDispatcher("/view/auth/register.jsp").forward(request, response);
            return;
        }

        // Create user - password will be hashed in the DAO
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);
        user.setAddress(address);
        user.setRole(role);
        user.setRegistrationDate(new java.sql.Date(System.currentTimeMillis()));
        user.setActive(true);

        int userId = userDAO.addUser(user);
        if (userId > 0) {
            // If registering as a donor, create donor record
            if ("donor".equals(role)) {
                Donor donor = new Donor();
                donor.setUserId(userId);
                donor.setBloodGroup(bloodGroup); // Using setBloodGroup instead of setBloodType
                donor.setLocation(address); // Using setLocation instead of setAddress
                // Phone is stored in the User record, not needed in Donor
                donor.setAvailable(true); // Explicitly set donor as available by default
                donor.setDonationCount(0);
                donor.setMedicalHistory(""); // Setting an empty medical history initially
                
                // Log the donor creation
                System.out.println("Creating new donor with userId: " + userId + ", bloodGroup: " + bloodGroup + ", available: true");
                
                donorDAO.addDonor(donor);
            }

            request.setAttribute("success", "Registration successful. Please login.");
            request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Registration failed. Please try again.");
            request.getRequestDispatcher("/view/auth/register.jsp").forward(request, response);
        }
    }

    private void redirectBasedOnRole(HttpServletResponse response, String role, String contextPath) throws IOException {
        switch (role) {
            case "admin":
                response.sendRedirect(contextPath + "/admin/dashboard");
                break;
            case "donor":
                response.sendRedirect(contextPath + "/donor/dashboard");
                break;
            case "general":
                response.sendRedirect(contextPath + "/user/dashboard");
                break;
            default:
                response.sendRedirect(contextPath + "/");
        }
    }
}
