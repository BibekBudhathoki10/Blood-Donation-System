package model;

import java.sql.Date;

public class Donor {
    private int id;
    private int userId;
    private String bloodGroup;
    private Date lastDonationDate;
    private boolean available;
    private String medicalHistory;
    private int donationCount;
    private String location;

    // Constructors
    public Donor() {
    }

    public Donor(int id, int userId, String bloodGroup, Date lastDonationDate, boolean available, 
                String medicalHistory, int donationCount, String location) {
        this.id = id;
        this.userId = userId;
        this.bloodGroup = bloodGroup;
        this.lastDonationDate = lastDonationDate;
        this.available = available;
        this.medicalHistory = medicalHistory;
        this.donationCount = donationCount;
        this.location = location;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public Date getLastDonationDate() {
        return lastDonationDate;
    }

    public void setLastDonationDate(Date lastDonationDate) {
        this.lastDonationDate = lastDonationDate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public int getDonationCount() {
        return donationCount;
    }

    public void setDonationCount(int donationCount) {
        this.donationCount = donationCount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Donor{" +
                "id=" + id +
                ", userId=" + userId +
                ", bloodGroup='" + bloodGroup + '\'' +
                ", lastDonationDate=" + lastDonationDate +
                ", available=" + available +
                ", medicalHistory='" + medicalHistory + '\'' +
                ", donationCount=" + donationCount +
                ", location='" + location + '\'' +
                '}';
    }
}

