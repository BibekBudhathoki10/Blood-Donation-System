package controller;

import model.User;
import model.UserDAO;
import model.Donor;
import model.DonorDAO;
import util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

@WebServlet("/auth/*")
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
      String pathInfo = request.getPathInfo();
      
      if (pathInfo == null || pathInfo.equals("/")) {
          response.sendRedirect(request.getContextPath() + "/auth/login");
          return;
      }
      
      switch (pathInfo) {
          case "/login":
              request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
              break;
          case "/register":
              request.getRequestDispatcher("/view/auth/register.jsp").forward(request, response);
              break;
          case "/logout":
              logout(request, response);
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
          case "/login":
              login(request, response);
              break;
          case "/register":
              register(request, response);
              break;
          default:
              response.sendError(HttpServletResponse.SC_NOT_FOUND);
              break;
      }
  }

  private void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String email = request.getParameter("email");
      String password = request.getParameter("password");
      
      // Validate input
      if (!ValidationUtil.isValidEmail(email)) {
          request.setAttribute("error", "Invalid email format");
          request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
          return;
      }
      
      // Authenticate user
      User user = userDAO.authenticateUser(email, password);
      
      if (user == null) {
          request.setAttribute("error", "Invalid email or password");
          request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
          return;
      }
      
      // Create session
      HttpSession session = request.getSession();
      session.setAttribute("user", user);
      session.setAttribute("userId", user.getId());
      session.setAttribute("userRole", user.getRole());
      
      // If user is a donor, get donor information
      if ("donor".equals(user.getRole())) {
          Donor donor = donorDAO.getDonorByUserId(user.getId());
          if (donor != null) {
              session.setAttribute("donor", donor);
              session.setAttribute("donorId", donor.getId());
          }
      }
      
      // Redirect based on role
      switch (user.getRole()) {
          case "admin":
              response.sendRedirect(request.getContextPath() + "/admin/dashboard");
              break;
          case "donor":
              response.sendRedirect(request.getContextPath() + "/donor/dashboard");
              break;
          case "general":
              response.sendRedirect(request.getContextPath() + "/user/dashboard");
              break;
          default:
              response.sendRedirect(request.getContextPath() + "/");
              break;
      }
  }

  private void register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      String name = request.getParameter("name");
      String email = request.getParameter("email");
      String password = request.getParameter("password");
      String confirmPassword = request.getParameter("confirmPassword");
      String phone = request.getParameter("phone");
      String address = request.getParameter("address");
      String role = request.getParameter("role");
      
      // For donor registration
      String bloodGroup = request.getParameter("bloodGroup");
      String medicalHistory = request.getParameter("medicalHistory");
      
      // Validate input
      boolean hasError = false;
      
      if (!ValidationUtil.isNotEmpty(name)) {
          request.setAttribute("nameError", "Name is required");
          hasError = true;
      }
      
      if (!ValidationUtil.isValidEmail(email)) {
          request.setAttribute("emailError", "Invalid email format");
          hasError = true;
      } else if (userDAO.getUserByEmail(email) != null) {
          request.setAttribute("emailError", "Email already exists");
          hasError = true;
      }
      
      if (!ValidationUtil.isValidPassword(password)) {
          request.setAttribute("passwordError", "Password must be at least 8 characters and contain letters and numbers");
          hasError = true;
      }
      
      if (!password.equals(confirmPassword)) {
          request.setAttribute("confirmPasswordError", "Passwords do not match");
          hasError = true;
      }
      
      if (!ValidationUtil.isValidPhone(phone)) {
          request.setAttribute("phoneError", "Invalid phone number");
          hasError = true;
      }
      
      if (!ValidationUtil.isNotEmpty(address)) {
          request.setAttribute("addressError", "Address is required");
          hasError = true;
      }
      
      if ("donor".equals(role) && !ValidationUtil.isValidBloodGroup(bloodGroup)) {
          request.setAttribute("bloodGroupError", "Invalid blood group");
          hasError = true;
      }
      
      if (hasError) {
          // Preserve input values
          request.setAttribute("name", name);
          request.setAttribute("email", email);
          request.setAttribute("phone", phone);
          request.setAttribute("address", address);
          request.setAttribute("role", role);
          request.setAttribute("bloodGroup", bloodGroup);
          request.setAttribute("medicalHistory", medicalHistory);
          
          request.getRequestDispatcher("/view/auth/register.jsp").forward(request, response);
          return;
      }
      
      // Create user
      User user = new User();
      user.setName(name);
      user.setEmail(email);
      user.setPassword(password);
      user.setPhone(phone);
      user.setAddress(address);
      user.setRole(role);
      user.setRegistrationDate(Date.valueOf(LocalDate.now()));
      user.setActive(true);
      
      boolean userCreated = userDAO.addUser(user);
      
      if (!userCreated) {
          request.setAttribute("error", "Failed to create user");
          request.getRequestDispatcher("/view/auth/register.jsp").forward(request, response);
          return;
      }
      
      // If donor, create donor profile
      if ("donor".equals(role)) {
          Donor donor = new Donor();
          donor.setUserId(user.getId());
          donor.setBloodGroup(bloodGroup);
          donor.setAvailable(true);
          donor.setMedicalHistory(medicalHistory);
          donor.setDonationCount(0);
          donor.setLocation(address);
          
          boolean donorCreated = donorDAO.addDonor(donor);
          
          if (!donorCreated) {
              request.setAttribute("error", "Failed to create donor profile");
              request.getRequestDispatcher("/view/auth/register.jsp").forward(request, response);
              return;
          }
      }
      
      // Redirect to login page with success message
      request.setAttribute("success", "Registration successful. Please login.");
      request.getRequestDispatcher("/view/auth/login.jsp").forward(request, response);
  }

  private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
      HttpSession session = request.getSession(false);
      if (session != null) {
          session.invalidate();
      }
      response.sendRedirect(request.getContextPath() + "/");
  }
}

