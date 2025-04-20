package model;

import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonationEventDAO {
    private Connection connection;

    public DonationEventDAO() {
        try {
            connection = DBConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create a new donation event
    public boolean addDonationEvent(DonationEvent event) {
        String sql = "INSERT INTO donation_events (title, description, event_date, start_time, end_time, location, organizer, contact_person, contact_email, contact_phone, max_participants, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, event.getTitle());
            statement.setString(2, event.getDescription());
            statement.setDate(3, event.getEventDate());
            statement.setString(4, event.getStartTime());
            statement.setString(5, event.getEndTime());
            statement.setString(6, event.getLocation());
            statement.setString(7, event.getOrganizer());
            statement.setString(8, event.getContactPerson());
            statement.setString(9, event.getContactEmail());
            statement.setString(10, event.getContactPhone());
            statement.setInt(11, event.getMaxParticipants());
            statement.setTimestamp(12, event.getCreatedAt());
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        event.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Read a donation event by ID
    public DonationEvent getDonationEventById(int id) {
        String sql = "SELECT * FROM donation_events WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractDonationEventFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Read all donation events
    public List<DonationEvent> getAllDonationEvents() {
        List<DonationEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM donation_events ORDER BY event_date DESC";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                events.add(extractDonationEventFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    // Read upcoming donation events
    public List<DonationEvent> getUpcomingDonationEvents() {
        List<DonationEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM donation_events WHERE event_date >= CURRENT_DATE ORDER BY event_date ASC";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                events.add(extractDonationEventFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    // Read past donation events
    public List<DonationEvent> getPastDonationEvents() {
        List<DonationEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM donation_events WHERE event_date < CURRENT_DATE ORDER BY event_date DESC";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                events.add(extractDonationEventFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    // Update a donation event
    public boolean updateDonationEvent(DonationEvent event) {
        String sql = "UPDATE donation_events SET title = ?, description = ?, event_date = ?, start_time = ?, end_time = ?, location = ?, organizer = ?, contact_person = ?, contact_email = ?, contact_phone = ?, max_participants = ?, updated_at = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, event.getTitle());
            statement.setString(2, event.getDescription());
            statement.setDate(3, event.getEventDate());
            statement.setString(4, event.getStartTime());
            statement.setString(5, event.getEndTime());
            statement.setString(6, event.getLocation());
            statement.setString(7, event.getOrganizer());
            statement.setString(8, event.getContactPerson());
            statement.setString(9, event.getContactEmail());
            statement.setString(10, event.getContactPhone());
            statement.setInt(11, event.getMaxParticipants());
            statement.setTimestamp(12, event.getUpdatedAt());
            statement.setInt(13, event.getId());
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete a donation event
    public boolean deleteDonationEvent(int id) {
        String sql = "DELETE FROM donation_events WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get participant count for an event
    public int getParticipantCount(int eventId) {
        String sql = "SELECT COUNT(*) FROM event_participants WHERE event_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, eventId);
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

    // Helper method to extract donation event from ResultSet
    private DonationEvent extractDonationEventFromResultSet(ResultSet resultSet) throws SQLException {
        DonationEvent event = new DonationEvent();
        event.setId(resultSet.getInt("id"));
        event.setTitle(resultSet.getString("title"));
        event.setDescription(resultSet.getString("description"));
        event.setEventDate(resultSet.getDate("event_date"));
        event.setStartTime(resultSet.getString("start_time"));
        event.setEndTime(resultSet.getString("end_time"));
        event.setLocation(resultSet.getString("location"));
        event.setOrganizer(resultSet.getString("organizer"));
        event.setContactPerson(resultSet.getString("contact_person"));
        event.setContactEmail(resultSet.getString("contact_email"));
        event.setContactPhone(resultSet.getString("contact_phone"));
        event.setMaxParticipants(resultSet.getInt("max_participants"));
        event.setCreatedAt(resultSet.getTimestamp("created_at"));
        event.setUpdatedAt(resultSet.getTimestamp("updated_at"));
        return event;
    }

    // Close the database connection
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
