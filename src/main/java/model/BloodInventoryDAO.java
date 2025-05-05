package model;

import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BloodInventoryDAO {
    private Connection connection;

    public BloodInventoryDAO() {
        try {
            connection = DBConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create a new blood inventory entry
    public boolean addBloodInventory(BloodInventory inventory) {
        if (connection == null) {
            System.err.println("Database connection is null");
            try {
                connection = DBConnection.getConnection();
            } catch (SQLException e) {
                System.err.println("Failed to reconnect to database: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }

        try {
            if (connection.isClosed()) {
                System.err.println("Database connection is closed");
                connection = DBConnection.getConnection();
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        String sql = "INSERT INTO blood_inventory (blood_group, quantity, collection_date, expiry_date, status, donor_id, location) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, inventory.getBloodGroup());
            statement.setInt(2, inventory.getQuantity());
            statement.setDate(3, inventory.getCollectionDate());
            statement.setDate(4, inventory.getExpiryDate());
            statement.setString(5, inventory.getStatus());
            statement.setInt(6, inventory.getDonorId());
            statement.setString(7, inventory.getLocation());
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        inventory.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error adding blood inventory: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Read a blood inventory entry by ID
    public BloodInventory getBloodInventoryById(int id) {
        String sql = "SELECT * FROM blood_inventory WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractBloodInventoryFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Read all blood inventory entries
    public List<BloodInventory> getAllBloodInventory() {
        List<BloodInventory> inventoryList = new ArrayList<>();
        String sql = "SELECT * FROM blood_inventory";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                inventoryList.add(extractBloodInventoryFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inventoryList;
    }

    // Read blood inventory entries by blood group
    public List<BloodInventory> getBloodInventoryByBloodGroup(String bloodGroup) {
        List<BloodInventory> inventoryList = new ArrayList<>();
        String sql = "SELECT * FROM blood_inventory WHERE blood_group = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, bloodGroup);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    inventoryList.add(extractBloodInventoryFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inventoryList;
    }

    // Read blood inventory entries by status
    public List<BloodInventory> getBloodInventoryByStatus(String status) {
        List<BloodInventory> inventoryList = new ArrayList<>();
        String sql = "SELECT * FROM blood_inventory WHERE status = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    inventoryList.add(extractBloodInventoryFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inventoryList;
    }

    // Read available blood inventory entries by blood group
    public List<BloodInventory> getAvailableBloodInventoryByBloodGroup(String bloodGroup) {
        List<BloodInventory> inventoryList = new ArrayList<>();
        String sql = "SELECT * FROM blood_inventory WHERE blood_group = ? AND status = 'available'";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, bloodGroup);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    inventoryList.add(extractBloodInventoryFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inventoryList;
    }

    // Get total available quantity by blood group
    public int getTotalAvailableQuantityByBloodGroup(String bloodGroup) {
        String sql = "SELECT SUM(quantity) FROM blood_inventory WHERE blood_group = ? AND status = 'available'";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, bloodGroup);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Get total available quantity across all blood groups
    public int getTotalAvailableQuantity() {
        String sql = "SELECT SUM(quantity) FROM blood_inventory WHERE status = 'available'";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Update a blood inventory entry
    public boolean updateBloodInventory(BloodInventory inventory) {
        String sql = "UPDATE blood_inventory SET blood_group = ?, quantity = ?, collection_date = ?, expiry_date = ?, status = ?, donor_id = ?, location = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, inventory.getBloodGroup());
            statement.setInt(2, inventory.getQuantity());
            statement.setDate(3, inventory.getCollectionDate());
            statement.setDate(4, inventory.getExpiryDate());
            statement.setString(5, inventory.getStatus());
            statement.setInt(6, inventory.getDonorId());
            statement.setString(7, inventory.getLocation());
            statement.setInt(8, inventory.getId());
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update blood inventory status
    public boolean updateBloodInventoryStatus(int id, String status) {
        String sql = "UPDATE blood_inventory SET status = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, id);
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update expired blood inventory
    public boolean updateExpiredBloodInventory() {
        String sql = "UPDATE blood_inventory SET status = 'expired' WHERE expiry_date < CURRENT_DATE AND status = 'available'";
        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(sql) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete a blood inventory entry
    public boolean deleteBloodInventory(int id) {
        String sql = "DELETE FROM blood_inventory WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper method to extract blood inventory from ResultSet
    private BloodInventory extractBloodInventoryFromResultSet(ResultSet resultSet) throws SQLException {
        BloodInventory inventory = new BloodInventory();
        inventory.setId(resultSet.getInt("id"));
        inventory.setBloodGroup(resultSet.getString("blood_group"));
        inventory.setQuantity(resultSet.getInt("quantity"));
        inventory.setCollectionDate(resultSet.getDate("collection_date"));
        inventory.setExpiryDate(resultSet.getDate("expiry_date"));
        inventory.setStatus(resultSet.getString("status"));
        inventory.setDonorId(resultSet.getInt("donor_id"));
        inventory.setLocation(resultSet.getString("location"));
        return inventory;
    }
}
