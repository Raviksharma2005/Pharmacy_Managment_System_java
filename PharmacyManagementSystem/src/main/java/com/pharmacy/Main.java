package com.pharmacy;

import com.pharmacy.controllers.LoginController;
import com.pharmacy.util.DatabaseConnector;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Main extends Application {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Setup database tables before launching the application
        setupDatabase();
        
        // Load the login view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        Parent root = loader.load();
        
        // Set the controller
        LoginController controller = loader.getController();
        controller.setPrimaryStage(primaryStage);
        
        // Set up the scene
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Pharmacy Management System");
        primaryStage.setResizable(false);
        
        // Show the stage
        primaryStage.show();
    }
    
    /**
     * Setup database tables using the SQL setup script
     */
    private void setupDatabase() {
        LOGGER.info("Setting up database tables...");
        Connection connection = null;
        Statement statement = null;
        
        try {
            connection = DatabaseConnector.getInstance().getConnection();
            statement = connection.createStatement();
            
            // Load the SQL setup script from resources
            try (InputStream inputStream = getClass().getResourceAsStream("/setup_tables.sql")) {
                if (inputStream != null) {
                    String sql = new BufferedReader(new InputStreamReader(inputStream))
                            .lines().collect(Collectors.joining("\n"));
                    
                    // Split and execute each statement
                    String[] statements = sql.split(";");
                    for (String stmt : statements) {
                        if (!stmt.trim().isEmpty()) {
                            try {
                                statement.execute(stmt);
                            } catch (SQLException e) {
                                LOGGER.log(Level.WARNING, "Error executing statement: " + stmt, e);
                                // Continue with next statement
                            }
                        }
                    }
                    
                    LOGGER.info("Database setup completed successfully");
                } else {
                    LOGGER.warning("Setup script not found in resources");
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error setting up database", e);
        } finally {
            DatabaseConnector.getInstance().closeResources(connection, statement, null);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
} 