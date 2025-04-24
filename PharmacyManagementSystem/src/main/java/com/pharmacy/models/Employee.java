package com.pharmacy.models;

import java.time.LocalDate;

/**
 * Model class for Employee
 */
public class Employee {
    private int employeeId;
    private String name;
    private String role;
    private String contactNumber;
    private String email;
    private LocalDate hireDate;
    private double salary;

    // Constructor
    public Employee(int employeeId, String name, String role, String contactNumber, 
                   String email, LocalDate hireDate, double salary) {
        this.employeeId = employeeId;
        this.name = name != null ? name : "";
        this.role = role != null ? role : "";
        this.contactNumber = contactNumber != null ? contactNumber : "";
        this.email = email != null ? email : "";
        this.hireDate = hireDate != null ? hireDate : LocalDate.now();
        this.salary = salary;
    }
    
    // Constructor used in AdminDashboardController
    public Employee(int employeeId, String name, String role, String email, String phone, String address, String username, String password) {
        this.employeeId = employeeId;
        this.name = name != null ? name : "";
        this.role = role != null ? role : "";
        this.contactNumber = phone != null ? phone : "";
        this.email = email != null ? email : "";
        this.hireDate = LocalDate.now(); // Default to current date
        this.salary = 0.0; // Default salary
    }

    // Default constructor
    public Employee() {
        this.name = "";
        this.role = "";
        this.contactNumber = "";
        this.email = "";
        this.hireDate = LocalDate.now();
        this.salary = 0.0;
    }

    // Getters and Setters
    public int getId() {
        return employeeId;
    }

    public void setId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name != null ? name : "";
    }

    public void setName(String name) {
        this.name = name != null ? name : "";
    }

    public String getRole() {
        return role != null ? role : "";
    }

    public void setRole(String role) {
        this.role = role != null ? role : "";
    }

    public String getContactNumber() {
        return contactNumber != null ? contactNumber : "";
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber != null ? contactNumber : "";
    }

    public String getEmail() {
        return email != null ? email : "";
    }

    public void setEmail(String email) {
        this.email = email != null ? email : "";
    }

    public LocalDate getHireDate() {
        return hireDate != null ? hireDate : LocalDate.now();
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate != null ? hireDate : LocalDate.now();
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
    
    // For backward compatibility
    public String getPhone() {
        return contactNumber != null ? contactNumber : "";
    }
    
    public String getAddress() {
        return ""; // Not stored in this model anymore
    }
    
    public String getUsername() {
        return ""; // Not stored in this model anymore
    }
    
    public String getPassword() {
        return ""; // Not stored in this model anymore
    }

    @Override
    public String toString() {
        return name != null ? name : "";
    }

    // Check if this employee is an admin
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }
} 