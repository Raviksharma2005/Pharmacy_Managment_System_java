package com.pharmacy.dao;

import com.pharmacy.models.Category;
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
 * Data Access Object for Medicine Category entity
 */
public class CategoryDAO {
    private static final Logger LOGGER = Logger.getLogger(CategoryDAO.class.getName());
    private final DatabaseConnector dbConnector;
    
    public CategoryDAO() {
        this.dbConnector = DatabaseConnector.getInstance();
    }
    
    /**
     * Get all categories from the database
     * @return List of Category objects
     */
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT * FROM Medicine_Category";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Category category = mapResultSetToCategory(resultSet);
                categories.add(category);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all categories", e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return categories;
    }
    
    /**
     * Get category by ID
     * @param id Category ID
     * @return Category object if found, null otherwise
     */
    public Category getCategoryById(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT * FROM Medicine_Category WHERE category_id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToCategory(resultSet);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting category by ID: " + id, e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return null;
    }
    
    /**
     * Add a new category to the database
     * @param category Category to add
     * @return true if successful, false otherwise
     */
    public boolean addCategory(Category category) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "INSERT INTO Medicine_Category (category_name, description) VALUES (?, ?)";
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            statement.setString(1, category.getCategoryName());
            statement.setString(2, category.getDescription());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    category.setCategoryId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding category", e);
        } finally {
            dbConnector.closeResources(connection, statement, null);
        }
        
        return false;
    }
    
    /**
     * Update an existing category in the database
     * @param category Category to update
     * @return true if successful, false otherwise
     */
    public boolean updateCategory(Category category) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "UPDATE Medicine_Category SET category_name = ?, description = ? WHERE category_id = ?";
            statement = connection.prepareStatement(query);
            
            statement.setString(1, category.getCategoryName());
            statement.setString(2, category.getDescription());
            statement.setInt(3, category.getCategoryId());
            
            int rowsAffected = statement.executeUpdate();
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating category with ID: " + category.getCategoryId(), e);
        } finally {
            dbConnector.closeResources(connection, statement, null);
        }
        
        return false;
    }
    
    /**
     * Delete a category from the database
     * @param id Category ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteCategory(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "DELETE FROM Medicine_Category WHERE category_id = ?";
            statement = connection.prepareStatement(query);
            
            statement.setInt(1, id);
            
            int rowsAffected = statement.executeUpdate();
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting category with ID: " + id, e);
        } finally {
            dbConnector.closeResources(connection, statement, null);
        }
        
        return false;
    }
    
    /**
     * Map ResultSet to Category object
     * @param resultSet ResultSet to map
     * @return Category object
     * @throws SQLException if an error occurs
     */
    private Category mapResultSetToCategory(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("category_id");
        String name = resultSet.getString("category_name");
        String description = resultSet.getString("description");
        
        return new Category(id, name, description);
    }
} 