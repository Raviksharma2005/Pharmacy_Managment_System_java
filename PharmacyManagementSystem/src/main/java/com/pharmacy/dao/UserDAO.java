package com.pharmacy.dao;

import com.pharmacy.models.User;
import com.pharmacy.util.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for User authentication
 */
public class UserDAO {
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    private final DatabaseConnector dbConnector;
    
    public UserDAO() {
        this.dbConnector = DatabaseConnector.getInstance();
    }
    
    /**
     * Authenticate a user by username, password and role
     * @param username Username
     * @param password Password
     * @param role User role
     * @return User object if authentication successful, null otherwise
     */
    public User authenticate(String username, String password, String role) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT * FROM Users WHERE username = ? AND password = ? AND role = ?";
            statement = connection.prepareStatement(query);
            
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, role);
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                int id = resultSet.getInt("user_id");
                return new User(id, username, password, role);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error authenticating user: " + username, e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return null;
    }
    
    /**
     * Get user by username
     * @param username Username to search for
     * @return User object if found, null otherwise
     */
    public User getUserByUsername(String username) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT * FROM Users WHERE username = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                int id = resultSet.getInt("user_id");
                String password = resultSet.getString("password");
                String role = resultSet.getString("role");
                return new User(id, username, password, role);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting user by username: " + username, e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return null;
    }
    
    /**
     * Add a new user
     * @param user User to add
     * @return true if successful, false otherwise
     */
    public boolean addUser(User user) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "INSERT INTO Users (username, password, role) VALUES (?, ?, ?)";
            statement = connection.prepareStatement(query);
            
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole());
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding user: " + user.getUsername(), e);
        } finally {
            dbConnector.closeResources(connection, statement, null);
        }
        
        return false;
    }
    
    /**
     * Update a user's password
     * @param username Username
     * @param newPassword New password
     * @return true if successful, false otherwise
     */
    public boolean updatePassword(String username, String newPassword) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "UPDATE Users SET password = ? WHERE username = ?";
            statement = connection.prepareStatement(query);
            
            statement.setString(1, newPassword);
            statement.setString(2, username);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating password for user: " + username, e);
        } finally {
            dbConnector.closeResources(connection, statement, null);
        }
        
        return false;
    }
    
    /**
     * Link user to employee
     * @param userId User ID
     * @param employeeId Employee ID
     * @return true if successful, false otherwise
     */
    public boolean linkUserToEmployee(int userId, int employeeId) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "UPDATE Users SET employee_id = ? WHERE user_id = ?";
            statement = connection.prepareStatement(query);
            
            statement.setInt(1, employeeId);
            statement.setInt(2, userId);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error linking user to employee. User ID: " + userId + ", Employee ID: " + employeeId, e);
        } finally {
            dbConnector.closeResources(connection, statement, null);
        }
        
        return false;
    }
    
    /**
     * Log user activity
     * @param userId User ID
     * @param action Action performed
     * @param details Details of the action
     */
    public void logActivity(int userId, String action, String details) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "INSERT INTO Audit_Log (table_name, operation_type, performed_by, operation_details) " +
                          "VALUES ('System', ?, ?, ?)";
            statement = connection.prepareStatement(query);
            
            statement.setString(1, action);
            statement.setString(2, String.valueOf(userId));
            statement.setString(3, details);
            
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error logging activity for user ID: " + userId, e);
        } finally {
            dbConnector.closeResources(connection, statement, null);
        }
    }
} 