# PharmaCare Management System Validation Plan

This validation plan outlines the steps to validate all functionality of the PharmaCare Management System.

## 1. Database Connectivity Validation

- [ ] Verify database connection using the following steps:
  - Run the application
  - Check if the `DatabaseConnector` successfully connects to the MySQL database
  - Verify the connection parameters (URL, username, password)
  - Use the `testConnection()` method to confirm connectivity

```java
// Test database connection
DatabaseConnector connector = DatabaseConnector.getInstance();
boolean isConnected = connector.testConnection();
System.out.println("Database connection status: " + (isConnected ? "Connected" : "Failed"));
```

## 2. User Authentication Validation

- [ ] Test login functionality with valid credentials:
  - Admin user login (verify redirection to Admin Dashboard)
  - Pharmacist user login (verify redirection to Pharmacist Dashboard)
- [ ] Test login with invalid credentials:
  - Wrong username/password combination
  - Missing role selection
  - Non-existent user
- [ ] Verify error messages are displayed appropriately
- [ ] Validate logout functionality from both dashboards
- [ ] Test password update functionality (if available)

## 3. Admin Dashboard Validation

- [ ] Verify dashboard statistics:
  - Total products count
  - Today's sales
  - Revenue metrics
  - Low stock alerts
- [ ] Test employee management:
  - Add new employee
  - Edit employee details
  - Delete employee
  - Search functionality
- [ ] Validate supplier management:
  - Add supplier
  - Edit supplier
  - Delete supplier
  - Search functionality
- [ ] Test category management:
  - Add category
  - Edit category
  - Delete category
- [ ] Validate user account management:
  - Create user accounts
  - Edit user permissions
  - Deactivate accounts

## 4. Pharmacist Dashboard Validation

- [ ] Verify dashboard statistics display:
  - Total products
  - Today's sales
  - Revenue
  - Low stock alerts
  - Total categories
- [ ] Test product viewing functionality:
  - Search products
  - Filter by low stock
  - View product details
- [ ] Validate medicine selling process:
  - Search medicines
  - Add to cart
  - Update quantities
  - Remove from cart
  - Process sale with customer information
  - Generate invoice
- [ ] Test sales history:
  - View sales by date range
  - Check sales details
  - Verify totals calculation
- [ ] Validate stock management:
  - Update stock quantities
  - View low stock items
  - Search stock

## 5. Product Management Validation

- [ ] Test product data manipulation:
  - Add new product
  - Edit product details
  - Update product stock
  - Delete product
- [ ] Validate search and filtering:
  - Search by name
  - Filter by category
  - Filter by stock status
- [ ] Test low stock alerts:
  - Verify products below minimum stock are flagged
  - Check stock status indicators

## 6. Sales Processing Validation

- [ ] Validate the sales workflow:
  - Adding items to cart
  - Updating quantities
  - Removing items
  - Calculating totals correctly
- [ ] Test customer information entry
- [ ] Verify invoice generation:
  - Correct product details
  - Accurate pricing
  - Tax calculation
  - Discount application
- [ ] Test payment processing
- [ ] Validate receipt printing (if available)

## 7. Reporting Validation

- [ ] Test sales reports:
  - Daily reports
  - Weekly reports
  - Monthly reports
  - Custom date range reports
- [ ] Validate inventory reports:
  - Current stock reports
  - Low stock reports
  - Expired products reports
- [ ] Test revenue reports and profit calculations

## 8. Data Integrity Validation

- [ ] Verify data is correctly stored in database tables:
  - Users
  - Customers
  - Employees
  - Suppliers
  - Medicines
  - Invoices
  - Payments
- [ ] Test data is properly displayed in UI
- [ ] Validate foreign key relationships
- [ ] Test transaction handling

## 9. UI/UX Validation

- [ ] Verify responsive design
- [ ] Test theme application (dark theme)
- [ ] Validate form input validations
- [ ] Check error messaging
- [ ] Test navigation between screens
- [ ] Verify accessibility features

## 10. Performance Validation

- [ ] Test system with large datasets:
  - Many products
  - Extensive sales history
  - Multiple users
- [ ] Verify response times for operations
- [ ] Test concurrent user access (if applicable)

## 11. Security Validation

- [ ] Validate role-based access control:
  - Admin permissions
  - Pharmacist permissions
- [ ] Test password handling
- [ ] Verify audit logging:
  - Login activities
  - Critical operations
  - Data modifications

## Instructions for Running Validation Tests

1. Setup test environment:
   - Install MySQL database
   - Execute database_setup.sql script
   - Configure database connection in DatabaseConnector.java

2. Compile and run the application:
   ```
   mvn clean package
   java -jar target/pharmacy-management-system.jar
   ```

3. Create test accounts:
   - Admin user: username="admin", password="admin123"
   - Pharmacist: username="pharmacist", password="pharm123"

4. Execute each validation test in the checklist
5. Document any issues or failures
6. Fix identified issues and revalidate

## Test Data

Sample product data for testing:

| ID | Name | Description | Price | Stock | Category | Supplier | Min Stock |
|----|------|-------------|-------|-------|----------|----------|-----------|
| 1 | Paracetamol | Pain reliever | 5.99 | 100 | Pain Relief | PharmaCorp | 20 |
| 2 | Amoxicillin | Antibiotic | 12.50 | 75 | Antibiotics | MediSupply | 15 |
| 3 | Vitamin C | Immune booster | 8.75 | 150 | Vitamins | NaturalHealth | 30 |
| 4 | Ibuprofen | Anti-inflammatory | 6.50 | 85 | Pain Relief | PharmaCorp | 25 |
| 5 | Loratadine | Antihistamine | 9.99 | 5 | Allergy | MediSupply | 10 | 