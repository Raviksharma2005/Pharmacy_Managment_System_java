package com.pharmacy.utils;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for validating UI components and functionality
 */
public class UIValidator {
    private static final Logger LOGGER = Logger.getLogger(UIValidator.class.getName());
    
    /**
     * Validate login screen loading
     * @return true if login screen loads successfully, false otherwise
     */
    public static boolean validateLoginScreenLoading() {
        try {
            FXMLLoader loader = new FXMLLoader(UIValidator.class.getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            
            // If we get here, the FXML loaded successfully
            LOGGER.info("Login screen loading validation: PASSED");
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading login screen", e);
            return false;
        }
    }
    
    /**
     * Validate admin dashboard screen loading
     * @return true if admin dashboard screen loads successfully, false otherwise
     */
    public static boolean validateAdminDashboardLoading() {
        try {
            FXMLLoader loader = new FXMLLoader(UIValidator.class.getResource("/fxml/AdminDashboard.fxml"));
            Parent root = loader.load();
            
            // If we get here, the FXML loaded successfully
            LOGGER.info("Admin dashboard loading validation: PASSED");
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading admin dashboard", e);
            return false;
        }
    }
    
    /**
     * Validate pharmacist dashboard screen loading
     * @return true if pharmacist dashboard screen loads successfully, false otherwise
     */
    public static boolean validatePharmacistDashboardLoading() {
        try {
            FXMLLoader loader = new FXMLLoader(UIValidator.class.getResource("/fxml/PharmacistDashboard.fxml"));
            Parent root = loader.load();
            
            // If we get here, the FXML loaded successfully
            LOGGER.info("Pharmacist dashboard loading validation: PASSED");
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading pharmacist dashboard", e);
            return false;
        }
    }
    
    /**
     * Validate CSS theme loading
     * @return true if CSS theme loads successfully, false otherwise
     */
    public static boolean validateCSSThemeLoading() {
        try {
            String cssPath = UIValidator.class.getResource("/css/dark-theme.css").toExternalForm();
            
            // If we get here, the CSS file exists and is accessible
            LOGGER.info("CSS theme loading validation: PASSED");
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading CSS theme", e);
            return false;
        }
    }
    
    /**
     * Validate login form validation
     * @param stage The JavaFX stage to use
     * @return true if login form validation works correctly, false otherwise
     */
    public static boolean validateLoginFormValidation(Stage stage) {
        final AtomicBoolean validationPassed = new AtomicBoolean(false);
        final CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            try {
                // Load the login form
                FXMLLoader loader = new FXMLLoader(UIValidator.class.getResource("/fxml/Login.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
                
                // Find the login form elements (assuming IDs)
                TextField usernameField = (TextField) scene.lookup("#usernameField");
                PasswordField passwordField = (PasswordField) scene.lookup("#passwordField");
                ComboBox<String> roleComboBox = (ComboBox<String>) scene.lookup("#userRoleCombo");
                Button loginButton = (Button) scene.lookup("#loginButton");
                Label errorLabel = (Label) scene.lookup("#errorLabel");
                
                // Test empty form submission
                loginButton.fire();
                
                // Check if error is displayed
                if (errorLabel.getText().contains("Please fill in all fields")) {
                    // Fill in form partially and try again
                    usernameField.setText("test");
                    loginButton.fire();
                    
                    // Check if error still displayed
                    if (errorLabel.getText().contains("Please fill in all fields")) {
                        // Form validation works as expected
                        validationPassed.set(true);
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error validating login form", e);
            } finally {
                latch.countDown();
            }
        });
        
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        LOGGER.info("Login form validation: " + (validationPassed.get() ? "PASSED" : "FAILED"));
        return validationPassed.get();
    }
    
    /**
     * Run all UI validation tests
     * @return true if all tests pass, false otherwise
     */
    public static boolean runUIValidations() {
        boolean loginScreenValid = validateLoginScreenLoading();
        boolean adminDashboardValid = validateAdminDashboardLoading();
        boolean pharmacistDashboardValid = validatePharmacistDashboardLoading();
        boolean cssThemeValid = validateCSSThemeLoading();
        
        boolean allValid = loginScreenValid && adminDashboardValid && 
                         pharmacistDashboardValid && cssThemeValid;
        
        LOGGER.info("UI validation summary: " + (allValid ? "ALL PASSED" : "SOME FAILED"));
        return allValid;
    }
} 