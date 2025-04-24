package com.pharmacy.utils;

import com.pharmacy.dao.*;
import com.pharmacy.models.*;
import com.pharmacy.util.DatabaseConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for validating system functionality
 */
public class SystemValidator {
    private static final Logger LOGGER = Logger.getLogger(SystemValidator.class.getName());
    
    /**
     * Check if the system meets all requirements
     * @return true if the system meets all requirements, false otherwise
     */
    public static boolean checkSystemRequirements() {
        return checkDatabaseConnection();
    }
    
    /**
     * Check if the database connection is working
     * @return true if the database connection is working, false otherwise
     */
    public static boolean checkDatabaseConnection() {
        LOGGER.info("Checking database connection...");
        DatabaseConnector connector = DatabaseConnector.getInstance();
        
        try {
            Connection connection = connector.getConnection();
            boolean isConnected = connection != null && !connection.isClosed();
            connector.closeResources(connection, null, null);
            
            if (isConnected) {
                LOGGER.info("Database connection test successful");
            } else {
                LOGGER.severe("Database connection test failed");
            }
            
            return isConnected;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Database connection test failed", e);
            return false;
        }
    }
    
    /**
     * Validate user authentication
     * @param username Username
     * @param password Password
     * @param role User role
     * @return true if authentication is successful, false otherwise
     */
    public static boolean validateUserAuthentication(String username, String password, String role) {
        UserDAO userDAO = new UserDAO();
        User user = userDAO.authenticate(username, password, role);
        boolean isAuthenticated = (user != null);
        LOGGER.info("User authentication validation for " + username + ": " + (isAuthenticated ? "PASSED" : "FAILED"));
        return isAuthenticated;
    }
    
    /**
     * Validate product data retrieval
     * @return true if products can be retrieved, false otherwise
     */
    public static boolean validateProductRetrieval() {
        ProductDAO productDAO = new ProductDAO();
        try {
            List<Product> products = productDAO.getAllProducts();
            boolean hasProducts = (products != null && !products.isEmpty());
            LOGGER.info("Product retrieval validation: " + (hasProducts ? "PASSED" : "FAILED"));
            return hasProducts;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error validating product retrieval", e);
            return false;
        }
    }
    
    /**
     * Validate user data retrieval
     * @return true if users can be retrieved, false otherwise
     */
    public static boolean validateUserRetrieval() {
        UserDAO userDAO = new UserDAO();
        try {
            // Try to get a known user (admin)
            User user = userDAO.getUserByUsername("admin");
            boolean hasUser = (user != null);
            LOGGER.info("User retrieval validation: " + (hasUser ? "PASSED" : "FAILED"));
            return hasUser;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error validating user retrieval", e);
            return false;
        }
    }
    
    /**
     * Validate category data retrieval
     * @return true if categories can be retrieved, false otherwise
     */
    public static boolean validateCategoryRetrieval() {
        CategoryDAO categoryDAO = new CategoryDAO();
        try {
            List<Category> categories = categoryDAO.getAllCategories();
            boolean hasCategories = (categories != null);
            LOGGER.info("Category retrieval validation: " + (hasCategories ? "PASSED" : "FAILED"));
            return hasCategories;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error validating category retrieval", e);
            return false;
        }
    }
    
    /**
     * Validate medicine data retrieval
     * @return true if medicines can be retrieved, false otherwise
     */
    public static boolean validateMedicineRetrieval() {
        MedicineDAO medicineDAO = new MedicineDAO();
        try {
            List<Product> medicines = medicineDAO.getAllMedicines();
            boolean hasMedicines = (medicines != null);
            LOGGER.info("Medicine retrieval validation: " + (hasMedicines ? "PASSED" : "FAILED"));
            return hasMedicines;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error validating medicine retrieval", e);
            return false;
        }
    }
    
    /**
     * Run all database validation tests
     * @return true if all tests pass, false otherwise
     */
    public static boolean runDatabaseValidations() {
        boolean connectionValid = checkDatabaseConnection();
        if (!connectionValid) {
            LOGGER.severe("Database connection validation failed. Cannot proceed with other validations.");
            return false;
        }
        
        boolean userRetrievalValid = validateUserRetrieval();
        boolean productRetrievalValid = validateProductRetrieval();
        boolean categoryRetrievalValid = validateCategoryRetrieval();
        boolean medicineRetrievalValid = validateMedicineRetrieval();
        
        boolean allValid = userRetrievalValid && productRetrievalValid && 
                         categoryRetrievalValid && medicineRetrievalValid;
        
        LOGGER.info("Database validation summary: " + (allValid ? "ALL PASSED" : "SOME FAILED"));
        return allValid;
    }
    
    /**
     * Test method to run all validations
     */
    public static void main(String[] args) {
        System.out.println("Starting system validation...");
        
        // Validate database
        boolean dbValid = runDatabaseValidations();
        System.out.println("Database validations: " + (dbValid ? "PASSED" : "FAILED"));
        
        // Validate authentication
        boolean adminAuthValid = validateUserAuthentication("admin", "admin123", "admin");
        boolean pharmacistAuthValid = validateUserAuthentication("pharmacist", "pharm123", "pharmacist");
        System.out.println("Admin authentication: " + (adminAuthValid ? "PASSED" : "FAILED"));
        System.out.println("Pharmacist authentication: " + (pharmacistAuthValid ? "PASSED" : "FAILED"));
        
        System.out.println("System validation completed.");
    }
} 