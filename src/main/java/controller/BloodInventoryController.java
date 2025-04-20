package controller;

import model.BloodInventory;
import model.BloodInventoryDAO;
import util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet("/inventory/*")
public class BloodInventoryController extends HttpServlet {
    private BloodInventoryDAO bloodInventoryDAO;

    @Override
    public void init() throws ServletException {
        bloodInventoryDAO = new BloodInventoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/inventory/list");
            return;
        }
        
        switch (pathInfo) {
            case "/list":
                listInventory(request, response);
                break;
            case "/add":
                showAddInventory(request, response);
                break;
            case "/edit":
                showEditInventory(request, response);
                break;
            case "/view":
                viewInventory(request, response);
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
                addInventory(request, response);
                break;
            case "/edit":
                updateInventory(request, response);
                break;
            case "/delete":
                deleteInventory(request, response);
                break;
            case "/update-status":
                updateInventoryStatus(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    private void listInventory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is admin
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        if (!"admin".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        // Update expired blood inventory
        bloodInventoryDAO.updateExpiredBloodInventory();
        
        // Get filter parameters
        String bloodGroup = request.getParameter("bloodGroup");
        String status = request.getParameter("status");
        
        List<BloodInventory> inventoryList;
        
        if (ValidationUtil.isValidBloodGroup(bloodGroup) && ValidationUtil.isNotEmpty(status)) {
            // Filter by both blood group and status
            if ("available".equals(status)) {
                inventoryList = bloodInventoryDAO.getAvailableBloodInventoryByBloodGroup(bloodGroup);
            } else {
                // Get by blood group first, then filter by status
                inventoryList = bloodInventoryDAO.getBloodInventoryByBloodGroup(bloodGroup);
                inventoryList.removeIf(inventory -> !inventory.getStatus().equals(status));
            }
        } else if (ValidationUtil.isValidBloodGroup(bloodGroup)) {
            // Filter by blood group only
            inventoryList = bloodInventoryDAO.getBloodInventoryByBloodGroup(bloodGroup);
        } else if (ValidationUtil.isNotEmpty(status)) {
            // Filter by status only
            inventoryList = bloodInventoryDAO.getBloodInventoryByStatus(status);
        } else {
            // No filters, get all
            inventoryList = bloodInventoryDAO.getAllBloodInventory();
        }
        
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
        
        request.setAttribute("inventoryList", inventoryList);
        request.setAttribute("bloodGroup", bloodGroup);
        request.setAttribute("status", status);
        
        request.getRequestDispatcher("/view/admin/inventory/index.jsp").forward(request, response);
    }

    private void showAddInventory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is admin
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        if (!"admin".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        request.getRequestDispatcher("/view/admin/inventory/add.jsp").forward(request, response);
    }

    private void addInventory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is admin
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        if (!"admin".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        // Get form data
        String bloodGroup = request.getParameter("bloodGroup");
        String quantityStr = request.getParameter("quantity");
        String collectionDateStr = request.getParameter("collectionDate");
        String expiryDateStr = request.getParameter("expiryDate");
        String status = request.getParameter("status");
        String donorIdStr = request.getParameter("donorId");
        String location = request.getParameter("location");
        
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
        
        if (!ValidationUtil.isNotEmpty(collectionDateStr)) {
            request.setAttribute("collectionDateError", "Collection date is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(expiryDateStr)) {
            request.setAttribute("expiryDateError", "Expiry date is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(status)) {
            request.setAttribute("statusError", "Status is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isPositiveNumeric(donorIdStr)) {
            request.setAttribute("donorIdError", "Donor ID must be a positive number");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(location)) {
            request.setAttribute("locationError", "Location is required");
            hasError = true;
        }
        
        if (hasError) {
            // Preserve form data
            request.setAttribute("bloodGroup", bloodGroup);
            request.setAttribute("quantity", quantityStr);
            request.setAttribute("collectionDate", collectionDateStr);
            request.setAttribute("expiryDate", expiryDateStr);
            request.setAttribute("status", status);
            request.setAttribute("donorId", donorIdStr);
            request.setAttribute("location", location);
            
            request.getRequestDispatcher("/view/admin/inventory/add.jsp").forward(request, response);
            return;
        }
        
        try {
            // Parse data
            int quantity = Integer.parseInt(quantityStr);
            Date collectionDate = Date.valueOf(collectionDateStr);
            Date expiryDate = Date.valueOf(expiryDateStr);
            int donorId = Integer.parseInt(donorIdStr);
            
            // Create blood inventory
            BloodInventory inventory = new BloodInventory();
            inventory.setBloodGroup(bloodGroup);
            inventory.setQuantity(quantity);
            inventory.setCollectionDate(collectionDate);
            inventory.setExpiryDate(expiryDate);
            inventory.setStatus(status);
            inventory.setDonorId(donorId);
            inventory.setLocation(location);
            
            boolean inventoryAdded = bloodInventoryDAO.addBloodInventory(inventory);
            
            if (inventoryAdded) {
                request.setAttribute("success", "Blood inventory added successfully");
                response.sendRedirect(request.getContextPath() + "/inventory/list");
            } else {
                request.setAttribute("error", "Failed to add blood inventory");
                request.getRequestDispatcher("/view/admin/inventory/add.jsp").forward(request, response);
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Invalid date format");
            request.getRequestDispatcher("/view/admin/inventory/add.jsp").forward(request, response);
        }
    }

    private void showEditInventory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is admin
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        if (!"admin".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        String inventoryIdStr = request.getParameter("id");
        
        if (!ValidationUtil.isPositiveNumeric(inventoryIdStr)) {
            response.sendRedirect(request.getContextPath() + "/inventory/list");
            return;
        }
        
        int inventoryId = Integer.parseInt(inventoryIdStr);
        
        // Get inventory
        BloodInventory inventory = bloodInventoryDAO.getBloodInventoryById(inventoryId);
        
        if (inventory == null) {
            response.sendRedirect(request.getContextPath() + "/inventory/list");
            return;
        }
        
        request.setAttribute("inventory", inventory);
        
        request.getRequestDispatcher("/view/admin/inventory/edit.jsp").forward(request, response);
    }

    private void updateInventory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is admin
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        if (!"admin".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        String inventoryIdStr = request.getParameter("id");
        
        if (!ValidationUtil.isPositiveNumeric(inventoryIdStr)) {
            response.sendRedirect(request.getContextPath() + "/inventory/list");
            return;
        }
        
        int inventoryId = Integer.parseInt(inventoryIdStr);
        
        // Get inventory
        BloodInventory inventory = bloodInventoryDAO.getBloodInventoryById(inventoryId);
        
        if (inventory == null) {
            response.sendRedirect(request.getContextPath() + "/inventory/list");
            return;
        }
        
        // Get form data
        String bloodGroup = request.getParameter("bloodGroup");
        String quantityStr = request.getParameter("quantity");
        String collectionDateStr = request.getParameter("collectionDate");
        String expiryDateStr = request.getParameter("expiryDate");
        String status = request.getParameter("status");
        String donorIdStr = request.getParameter("donorId");
        String location = request.getParameter("location");
        
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
        
        if (!ValidationUtil.isNotEmpty(collectionDateStr)) {
            request.setAttribute("collectionDateError", "Collection date is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(expiryDateStr)) {
            request.setAttribute("expiryDateError", "Expiry date is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(status)) {
            request.setAttribute("statusError", "Status is required");
            hasError = true;
        }
        
        if (!ValidationUtil.isPositiveNumeric(donorIdStr)) {
            request.setAttribute("donorIdError", "Donor ID must be a positive number");
            hasError = true;
        }
        
        if (!ValidationUtil.isNotEmpty(location)) {
            request.setAttribute("locationError", "Location is required");
            hasError = true;
        }
        
        if (hasError) {
            // Preserve form data
            request.setAttribute("inventory", inventory);
            request.setAttribute("bloodGroup", bloodGroup);
            request.setAttribute("quantity", quantityStr);
            request.setAttribute("collectionDate", collectionDateStr);
            request.setAttribute("expiryDate", expiryDateStr);
            request.setAttribute("status", status);
            request.setAttribute("donorId", donorIdStr);
            request.setAttribute("location", location);
            
            request.getRequestDispatcher("/view/admin/inventory/edit.jsp").forward(request, response);
            return;
        }
        
        try {
            // Parse data
            int quantity = Integer.parseInt(quantityStr);
            Date collectionDate = Date.valueOf(collectionDateStr);
            Date expiryDate = Date.valueOf(expiryDateStr);
            int donorId = Integer.parseInt(donorIdStr);
            
            // Update inventory
            inventory.setBloodGroup(bloodGroup);
            inventory.setQuantity(quantity);
            inventory.setCollectionDate(collectionDate);
            inventory.setExpiryDate(expiryDate);
            inventory.setStatus(status);
            inventory.setDonorId(donorId);
            inventory.setLocation(location);
            
            boolean inventoryUpdated = bloodInventoryDAO.updateBloodInventory(inventory);
            
            if (inventoryUpdated) {
                request.setAttribute("success", "Blood inventory updated successfully");
                response.sendRedirect(request.getContextPath() + "/inventory/list");
            } else {
                request.setAttribute("error", "Failed to update blood inventory");
                request.setAttribute("inventory", inventory);
                request.getRequestDispatcher("/view/admin/inventory/edit.jsp").forward(request, response);
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Invalid date format");
            request.setAttribute("inventory", inventory);
            request.getRequestDispatcher("/view/admin/inventory/edit.jsp").forward(request, response);
        }
    }

    private void deleteInventory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is admin
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        if (!"admin".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        String inventoryIdStr = request.getParameter("id");
        
        if (!ValidationUtil.isPositiveNumeric(inventoryIdStr)) {
            response.sendRedirect(request.getContextPath() + "/inventory/list");
            return;
        }
        
        int inventoryId = Integer.parseInt(inventoryIdStr);
        
        // Delete inventory
        boolean inventoryDeleted = bloodInventoryDAO.deleteBloodInventory(inventoryId);
        
        if (inventoryDeleted) {
            request.setAttribute("success", "Blood inventory deleted successfully");
        } else {
            request.setAttribute("error", "Failed to delete blood inventory");
        }
        
        response.sendRedirect(request.getContextPath() + "/inventory/list");
    }

    private void viewInventory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is admin
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        if (!"admin".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        String inventoryIdStr = request.getParameter("id");
        
        if (!ValidationUtil.isPositiveNumeric(inventoryIdStr)) {
            response.sendRedirect(request.getContextPath() + "/inventory/list");
            return;
        }
        
        int inventoryId = Integer.parseInt(inventoryIdStr);
        
        // Get inventory
        BloodInventory inventory = bloodInventoryDAO.getBloodInventoryById(inventoryId);
        
        if (inventory == null) {
            response.sendRedirect(request.getContextPath() + "/inventory/list");
            return;
        }
        
        request.setAttribute("inventory", inventory);
        
        request.getRequestDispatcher("/view/admin/inventory/view.jsp").forward(request, response);
    }

    private void updateInventoryStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is admin
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        if (!"admin".equals(userRole)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        String inventoryIdStr = request.getParameter("id");
        String status = request.getParameter("status");
        
        if (!ValidationUtil.isPositiveNumeric(inventoryIdStr) || !ValidationUtil.isNotEmpty(status)) {
            response.sendRedirect(request.getContextPath() + "/inventory/list");
            return;
        }
        
        int inventoryId = Integer.parseInt(inventoryIdStr);
        
        // Update inventory status
        boolean statusUpdated = bloodInventoryDAO.updateBloodInventoryStatus(inventoryId, status);
        
        if (statusUpdated) {
            request.setAttribute("success", "Blood inventory status updated successfully");
        } else {
            request.setAttribute("error", "Failed to update blood inventory status");
        }
        
        response.sendRedirect(request.getContextPath() + "/inventory/list");
    }
}

