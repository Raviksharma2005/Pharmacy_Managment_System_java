package com.pharmacy.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import com.pharmacy.models.User;
import com.pharmacy.dao.UserDAO;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.time.LocalDate;
import com.pharmacy.utils.SystemValidator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController implements Initializable {
    
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> userRoleCombo;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    
    private UserDAO userDAO;
    private Stage primaryStage;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set up role combo box
        userRoleCombo.setItems(FXCollections.observableArrayList(
            "Admin", "Pharmacist"
        ));
        
        // Initialize the UserDAO
        userDAO = new UserDAO();
        
        // Clear any previous error messages
        errorLabel.setText("");
    }
    
    /**
     * Sets the primary stage of the application
     * @param stage The primary stage
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String role = userRoleCombo.getValue();
        
        if (username.isEmpty() || password.isEmpty() || role == null) {
            errorLabel.setText("Please fill in all fields");
            return;
        }
        
        try {
            // Check if database connection is available
            if (!SystemValidator.checkDatabaseConnection()) {
                errorLabel.setText("Cannot connect to database. Please check your connection.");
                return;
            }
            
            // Authenticate user
            User authenticatedUser = userDAO.authenticate(username, password, role.toLowerCase());
            
            if (authenticatedUser != null) {
                // Log the successful login activity
                userDAO.logActivity(authenticatedUser.getId(), "Login", "User logged in");
                
                // Load appropriate dashboard based on role
                loadDashboard(role);
            } else {
                errorLabel.setText("Invalid username, password, or role");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during login process", e);
            errorLabel.setText("An error occurred: " + e.getMessage());
        }
    }
    
    private void loadDashboard(String role) {
        try {
            String fxmlPath = role.equalsIgnoreCase("admin") ?
                    "/fxml/AdminDashboard.fxml" : "/fxml/PharmacistDashboard.fxml";
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent dashboardRoot = loader.load();
            
            // Get the stage
            Stage stage = (Stage) loginButton.getScene().getWindow();
            if (primaryStage != null) {
                stage = primaryStage;
            }
            
            // Create new scene and set to stage
            Scene scene = new Scene(dashboardRoot);
            stage.setScene(scene);
            stage.setTitle(role.substring(0, 1).toUpperCase() + role.substring(1) + " Dashboard");
            stage.setMaximized(true);
            stage.show();
            
            LOGGER.info("Loaded dashboard for role: " + role);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading dashboard", e);
            errorLabel.setText("Failed to load dashboard: " + e.getMessage());
        }
    }
} 