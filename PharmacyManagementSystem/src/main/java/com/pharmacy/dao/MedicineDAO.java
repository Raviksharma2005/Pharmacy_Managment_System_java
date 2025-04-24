package com.pharmacy.dao;

import com.pharmacy.models.Product;
import com.pharmacy.util.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Medicine entity
 */
public class MedicineDAO {
    private static final Logger LOGGER = Logger.getLogger(MedicineDAO.class.getName());
    private final DatabaseConnector dbConnector;
    
    public MedicineDAO() {
        this.dbConnector = DatabaseConnector.getInstance();
    }
    
    /**
     * Get all medicines from the database
     * @return List of Product objects
     */
    public List<Product> getAllMedicines() {
        List<Product> medicines = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT m.*, c.category_name FROM Medicine m " +
                           "LEFT JOIN Medicine_Category c ON m.category_id = c.category_id";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Product medicine = mapResultSetToMedicine(resultSet);
                medicines.add(medicine);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all medicines", e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return medicines;
    }
    
    /**
     * Get medicine by ID
     * @param id Medicine ID
     * @return Product object if found, null otherwise
     */
    public Product getMedicineById(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT m.*, c.category_name FROM Medicine m " +
                           "LEFT JOIN Medicine_Category c ON m.category_id = c.category_id " +
                           "WHERE m.medicine_id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToMedicine(resultSet);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting medicine by ID: " + id, e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return null;
    }
    
    /**
     * Add a new medicine to the database
     * @param medicine Medicine to add
     * @return true if successful, false otherwise
     */
    public boolean addMedicine(Product medicine) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "INSERT INTO Medicine (name, brand, category, stock_quantity, expiry_date, " +
                    "price, description, category_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            statement.setString(1, medicine.getName());
            statement.setString(2, medicine.getBrand());
            statement.setString(3, medicine.getCategory());
            statement.setInt(4, medicine.getStockQuantity());
            statement.setObject(5, medicine.getExpiryDate());
            statement.setDouble(6, medicine.getPrice());
            statement.setString(7, medicine.getDescription());
            statement.setInt(8, medicine.getCategoryId());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    medicine.setId(generatedKeys.getInt(1));
                }
                // Record stock transaction
                addStockTransaction(medicine.getId(), medicine.getStockQuantity(), "purchase", 0);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding medicine", e);
        } finally {
            dbConnector.closeResources(connection, statement, null);
        }
        
        return false;
    }
    
    /**
     * Update an existing medicine in the database
     * @param medicine Medicine to update
     * @return true if successful, false otherwise
     */
    public boolean updateMedicine(Product medicine) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConnector.getConnection();
            // First, get the current quantity
            Product currentMedicine = getMedicineById(medicine.getId());
            int quantityDifference = medicine.getStockQuantity() - currentMedicine.getStockQuantity();
            
            String query = "UPDATE Medicine SET name = ?, brand = ?, category = ?, stock_quantity = ?, " +
                    "expiry_date = ?, price = ?, description = ?, category_id = ? WHERE medicine_id = ?";
            statement = connection.prepareStatement(query);
            
            statement.setString(1, medicine.getName());
            statement.setString(2, medicine.getBrand());
            statement.setString(3, medicine.getCategory());
            statement.setInt(4, medicine.getStockQuantity());
            statement.setObject(5, medicine.getExpiryDate());
            statement.setDouble(6, medicine.getPrice());
            statement.setString(7, medicine.getDescription());
            statement.setInt(8, medicine.getCategoryId());
            statement.setInt(9, medicine.getId());
            
            int rowsAffected = statement.executeUpdate();
            
            // If stock quantity changed, record a transaction
            if (rowsAffected > 0 && quantityDifference != 0) {
                String transactionType = quantityDifference > 0 ? "purchase" : "adjustment";
                addStockTransaction(medicine.getId(), Math.abs(quantityDifference), transactionType, 0);
            }
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating medicine with ID: " + medicine.getId(), e);
        } finally {
            dbConnector.closeResources(connection, statement, null);
        }
        
        return false;
    }
    
    /**
     * Delete a medicine from the database
     * @param id Medicine ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteMedicine(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "DELETE FROM Medicine WHERE medicine_id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting medicine with ID: " + id, e);
        } finally {
            dbConnector.closeResources(connection, statement, null);
        }
        
        return false;
    }
    
    /**
     * Search medicines by name, description or category
     * @param searchText Text to search for
     * @return List of matching Product objects
     */
    public List<Product> searchMedicines(String searchText) {
        List<Product> medicines = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT m.*, c.category_name FROM Medicine m " +
                           "LEFT JOIN Medicine_Category c ON m.category_id = c.category_id " +
                           "WHERE m.name LIKE ? OR m.description LIKE ? OR m.category LIKE ? OR c.category_name LIKE ?";
            statement = connection.prepareStatement(query);
            
            String searchPattern = "%" + searchText + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            statement.setString(4, searchPattern);
            
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Product medicine = mapResultSetToMedicine(resultSet);
                medicines.add(medicine);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching for medicines with term: " + searchText, e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return medicines;
    }
    
    /**
     * Get medicines with low stock (below 10 units)
     * @return List of Product objects with low stock
     */
    public List<Product> getLowStockMedicines() {
        List<Product> medicines = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT m.*, c.category_name FROM Medicine m " +
                           "LEFT JOIN Medicine_Category c ON m.category_id = c.category_id " +
                           "WHERE m.stock_quantity < 10";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Product medicine = mapResultSetToMedicine(resultSet);
                medicines.add(medicine);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting low stock medicines", e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return medicines;
    }
    
    /**
     * Add a stock transaction
     * @param medicineId Medicine ID
     * @param quantity Quantity changed
     * @param transactionType Type of transaction (purchase, sale, return)
     * @param referenceId Reference ID for the transaction (invoice, purchase, etc.)
     * @return true if successful, false otherwise
     */
    public boolean addStockTransaction(int medicineId, int quantity, String transactionType, int referenceId) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "INSERT INTO Stock_Transactions (medicine_id, change_quantity, transaction_type, " +
                    "transaction_date, reference_id) VALUES (?, ?, ?, CURDATE(), ?)";
            statement = connection.prepareStatement(query);
            
            statement.setInt(1, medicineId);
            statement.setInt(2, quantity);
            statement.setString(3, transactionType);
            statement.setInt(4, referenceId);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding stock transaction for medicine ID: " + medicineId, e);
        } finally {
            dbConnector.closeResources(connection, statement, null);
        }
        
        return false;
    }
    
    /**
     * Get all medicine categories
     * @return List of category names
     */
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT category_name FROM Medicine_Category";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                categories.add(resultSet.getString("category_name"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all categories", e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return categories;
    }
    
    /**
     * Map ResultSet to Product object
     * @param resultSet ResultSet to map
     * @return Product object
     * @throws SQLException if an error occurs
     */
    private Product mapResultSetToMedicine(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("medicine_id");
        String name = resultSet.getString("name");
        String brand = resultSet.getString("brand");
        
        // Use category_name from joined table if available, otherwise use category from medicine table
        String category = null;
        try {
            category = resultSet.getString("category_name");
            if (category == null || category.isEmpty()) {
                category = resultSet.getString("category");
            }
        } catch (SQLException e) {
            category = resultSet.getString("category");
        }
        
        int stockQuantity = resultSet.getInt("stock_quantity");
        LocalDate expiryDate = resultSet.getObject("expiry_date", LocalDate.class);
        double price = resultSet.getDouble("price");
        String description = resultSet.getString("description");
        int categoryId = resultSet.getInt("category_id");
        
        return new Product(id, name, brand, category, stockQuantity, expiryDate, price, description, categoryId);
    }
} 