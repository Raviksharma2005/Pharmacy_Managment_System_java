package com.pharmacy.models;

public class Supplier {
    private int supplierId;
    private String name;
    private String contactNumber;
    private String email;
    private String address;

    // Constructor
    public Supplier(int supplierId, String name, String contactNumber, String email, String address) {
        this.supplierId = supplierId;
        this.name = name;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
    }
    
    // Constructor used in AdminDashboardController
    public Supplier(int supplierId, String name, String contactPerson, String email, String phone, String address, String notes) {
        this.supplierId = supplierId;
        this.name = name;
        this.contactNumber = phone;
        this.email = email;
        this.address = address;
        // notes parameter is ignored as there's no corresponding field
    }

    // Default constructor
    public Supplier() {
    }

    // Getters and Setters
    public int getId() {
        return supplierId;
    }

    public void setId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    // For backward compatibility
    public String getContactPerson() {
        return name; // Use name as contact person for now
    }
    
    public String getPhone() {
        return contactNumber;
    }

    @Override
    public String toString() {
        return name;
    }
} 