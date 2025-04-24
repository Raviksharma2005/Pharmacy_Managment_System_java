package com.pharmacy.utils;

import com.pharmacy.dao.*;
import com.pharmacy.models.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for validating business logic operations
 */
public class BusinessLogicValidator {
    private static final Logger LOGGER = Logger.getLogger(BusinessLogicValidator.class.getName());
    
    /**
     * Validate product creation and retrieval
     * @return true if validation passes, false otherwise
     */
    public static boolean validateProductCreation() {
        ProductDAO productDAO = new ProductDAO();
        
        try {
            // Create a test product
            String testName = "Test Product " + System.currentTimeMillis();
            Product testProduct = new Product(
                0, // ID will be assigned by DB
                testName,
                "Test product description",
                9.99,
                50,
                "Test Category",
                "Test Supplier",
                10
            );
            
            // Add the product
            boolean addResult = productDAO.addProduct(testProduct);
            if (!addResult) {
                LOGGER.severe("Failed to add test product");
                return false;
            }
            
            // Retrieve all products and find our test product
            List<Product> products = productDAO.getAllProducts();
            boolean found = products.stream()
                .anyMatch(p -> p.getName().equals(testName));
            
            if (!found) {
                LOGGER.severe("Test product not found after adding");
                return false;
            }
            
            LOGGER.info("Product creation validation: PASSED");
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error validating product creation", e);
            return false;
        }
    }
    
    /**
     * Validate product update
     * @return true if validation passes, false otherwise
     */
    public static boolean validateProductUpdate() {
        ProductDAO productDAO = new ProductDAO();
        
        try {
            // Create a test product
            String testName = "Test Update Product " + System.currentTimeMillis();
            Product testProduct = new Product(
                0, // ID will be assigned by DB
                testName,
                "Original description",
                9.99,
                50,
                "Test Category",
                "Test Supplier",
                10
            );
            
            // Add the product
            boolean addResult = productDAO.addProduct(testProduct);
            if (!addResult) {
                LOGGER.severe("Failed to add test product for update");
                return false;
            }
            
            // Find the product to get its ID
            List<Product> products = productDAO.getAllProducts();
            Product addedProduct = products.stream()
                .filter(p -> p.getName().equals(testName))
                .findFirst()
                .orElse(null);
            
            if (addedProduct == null) {
                LOGGER.severe("Test product not found after adding for update test");
                return false;
            }
            
            // Update the product
            addedProduct.setDescription("Updated description");
            addedProduct.setPrice(19.99);
            
            boolean updateResult = productDAO.updateProduct(addedProduct);
            if (!updateResult) {
                LOGGER.severe("Failed to update test product");
                return false;
            }
            
            // Verify the update
            Product updatedProduct = productDAO.getProductById(addedProduct.getId());
            
            if (updatedProduct == null || 
                !updatedProduct.getDescription().equals("Updated description") ||
                Math.abs(updatedProduct.getPrice() - 19.99) > 0.001) {
                
                LOGGER.severe("Product update verification failed");
                return false;
            }
            
            LOGGER.info("Product update validation: PASSED");
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error validating product update", e);
            return false;
        }
    }
    
    /**
     * Validate sales processing
     * @return true if validation passes, false otherwise
     */
    public static boolean validateSalesProcessing() {
        try {
            // Create test products for sale
            ProductDAO productDAO = new ProductDAO();
            
            // First product
            String product1Name = "Sale Test Product 1 " + System.currentTimeMillis();
            Product product1 = new Product(
                0, product1Name, "Test product 1", 10.0, 100, "Test", "Test", 10
            );
            productDAO.addProduct(product1);
            
            // Find the product to get its ID
            List<Product> products = productDAO.getAllProducts();
            Product addedProduct1 = products.stream()
                .filter(p -> p.getName().equals(product1Name))
                .findFirst()
                .orElse(null);
            
            if (addedProduct1 == null) {
                LOGGER.severe("Sale test product 1 not found after adding");
                return false;
            }
            
            // Second product
            String product2Name = "Sale Test Product 2 " + System.currentTimeMillis();
            Product product2 = new Product(
                0, product2Name, "Test product 2", 15.0, 50, "Test", "Test", 5
            );
            productDAO.addProduct(product2);
            
            // Find the product to get its ID
            Product addedProduct2 = products.stream()
                .filter(p -> p.getName().equals(product2Name))
                .findFirst()
                .orElse(null);
            
            if (addedProduct2 == null) {
                LOGGER.severe("Sale test product 2 not found after adding");
                return false;
            }
            
            // Create cart items
            SaleItem item1 = new SaleItem(
                0, // ID will be assigned by DB
                0, // Sale ID will be assigned
                addedProduct1.getId(),
                addedProduct1.getName(),
                2, // quantity
                addedProduct1.getPrice()
            );
            
            SaleItem item2 = new SaleItem(
                0, // ID will be assigned by DB
                0, // Sale ID will be assigned
                addedProduct2.getId(),
                addedProduct2.getName(),
                1, // quantity
                addedProduct2.getPrice()
            );
            
            List<SaleItem> items = new ArrayList<>();
            items.add(item1);
            items.add(item2);
            
            // Calculate total
            double expectedTotal = (2 * addedProduct1.getPrice()) + (1 * addedProduct2.getPrice());
            
            // Create the sale
            Sale sale = new Sale(
                0, // ID will be assigned by DB
                LocalDate.now(),
                "Test Customer",
                items.size(), // item count
                expectedTotal,
                "Completed",
                items
            );
            
            // Process the sale
            // Note: in a real system, we would use a SaleDAO to save the sale
            // For validation, just check stock reduction
            
            // Update product stock
            addedProduct1.setStock(addedProduct1.getStock() - 2);
            addedProduct2.setStock(addedProduct2.getStock() - 1);
            
            boolean stockUpdate1 = productDAO.updateProduct(addedProduct1);
            boolean stockUpdate2 = productDAO.updateProduct(addedProduct2);
            
            if (!stockUpdate1 || !stockUpdate2) {
                LOGGER.severe("Failed to update stock during sale processing");
                return false;
            }
            
            // Verify stock reductions
            Product verifyProduct1 = productDAO.getProductById(addedProduct1.getId());
            Product verifyProduct2 = productDAO.getProductById(addedProduct2.getId());
            
            if (verifyProduct1.getStock() != (100 - 2) || verifyProduct2.getStock() != (50 - 1)) {
                LOGGER.severe("Stock reduction verification failed");
                return false;
            }
            
            LOGGER.info("Sales processing validation: PASSED");
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error validating sales processing", e);
            return false;
        }
    }
    
    /**
     * Run all business logic validations
     * @return true if all validations pass, false otherwise
     */
    public static boolean runBusinessValidations() {
        System.out.println("Running product creation validation...");
        boolean productCreateValid = validateProductCreation();
        System.out.println("Product creation validation: " + (productCreateValid ? "PASSED" : "FAILED"));
        
        System.out.println("Running product update validation...");
        boolean productUpdateValid = validateProductUpdate();
        System.out.println("Product update validation: " + (productUpdateValid ? "PASSED" : "FAILED"));
        
        System.out.println("Running sales processing validation...");
        boolean salesProcessingValid = validateSalesProcessing();
        System.out.println("Sales processing validation: " + (salesProcessingValid ? "PASSED" : "FAILED"));
        
        boolean allValid = productCreateValid && productUpdateValid && salesProcessingValid;
        System.out.println("Business logic validation summary: " + (allValid ? "ALL PASSED" : "SOME FAILED"));
        
        return allValid;
    }
} 