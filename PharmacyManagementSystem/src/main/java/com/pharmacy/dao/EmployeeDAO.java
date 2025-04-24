package com.pharmacy.dao;

import com.pharmacy.models.Employee;
import com.pharmacy.util.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Employee entity
 */
public class EmployeeDAO {
    private static final Logger LOGGER = Logger.getLogger(EmployeeDAO.class.getName());
    private final DatabaseConnector dbConnector;
    
    public EmployeeDAO() {
        this.dbConnector = DatabaseConnector.getInstance();
    }
    
    /**
     * Get all employees from the database
     * @return List of Employee objects
     */
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT * FROM Employee";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Employee employee = mapResultSetToEmployee(resultSet);
                employees.add(employee);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all employees", e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return employees;
    }
    
    /**
     * Get employee by ID
     * @param id Employee ID
     * @return Employee object if found, null otherwise
     */
    public Employee getEmployeeById(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT * FROM Employee WHERE employee_id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToEmployee(resultSet);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting employee by ID: " + id, e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return null;
    }
    
    /**
     * Add a new employee to the database
     * @param employee Employee to add
     * @return true if successful, false otherwise
     */
    public boolean addEmployee(Employee employee) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "INSERT INTO Employee (name, role, contact_number, email, hire_date, salary) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            statement.setString(1, employee.getName());
            statement.setString(2, employee.getRole());
            statement.setString(3, employee.getContactNumber());
            statement.setString(4, employee.getEmail());
            statement.setObject(5, employee.getHireDate());
            statement.setDouble(6, employee.getSalary());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    employee.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding employee", e);
        } finally {
            dbConnector.closeResources(connection, statement, null);
        }
        
        return false;
    }
    
    /**
     * Update an existing employee in the database
     * @param employee Employee to update
     * @return true if successful, false otherwise
     */
    public boolean updateEmployee(Employee employee) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "UPDATE Employee SET name = ?, role = ?, contact_number = ?, " +
                    "email = ?, hire_date = ?, salary = ? WHERE employee_id = ?";
            statement = connection.prepareStatement(query);
            
            statement.setString(1, employee.getName());
            statement.setString(2, employee.getRole());
            statement.setString(3, employee.getContactNumber());
            statement.setString(4, employee.getEmail());
            statement.setObject(5, employee.getHireDate());
            statement.setDouble(6, employee.getSalary());
            statement.setInt(7, employee.getId());
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating employee with ID: " + employee.getId(), e);
        } finally {
            dbConnector.closeResources(connection, statement, null);
        }
        
        return false;
    }
    
    /**
     * Delete an employee from the database
     * @param id Employee ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteEmployee(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "DELETE FROM Employee WHERE employee_id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting employee with ID: " + id, e);
        } finally {
            dbConnector.closeResources(connection, statement, null);
        }
        
        return false;
    }
    
    /**
     * Search employees by name, role or email
     * @param searchText Text to search for
     * @return List of matching Employee objects
     */
    public List<Employee> searchEmployees(String searchText) {
        List<Employee> employees = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT * FROM Employee WHERE name LIKE ? OR role LIKE ? OR email LIKE ?";
            statement = connection.prepareStatement(query);
            
            String searchPattern = "%" + searchText + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Employee employee = mapResultSetToEmployee(resultSet);
                employees.add(employee);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching for employees with term: " + searchText, e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return employees;
    }
    
    /**
     * Get employees by role
     * @param role Role to filter by
     * @return List of Employee objects with the specified role
     */
    public List<Employee> getEmployeesByRole(String role) {
        List<Employee> employees = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = dbConnector.getConnection();
            String query = "SELECT * FROM Employee WHERE role = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, role);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Employee employee = mapResultSetToEmployee(resultSet);
                employees.add(employee);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting employees by role: " + role, e);
        } finally {
            dbConnector.closeResources(connection, statement, resultSet);
        }
        
        return employees;
    }
    
    /**
     * Map ResultSet to Employee object
     * @param resultSet ResultSet to map
     * @return Employee object
     * @throws SQLException if an error occurs
     */
    private Employee mapResultSetToEmployee(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("employee_id");
        String name = resultSet.getString("name");
        String role = resultSet.getString("role");
        String contactNumber = resultSet.getString("contact_number");
        String email = resultSet.getString("email");
        LocalDate hireDate = resultSet.getObject("hire_date", LocalDate.class);
        double salary = resultSet.getDouble("salary");
        
        return new Employee(id, name, role, contactNumber, email, hireDate, salary);
    }
} 