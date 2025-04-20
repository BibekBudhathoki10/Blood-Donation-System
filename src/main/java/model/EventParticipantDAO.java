package model;

import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventParticipantDAO {
    private Connection connection;

    public EventParticipantDAO() {
        try {
            connection = DBConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add a participant to an event
    public boolean addParticipant(EventParticipant participant) {
        String sql = "INSERT INTO event_participants (event_id, user_id, registration_date, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, participant.getEventId());
            stmt.setInt(2, participant.getUserId());
            stmt.setTimestamp(3, participant.getRegistrationDate());
            stmt.setString(4, participant.getStatus());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all participants for an event
    public List<EventParticipant> getParticipantsByEventId(int eventId) {
        List<EventParticipant> participants = new ArrayList<>();
        String sql = "SELECT * FROM event_participants WHERE event_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                EventParticipant participant = new EventParticipant();
                participant.setId(rs.getInt("id"));
                participant.setEventId(rs.getInt("event_id"));
                participant.setUserId(rs.getInt("user_id"));
                participant.setRegistrationDate(rs.getTimestamp("registration_date"));
                participant.setStatus(rs.getString("status"));

                participants.add(participant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return participants;
    }

    // Get all participants for an event with user details
    public List<EventParticipantDTO> getParticipantsWithUserDetailsByEventId(int eventId) {
        List<EventParticipantDTO> participants = new ArrayList<>();
        String sql = "SELECT ep.*, u.name, u.email, u.phone FROM event_participants ep " +
                "JOIN users u ON ep.user_id = u.id " +
                "WHERE ep.event_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                EventParticipantDTO participant = new EventParticipantDTO();
                participant.setId(rs.getInt("id"));
                participant.setEventId(rs.getInt("event_id"));
                participant.setUserId(rs.getInt("user_id"));
                participant.setRegistrationDate(rs.getTimestamp("registration_date"));
                participant.setStatus(rs.getString("status"));
                participant.setName(rs.getString("name"));
                participant.setEmail(rs.getString("email"));
                participant.setPhone(rs.getString("phone"));

                participants.add(participant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return participants;
    }

    // Update participant status
    public boolean updateParticipantStatus(int participantId, String status) {
        String sql = "UPDATE event_participants SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, participantId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Remove a participant from an event
    public boolean removeParticipant(int participantId) {
        String sql = "DELETE FROM event_participants WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, participantId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Check if a user is already registered for an event
    public boolean isUserRegisteredForEvent(int userId, int eventId) {
        String sql = "SELECT COUNT(*) FROM event_participants WHERE user_id = ? AND event_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, eventId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Get participant count for an event
    public int getParticipantCountForEvent(int eventId) {
        String sql = "SELECT COUNT(*) FROM event_participants WHERE event_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
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

    // Read an event participant by ID
    public EventParticipant getEventParticipantById(int id) {
        String sql = "SELECT * FROM event_participants WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractEventParticipantFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Read all event participants
    public List<EventParticipant> getAllEventParticipants() {
        List<EventParticipant> participants = new ArrayList<>();
        String sql = "SELECT * FROM event_participants";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                participants.add(extractEventParticipantFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participants;
    }

    // Read event participants by user ID
    public List<EventParticipant> getParticipantsByUserId(int userId) {
        List<EventParticipant> participants = new ArrayList<>();
        String sql = "SELECT * FROM event_participants WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    participants.add(extractEventParticipantFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participants;
    }

    // Read event participants by status
    public List<EventParticipant> getParticipantsByStatus(String status) {
        List<EventParticipant> participants = new ArrayList<>();
        String sql = "SELECT * FROM event_participants WHERE status = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    participants.add(extractEventParticipantFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participants;
    }

    // Update an event participant
    public boolean updateEventParticipant(EventParticipant participant) {
        String sql = "UPDATE event_participants SET event_id = ?, user_id = ?, status = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, participant.getEventId());
            statement.setInt(2, participant.getUserId());
            statement.setString(3, participant.getStatus());
            statement.setInt(4, participant.getId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update event participant status
    public boolean updateEventParticipantStatus(int id, String status) {
        String sql = "UPDATE event_participants SET status = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, id);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cancel event participation
    public boolean cancelEventParticipation(int userId, int eventId) {
        String sql = "UPDATE event_participants SET status = 'cancelled' WHERE user_id = ? AND event_id = ? AND status = 'registered'";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, eventId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete an event participant
    public boolean deleteEventParticipant(int id) {
        String sql = "DELETE FROM event_participants WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper method to extract event participant from ResultSet
    private EventParticipant extractEventParticipantFromResultSet(ResultSet resultSet) throws SQLException {
        EventParticipant participant = new EventParticipant();
        participant.setId(resultSet.getInt("id"));
        participant.setEventId(resultSet.getInt("event_id"));
        participant.setUserId(resultSet.getInt("user_id"));
        participant.setRegistrationDate(resultSet.getTimestamp("registration_date"));
        participant.setStatus(resultSet.getString("status"));
        return participant;
    }
}
