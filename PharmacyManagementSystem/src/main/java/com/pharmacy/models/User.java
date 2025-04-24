package com.pharmacy.models;

public class User {
    private int userId;
    private String username;
    private String password;
    private String role; // Admin, Pharmacist
    private Integer employeeId; // Can be null if not linked to an employee

    // Constructor
    public User(int userId, String username, String password, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.employeeId = null;
    }
    
    // Constructor with employee ID
    public User(int userId, String username, String password, String role, Integer employeeId) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.employeeId = employeeId;
    }

    // Default constructor
    public User() {
    }

    // Getters and Setters
    public int getId() {
        return userId;
    }

    public void setId(int userId) {
        this.userId = userId;
    }
    
    // For compatibility
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }
    
    /**
     * Check if user is an admin
     * @return true if user is admin
     */
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }
    
    /**
     * Check if user is a pharmacist
     * @return true if user is pharmacist
     */
    public boolean isPharmacist() {
        return "pharmacist".equalsIgnoreCase(role);
    }

    @Override
    public String toString() {
        return username + " (" + role + ")";
    }
} 