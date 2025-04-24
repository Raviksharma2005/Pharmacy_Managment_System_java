package com.pharmacy.utils;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class for running all system validations
 */
public class ValidationRunner extends Application {
    private static final Logger LOGGER = Logger.getLogger(ValidationRunner.class.getName());
    private static final CountDownLatch latch = new CountDownLatch(1);
    private static ValidationRunner instance;
    private Stage stage;
    
    public static void main(String[] args) {
        // Run system validation
        System.out.println("PharmaCare Management System Validation");
        System.out.println("======================================");
        
        // Check command line args
        boolean skipUI = Arrays.asList(args).contains("--skip-ui");
        boolean skipDB = Arrays.asList(args).contains("--skip-db");
        boolean skipBusiness = Arrays.asList(args).contains("--skip-business");
        
        // First run database validations if not skipped
        if (!skipDB) {
            System.out.println("\nRunning database validations...");
            boolean dbValid = SystemValidator.runDatabaseValidations();
            
            if (!dbValid) {
                System.out.println("Database validation failed. Please check your database configuration.");
                System.exit(1);
            }
        } else {
            System.out.println("\nSkipping database validations as requested.");
        }
        
        // Run UI validations if not skipped
        if (!skipUI) {
            System.out.println("\nRunning UI validations...");
            
            // Launch JavaFX application for UI validation
            launch(args);
            
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.log(Level.SEVERE, "Validation runner interrupted", e);
            }
        } else {
            System.out.println("\nSkipping UI validations as requested.");
        }
        
        // Run authentication validations
        System.out.println("\nRunning authentication validations...");
        runAuthenticationValidations();
        
        // Run business logic validations if not skipped
        if (!skipBusiness && !skipDB) {
            System.out.println("\nRunning business logic validations...");
            BusinessLogicValidator.runBusinessValidations();
        } else if (skipBusiness) {
            System.out.println("\nSkipping business logic validations as requested.");
        } else if (skipDB) {
            System.out.println("\nSkipping business logic validations because database validations were skipped.");
        }
        
        System.out.println("\nPHARMACARE VALIDATION COMPLETE");
    }
    
    @Override
    public void start(Stage primaryStage) {
        instance = this;
        this.stage = primaryStage;
        
        // Run UI validations in JavaFX thread
        boolean uiValid = UIValidator.runUIValidations();
        
        // Validate login form
        boolean loginFormValid = UIValidator.validateLoginFormValidation(stage);
        
        System.out.println("UI validations: " + (uiValid && loginFormValid ? "PASSED" : "FAILED"));
        
        // Exit JavaFX application
        Platform.runLater(() -> {
            latch.countDown();
            Platform.exit();
        });
    }
    
    /**
     * Run authentication validations
     */
    private static void runAuthenticationValidations() {
        boolean adminAuthValid = SystemValidator.validateUserAuthentication("admin", "admin123", "admin");
        boolean pharmacistAuthValid = SystemValidator.validateUserAuthentication("pharmacist", "pharm123", "pharmacist");
        boolean invalidAuthRejected = !SystemValidator.validateUserAuthentication("admin", "wrongpass", "admin");
        
        System.out.println("Admin authentication: " + (adminAuthValid ? "PASSED" : "FAILED"));
        System.out.println("Pharmacist authentication: " + (pharmacistAuthValid ? "PASSED" : "FAILED"));
        System.out.println("Invalid credentials rejected: " + (invalidAuthRejected ? "PASSED" : "FAILED"));
    }
    
    /**
     * Get the primary stage (for UI validation)
     * @return the primary stage
     */
    public Stage getStage() {
        return stage;
    }
    
    /**
     * Get the singleton instance
     * @return the instance
     */
    public static ValidationRunner getInstance() {
        return instance;
    }
} 