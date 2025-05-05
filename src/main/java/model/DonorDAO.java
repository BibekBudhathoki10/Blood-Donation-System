package model;

import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonorDAO {
    private Connection connection;

    public DonorDAO() {
        try {
            connection = DBConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create a new donor
    public boolean addDonor(Donor donor) {
        String sql = "INSERT INTO donors (user_id, blood_group, last_donation_date, available, medical_history, donation_count, location) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, donor.getUserId());
            statement.setString(2, donor.getBloodGroup());
            
            if (donor.getLastDonationDate() != null) {
                statement.setDate(3, donor.getLastDonationDate());
            } else {
                statement.setNull(3, java.sql.Types.DATE);
            }
            
            statement.setBoolean(4, donor.isAvailable());
            statement.setString(5, donor.getMedicalHistory());
            statement.setInt(6, donor.getDonationCount());
            statement.setString(7, donor.getLocation());
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        donor.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Read a donor by ID
    public Donor getDonorById(int id) {
        String sql = "SELECT * FROM donors WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractDonorFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Read a donor by user ID
    public Donor getDonorByUserId(int userId) {
        String sql = "SELECT * FROM donors WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractDonorFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Read all donors
    public List<Donor> getAllDonors() {
        List<Donor> donors = new ArrayList<>();
        String sql = "SELECT * FROM donors";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                donors.add(extractDonorFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return donors;
    }

    // Read donors by blood group
    public List<Donor> getDonorsByBloodGroup(String bloodGroup) {
        List<Donor> donors = new ArrayList<>();
        String sql = "SELECT * FROM donors WHERE blood_group = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, bloodGroup);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    donors.add(extractDonorFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return donors;
    }

    // Read available donors
    public List<Donor> getAvailableDonors() {
        List<Donor> donors = new ArrayList<>();
        String sql = "SELECT * FROM donors WHERE available = true";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                donors.add(extractDonorFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return donors;
    }

    // Read available donors by blood group
    public List<Donor> getAvailableDonorsByBloodGroup(String bloodGroup) {
        List<Donor> donors = new ArrayList<>();
        String sql = "SELECT * FROM donors WHERE blood_group = ? AND available = true";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, bloodGroup);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    donors.add(extractDonorFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return donors;
    }

    // Update a donor
    public boolean updateDonor(Donor donor) {
        String sql = "UPDATE donors SET blood_group = ?, last_donation_date = ?, available = ?, medical_history = ?, donation_count = ?, location = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, donor.getBloodGroup());
            
            if (donor.getLastDonationDate() != null) {
                statement.setDate(2, donor.getLastDonationDate());
            } else {
                statement.setNull(2, java.sql.Types.DATE);
            }
            
            statement.setBoolean(3, donor.isAvailable());
            statement.setString(4, donor.getMedicalHistory());
            statement.setInt(5, donor.getDonationCount());
            statement.setString(6, donor.getLocation());
            statement.setInt(7, donor.getId());
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update donor availability
    public boolean updateDonorAvailability(int id, boolean available) {
        String sql = "UPDATE donors SET available = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, available);
            statement.setInt(2, id);
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update donation count
    public boolean incrementDonationCount(int id) {
        String sql = "UPDATE donors SET donation_count = donation_count + 1, last_donation_date = CURRENT_DATE WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete a donor
    public boolean deleteDonor(int id) {
        String sql = "DELETE FROM donors WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper method to extract donor from ResultSet
    private Donor extractDonorFromResultSet(ResultSet resultSet) throws SQLException {
        Donor donor = new Donor();
        donor.setId(resultSet.getInt("id"));
        donor.setUserId(resultSet.getInt("user_id"));
        donor.setBloodGroup(resultSet.getString("blood_group"));
        donor.setLastDonationDate(resultSet.getDate("last_donation_date"));
        donor.setAvailable(resultSet.getBoolean("available"));
        donor.setMedicalHistory(resultSet.getString("medical_history"));
        donor.setDonationCount(resultSet.getInt("donation_count"));
        donor.setLocation(resultSet.getString("location"));
        return donor;
    }
}
