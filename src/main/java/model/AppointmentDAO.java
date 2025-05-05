package model;

import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppointmentDAO {
    private Connection connection;

    public AppointmentDAO() {
        try {
            connection = DBConnection.getConnection();
            // Check if connection is valid
            if (connection == null || connection.isClosed()) {
                System.err.println("Error: Database connection is null or closed in AppointmentDAO constructor");
            } else {
                System.out.println("AppointmentDAO initialized with valid connection");
                // Ensure the blood_request_appointments table exists
                createBloodRequestAppointmentsTable();
            }
        } catch (SQLException e) {
            System.err.println("Error initializing AppointmentDAO: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Create the blood_request_appointments table if it doesn't exist
    private void createBloodRequestAppointmentsTable() {
        try {
            if (!tableExists(connection, "blood_request_appointments")) {
                System.out.println("Creating blood_request_appointments table...");
                Statement statement = connection.createStatement();
                String sql = "CREATE TABLE blood_request_appointments (" +
                             "id INT AUTO_INCREMENT PRIMARY KEY, " +
                             "blood_request_id INT NOT NULL, " +
                             "appointment_id INT NOT NULL, " +
                             "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                             "FOREIGN KEY (blood_request_id) REFERENCES blood_requests(id) ON DELETE CASCADE, " +
                             "FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE, " +
                             "UNIQUE (blood_request_id, appointment_id)" +
                             ")";
                statement.executeUpdate(sql);
                System.out.println("blood_request_appointments table created successfully");
                statement.close();
            } else {
                System.out.println("blood_request_appointments table already exists");
            }
        } catch (SQLException e) {
            System.err.println("Error creating blood_request_appointments table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Check if the appointments table exists
    public boolean checkTableExists() {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                if (tableName.equalsIgnoreCase("appointments")) {
                    tables.close();
                    return true;
                }
            }
            tables.close();
        } catch (SQLException e) {
            System.err.println("Error checking if table exists: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Update the addAppointment method with better error handling
    public boolean addAppointment(Appointment appointment) {
        StringBuilder errorDetails = new StringBuilder();
        return addAppointment(appointment, errorDetails);
    }
    
    // Overloaded method with error details parameter
    public boolean addAppointment(Appointment appointment, StringBuilder errorDetails) {
        Connection localConnection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        boolean originalAutoCommit = true;
        
        try {
            // Get a fresh connection to ensure it's valid
            localConnection = DBConnection.getConnection();
            
            // Check connection
            if (localConnection == null || localConnection.isClosed()) {
                errorDetails.append("Database connection is null or closed");
                System.err.println(errorDetails.toString());
                return false;
            }
            
            // Print database information for debugging
            printDatabaseInfo(localConnection, errorDetails);
            
            // Save original auto-commit state and start transaction
            originalAutoCommit = localConnection.getAutoCommit();
            localConnection.setAutoCommit(false);
            
            // Validate required fields
            if (appointment.getDonorId() <= 0) {
                errorDetails.append("Error: Invalid donor ID: ").append(appointment.getDonorId());
                System.err.println(errorDetails.toString());
                return false;
            }
            
            if (appointment.getAppointmentDate() == null) {
                errorDetails.append("Error: Appointment date is null");
                System.err.println(errorDetails.toString());
                return false;
            }
            
            if (appointment.getAppointmentTime() == null) {
                errorDetails.append("Error: Appointment time is null");
                System.err.println(errorDetails.toString());
                return false;
            }
            
            // Debug: Print appointment time value
            System.out.println("Appointment time value: " + appointment.getAppointmentTime());
            System.out.println("Appointment time class: " + appointment.getAppointmentTime().getClass().getName());
            
            // Check if table exists before proceeding
            if (!tableExists(localConnection, "appointments")) {
                errorDetails.append("Error: Appointments table does not exist in the database");
                System.err.println(errorDetails.toString());
                return false;
            }
            
            // Get the actual table name with correct case
            String actualTableName = getActualTableName(localConnection, "appointments");
            if (actualTableName == null) {
                errorDetails.append("Error: Could not determine actual table name for 'appointments'");
                System.err.println(errorDetails.toString());
                return false;
            }
            
            // Prepare notes field with blood request ID if available
            String notes = appointment.getNotes();
            if (appointment.getBloodRequestId() != null && appointment.getBloodRequestId() > 0) {
                String bloodRequestTag = "[BloodRequestID:" + appointment.getBloodRequestId() + "]";
                if (notes == null || notes.isEmpty()) {
                    notes = bloodRequestTag;
                } else if (!notes.contains(bloodRequestTag)) {
                    notes = bloodRequestTag + " " + notes;
                }
                System.out.println("Added blood request ID tag to notes: " + notes);
            } else {
                System.out.println("No blood request ID available for this appointment");
            }
            
            // Use the actual table name in the SQL query - MODIFIED to match the existing schema
            String sql = "INSERT INTO " + actualTableName + " (donor_id, appointment_date, appointment_time, status, notes) VALUES (?, ?, ?, ?, ?)";
            statement = localConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            System.out.println("Adding appointment with donor_id: " + appointment.getDonorId() + 
                              ", date: " + appointment.getAppointmentDate() + 
                              ", time: " + appointment.getAppointmentTime() + 
                              ", status: " + appointment.getStatus() +
                              ", blood request ID: " + appointment.getBloodRequestId());
            
            statement.setInt(1, appointment.getDonorId());
            statement.setDate(2, appointment.getAppointmentDate());
            statement.setTime(3, appointment.getAppointmentTime());
            statement.setString(4, appointment.getStatus() != null ? appointment.getStatus() : "scheduled");
            statement.setString(5, notes);
            
            // Execute the insert
            System.out.println("Executing SQL: " + sql);
            System.out.println("With parameters: " + appointment.getDonorId() + ", " + 
                              appointment.getAppointmentDate() + ", " + 
                              appointment.getAppointmentTime() + ", " + 
                              (appointment.getStatus() != null ? appointment.getStatus() : "scheduled") + ", " + 
                              notes);
            
            int affectedRows = statement.executeUpdate();
            System.out.println("Affected rows: " + affectedRows);
            
            if (affectedRows > 0) {
                // Get generated ID
                generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int appointmentId = generatedKeys.getInt(1);
                    appointment.setId(appointmentId);
                    System.out.println("Appointment created successfully with ID: " + appointment.getId());
                    
                    // If there's a blood request ID, link it in the blood_request_appointments table
                    if (appointment.getBloodRequestId() != null && appointment.getBloodRequestId() > 0) {
                        boolean linked = linkAppointmentToBloodRequest(localConnection, appointment.getId(), appointment.getBloodRequestId());
                        if (!linked) {
                            errorDetails.append("Warning: Failed to link appointment to blood request in the blood_request_appointments table");
                            System.err.println(errorDetails.toString());
                            // Continue anyway, as the appointment was created successfully
                        }
                    }
                    
                    // Commit the transaction
                    localConnection.commit();
                    return true;
                } else {
                    errorDetails.append("Failed to get generated ID for appointment");
                    System.err.println(errorDetails.toString());
                    localConnection.rollback();
                }
            } else {
                errorDetails.append("No rows affected when adding appointment");
                System.err.println(errorDetails.toString());
                localConnection.rollback();
            }
        } catch (SQLException e) {
            errorDetails.append("SQL Exception in addAppointment: ").append(e.getMessage());
            System.err.println(errorDetails.toString());
            e.printStackTrace();
    
            // Check for specific SQL errors
            if (e.getMessage().contains("foreign key constraint")) {
                errorDetails.append(" Foreign key constraint violation. Check donor_id.");
                System.err.println("Foreign key constraint violation. Check donor_id.");
            } else if (e.getMessage().contains("unique constraint")) {
                errorDetails.append(" Unique constraint violation. Appointment may already exist.");
                System.err.println("Unique constraint violation. Appointment may already exist.");
            } else if (e.getMessage().contains("Table") && e.getMessage().contains("doesn't exist")) {
                errorDetails.append(" Table doesn't exist. Database schema may be incorrect.");
                System.err.println("Table doesn't exist. Database schema may be incorrect.");
            } else if (e.getMessage().contains("Data truncation")) {
                errorDetails.append(" Data truncation error. Check the format of date, time, or other fields.");
                System.err.println("Data truncation error. Check the format of date, time, or other fields.");
            } else if (e.getMessage().contains("Incorrect")) {
                errorDetails.append(" Incorrect data format. Check the format of date, time, or other fields.");
                System.err.println("Incorrect data format. Check the format of date, time, or other fields.");
            }
    
            // Rollback transaction on error
            try {
                if (localConnection != null && !localConnection.isClosed()) {
                    localConnection.rollback();
                }
            } catch (SQLException rollbackEx) {
                errorDetails.append(" Error rolling back transaction: ").append(rollbackEx.getMessage());
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
        } catch (Exception e) {
            errorDetails.append("General Exception in addAppointment: ").append(e.getMessage());
            System.err.println(errorDetails.toString());
            e.printStackTrace();
    
            // Rollback transaction on error
            try {
                if (localConnection != null && !localConnection.isClosed()) {
                    localConnection.rollback();
                }
            } catch (SQLException rollbackEx) {
                errorDetails.append(" Error rolling back transaction: ").append(rollbackEx.getMessage());
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
        } finally {
            // Close resources and restore auto-commit state
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (statement != null) statement.close();
                if (localConnection != null && !localConnection.isClosed()) {
                    localConnection.setAutoCommit(originalAutoCommit);
                }
            } catch (SQLException e) {
                errorDetails.append(" Error closing resources: ").append(e.getMessage());
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        return false;
    }
    
    // Link an appointment to a blood request in the blood_request_appointments table
    private boolean linkAppointmentToBloodRequest(Connection conn, int appointmentId, int bloodRequestId) {
        PreparedStatement statement = null;
        try {
            // Check if the blood_request_appointments table exists, create it if not
            if (!tableExists(conn, "blood_request_appointments")) {
                createBloodRequestAppointmentsTable();
            }
            
            // Insert the link
            String sql = "INSERT INTO blood_request_appointments (blood_request_id, appointment_id) VALUES (?, ?)";
            statement = conn.prepareStatement(sql);
            statement.setInt(1, bloodRequestId);
            statement.setInt(2, appointmentId);
            
            System.out.println("Linking appointment ID: " + appointmentId + " to blood request ID: " + bloodRequestId);
            int affectedRows = statement.executeUpdate();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error linking appointment to blood request: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                System.err.println("Error closing statement: " + e.getMessage());
            }
        }
    }
    
    // Helper method to get the actual table name with correct case
    private String getActualTableName(Connection conn, String tableName) {
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            
            while (tables.next()) {
                String currentTable = tables.getString("TABLE_NAME");
                if (currentTable.equalsIgnoreCase(tableName)) {
                    String actualName = currentTable;
                    tables.close();
                    return actualName;
                }
            }
            tables.close();
        } catch (SQLException e) {
            System.err.println("Error getting actual table name: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // Helper method to check if a table exists
    private boolean tableExists(Connection conn, String tableName) {
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            
            while (tables.next()) {
                String currentTable = tables.getString("TABLE_NAME");
                if (currentTable.equalsIgnoreCase(tableName)) {
                    tables.close();
                    return true;
                }
            }
            tables.close();
        } catch (SQLException e) {
            System.err.println("Error checking if table exists: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Helper method to print database information
    private void printDatabaseInfo(Connection conn, StringBuilder errorBuilder) {
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            String dbProduct = metaData.getDatabaseProductName();
            String dbVersion = metaData.getDatabaseProductVersion();
            
            System.out.println("Database Product: " + dbProduct);
            System.out.println("Database Version: " + dbVersion);
            
            // List all tables
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            System.out.println("Tables in database:");
            while (tables.next()) {
                System.out.println(" - " + tables.getString("TABLE_NAME"));
            }
            tables.close();
        } catch (SQLException e) {
            errorBuilder.append("Error getting database info: ").append(e.getMessage());
            System.err.println("Error getting database info: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Read an appointment by ID
    public Appointment getAppointmentById(int id) {
        String sql = "SELECT * FROM appointments WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Appointment appointment = extractAppointmentFromResultSet(resultSet);
                    
                    // Get blood request ID from the blood_request_appointments table
                    Integer bloodRequestId = getBloodRequestIdForAppointment(id);
                    if (bloodRequestId != null) {
                        appointment.setBloodRequestId(bloodRequestId);
                    }
                    
                    return appointment;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting appointment by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Read all appointments
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments ORDER BY appointment_date, appointment_time";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Appointment appointment = extractAppointmentFromResultSet(resultSet);
                appointments.add(appointment);
            }
            
            // Get blood request IDs for all appointments
            if (!appointments.isEmpty()) {
                Map<Integer, Integer> appointmentToBloodRequestMap = getAllAppointmentBloodRequestLinks();
                for (Appointment appointment : appointments) {
                    Integer bloodRequestId = appointmentToBloodRequestMap.get(appointment.getId());
                    if (bloodRequestId != null) {
                        appointment.setBloodRequestId(bloodRequestId);
                    } else {
                        // Try to extract from notes as a fallback
                        Integer extractedId = extractBloodRequestIdFromNotes(appointment.getNotes());
                        if (extractedId != null) {
                            appointment.setBloodRequestId(extractedId);
                            // Add to the link table for future queries
                            linkAppointmentToBloodRequest(connection, appointment.getId(), extractedId);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting all appointments: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    // Read appointments by donor ID
    public List<Appointment> getAppointmentsByDonorId(int donorId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE donor_id = ? ORDER BY appointment_date, appointment_time";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, donorId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Appointment appointment = extractAppointmentFromResultSet(resultSet);
                    appointments.add(appointment);
                }
            }
            
            // Get blood request IDs for these appointments
            if (!appointments.isEmpty()) {
                Map<Integer, Integer> appointmentToBloodRequestMap = getAllAppointmentBloodRequestLinks();
                for (Appointment appointment : appointments) {
                    Integer bloodRequestId = appointmentToBloodRequestMap.get(appointment.getId());
                    if (bloodRequestId != null) {
                        appointment.setBloodRequestId(bloodRequestId);
                    } else {
                        // Try to extract from notes as a fallback
                        Integer extractedId = extractBloodRequestIdFromNotes(appointment.getNotes());
                        if (extractedId != null) {
                            appointment.setBloodRequestId(extractedId);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting appointments by donor ID: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    // Read appointments by blood request ID - Improved to use the blood_request_appointments table
    public List<Appointment> getAppointmentsByBloodRequestId(int bloodRequestId) {
        List<Appointment> appointments = new ArrayList<>();
        
        // First try to find appointments using the blood_request_appointments table
        if (tableExists(connection, "blood_request_appointments")) {
            String sql = "SELECT a.* FROM appointments a " +
                         "JOIN blood_request_appointments bra ON a.id = bra.appointment_id " +
                         "WHERE bra.blood_request_id = ? " +
                         "ORDER BY a.appointment_date, a.appointment_time";
            
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, bloodRequestId);
                System.out.println("Executing SQL: " + sql + " with parameter: " + bloodRequestId);
                
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Appointment appointment = extractAppointmentFromResultSet(resultSet);
                        appointment.setBloodRequestId(bloodRequestId);
                        appointments.add(appointment);
                        System.out.println("Found appointment ID: " + appointment.getId() + " for blood request ID: " + bloodRequestId);
                    }
                }
                
                System.out.println("Found " + appointments.size() + " appointments for blood request ID: " + bloodRequestId);
            } catch (SQLException e) {
                System.err.println("Error getting appointments by blood request ID from link table: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // If no appointments found, try the fallback method with notes
        if (appointments.isEmpty()) {
            String sql = "SELECT * FROM appointments WHERE notes LIKE ? ORDER BY appointment_date, appointment_time";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, "%[BloodRequestID:" + bloodRequestId + "]%");
                System.out.println("Executing fallback SQL: " + sql + " with parameter: %[BloodRequestID:" + bloodRequestId + "]%");
                
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Appointment appointment = extractAppointmentFromResultSet(resultSet);
                        appointment.setBloodRequestId(bloodRequestId);
                        appointments.add(appointment);
                        System.out.println("Found appointment ID: " + appointment.getId() + " for blood request ID: " + bloodRequestId + " using notes");
                        
                        // Add to the link table for future queries
                        linkAppointmentToBloodRequest(connection, appointment.getId(), bloodRequestId);
                    }
                }
                
                System.out.println("Found " + appointments.size() + " appointments for blood request ID: " + bloodRequestId + " using notes");
            } catch (SQLException e) {
                System.err.println("Error getting appointments by blood request ID from notes: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return appointments;
    }

    // Read appointments by status
    public List<Appointment> getAppointmentsByStatus(String status) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE status = ? ORDER BY appointment_date, appointment_time";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Appointment appointment = extractAppointmentFromResultSet(resultSet);
                    appointments.add(appointment);
                }
            }
            
            // Get blood request IDs for these appointments
            if (!appointments.isEmpty()) {
                Map<Integer, Integer> appointmentToBloodRequestMap = getAllAppointmentBloodRequestLinks();
                for (Appointment appointment : appointments) {
                    Integer bloodRequestId = appointmentToBloodRequestMap.get(appointment.getId());
                    if (bloodRequestId != null) {
                        appointment.setBloodRequestId(bloodRequestId);
                    } else {
                        // Try to extract from notes as a fallback
                        Integer extractedId = extractBloodRequestIdFromNotes(appointment.getNotes());
                        if (extractedId != null) {
                            appointment.setBloodRequestId(extractedId);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting appointments by status: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    // Read appointments by date
    public List<Appointment> getAppointmentsByDate(Date date) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE appointment_date = ? ORDER BY appointment_time";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, date);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Appointment appointment = extractAppointmentFromResultSet(resultSet);
                    appointments.add(appointment);
                }
            }
            
            // Get blood request IDs for these appointments
            if (!appointments.isEmpty()) {
                Map<Integer, Integer> appointmentToBloodRequestMap = getAllAppointmentBloodRequestLinks();
                for (Appointment appointment : appointments) {
                    Integer bloodRequestId = appointmentToBloodRequestMap.get(appointment.getId());
                    if (bloodRequestId != null) {
                        appointment.setBloodRequestId(bloodRequestId);
                    } else {
                        // Try to extract from notes as a fallback
                        Integer extractedId = extractBloodRequestIdFromNotes(appointment.getNotes());
                        if (extractedId != null) {
                            appointment.setBloodRequestId(extractedId);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting appointments by date: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    // Read upcoming appointments
    public List<Appointment> getUpcomingAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE appointment_date >= CURRENT_DATE AND status = 'scheduled' ORDER BY appointment_date, appointment_time";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Appointment appointment = extractAppointmentFromResultSet(resultSet);
                appointments.add(appointment);
            }
            
            // Get blood request IDs for these appointments
            if (!appointments.isEmpty()) {
                Map<Integer, Integer> appointmentToBloodRequestMap = getAllAppointmentBloodRequestLinks();
                for (Appointment appointment : appointments) {
                    Integer bloodRequestId = appointmentToBloodRequestMap.get(appointment.getId());
                    if (bloodRequestId != null) {
                        appointment.setBloodRequestId(bloodRequestId);
                    } else {
                        // Try to extract from notes as a fallback
                        Integer extractedId = extractBloodRequestIdFromNotes(appointment.getNotes());
                        if (extractedId != null) {
                            appointment.setBloodRequestId(extractedId);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting upcoming appointments: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    // Read upcoming appointments for a donor
    public List<Appointment> getUpcomingAppointmentsForDonor(int donorId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE donor_id = ? AND appointment_date >= CURRENT_DATE AND status = 'scheduled' ORDER BY appointment_date, appointment_time";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, donorId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Appointment appointment = extractAppointmentFromResultSet(resultSet);
                    appointments.add(appointment);
                }
            }
            
            // Get blood request IDs for these appointments
            if (!appointments.isEmpty()) {
                Map<Integer, Integer> appointmentToBloodRequestMap = getAllAppointmentBloodRequestLinks();
                for (Appointment appointment : appointments) {
                    Integer bloodRequestId = appointmentToBloodRequestMap.get(appointment.getId());
                    if (bloodRequestId != null) {
                        appointment.setBloodRequestId(bloodRequestId);
                    } else {
                        // Try to extract from notes as a fallback
                        Integer extractedId = extractBloodRequestIdFromNotes(appointment.getNotes());
                        if (extractedId != null) {
                            appointment.setBloodRequestId(extractedId);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting upcoming appointments for donor: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    // Update an appointment with transaction support - Modified to preserve blood request ID in notes
    public boolean updateAppointment(Appointment appointment) {
        Connection localConnection = null;
        PreparedStatement statement = null;
        boolean originalAutoCommit = true;
        
        try {
            // Get a fresh connection
            localConnection = DBConnection.getConnection();
            
            // Save original auto-commit state and start transaction
            originalAutoCommit = localConnection.getAutoCommit();
            localConnection.setAutoCommit(false);
            
            // First, get the current appointment to preserve any blood request ID in notes
            Appointment currentAppointment = getAppointmentById(appointment.getId());
            String notes = appointment.getNotes();
            
            // If the current appointment has a blood request ID in notes, preserve it
            if (currentAppointment != null) {
                Integer bloodRequestId = extractBloodRequestIdFromNotes(currentAppointment.getNotes());
                if (bloodRequestId != null) {
                    String bloodRequestTag = "[BloodRequestID:" + bloodRequestId + "]";
                    if (notes == null || notes.isEmpty()) {
                        notes = bloodRequestTag;
                    } else if (!notes.contains(bloodRequestTag)) {
                        notes = bloodRequestTag + " " + notes;
                    }
                }
            }
            
            // If the appointment has a new blood request ID, add it to notes
            if (appointment.getBloodRequestId() != null && appointment.getBloodRequestId() > 0) {
                String bloodRequestTag = "[BloodRequestID:" + appointment.getBloodRequestId() + "]";
                if (notes == null || notes.isEmpty()) {
                    notes = bloodRequestTag;
                } else if (!notes.contains(bloodRequestTag)) {
                    notes = bloodRequestTag + " " + notes;
                }
                
                // Update the blood_request_appointments table
                if (tableExists(localConnection, "blood_request_appointments")) {
                    // First check if there's already a link
                    Integer existingBloodRequestId = getBloodRequestIdForAppointment(appointment.getId());
                    
                    if (existingBloodRequestId == null || !existingBloodRequestId.equals(appointment.getBloodRequestId())) {
                        // Remove any existing link
                        if (existingBloodRequestId != null) {
                            removeAppointmentBloodRequestLink(localConnection, appointment.getId());
                        }
                        
                        // Add the new link
                        linkAppointmentToBloodRequest(localConnection, appointment.getId(), appointment.getBloodRequestId());
                    }
                }
            }
            
            String sql = "UPDATE appointments SET appointment_date = ?, appointment_time = ?, status = ?, notes = ? WHERE id = ?";
            statement = localConnection.prepareStatement(sql);
            
            statement.setDate(1, appointment.getAppointmentDate());
            statement.setTime(2, appointment.getAppointmentTime());
            statement.setString(3, appointment.getStatus());
            statement.setString(4, notes);
            statement.setInt(5, appointment.getId());
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                localConnection.commit();
                return true;
            } else {
                localConnection.rollback();
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error updating appointment: " + e.getMessage());
            e.printStackTrace();
            
            // Rollback transaction on error
            try {
                if (localConnection != null) {
                    localConnection.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
        } finally {
            // Close resources and restore auto-commit state
            try {
                if (statement != null) statement.close();
                if (localConnection != null) {
                    localConnection.setAutoCommit(originalAutoCommit);
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        return false;
    }

    // Update appointment status
    public boolean updateAppointmentStatus(int id, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, id);
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating appointment status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Delete an appointment
    public boolean deleteAppointment(int id) {
        // First remove any links in the blood_request_appointments table
        removeAppointmentBloodRequestLink(connection, id);
        
        // Then delete the appointment
        String sql = "DELETE FROM appointments WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting appointment: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Remove a link between an appointment and a blood request
    private boolean removeAppointmentBloodRequestLink(Connection conn, int appointmentId) {
        if (!tableExists(conn, "blood_request_appointments")) {
            return false;
        }
        
        String sql = "DELETE FROM blood_request_appointments WHERE appointment_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, appointmentId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing appointment-blood request link: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Get the blood request ID for an appointment from the blood_request_appointments table
    private Integer getBloodRequestIdForAppointment(int appointmentId) {
        if (!tableExists(connection, "blood_request_appointments")) {
            return null;
        }
        
        String sql = "SELECT blood_request_id FROM blood_request_appointments WHERE appointment_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, appointmentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("blood_request_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting blood request ID for appointment: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Get all appointment-blood request links
    private Map<Integer, Integer> getAllAppointmentBloodRequestLinks() {
        Map<Integer, Integer> links = new HashMap<>();
        
        if (!tableExists(connection, "blood_request_appointments")) {
            return links;
        }
        
        String sql = "SELECT appointment_id, blood_request_id FROM blood_request_appointments";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int appointmentId = resultSet.getInt("appointment_id");
                int bloodRequestId = resultSet.getInt("blood_request_id");
                links.put(appointmentId, bloodRequestId);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all appointment-blood request links: " + e.getMessage());
            e.printStackTrace();
        }
        return links;
    }

    // Helper method to extract blood request ID from notes
    private Integer extractBloodRequestIdFromNotes(String notes) {
        if (notes == null || notes.isEmpty()) {
            return null;
        }
        
        Pattern pattern = Pattern.compile("\\[BloodRequestID:(\\d+)\\]");
        Matcher matcher = pattern.matcher(notes);
        
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        return null;
    }

    // Helper method to extract appointment from ResultSet - Modified to extract blood request ID from notes
    private Appointment extractAppointmentFromResultSet(ResultSet resultSet) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setId(resultSet.getInt("id"));
        appointment.setDonorId(resultSet.getInt("donor_id"));
        appointment.setAppointmentDate(resultSet.getDate("appointment_date"));
        appointment.setAppointmentTime(resultSet.getTime("appointment_time"));
        appointment.setStatus(resultSet.getString("status"));
        
        String notes = resultSet.getString("notes");
        appointment.setNotes(notes);
        
        // Extract blood request ID from notes if present
        Integer bloodRequestId = extractBloodRequestIdFromNotes(notes);
        if (bloodRequestId != null) {
            appointment.setBloodRequestId(bloodRequestId);
            System.out.println("Extracted blood request ID " + bloodRequestId + " from notes: " + notes);
        }
        
        // Handle created_at which might be null
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            appointment.setCreatedAt(createdAt);
        }
        
        return appointment;
    }
    
    // Test method to directly execute SQL and check for errors
    public boolean testDatabaseAccess() {
        try {
            // Test if we can execute a simple query
            String sql = "SELECT 1";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {
                if (resultSet.next()) {
                    System.out.println("Database access test successful");
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Database access test failed: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Method to describe the appointments table structure
    public String describeTable() {
        StringBuilder description = new StringBuilder();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            
            // Get the actual table name with correct case
            String actualTableName = getActualTableName(connection, "appointments");
            if (actualTableName == null) {
                return "Table 'appointments' not found";
            }
            
            ResultSet columns = metaData.getColumns(null, null, actualTableName, null);
            
            description.append("Table structure for '").append(actualTableName).append("':\n");
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String dataType = columns.getString("TYPE_NAME");
                String nullable = columns.getInt("NULLABLE") == 1 ? "NULL" : "NOT NULL";
                
                description.append(columnName).append(" ").append(dataType)
                          .append(" ").append(nullable).append("\n");
            }
            columns.close();
        } catch (SQLException e) {
            description.append("Error describing table: ").append(e.getMessage());
            e.printStackTrace();
        }
        return description.toString();
    }
    
    // Method to list all tables in the database
    public List<String> listAllTables() {
        List<String> tableList = new ArrayList<>();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            
            while (tables.next()) {
                tableList.add(tables.getString("TABLE_NAME"));
            }
            tables.close();
        } catch (SQLException e) {
            System.err.println("Error listing tables: " + e.getMessage());
            e.printStackTrace();
        }
        return tableList;
    }
}
