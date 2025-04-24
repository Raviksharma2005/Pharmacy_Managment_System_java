package com.pharmacy.models;

import java.time.LocalDate;

/**
 * Model class for Medicine/Product
 */
public class Product {
    private int medicineId;
    private String name;
    private String brand;
    private String category;
    private int stockQuantity;
    private LocalDate expiryDate;
    private double price;
    private String description;
    private int categoryId;
    
    /**
     * Constructor for Product
     */
    public Product(int medicineId, String name, String brand, String category, int stockQuantity, 
                  LocalDate expiryDate, double price, String description, int categoryId) {
        this.medicineId = medicineId;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.stockQuantity = stockQuantity;
        this.expiryDate = expiryDate;
        this.price = price;
        this.description = description;
        this.categoryId = categoryId;
    }
    
    // Shorter constructor for simpler usage
    public Product(int medicineId, String name, String description, double price, int stockQuantity, String category) {
        this.medicineId = medicineId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.expiryDate = LocalDate.now().plusYears(1); // Default expiry of 1 year
    }
    
    // Constructor used in AdminDashboardController and PharmacistDashboardController
    public Product(int medicineId, String name, String description, double price, int stockQuantity, String category, String supplier, int minStock) {
        this.medicineId = medicineId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.brand = supplier; // Map supplier to brand
        this.expiryDate = LocalDate.now().plusYears(1); // Default expiry of 1 year
        this.categoryId = 0; // Default category ID
    }
    
    // Default constructor
    public Product() {
    }
    
    // Getters and Setters
    public int getId() {
        return medicineId;
    }
    
    public void setId(int medicineId) {
        this.medicineId = medicineId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public int getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public LocalDate getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    // Methods for compatibility with AdminDashboardController
    public String getSupplier() {
        return brand;
    }
    
    public void setSupplier(String supplier) {
        this.brand = supplier;
    }
    
    public int getMinStock() {
        return 10; // Default minimum stock level
    }
    
    public void setMinStock(int minStock) {
        // This is a placeholder since the actual field doesn't exist
    }
    
    // For backward compatibility with existing code
    public int getQuantity() {
        return stockQuantity;
    }
    
    public void setQuantity(int quantity) {
        this.stockQuantity = quantity;
    }
    
    public int getStock() {
        return stockQuantity;
    }
    
    public void setStock(int stock) {
        this.stockQuantity = stock;
    }
    
    // Check if stock is low (less than 10 by default)
    public boolean isLowOnStock() {
        return stockQuantity < 10;
    }
    
    @Override
    public String toString() {
        return name;
    }
} 