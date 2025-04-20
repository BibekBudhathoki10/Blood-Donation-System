package model;

import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BloodRequestDAO {
    private Connection connection;

    public BloodRequestDAO() {
        try {
            connection = DBConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create a new blood request
    public boolean addBloodRequest(BloodRequest request) {
        String sql = "INSERT INTO blood_requests (user_id, blood_group, quantity, urgency, status, hospital_name, hospital_address, patient_name, contact_person, contact_phone, reason, request_date, required_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, request.getUserId());
            statement.setString(2, request.getBloodGroup());
            statement.setInt(3, request.getQuantity());
            statement.setString(4, request.getUrgency());
            statement.setString(5, request.getStatus());
            statement.setString(6, request.getHospitalName());
            statement.setString(7, request.getHospitalAddress());
            statement.setString(8, request.getPatientName());
            statement.setString(9, request.getContactPerson());
            statement.setString(10, request.getContactPhone());
            statement.setString(11, request.getReason());
            statement.setTimestamp(12, request.getRequestDate());
            statement.setDate(13, request.getRequiredDate());
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        request.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Read a blood request by ID
    public BloodRequest getBloodRequestById(int id) {
        String sql = "SELECT * FROM blood_requests WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractBloodRequestFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Read all blood requests
    public List<BloodRequest> getAllBloodRequests() {
        List<BloodRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM blood_requests ORDER BY request_date DESC";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                requests.add(extractBloodRequestFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    // Read blood requests by user ID
    public List<BloodRequest> getBloodRequestsByUserId(int userId) {
        List<BloodRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM blood_requests WHERE user_id = ? ORDER BY request_date DESC";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    requests.add(extractBloodRequestFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    // Read blood requests by status
    public List<BloodRequest> getBloodRequestsByStatus(String status) {
        List<BloodRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM blood_requests WHERE status = ? ORDER BY request_date DESC";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    requests.add(extractBloodRequestFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    // Read blood requests by blood group
    public List<BloodRequest> getBloodRequestsByBloodGroup(String bloodGroup) {
        List<BloodRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM blood_requests WHERE blood_group = ? ORDER BY request_date DESC";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, bloodGroup);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    requests.add(extractBloodRequestFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    // Read blood requests by urgency
    public List<BloodRequest> getBloodRequestsByUrgency(String urgency) {
        List<BloodRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM blood_requests WHERE urgency = ? ORDER BY request_date DESC";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, urgency);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    requests.add(extractBloodRequestFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    // Update a blood request
    public boolean updateBloodRequest(BloodRequest request) {
        String sql = "UPDATE blood_requests SET blood_group = ?, quantity = ?, urgency = ?, status = ?, hospital_name = ?, hospital_address = ?, patient_name = ?, contact_person = ?, contact_phone = ?, reason = ?, required_date = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, request.getBloodGroup());
            statement.setInt(2, request.getQuantity());
            statement.setString(3, request.getUrgency());
            statement.setString(4, request.getStatus());
            statement.setString(5, request.getHospitalName());
            statement.setString(6, request.getHospitalAddress());
            statement.setString(7, request.getPatientName());
            statement.setString(8, request.getContactPerson());
            statement.setString(9, request.getContactPhone());
            statement.setString(10, request.getReason());
            statement.setDate(11, request.getRequiredDate());
            statement.setInt(12, request.getId());
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update blood request status
    public boolean updateBloodRequestStatus(int id, String status) {
        String sql = "UPDATE blood_requests SET status = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, id);
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete a blood request
    public boolean deleteBloodRequest(int id) {
        String sql = "DELETE FROM blood_requests WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper method to extract blood request from ResultSet
    private BloodRequest extractBloodRequestFromResultSet(ResultSet resultSet) throws SQLException {
        BloodRequest request = new BloodRequest();
        request.setId(resultSet.getInt("id"));
        request.setUserId(resultSet.getInt("user_id"));
        request.setBloodGroup(resultSet.getString("blood_group"));
        request.setQuantity(resultSet.getInt("quantity"));
        request.setUrgency(resultSet.getString("urgency"));
        request.setStatus(resultSet.getString("status"));
        request.setHospitalName(resultSet.getString("hospital_name"));
        request.setHospitalAddress(resultSet.getString("hospital_address"));
        request.setPatientName(resultSet.getString("patient_name"));
        request.setContactPerson(resultSet.getString("contact_person"));
        request.setContactPhone(resultSet.getString("contact_phone"));
        request.setReason(resultSet.getString("reason"));
        request.setRequestDate(resultSet.getTimestamp("request_date"));
        request.setRequiredDate(resultSet.getDate("required_date"));
        return request;
    }
}

