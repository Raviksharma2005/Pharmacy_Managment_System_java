package com.pharmacy.util;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database connector utility class for managing JDBC connections
 */
public class DatabaseConnector {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnector.class.getName());
    
    private static DatabaseConnector instance;
    private String jdbcUrl;
    private String username;
    private String password;
    
    private DatabaseConnector() {
        // Default database connection settings
        this.jdbcUrl = "jdbc:mysql://127.0.0.1:3306/PMS";
        this.username = "root";
        this.password = "Ribs2675!!";
        
        // Create the database and tables if they don't exist
        initializeDatabase();
    }
    
    private void initializeDatabase() {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            // First try to connect with database creation flag
            conn = DriverManager.getConnection(jdbcUrl, username, password);
            LOGGER.info("Connected to database successfully");
            
            // Create tables if they don't exist
            stmt = conn.createStatement();
            
            // Create Customer table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS Customer (" +
                "customer_id INT PRIMARY KEY AUTO_INCREMENT, " +
                "name VARCHAR(100) NOT NULL, " +
                "email VARCHAR(100) NOT NULL, " +
                "phone VARCHAR(15) NOT NULL, " +
                "address TEXT)");
            
            // Create Invoice table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS Invoice (" +
                "invoice_id INT PRIMARY KEY AUTO_INCREMENT, " +
                "employee_id INT, " +
                "customer_id INT, " +
                "invoice_date DATE, " +
                "total_amount DECIMAL(10,2), " +
                "discount_amount DECIMAL(10,2), " +
                "tax_amount DECIMAL(10,2), " +
                "final_amount DECIMAL(10,2))");
            
            // Create Invoice_Item table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS Invoice_Item (" +
                "invoice_item_id INT PRIMARY KEY AUTO_INCREMENT, " +
                "invoice_id INT, " +
                "medicine_id INT, " +
                "quantity INT, " +
                "price_per_unit DECIMAL(10,2), " +
                "total_price DECIMAL(10,2))");
            
            // Create Stock_Transactions table
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS Stock_Transactions (" +
                "transaction_id INT PRIMARY KEY AUTO_INCREMENT, " +
                "medicine_id INT, " +
                "change_quantity INT, " +
                "transaction_type VARCHAR(10), " +
                "transaction_date DATE, " +
                "reference_id INT)");
            
            // Insert a default customer if none exists
            stmt.execute(
                "INSERT INTO Customer (name, email, phone, address) " +
                "SELECT 'Walk-in Customer', 'customer@pharmacy.com', '0000000000', 'Default customer' " +
                "FROM dual " +
                "WHERE NOT EXISTS (SELECT 1 FROM Customer LIMIT 1)");
            
            LOGGER.info("Database initialized successfully");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error initializing database: " + e.getMessage(), e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Get singleton instance of the database connector
     * @return DatabaseConnector instance
     */
    public static synchronized DatabaseConnector getInstance() {
        if (instance == null) {
            instance = new DatabaseConnector();
        }
        return instance;
    }
    
    /**
     * Get a database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Create and return a connection
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            connection.setAutoCommit(true); // Set default autocommit to true
            return connection;
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found", e);
            throw new SQLException("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error connecting to database: " + e.getMessage(), e);
            
            // Try to connect with a new database with create option
            if (e.getMessage().contains("Unknown database")) {
                try {
                    String createDbUrl = "jdbc:mysql://localhost:3306/?createDatabaseIfNotExist=true&useSSL=false";
                    Connection adminConn = DriverManager.getConnection(createDbUrl, username, password);
                    Statement stmt = adminConn.createStatement();
                    
                    // Create the pharmacy_db database
                    stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS pharmacy_db");
                    
                    // Close the admin connection
                    adminConn.close();
                    
                    // Now try to connect again
                    return DriverManager.getConnection(jdbcUrl, username, password);
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Failed to create database: " + ex.getMessage(), ex);
                    throw ex;
                }
            }
            
            throw e;
        }
    }
    
    /**
     * Close database resources safely
     * @param connection Connection to close
     * @param statement Statement to close
     * @param resultSet ResultSet to close
     */
    public void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error closing ResultSet", e);
        }
        
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error closing Statement", e);
        }
        
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error closing Connection", e);
        }
    }
    
    /**
     * Allow configuration changes for testing
     * @param jdbcUrl New database URL
     * @param username New database username
     * @param password New database password
     */
    public void setDatabaseCredentials(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }
} 