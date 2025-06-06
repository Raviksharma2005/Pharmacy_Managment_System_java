package com.pharmacy.models;

/**
 * Model class for Customer
 */
public class Customer {
    private int customerId;
    private String name;
    private String email;
    private String phone;
    private String address;
    
    /**
     * Constructor
     */
    public Customer(int customerId, String name, String email, String phone, String address) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
    
    /**
     * Default constructor
     */
    public Customer() {
    }
    
    // Getters and Setters
    public int getId() {
        return customerId;
    }
    
    public void setId(int customerId) {
        this.customerId = customerId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    @Override
    public String toString() {
        return name;
    }
} 