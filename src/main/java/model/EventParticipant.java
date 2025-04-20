package model;

import java.sql.Timestamp;

public class EventParticipant {
    private int id;
    private int eventId;
    private int userId;
    private Timestamp registrationDate;
    private String status; // registered, attended, cancelled

    // Constructors
    public EventParticipant() {
    }

    public EventParticipant(int id, int eventId, int userId, Timestamp registrationDate, String status) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.registrationDate = registrationDate;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Timestamp registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "EventParticipant{" +
                "id=" + id +
                ", eventId=" + eventId +
                ", userId=" + userId +
                ", registrationDate=" + registrationDate +
                ", status='" + status + '\'' +
                '}';
    }
}

