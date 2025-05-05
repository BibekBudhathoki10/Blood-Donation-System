package model;

import java.sql.Date;
import java.sql.Timestamp;

public class BloodRequest {
    private int id;
    private int userId;
    // Add the donorId field after the userId field
    private int donorId;
    private String bloodGroup;
    private int quantity;
    private String urgency; // normal, urgent, critical
    private String status; // pending, approved, rejected, fulfilled
    private String hospitalName;
    private String hospitalAddress;
    private String patientName;
    private String contactPerson;
    private String contactPhone;
    private String reason;
    private Timestamp requestDate;
    private Date requiredDate;

    // Constructors
    public BloodRequest() {
    }

    public BloodRequest(int id, int userId, int donorId, String bloodGroup, int quantity, String urgency, String status,
                       String hospitalName, String hospitalAddress, String patientName, String contactPerson,
                       String contactPhone, String reason, Timestamp requestDate, Date requiredDate) {
        this.id = id;
        this.userId = userId;
        this.donorId = donorId;
        this.bloodGroup = bloodGroup;
        this.quantity = quantity;
        this.urgency = urgency;
        this.status = status;
        this.hospitalName = hospitalName;
        this.hospitalAddress = hospitalAddress;
        this.patientName = patientName;
        this.contactPerson = contactPerson;
        this.contactPhone = contactPhone;
        this.reason = reason;
        this.requestDate = requestDate;
        this.requiredDate = requiredDate;
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

    // For backward compatibility with any code that might use getRequesterId
    public int getRequesterId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Add the getter and setter for donorId after the userId getter/setter methods
    public int getDonorId() {
        return donorId;
    }

    public void setDonorId(int donorId) {
        this.donorId = donorId;
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

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getHospitalAddress() {
        return hospitalAddress;
    }

    public void setHospitalAddress(String hospitalAddress) {
        this.hospitalAddress = hospitalAddress;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Timestamp getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Timestamp requestDate) {
        this.requestDate = requestDate;
    }

    public Date getRequiredDate() {
        return requiredDate;
    }

    public void setRequiredDate(Date requiredDate) {
        this.requiredDate = requiredDate;
    }

    @Override
    public String toString() {
        return "BloodRequest{" +
                "id=" + id +
                ", userId=" + userId +
                ", donorId=" + donorId +
                ", bloodGroup='" + bloodGroup + '\'' +
                ", quantity=" + quantity +
                ", urgency='" + urgency + '\'' +
                ", status='" + status + '\'' +
                ", hospitalName='" + hospitalName + '\'' +
                ", hospitalAddress='" + hospitalAddress + '\'' +
                ", patientName='" + patientName + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", reason='" + reason + '\'' +
                ", requestDate=" + requestDate +
                ", requiredDate=" + requiredDate +
                '}';
    }
}
