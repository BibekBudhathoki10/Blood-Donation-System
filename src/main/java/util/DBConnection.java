package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBConnection {
    private static Connection connection = null;
    
    // Database configuration
    private static final String URL = "jdbc:mysql://localhost:3306/blood_donation_system";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    // Get database connection
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("Connecting to database: " + URL);
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connection established successfully");
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
                throw new SQLException("MySQL JDBC Driver not found", e);
            } catch (SQLException e) {
                System.err.println("Database connection error: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }
    
    // Close database connection
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("Database connection test successful");
                return true;
            } else {
                System.err.println("Database connection test failed: Connection is null or closed");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null && conn != connection) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static boolean tableExists(String tableName) {
        Connection conn = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            DatabaseMetaData dbm = conn.getMetaData();
            rs = dbm.getTables(null, null, "%", new String[]{"TABLE"});
            
            while (rs.next()) {
                String currentTable = rs.getString("TABLE_NAME");
                if (currentTable.equalsIgnoreCase(tableName)) {
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error checking if table exists: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (conn != null && conn != connection) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
    
    public static List<String> listTables() {
        List<String> tables = new ArrayList<>();
        Connection conn = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            DatabaseMetaData dbm = conn.getMetaData();
            rs = dbm.getTables(null, null, "%", new String[]{"TABLE"});
            
            System.out.println("Tables in database:");
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                tables.add(tableName);
                System.out.println(" - " + tableName);
            }
        } catch (SQLException e) {
            System.err.println("Error listing tables: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (conn != null && conn != connection) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        return tables;
    }
    
    public static boolean createTable(String tableName, String createTableSQL) {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            
            // Check if table already exists
            if (tableExists(tableName)) {
                System.out.println("Table " + tableName + " already exists");
                return true;
            }
            
            // Create the table
            stmt.executeUpdate(createTableSQL);
            System.out.println("Table " + tableName + " created successfully");
            return true;
        } catch (SQLException e) {
            System.err.println("Error creating table " + tableName + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null && conn != connection) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
