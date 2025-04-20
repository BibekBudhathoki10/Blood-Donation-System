package controller;

import model.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"", "/home"})
public class HomeController extends HttpServlet {
  private DonationEventDAO donationEventDAO;
  private BloodInventoryDAO bloodInventoryDAO;

  @Override
  public void init() throws ServletException {
      donationEventDAO = new DonationEventDAO();
      bloodInventoryDAO = new BloodInventoryDAO();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      // Get upcoming events
      List<DonationEvent> upcomingEvents = donationEventDAO.getUpcomingDonationEvents();
      request.setAttribute("upcomingEvents", upcomingEvents);
      
      // Get blood inventory statistics
      int aPositiveCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("A+");
      int aNegativeCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("A-");
      int bPositiveCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("B+");
      int bNegativeCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("B-");
      int abPositiveCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("AB+");
      int abNegativeCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("AB-");
      int oPositiveCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("O+");
      int oNegativeCount = bloodInventoryDAO.getTotalAvailableQuantityByBloodGroup("O-");
      
      request.setAttribute("aPositiveCount", aPositiveCount);
      request.setAttribute("aNegativeCount", aNegativeCount);
      request.setAttribute("bPositiveCount", bPositiveCount);
      request.setAttribute("bNegativeCount", bNegativeCount);
      request.setAttribute("abPositiveCount", abPositiveCount);
      request.setAttribute("abNegativeCount", abNegativeCount);
      request.setAttribute("oPositiveCount", oPositiveCount);
      request.setAttribute("oNegativeCount", oNegativeCount);
      
      // Forward to the home page
      request.getRequestDispatcher("/view/common/index.jsp").forward(request, response);
  }
}

