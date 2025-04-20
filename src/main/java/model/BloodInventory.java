package model;

import java.sql.Date;

public class BloodInventory {
    private int id;
    private String bloodGroup;
    private int quantity; // in units
    private Date collectionDate;
    private Date expiryDate;
    private String status; // available, reserved, used, expired
    private int donorId;
    private String location;

    // Constructors
    public BloodInventory() {
    }

    public BloodInventory(int id, String bloodGroup, int quantity, Date collectionDate, Date expiryDate, 
                         String status, int donorId, String location) {
        this.id = id;
        this.bloodGroup = bloodGroup;
        this.quantity = quantity;
        this.collectionDate = collectionDate;
        this.expiryDate = expiryDate;
        this.status = status;
        this.donorId = donorId;
        this.location = location;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(Date collectionDate) {
        this.collectionDate = collectionDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getDonorId() {
        return donorId;
    }

    public void setDonorId(int donorId) {
        this.donorId = donorId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "BloodInventory{" +
                "id=" + id +
                ", bloodGroup='" + bloodGroup + '\'' +
                ", quantity=" + quantity +
                ", collectionDate=" + collectionDate +
                ", expiryDate=" + expiryDate +
                ", status='" + status + '\'' +
                ", donorId=" + donorId +
                ", location='" + location + '\'' +
                '}';
    }
}

