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
 * Data Access Object for Product entity
 */
public class ProductDAO {
    private static final Logger LOGGER = Logger.getLogger(ProductDAO.class.getName());
    private final DatabaseConnector dbConnector;
    
    public ProductDAO() {
        this.dbConnector = DatabaseConnector.getInstance();
    }
    
    /**
     * Get all products from the database
     * @return List of Product objects
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT * FROM products";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Product product = mapResultSetToProduct(resultSet);
                products.add(product);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all products", e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return products;
    }
    
    /**
     * Get product by ID
     * @param id Product ID
     * @return Product object if found, null otherwise
     */
    public Product getProductById(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT * FROM products WHERE medicine_id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToProduct(resultSet);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting product by ID: " + id, e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return null;
    }
    
    /**
     * Add a new product to the database
     * @param product Product to add
     * @return true if successful, false otherwise
     */
    public boolean addProduct(Product product) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "INSERT INTO products (name, brand, category, stock_quantity, expiry_date, " +
                    "price, description, category_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            statement.setString(1, product.getName());
            statement.setString(2, product.getBrand());
            statement.setString(3, product.getCategory());
            statement.setInt(4, product.getStockQuantity());
            statement.setObject(5, product.getExpiryDate());
            statement.setDouble(6, product.getPrice());
            statement.setString(7, product.getDescription());
            statement.setInt(8, product.getCategoryId());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding product", e);
        } finally {
            dbConnector.closeResources(connection, statement, null);
        }
        
        return false;
    }
    
    /**
     * Update an existing product in the database
     * @param product Product to update
     * @return true if successful, false otherwise
     */
    public boolean updateProduct(Product product) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "UPDATE products SET name = ?, brand = ?, category = ?, stock_quantity = ?, " +
                    "expiry_date = ?, price = ?, description = ?, category_id = ? WHERE medicine_id = ?";
            statement = connection.prepareStatement(query);
            
            statement.setString(1, product.getName());
            statement.setString(2, product.getBrand());
            statement.setString(3, product.getCategory());
            statement.setInt(4, product.getStockQuantity());
            statement.setObject(5, product.getExpiryDate());
            statement.setDouble(6, product.getPrice());
            statement.setString(7, product.getDescription());
            statement.setInt(8, product.getCategoryId());
            statement.setInt(9, product.getId());
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating product with ID: " + product.getId(), e);
        } finally {
            dbConnector.closeResources(connection, statement, null);
        }
        
        return false;
    }
    
    /**
     * Delete a product from the database
     * @param id Product ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteProduct(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "DELETE FROM products WHERE medicine_id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting product with ID: " + id, e);
        } finally {
            dbConnector.closeResources(connection, statement, null);
        }
        
        return false;
    }
    
    /**
     * Search products by name, description or category
     * @param searchText Text to search for
     * @return List of matching Product objects
     */
    public List<Product> searchProducts(String searchText) {
        List<Product> products = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT * FROM products WHERE name LIKE ? OR description LIKE ? OR category LIKE ?";
            statement = connection.prepareStatement(query);
            
            String searchPattern = "%" + searchText + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Product product = mapResultSetToProduct(resultSet);
                products.add(product);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching for products with term: " + searchText, e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return products;
    }
    
    /**
     * Get products with low stock (below min stock level)
     * @return List of Product objects with low stock
     */
    public List<Product> getLowStockProducts() {
        List<Product> products = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT * FROM products WHERE stock_quantity < 10"; // Using default min stock of 10
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Product product = mapResultSetToProduct(resultSet);
                products.add(product);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting low stock products", e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return products;
    }
    
    /**
     * Map ResultSet to Product object
     * @param resultSet ResultSet to map
     * @return Product object
     * @throws SQLException if an error occurs
     */
    private Product mapResultSetToProduct(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("medicine_id");
        String name = resultSet.getString("name");
        String brand = resultSet.getString("brand");
        String category = resultSet.getString("category");
        int stockQuantity = resultSet.getInt("stock_quantity");
        LocalDate expiryDate = resultSet.getObject("expiry_date", LocalDate.class);
        double price = resultSet.getDouble("price");
        String description = resultSet.getString("description");
        int categoryId = resultSet.getInt("category_id");
        
        return new Product(id, name, brand, category, stockQuantity, expiryDate, price, description, categoryId);
    }
} 