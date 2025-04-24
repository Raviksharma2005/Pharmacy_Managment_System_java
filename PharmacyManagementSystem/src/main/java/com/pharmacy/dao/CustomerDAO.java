package com.pharmacy.dao;

import com.pharmacy.models.Customer;
import com.pharmacy.util.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Customer entity
 */
public class CustomerDAO {
    private static final Logger LOGGER = Logger.getLogger(CustomerDAO.class.getName());
    private final DatabaseConnector dbConnector;
    
    public CustomerDAO() {
        this.dbConnector = DatabaseConnector.getInstance();
    }
    
    /**
     * Get all customers from the database
     * @return List of Customer objects
     */
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT * FROM Customer";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Customer customer = mapResultSetToCustomer(resultSet);
                customers.add(customer);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all customers", e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return customers;
    }
    
    /**
     * Get customer by ID
     * @param id Customer ID
     * @return Customer object if found, null otherwise
     */
    public Customer getCustomerById(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT * FROM Customer WHERE customer_id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToCustomer(resultSet);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting customer by ID: " + id, e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return null;
    }
    
    /**
     * Search for customer by name, email or phone
     * @param searchText Text to search for
     * @return List of matching Customer objects
     */
    public List<Customer> searchCustomers(String searchText) {
        List<Customer> customers = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT * FROM Customer WHERE name LIKE ? OR email LIKE ? OR phone LIKE ?";
            statement = connection.prepareStatement(query);
            
            String searchPattern = "%" + searchText + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Customer customer = mapResultSetToCustomer(resultSet);
                customers.add(customer);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching for customers with term: " + searchText, e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return customers;
    }
    
    /**
     * Add a new customer to the database
     * @param customer Customer to add
     * @return true if successful, false otherwise
     */
    public boolean addCustomer(Customer customer) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "INSERT INTO Customer (name, email, phone, address) VALUES (?, ?, ?, ?)";
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getEmail());
            statement.setString(3, customer.getPhone());
            statement.setString(4, customer.getAddress());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    customer.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding customer", e);
        } finally {
            dbConnector.closeResources(connection, statement, null);
        }
        
        return false;
    }
    
    /**
     * Update an existing customer in the database
     * @param customer Customer to update
     * @return true if successful, false otherwise
     */
    public boolean updateCustomer(Customer customer) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "UPDATE Customer SET name = ?, email = ?, phone = ?, address = ? WHERE customer_id = ?";
            statement = connection.prepareStatement(query);
            
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getEmail());
            statement.setString(3, customer.getPhone());
            statement.setString(4, customer.getAddress());
            statement.setInt(5, customer.getId());
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating customer with ID: " + customer.getId(), e);
        } finally {
            dbConnector.closeResources(connection, statement, null);
        }
        
        return false;
    }
    
    /**
     * Delete a customer from the database
     * @param id Customer ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteCustomer(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "DELETE FROM Customer WHERE customer_id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting customer with ID: " + id, e);
        } finally {
            dbConnector.closeResources(connection, statement, null);
        }
        
        return false;
    }
    
    /**
     * Get customer by name
     * @param name Customer name to search for
     * @return First Customer object with matching name if found, null otherwise
     */
    public Customer getCustomerByName(String name) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT * FROM Customer WHERE name = ? LIMIT 1";
            statement = connection.prepareStatement(query);
            statement.setString(1, name);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToCustomer(resultSet);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting customer by name: " + name, e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return null;
    }
    
    /**
     * Map ResultSet to Customer object
     * @param resultSet ResultSet to map
     * @return Customer object
     * @throws SQLException if an error occurs
     */
    private Customer mapResultSetToCustomer(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("customer_id");
        String name = resultSet.getString("name");
        String email = resultSet.getString("email");
        String phone = resultSet.getString("phone");
        String address = resultSet.getString("address");
        
        return new Customer(id, name, email, phone, address);
    }
} 