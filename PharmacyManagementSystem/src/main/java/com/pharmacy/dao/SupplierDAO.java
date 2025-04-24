package com.pharmacy.dao;

import com.pharmacy.models.Supplier;
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
 * Data Access Object for Supplier entity
 */
public class SupplierDAO {
    private static final Logger LOGGER = Logger.getLogger(SupplierDAO.class.getName());
    private final DatabaseConnector dbConnector;
    
    public SupplierDAO() {
        this.dbConnector = DatabaseConnector.getInstance();
    }
    
    /**
     * Get all suppliers from the database
     * @return List of Supplier objects
     */
    public List<Supplier> getAllSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT * FROM Supplier";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Supplier supplier = mapResultSetToSupplier(resultSet);
                suppliers.add(supplier);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all suppliers", e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return suppliers;
    }
    
    /**
     * Get supplier by ID
     * @param id Supplier ID
     * @return Supplier object if found, null otherwise
     */
    public Supplier getSupplierById(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT * FROM Supplier WHERE supplier_id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToSupplier(resultSet);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting supplier by ID: " + id, e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return null;
    }
    
    /**
     * Add a new supplier to the database
     * @param supplier Supplier to add
     * @return true if successful, false otherwise
     */
    public boolean addSupplier(Supplier supplier) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "INSERT INTO Supplier (name, contact_number, email, address) VALUES (?, ?, ?, ?)";
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            statement.setString(1, supplier.getName());
            statement.setString(2, supplier.getContactNumber());
            statement.setString(3, supplier.getEmail());
            statement.setString(4, supplier.getAddress());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    supplier.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding supplier", e);
        } finally {
            dbConnector.closeResources(connection, statement, null);
        }
        
        return false;
    }
    
    /**
     * Update an existing supplier in the database
     * @param supplier Supplier to update
     * @return true if successful, false otherwise
     */
    public boolean updateSupplier(Supplier supplier) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "UPDATE Supplier SET name = ?, contact_number = ?, email = ?, address = ? " +
                    "WHERE supplier_id = ?";
            statement = connection.prepareStatement(query);
            
            statement.setString(1, supplier.getName());
            statement.setString(2, supplier.getContactNumber());
            statement.setString(3, supplier.getEmail());
            statement.setString(4, supplier.getAddress());
            statement.setInt(5, supplier.getId());
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating supplier with ID: " + supplier.getId(), e);
        } finally {
            dbConnector.closeResources(connection, statement, null);
        }
        
        return false;
    }
    
    /**
     * Delete a supplier from the database
     * @param id Supplier ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteSupplier(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "DELETE FROM Supplier WHERE supplier_id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting supplier with ID: " + id, e);
        } finally {
            dbConnector.closeResources(connection, statement, null);
        }
        
        return false;
    }
    
    /**
     * Search suppliers by name or email
     * @param searchText Text to search for
     * @return List of matching Supplier objects
     */
    public List<Supplier> searchSuppliers(String searchText) {
        List<Supplier> suppliers = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT * FROM Supplier WHERE name LIKE ? OR email LIKE ?";
            statement = connection.prepareStatement(query);
            
            String searchPattern = "%" + searchText + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Supplier supplier = mapResultSetToSupplier(resultSet);
                suppliers.add(supplier);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching for suppliers with term: " + searchText, e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return suppliers;
    }
    
    /**
     * Map ResultSet to Supplier object
     * @param resultSet ResultSet to map
     * @return Supplier object
     * @throws SQLException if an error occurs
     */
    private Supplier mapResultSetToSupplier(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("supplier_id");
        String name = resultSet.getString("name");
        String contactNumber = resultSet.getString("contact_number");
        String email = resultSet.getString("email");
        String address = resultSet.getString("address");
        
        return new Supplier(id, name, contactNumber, email, address);
    }
} 