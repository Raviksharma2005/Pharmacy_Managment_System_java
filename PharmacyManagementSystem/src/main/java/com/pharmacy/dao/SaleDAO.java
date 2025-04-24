package com.pharmacy.dao;

import com.pharmacy.models.Sale;
import com.pharmacy.models.SaleItem;
import com.pharmacy.models.Customer;
import com.pharmacy.util.DatabaseConnector;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Sales/Invoices
 */
public class SaleDAO {
    private static final Logger LOGGER = Logger.getLogger(SaleDAO.class.getName());
    private final DatabaseConnector dbConnector;
    
    public SaleDAO() {
        this.dbConnector = DatabaseConnector.getInstance();
        // Ensure required tables exist
        ensureSaleTablesExist();
    }
    
    /**
     * Ensure that all tables required for sale operations exist
     */
    private void ensureSaleTablesExist() {
        Connection connection = null;
        Statement statement = null;
        
        try {
            connection = dbConnector.getConnection();
            statement = connection.createStatement();
            
            // Check if Customer table exists, create if not
            try {
                statement.execute("SELECT 1 FROM Customer LIMIT 1");
            } catch (SQLException e) {
                LOGGER.info("Creating Customer table");
                statement.execute(
                    "CREATE TABLE IF NOT EXISTS Customer (" +
                    "customer_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100) NOT NULL, " +
                    "phone VARCHAR(15) NOT NULL, " +
                    "address TEXT)");
                
                // Add default customer
                statement.execute(
                    "INSERT INTO Customer (name, email, phone, address) " +
                    "VALUES ('Walk-in Customer', 'customer@pharmacy.com', '0000000000', 'Default customer')");
            }
            
            // Check if Invoice table exists, create if not
            try {
                statement.execute("SELECT 1 FROM Invoice LIMIT 1");
            } catch (SQLException e) {
                LOGGER.info("Creating Invoice table");
                statement.execute(
                    "CREATE TABLE IF NOT EXISTS Invoice (" +
                    "invoice_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "employee_id INT, " +
                    "customer_id INT, " +
                    "invoice_date DATE, " +
                    "total_amount DECIMAL(10,2), " +
                    "discount_amount DECIMAL(10,2), " +
                    "tax_amount DECIMAL(10,2), " +
                    "final_amount DECIMAL(10,2))");
            }
            
            // Check if Invoice_Item table exists, create if not
            try {
                statement.execute("SELECT 1 FROM Invoice_Item LIMIT 1");
            } catch (SQLException e) {
                LOGGER.info("Creating Invoice_Item table");
                statement.execute(
                    "CREATE TABLE IF NOT EXISTS Invoice_Item (" +
                    "invoice_item_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "invoice_id INT, " +
                    "medicine_id INT, " +
                    "quantity INT, " +
                    "price_per_unit DECIMAL(10,2), " +
                    "total_price DECIMAL(10,2))");
            }
            
            // Check if Stock_Transactions table exists, create if not
            try {
                statement.execute("SELECT 1 FROM Stock_Transactions LIMIT 1");
            } catch (SQLException e) {
                LOGGER.info("Creating Stock_Transactions table");
                statement.execute(
                    "CREATE TABLE IF NOT EXISTS Stock_Transactions (" +
                    "transaction_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "medicine_id INT, " +
                    "change_quantity INT, " +
                    "transaction_type VARCHAR(10), " +
                    "transaction_date DATE, " +
                    "reference_id INT)");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error ensuring sale tables exist", e);
        } finally {
            dbConnector.closeResources(connection, statement, null);
        }
    }
    
    /**
     * Get all sales from the database
     * @return List of Sale objects
     */
    public List<Sale> getAllSales() {
        List<Sale> sales = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT * FROM Invoice ORDER BY invoice_date DESC";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Sale sale = mapResultSetToSale(resultSet);
                // Get sale items for this sale
                sale.setItems(getSaleItemsBySaleId(sale.getInvoiceId()));
                sales.add(sale);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all sales", e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return sales;
    }
    
    /**
     * Get sales by date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of Sale objects in the date range
     */
    public List<Sale> getSalesByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Sale> sales = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT * FROM Invoice WHERE invoice_date BETWEEN ? AND ? ORDER BY invoice_date DESC";
            statement = connection.prepareStatement(query);
            statement.setDate(1, Date.valueOf(startDate));
            statement.setDate(2, Date.valueOf(endDate));
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Sale sale = mapResultSetToSale(resultSet);
                // Get sale items for this sale
                sale.setItems(getSaleItemsBySaleId(sale.getInvoiceId()));
                sales.add(sale);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting sales by date range", e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return sales;
    }
    
    /**
     * Get sales from today
     * @return List of Sales from today
     */
    public List<Sale> getTodaySales() {
        LocalDate today = LocalDate.now();
        return getSalesByDateRange(today, today);
    }
    
    /**
     * Get sale by ID
     * @param id Sale ID
     * @return Sale object if found, null otherwise
     */
    public Sale getSaleById(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT * FROM Invoice WHERE invoice_id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                Sale sale = mapResultSetToSale(resultSet);
                // Get sale items for this sale
                sale.setItems(getSaleItemsBySaleId(sale.getInvoiceId()));
                return sale;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting sale by ID: " + id, e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return null;
    }
    
    /**
     * Add a new sale to the database
     * @param sale Sale to add
     * @return true if successful, false otherwise
     */
    public boolean addSale(Sale sale) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        
        try {
            LOGGER.info("Starting to add sale with customer ID: " + sale.getCustomerId());
            connection = dbConnector.getConnection();
            
            // Start transaction
            connection.setAutoCommit(false);
            
            // Check if the customer exists
            Customer customer = null;
            int customerId = sale.getCustomerId();
            if (customerId > 0) {
                try {
                    PreparedStatement checkCustomer = connection.prepareStatement(
                        "SELECT * FROM Customer WHERE customer_id = ?");
                    checkCustomer.setInt(1, customerId);
                    ResultSet customerResult = checkCustomer.executeQuery();
                    
                    if (customerResult.next()) {
                        // Customer exists
                        LOGGER.info("Customer found with ID: " + customerId);
                    } else {
                        // Customer doesn't exist, use default ID 1
                        LOGGER.warning("Customer with ID " + customerId + " not found. Using default customer (ID: 1).");
                        customerId = 1;
                        
                        // Check if default customer exists
                        checkCustomer = connection.prepareStatement(
                            "SELECT COUNT(*) FROM Customer WHERE customer_id = 1");
                        customerResult = checkCustomer.executeQuery();
                        
                        if (customerResult.next() && customerResult.getInt(1) == 0) {
                            // Create default customer if not exist
                            LOGGER.info("Creating default customer");
                            PreparedStatement insertCustomer = connection.prepareStatement(
                                "INSERT INTO Customer (name, email, phone, address) VALUES (?, ?, ?, ?)");
                            insertCustomer.setString(1, "Walk-in Customer");
                            insertCustomer.setString(2, "customer@pharmacy.com");
                            insertCustomer.setString(3, "0000000000");
                            insertCustomer.setString(4, "General Customer");
                            insertCustomer.executeUpdate();
                        }
                    }
                    customerResult.close();
                    checkCustomer.close();
                } catch (SQLException e) {
                    // This might happen if Customer table doesn't exist
                    LOGGER.severe("Error checking customer: " + e.getMessage());
                    
                    // Try to create Customer table
                    try {
                        LOGGER.info("Attempting to create Customer table");
                        Statement createTableStmt = connection.createStatement();
                        createTableStmt.execute(
                            "CREATE TABLE IF NOT EXISTS Customer (" +
                            "customer_id INT PRIMARY KEY AUTO_INCREMENT, " +
                            "name VARCHAR(100) NOT NULL, " +
                            "email VARCHAR(100) UNIQUE NOT NULL, " +
                            "phone VARCHAR(15) NOT NULL, " +
                            "address TEXT)");
                        
                        // Add default customer
                        PreparedStatement insertCustomer = connection.prepareStatement(
                            "INSERT INTO Customer (name, email, phone, address) VALUES (?, ?, ?, ?)");
                        insertCustomer.setString(1, "Walk-in Customer");
                        insertCustomer.setString(2, "customer@pharmacy.com");
                        insertCustomer.setString(3, "0000000000");
                        insertCustomer.setString(4, "General Customer");
                        insertCustomer.executeUpdate();
                        
                        // Get the ID of the newly inserted customer
                        Statement getIdStmt = connection.createStatement();
                        ResultSet idRs = getIdStmt.executeQuery("SELECT LAST_INSERT_ID()");
                        if (idRs.next()) {
                            customerId = idRs.getInt(1);
                            LOGGER.info("Created default customer with ID: " + customerId);
                        } else {
                            customerId = 1; // Fallback
                        }
                    } catch (SQLException createEx) {
                        LOGGER.severe("Failed to create Customer table: " + createEx.getMessage());
                        LOGGER.severe("Proceeding with customer ID 1 and hoping for the best");
                        customerId = 1;
                    }
                }
            } else {
                // No customer ID provided, use default ID 1
                LOGGER.info("No customer ID provided. Using default customer (ID: 1).");
                customerId = 1;
            }
            
            // Insert sale
            String query = "INSERT INTO Invoice (employee_id, customer_id, invoice_date, total_amount, "
                    + "discount_amount, tax_amount, final_amount) VALUES (?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            // Default employee id to 1 if not set
            statement.setInt(1, sale.getEmployeeId() > 0 ? sale.getEmployeeId() : 1);
            
            // Use the verified customer ID
            statement.setInt(2, customerId);
            
            // Use current date if not set
            statement.setDate(3, Date.valueOf(sale.getInvoiceDate() != null ? 
                    sale.getInvoiceDate() : LocalDate.now()));
            
            statement.setDouble(4, sale.getTotalAmount());
            statement.setDouble(5, sale.getDiscountAmount());
            statement.setDouble(6, sale.getTaxAmount());
            statement.setDouble(7, sale.getFinalAmount());
            
            LOGGER.info("Executing invoice insert with: " + 
                     "emp_id=" + (sale.getEmployeeId() > 0 ? sale.getEmployeeId() : 1) + 
                     ", cust_id=" + customerId + 
                     ", date=" + (sale.getInvoiceDate() != null ? sale.getInvoiceDate() : LocalDate.now()) + 
                     ", amounts=" + sale.getTotalAmount() + "," + sale.getDiscountAmount() + "," + 
                     sale.getTaxAmount() + "," + sale.getFinalAmount());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get generated ID
                generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int saleId = generatedKeys.getInt(1);
                    sale.setInvoiceId(saleId);
                    LOGGER.info("Created invoice with ID: " + saleId);
                    
                    // Insert sale items
                    if (sale.getItems() != null && !sale.getItems().isEmpty()) {
                        boolean allItemsAdded = true;
                        
                        for (SaleItem item : sale.getItems()) {
                            item.setInvoiceId(saleId);
                            if (!addSaleItem(connection, item)) {
                                LOGGER.severe("Failed to add sale item: " + item.getProductName());
                                allItemsAdded = false;
                                break;
                            }
                            
                            // Record stock transaction and update stock
                            if (!updateProductStock(connection, item.getMedicineId(), item.getQuantity(), saleId)) {
                                LOGGER.severe("Failed to update stock for medicine ID: " + item.getMedicineId());
                                allItemsAdded = false;
                                break;
                            }
                        }
                        
                        if (!allItemsAdded) {
                            // Rollback on failure
                            LOGGER.warning("Rolling back transaction due to item addition failure");
                            connection.rollback();
                            return false;
                        }
                    } else {
                        LOGGER.warning("No items in sale, but creating empty invoice");
                    }
                    
                    // Commit transaction
                    LOGGER.info("Committing transaction");
                    connection.commit();
                    return true;
                } else {
                    LOGGER.severe("Failed to get generated invoice ID");
                }
            } else {
                LOGGER.severe("No rows affected when adding invoice");
            }
            
            // Rollback if we got here
            LOGGER.warning("Rolling back transaction");
            connection.rollback();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding sale: " + e.getMessage(), e);
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error rolling back transaction", ex);
            }
        } finally {
            if (generatedKeys != null) {
                try {
                    generatedKeys.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing ResultSet", e);
                }
            }
            
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error resetting auto-commit", e);
            }
            dbConnector.closeResources(connection, statement, null);
        }
        
        return false;
    }
    
    /**
     * Add a sale item to the database
     * @param connection Active database connection
     * @param item Sale item to add
     * @return true if successful, false otherwise
     */
    private boolean addSaleItem(Connection connection, SaleItem item) {
        PreparedStatement statement = null;
        
        try {
            String query = "INSERT INTO Invoice_Item (invoice_id, medicine_id, quantity, price_per_unit, total_price) "
                    + "VALUES (?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            statement.setInt(1, item.getInvoiceId());
            statement.setInt(2, item.getMedicineId());
            statement.setInt(3, item.getQuantity());
            statement.setDouble(4, item.getPricePerUnit());
            statement.setDouble(5, item.getTotalPrice());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    item.setInvoiceItemId(generatedKeys.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding sale item", e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing statement", e);
                }
            }
        }
        
        return false;
    }
    
    /**
     * Update product stock after a sale
     * @param connection Active database connection
     * @param medicineId Medicine ID
     * @param quantity Quantity sold
     * @param referenceId Reference ID (invoice ID)
     * @return true if successful, false otherwise
     */
    private boolean updateProductStock(Connection connection, int medicineId, int quantity, int referenceId) {
        PreparedStatement statement = null;
        
        try {
            // First get current stock
            String queryGet = "SELECT stock_quantity FROM Medicine WHERE medicine_id = ?";
            statement = connection.prepareStatement(queryGet);
            statement.setInt(1, medicineId);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                int currentStock = resultSet.getInt("stock_quantity");
                int newStock = currentStock - quantity;
                if (newStock < 0) newStock = 0; // Prevent negative stock
                
                // Close the first statement and result set
                resultSet.close();
                statement.close();
                
                // Update stock
                String queryUpdate = "UPDATE Medicine SET stock_quantity = ? WHERE medicine_id = ?";
                statement = connection.prepareStatement(queryUpdate);
                statement.setInt(1, newStock);
                statement.setInt(2, medicineId);
                
                int rowsAffected = statement.executeUpdate();
                
                if (rowsAffected > 0) {
                    // Record stock transaction
                    statement.close();
                    String queryTrans = "INSERT INTO Stock_Transactions (medicine_id, change_quantity, transaction_type, "
                            + "transaction_date, reference_id) VALUES (?, ?, ?, CURDATE(), ?)";
                    statement = connection.prepareStatement(queryTrans);
                    
                    statement.setInt(1, medicineId);
                    statement.setInt(2, quantity);
                    statement.setString(3, "sale");
                    statement.setInt(4, referenceId); // Now we use the invoice ID
                    
                    rowsAffected = statement.executeUpdate();
                    return rowsAffected > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating product stock", e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Error closing statement", e);
                }
            }
        }
        
        return false;
    }
    
    /**
     * Get sale items by sale ID
     * @param saleId Sale ID
     * @return List of SaleItem objects
     */
    public List<SaleItem> getSaleItemsBySaleId(int saleId) {
        List<SaleItem> items = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT i.*, m.name as product_name FROM Invoice_Item i "
                    + "JOIN Medicine m ON i.medicine_id = m.medicine_id "
                    + "WHERE i.invoice_id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, saleId);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                SaleItem item = new SaleItem(
                    resultSet.getInt("invoice_item_id"),
                    resultSet.getInt("invoice_id"),
                    resultSet.getInt("medicine_id"),
                    resultSet.getString("product_name"),
                    resultSet.getInt("quantity"),
                    resultSet.getDouble("price_per_unit")
                );
                item.setTotalPrice(resultSet.getDouble("total_price"));
                items.add(item);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting sale items by sale ID: " + saleId, e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return items;
    }
    
    /**
     * Map ResultSet row to Sale object
     * @param resultSet ResultSet containing sale data
     * @return Sale object
     * @throws SQLException if database error occurs
     */
    private Sale mapResultSetToSale(ResultSet resultSet) throws SQLException {
        Sale sale = new Sale();
        sale.setInvoiceId(resultSet.getInt("invoice_id"));
        sale.setEmployeeId(resultSet.getInt("employee_id"));
        sale.setCustomerId(resultSet.getInt("customer_id"));
        
        // Convert SQL date to LocalDate
        Date invoiceDate = resultSet.getDate("invoice_date");
        if (invoiceDate != null) {
            sale.setInvoiceDate(invoiceDate.toLocalDate());
        }
        
        sale.setTotalAmount(resultSet.getDouble("total_amount"));
        sale.setDiscountAmount(resultSet.getDouble("discount_amount"));
        sale.setTaxAmount(resultSet.getDouble("tax_amount"));
        sale.setFinalAmount(resultSet.getDouble("final_amount"));
        
        // Try to get customer name if available
        try {
            // Check if there's a customer_name column in the result set
            sale.setCustomerName(resultSet.getString("customer_name"));
        } catch (SQLException e) {
            // If the column doesn't exist, get it from the Customer table
            getCustomerName(sale);
        }
        
        return sale;
    }
    
    /**
     * Get customer name for a sale
     * @param sale Sale to get customer name for
     */
    private void getCustomerName(Sale sale) {
        if (sale.getCustomerId() <= 0) return;
        
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT name FROM Customer WHERE customer_id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, sale.getCustomerId());
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                sale.setCustomerName(resultSet.getString("name"));
            } else {
                sale.setCustomerName("Unknown Customer");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error getting customer name", e);
            sale.setCustomerName("Unknown Customer");
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
    }
} 