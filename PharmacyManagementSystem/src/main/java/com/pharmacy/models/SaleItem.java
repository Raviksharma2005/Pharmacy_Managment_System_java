package com.pharmacy.models;

/**
 * Model class for Invoice_Item
 */
public class SaleItem {
    private int invoiceItemId;
    private int invoiceId;
    private int medicineId;
    private int quantity;
    private double pricePerUnit;
    private double totalPrice;
    private String productName; // For display purposes

    // Constructor
    public SaleItem(int invoiceItemId, int invoiceId, int medicineId, int quantity, 
                   double pricePerUnit, double totalPrice) {
        this.invoiceItemId = invoiceItemId;
        this.invoiceId = invoiceId;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.totalPrice = totalPrice;
    }
    
    // Constructor with product name for display
    public SaleItem(int invoiceItemId, int invoiceId, int medicineId, String productName, 
                   int quantity, double pricePerUnit) {
        this.invoiceItemId = invoiceItemId;
        this.invoiceId = invoiceId;
        this.medicineId = medicineId;
        this.productName = productName;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.totalPrice = quantity * pricePerUnit;
    }

    // Default constructor
    public SaleItem() {
    }

    // Getters and Setters
    public int getInvoiceItemId() {
        return invoiceItemId;
    }

    public void setInvoiceItemId(int invoiceItemId) {
        this.invoiceItemId = invoiceItemId;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(int medicineId) {
        this.medicineId = medicineId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        // Update total price when quantity changes
        this.totalPrice = this.quantity * this.pricePerUnit;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
        // Update total price when price changes
        this.totalPrice = this.quantity * this.pricePerUnit;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    // For backward compatibility
    public int getId() {
        return invoiceItemId;
    }
    
    public void setId(int id) {
        this.invoiceItemId = id;
    }
    
    public int getSaleId() {
        return invoiceId;
    }
    
    public void setSaleId(int saleId) {
        this.invoiceId = saleId;
    }
    
    public int getProductId() {
        return medicineId;
    }
    
    public void setProductId(int productId) {
        this.medicineId = productId;
    }
    
    public double getPrice() {
        return pricePerUnit;
    }
    
    public void setPrice(double price) {
        setPricePerUnit(price);
    }
    
    public double getTotal() {
        return totalPrice;
    }

    @Override
    public String toString() {
        return productName + " x " + quantity;
    }
} 