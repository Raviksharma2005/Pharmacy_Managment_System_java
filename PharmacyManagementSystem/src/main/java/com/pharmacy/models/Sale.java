package com.pharmacy.models;

import java.time.LocalDate;
import java.util.List;

/**
 * Model class for Invoice/Sale
 */
public class Sale {
    private int invoiceId;
    private int employeeId;
    private int customerId;
    private LocalDate invoiceDate;
    private double totalAmount;
    private double discountAmount;
    private double taxAmount;
    private double finalAmount;
    private List<SaleItem> items;
    private String customerName; // For display purposes

    // Constructor
    public Sale(int invoiceId, int employeeId, int customerId, LocalDate invoiceDate, 
               double totalAmount, double discountAmount, double taxAmount, double finalAmount, 
               List<SaleItem> items, String customerName) {
        this.invoiceId = invoiceId;
        this.employeeId = employeeId;
        this.customerId = customerId;
        this.invoiceDate = invoiceDate;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.taxAmount = taxAmount;
        this.finalAmount = finalAmount;
        this.items = items;
        this.customerName = customerName;
    }
    
    // Simpler constructor for backward compatibility
    public Sale(int invoiceId, LocalDate invoiceDate, String customerName, int itemCount, 
               double totalAmount, String status, List<SaleItem> items) {
        this.invoiceId = invoiceId;
        this.invoiceDate = invoiceDate;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.finalAmount = totalAmount; // Default final amount equals total amount
        this.items = items;
    }

    // Default constructor
    public Sale() {
    }

    // Getters and Setters
    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public List<SaleItem> getItems() {
        return items;
    }

    public void setItems(List<SaleItem> items) {
        this.items = items;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    // Methods for backward compatibility
    public int getInvoiceNumber() {
        return invoiceId;
    }
    
    public void setInvoiceNumber(int invoiceNumber) {
        this.invoiceId = invoiceNumber;
    }
    
    public LocalDate getDate() {
        return invoiceDate;
    }
    
    public void setDate(LocalDate date) {
        this.invoiceDate = date;
    }
    
    public String getCustomer() {
        return customerName;
    }
    
    public void setCustomer(String customer) {
        this.customerName = customer;
    }
    
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }
    
    public String getStatus() {
        return "Completed"; // Default status
    }
    
    public void setStatus(String status) {
        // Not used in this model
    }
    
    public List<SaleItem> getSaleItems() {
        return items;
    }
    
    public double getTotal() {
        return finalAmount;
    }
    
    public int getItemsCount() {
        return getItemCount();
    }

    @Override
    public String toString() {
        return "Invoice #" + invoiceId;
    }
} 