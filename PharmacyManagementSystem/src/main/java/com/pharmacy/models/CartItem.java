package com.pharmacy.models;

import javafx.beans.property.*;

/**
 * Model class for Cart Item
 */
public class CartItem {
    private int id;
    private Product product;
    private int quantity;
    private double price;

    // Constructor
    public CartItem(int id, Product product, int quantity, double price) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    // Default constructor
    public CartItem() {
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotal() {
        return price * quantity;
    }

    // Methods for backward compatibility
    public int getProductId() {
        return product != null ? product.getId() : 0;
    }
    
    public String getProductName() {
        return product != null ? product.getName() : "";
    }
    
    // Increment quantity by 1
    public void incrementQuantity() {
        this.quantity += 1;
    }
    
    // Decrement quantity by 1, but don't go below 1
    public void decrementQuantity() {
        if (this.quantity > 1) {
            this.quantity -= 1;
        }
    }

    @Override
    public String toString() {
        return product.getName() + " x " + quantity;
    }
} 