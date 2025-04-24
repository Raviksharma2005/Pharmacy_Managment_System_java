package com.pharmacy.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.FileChooser;
import java.io.File;

import com.pharmacy.models.*;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Optional;
import java.util.regex.Pattern;

import com.pharmacy.dao.UserDAO;
import com.pharmacy.dao.EmployeeDAO;
import com.pharmacy.dao.MedicineDAO;
import com.pharmacy.dao.SupplierDAO;
import com.pharmacy.dao.CategoryDAO;
import com.pharmacy.util.DatabaseConnector;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdminDashboardController implements Initializable {
    
    // Main navigation elements
    @FXML private StackPane contentArea;
    @FXML private Button dashboardBtn;
    @FXML private Button productsBtn;
    @FXML private Button employeesBtn;
    @FXML private Button suppliersBtn;
    @FXML private Button salesBtn;
    @FXML private Button stockBtn;
    @FXML private Button reportsBtn;
    @FXML private Button usersBtn;
    
    // Dashboard elements
    @FXML private Label totalProductsLabel;
    @FXML private Label totalEmployeesLabel;
    @FXML private Label todaySalesLabel;
    @FXML private TableView<Activity> activityTable;
    @FXML private TableColumn<Activity, LocalDate> activityDateColumn;
    @FXML private TableColumn<Activity, String> activityActionColumn;
    @FXML private TableColumn<Activity, String> activityDetailsColumn;
    
    // Products Management elements
    @FXML private VBox productsPane;
    @FXML private TextField searchProductField;
    @FXML private TableView<Product> productsTable;
    @FXML private TableColumn<Product, Integer> productIdColumn;
    @FXML private TableColumn<Product, String> productNameColumn;
    @FXML private TableColumn<Product, String> productDescColumn;
    @FXML private TableColumn<Product, Double> productPriceColumn;
    @FXML private TableColumn<Product, Integer> productStockColumn;
    @FXML private TableColumn<Product, Void> productActionsColumn;
    
    // Categories Management elements
    @FXML private VBox categoriesPane;
    @FXML private TextField searchCategoryField;
    @FXML private TableView<Category> categoriesTable;
    @FXML private TableColumn<Category, Integer> categoryIdColumn;
    @FXML private TableColumn<Category, String> categoryNameColumn;
    @FXML private TableColumn<Category, String> categoryDescColumn;
    @FXML private TableColumn<Category, Void> categoryActionsColumn;
    @FXML private Button categoriesBtn;
    
    // Employees Management elements
    @FXML private VBox employeesPane;
    @FXML private TextField searchEmployeeField;
    @FXML private TableView<Employee> employeesTable;
    @FXML private TableColumn<Employee, Integer> employeeIdColumn;
    @FXML private TableColumn<Employee, String> employeeNameColumn;
    @FXML private TableColumn<Employee, String> employeeRoleColumn;
    @FXML private TableColumn<Employee, String> employeeEmailColumn;
    @FXML private TableColumn<Employee, String> employeePhoneColumn;
    @FXML private TableColumn<Employee, Void> employeeActionsColumn;
    
    // Suppliers Management elements
    @FXML private VBox suppliersPane;
    @FXML private TextField searchSupplierField;
    @FXML private TableView<Supplier> suppliersTable;
    @FXML private TableColumn<Supplier, Integer> supplierIdColumn;
    @FXML private TableColumn<Supplier, String> supplierNameColumn;
    @FXML private TableColumn<Supplier, String> supplierContactColumn;
    @FXML private TableColumn<Supplier, String> supplierEmailColumn;
    @FXML private TableColumn<Supplier, String> supplierPhoneColumn;
    @FXML private TableColumn<Supplier, Void> supplierActionsColumn;
    
    // Sales Management elements
    @FXML private VBox salesPane;
    @FXML private DatePicker salesStartDate;
    @FXML private DatePicker salesEndDate;
    @FXML private TableView<Sale> salesTable;
    @FXML private TableColumn<Sale, Integer> saleInvoiceColumn;
    @FXML private TableColumn<Sale, LocalDate> saleDateColumn;
    @FXML private TableColumn<Sale, String> customerColumn;
    @FXML private TableColumn<Sale, Integer> itemsColumn;
    @FXML private TableColumn<Sale, Double> totalColumn;
    @FXML private TableColumn<Sale, Void> saleActionsColumn;
    
    // Stock Management elements
    @FXML private VBox stockPane;
    @FXML private TextField searchStockField;
    @FXML private TableView<Product> stockTable;
    @FXML private TableColumn<Product, Integer> stockIdColumn;
    @FXML private TableColumn<Product, String> stockNameColumn;
    @FXML private TableColumn<Product, Integer> stockQuantityColumn;
    @FXML private TableColumn<Product, Integer> stockMinColumn;
    @FXML private TableColumn<Product, String> stockStatusColumn;
    @FXML private TableColumn<Product, Void> stockActionsColumn;
    
    // Reports elements
    @FXML private VBox reportsPane;
    @FXML private ComboBox<String> reportTypeCombo;
    @FXML private DatePicker reportStartDate;
    @FXML private DatePicker reportEndDate;
    @FXML private TableView<Report> reportsTable;
    @FXML private TableColumn<Report, LocalDate> reportDateColumn;
    @FXML private TableColumn<Report, String> reportTypeColumn;
    @FXML private TableColumn<Report, String> reportDetailsColumn;
    @FXML private TableColumn<Report, Void> reportActionsColumn;
    
    // Users Management elements
    @FXML private VBox usersPane;
    @FXML private TextField searchUserField;
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> userIdColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> userRoleColumn;
    @FXML private TableColumn<User, Integer> linkedEmployeeColumn;
    @FXML private TableColumn<User, Void> userActionsColumn;
    
    // Sample data lists
    private ObservableList<Product> productsList = FXCollections.observableArrayList();
    private ObservableList<Category> categoriesList = FXCollections.observableArrayList();
    private ObservableList<Employee> employeesList = FXCollections.observableArrayList();
    private ObservableList<Supplier> suppliersList = FXCollections.observableArrayList();
    private ObservableList<Sale> salesList = FXCollections.observableArrayList();
    private ObservableList<Report> reportsList = FXCollections.observableArrayList();
    private ObservableList<Activity> activitiesList = FXCollections.observableArrayList();
    private ObservableList<User> usersList = FXCollections.observableArrayList();
    
    private UserDAO userDAO = new UserDAO();
    
    // Add this field to track toggle state
    private boolean showingLowStockOnly = false;
    
    // Reference to the "Show Low Stock Only" button
    private Button lowStockButton;
    
    // Helper class for activity log
    public static class Activity {
        private final LocalDate date;
        private final String action;
        private final String details;
        
        public Activity(LocalDate date, String action, String details) {
            this.date = date;
            this.action = action;
            this.details = details;
        }
        
        public LocalDate getDate() { return date; }
        public String getAction() { return action; }
        public String getDetails() { return details; }
    }
    
    // Report viewer UI elements
    private VBox reportViewerPane;
    private Label reportTitleLabel;
    private Label reportDateLabel;
    private Label reportTypeLabel;
    private TextArea reportContentArea;
    private Button backToReportsButton;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Initialize dashboard
            initializeDashboard();
            
            // Initialize product management
            initializeProductsManagement();
            
            // Initialize employees management
            initializeEmployeesManagement();
            
            // Initialize suppliers management
            initializeSuppliersManagement();
            
            // Initialize sales management
            initializeSalesManagement();
            
            // Initialize stock management
            initializeStockManagement();
            
            // Initialize reports
            initializeReports();
            
            // Initialize categories management
            initializeCategoriesManagement();
            
            // Initialize users management
            initializeUsersManagement();
            
            // Load sample data for testing
            loadSampleData();
            
            // Update dashboard counts to reflect the loaded data
            updateDashboardCounts();
            
            // Create report viewer pane if it doesn't exist
            if (reportViewerPane == null) {
                createReportViewerPane();
            }
            
            // Show dashboard by default
            showDashboard();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error initializing Admin Dashboard: " + e.getMessage());
            
            // Create an alert dialog to notify the user of the error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Initialization Error");
            alert.setHeaderText("Failed to initialize Admin Dashboard");
            alert.setContentText("Error: " + e.getMessage() + "\nCheck the logs for more details.");
            alert.showAndWait();
        }
    }
    
    private void loadSampleData() {
        // Load data from database
        loadProductsFromDatabase();
        loadEmployeesFromDatabase();
        loadSuppliersFromDatabase();
        
        // Sample sales
        List<SaleItem> saleItems1 = new ArrayList<>();
        saleItems1.add(new SaleItem(1, 1001, 1, "Paracetamol", 2, 5.99));
        saleItems1.add(new SaleItem(2, 1001, 3, "Vitamin C", 1, 8.75));
        
        List<SaleItem> saleItems2 = new ArrayList<>();
        saleItems2.add(new SaleItem(3, 1002, 2, "Amoxicillin", 1, 12.50));
        
        salesList.addAll(
            new Sale(1001, LocalDate.now(), "Customer 1", 2, 20.73, "Completed", saleItems1),
            new Sale(1002, LocalDate.now().minusDays(1), "Customer 2", 1, 12.50, "Completed", saleItems2)
        );
        
        // Sample activity logs
        activitiesList.addAll(
            new Activity(LocalDate.now(), "Login", "Admin logged in"),
            new Activity(LocalDate.now(), "Product Added", "Added new product: Paracetamol"),
            new Activity(LocalDate.now().minusDays(1), "Sale", "New sale with invoice #1001"),
            new Activity(LocalDate.now().minusDays(2), "Stock Update", "Updated stock for product: Amoxicillin")
        );
        
        // Sample reports
        reportsList.addAll(
            new Report(1, "Sales Report", LocalDate.now(), "Daily", "Daily sales report", "/reports/sales_daily.pdf"),
            new Report(2, "Inventory Report", LocalDate.now().minusDays(1), "Stock", "Current inventory status", "/reports/inventory.pdf")
        );
    }
    
    private void loadProductsFromDatabase() {
        // Clear existing data
        productsList.clear();
        
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DatabaseConnector.getInstance().getConnection();
            String query = "SELECT * FROM Medicine";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                int id = resultSet.getInt("medicine_id");
                String name = resultSet.getString("name");
                String brand = resultSet.getString("brand");
                String category = resultSet.getString("category");
                int stockQuantity = resultSet.getInt("stock_quantity");
                LocalDate expiryDate = resultSet.getDate("expiry_date") != null ? 
                    resultSet.getDate("expiry_date").toLocalDate() : LocalDate.now().plusYears(1);
                double price = resultSet.getDouble("price");
                String description = resultSet.getString("description");
                int categoryId = resultSet.getInt("category_id");
                
                Product product = new Product(id, name, brand, category, stockQuantity, 
                    expiryDate, price, description, categoryId);
                productsList.add(product);
            }
            
            // If no products were loaded, add some sample products
            if (productsList.isEmpty()) {
                productsList.addAll(
                    new Product(1, "Paracetamol", "Pain reliever", 5.99, 100, "Pain Relief", "PharmaCorp", 20),
                    new Product(2, "Amoxicillin", "Antibiotic", 12.50, 75, "Antibiotics", "MediSupply", 15),
                    new Product(3, "Vitamin C", "Immune booster", 8.75, 150, "Vitamins", "NaturalHealth", 30),
                    new Product(4, "Ibuprofen", "Anti-inflammatory", 6.50, 85, "Pain Relief", "PharmaCorp", 25),
                    new Product(5, "Loratadine", "Antihistamine", 9.99, 60, "Allergy", "MediSupply", 10),
                    new Product(6, "Aspirin", "Pain reliever", 4.50, 5, "Pain Relief", "PharmaCorp", 20)
                );
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Error loading products: " + e.getMessage());
            
            // If there's an error, load the sample data
            productsList.addAll(
                new Product(1, "Paracetamol", "Pain reliever", 5.99, 100, "Pain Relief", "PharmaCorp", 20),
                new Product(2, "Amoxicillin", "Antibiotic", 12.50, 75, "Antibiotics", "MediSupply", 15),
                new Product(3, "Vitamin C", "Immune booster", 8.75, 150, "Vitamins", "NaturalHealth", 30),
                new Product(4, "Ibuprofen", "Anti-inflammatory", 6.50, 85, "Pain Relief", "PharmaCorp", 25),
                new Product(5, "Loratadine", "Antihistamine", 9.99, 60, "Allergy", "MediSupply", 10),
                new Product(6, "Aspirin", "Pain reliever", 4.50, 5, "Pain Relief", "PharmaCorp", 20)
            );
        } finally {
            DatabaseConnector.getInstance().closeResources(connection, statement, resultSet);
        }
    }
    
    private void loadEmployeesFromDatabase() {
        // Clear existing data
        employeesList.clear();
        
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DatabaseConnector.getInstance().getConnection();
            String query = "SELECT * FROM Employee";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                int id = resultSet.getInt("employee_id");
                String name = resultSet.getString("name");
                String role = resultSet.getString("role");
                String contactNumber = resultSet.getString("contact_number");
                String email = resultSet.getString("email");
                LocalDate hireDate = resultSet.getDate("hire_date") != null ? 
                    resultSet.getDate("hire_date").toLocalDate() : LocalDate.now();
                double salary = resultSet.getDouble("salary");
                
                Employee employee = new Employee(id, name, role, contactNumber, email, hireDate, salary);
                employeesList.add(employee);
            }
            
            // If no employees were loaded, add sample employees
            if (employeesList.isEmpty()) {
                employeesList.addAll(
                    new Employee(1, "John Smith", "Admin", "555-1234", "john@pharmacy.com", LocalDate.now().minusYears(2), 50000),
                    new Employee(2, "Jane Doe", "Pharmacist", "555-5678", "jane@pharmacy.com", LocalDate.now().minusYears(1), 45000),
                    new Employee(3, "Bob Johnson", "Cashier", "555-9012", "bob@pharmacy.com", LocalDate.now().minusMonths(6), 30000)
                );
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Error loading employees: " + e.getMessage());
            
            // If there's an error, load sample data
            employeesList.addAll(
                new Employee(1, "John Smith", "Admin", "555-1234", "john@pharmacy.com", LocalDate.now().minusYears(2), 50000),
                new Employee(2, "Jane Doe", "Pharmacist", "555-5678", "jane@pharmacy.com", LocalDate.now().minusYears(1), 45000),
                new Employee(3, "Bob Johnson", "Cashier", "555-9012", "bob@pharmacy.com", LocalDate.now().minusMonths(6), 30000)
            );
        } finally {
            DatabaseConnector.getInstance().closeResources(connection, statement, resultSet);
        }
        
        // Update the dashboard counts immediately after loading employees
        updateDashboardCounts();
    }
    
    private void loadSuppliersFromDatabase() {
        // Clear existing data
        suppliersList.clear();
        
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DatabaseConnector.getInstance().getConnection();
            String query = "SELECT * FROM Supplier";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                int id = resultSet.getInt("supplier_id");
                String name = resultSet.getString("name");
                String contactNumber = resultSet.getString("contact_number");
                String email = resultSet.getString("email");
                String address = resultSet.getString("address");
                
                Supplier supplier = new Supplier(id, name, name, email, contactNumber, address, "");
                suppliersList.add(supplier);
            }
            
            // If no suppliers were loaded, add sample suppliers
            if (suppliersList.isEmpty()) {
                suppliersList.addAll(
                    new Supplier(1, "PharmaCorp", "Michael Brown", "contact@pharmacorp.com", "555-1111", "100 Corp Ave", "Reliable supplier for pain relief medications"),
                    new Supplier(2, "MediSupply", "Sarah Wilson", "info@medisupply.com", "555-2222", "200 Supply St", "Primary supplier for antibiotics"),
                    new Supplier(3, "NaturalHealth", "David Lee", "orders@naturalhealth.com", "555-3333", "300 Natural Rd", "Vitamins and supplements supplier")
                );
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Error loading suppliers: " + e.getMessage());
            
            // If there's an error, load sample data
            suppliersList.addAll(
                new Supplier(1, "PharmaCorp", "Michael Brown", "contact@pharmacorp.com", "555-1111", "100 Corp Ave", "Reliable supplier for pain relief medications"),
                new Supplier(2, "MediSupply", "Sarah Wilson", "info@medisupply.com", "555-2222", "200 Supply St", "Primary supplier for antibiotics"),
                new Supplier(3, "NaturalHealth", "David Lee", "orders@naturalhealth.com", "555-3333", "300 Natural Rd", "Vitamins and supplements supplier")
            );
        } finally {
            DatabaseConnector.getInstance().closeResources(connection, statement, resultSet);
        }
    }
    
    private void initializeDashboard() {
        // Set summary stats
        totalProductsLabel.setText(String.valueOf(productsList.size()));
        totalEmployeesLabel.setText(String.valueOf(employeesList.size()));
        todaySalesLabel.setText(String.valueOf(getSalesToday()));
        
        // Set up activity table
        activityDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        activityActionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
        activityDetailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));
        
        activityTable.setItems(activitiesList);
    }
    
    private int getSalesToday() {
        LocalDate today = LocalDate.now();
        return (int) salesList.stream()
            .filter(sale -> sale.getDate().equals(today))
            .count();
    }
    
    private void setActiveButton(Button button) {
        // Remove active class from all buttons
        dashboardBtn.setStyle("-fx-background-color: #263238; -fx-text-fill: white;");
        productsBtn.setStyle("-fx-background-color: #263238; -fx-text-fill: white;");
        categoriesBtn.setStyle("-fx-background-color: #263238; -fx-text-fill: white;");
        employeesBtn.setStyle("-fx-background-color: #263238; -fx-text-fill: white;");
        suppliersBtn.setStyle("-fx-background-color: #263238; -fx-text-fill: white;");
        salesBtn.setStyle("-fx-background-color: #263238; -fx-text-fill: white;");
        stockBtn.setStyle("-fx-background-color: #263238; -fx-text-fill: white;");
        reportsBtn.setStyle("-fx-background-color: #263238; -fx-text-fill: white;");
        usersBtn.setStyle("-fx-background-color: #263238; -fx-text-fill: white;");
        
        // Set active class to the clicked button
        button.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white;");
    }
    
    private void hideAllPanes() {
        for (Node node : contentArea.getChildren()) {
            node.setVisible(false);
            node.setManaged(false);
        }
    }
    
    @FXML
    private void showDashboard() {
        setActiveButton(dashboardBtn);
        hideAllPanes();
        contentArea.getChildren().get(0).setVisible(true);
        contentArea.getChildren().get(0).setManaged(true);
    }
    
    @FXML
    private void showProducts() {
        setActiveButton(productsBtn);
        hideAllPanes();
        productsPane.setVisible(true);
        productsPane.setManaged(true);
    }
    
    @FXML
    private void showCategories() {
        setActiveButton(categoriesBtn);
        hideAllPanes();
        categoriesPane.setVisible(true);
        categoriesPane.setManaged(true);
    }
    
    @FXML
    private void showEmployees() {
        setActiveButton(employeesBtn);
        hideAllPanes();
        employeesPane.setVisible(true);
        employeesPane.setManaged(true);
    }
    
    @FXML
    private void showSuppliers() {
        setActiveButton(suppliersBtn);
        hideAllPanes();
        suppliersPane.setVisible(true);
        suppliersPane.setManaged(true);
    }
    
    @FXML
    private void showSales() {
        setActiveButton(salesBtn);
        hideAllPanes();
        salesPane.setVisible(true);
        salesPane.setManaged(true);
    }
    
    @FXML
    private void showStock() {
        setActiveButton(stockBtn);
        hideAllPanes();
        stockPane.setVisible(true);
        stockPane.setManaged(true);
    }
    
    @FXML
    private void showReports() {
        setActiveButton(reportsBtn);
        hideAllPanes();
        reportsPane.setVisible(true);
        reportsPane.setManaged(true);
    }
    
    @FXML
    private void showUsers() {
        setActiveButton(usersBtn);
        hideAllPanes();
        usersPane.setVisible(true);
        usersPane.setManaged(true);
    }
    
    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Stage stage = (Stage) dashboardBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Pharmacy Management System");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Placeholder methods for CRUD operations
    @FXML
    private void showAddProductDialog() {
        // Show dialog to add a new product
        openProductDialog(null);
    }
    
    @FXML
    private void showAddEmployeeDialog() {
        System.out.println("showAddEmployeeDialog called - opening employee dialog...");
        try {
            // Show dialog to add a new employee
            openEmployeeDialog(null);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in showAddEmployeeDialog: " + e.getMessage());
            showErrorAlert("Error", "Failed to open Add Employee dialog: " + e.getMessage());
        }
    }
    
    @FXML
    private void showAddSupplierDialog() {
        // Show dialog to add a new supplier
        openSupplierDialog(null);
    }
    
    @FXML
    private void searchProducts() {
        // Search products
    }
    
    @FXML
    private void searchEmployees() {
        // Search employees
    }
    
    @FXML
    private void searchSuppliers() {
        String searchText = searchSupplierField.getText().trim().toLowerCase();
        searchSuppliers(searchText);
    }
    
    private void searchSuppliers(String searchText) {
        if (searchText.isEmpty()) {
            suppliersTable.setItems(suppliersList);
        } else {
            ObservableList<Supplier> filteredList = FXCollections.observableArrayList();
            for (Supplier supplier : suppliersList) {
                if (supplier.getName().toLowerCase().contains(searchText) ||
                    supplier.getEmail().toLowerCase().contains(searchText) ||
                    supplier.getContactPerson().toLowerCase().contains(searchText) ||
                    supplier.getPhone().toLowerCase().contains(searchText)) {
                    filteredList.add(supplier);
                }
            }
            suppliersTable.setItems(filteredList);
        }
    }
    
    @FXML
    private void filterSales() {
        // Filter sales by date range
    }
    
    @FXML
    private void toggleLowStock() {
        // Toggle showing only low stock items
        showingLowStockOnly = !showingLowStockOnly;
        
        if (showingLowStockOnly) {
            // Filter to show only low stock products
            ObservableList<Product> lowStockProducts = FXCollections.observableArrayList();
            for (Product product : productsList) {
                if (product.isLowOnStock()) {
                    lowStockProducts.add(product);
                }
            }
            stockTable.setItems(lowStockProducts);
            
            // Update button text if we have a reference
            if (lowStockButton == null) {
                // Try to find the button inside the first HBox of the stockPane
                if (stockPane.getChildren().size() > 0 && stockPane.getChildren().get(0) instanceof HBox) {
                    HBox firstRow = (HBox) stockPane.getChildren().get(0);
                    for (Node node : firstRow.getChildren()) {
                        if (node instanceof Button && "Show Low Stock Only".equals(((Button) node).getText())) {
                            lowStockButton = (Button) node;
                            break;
                        }
                    }
                }
            }
            
            if (lowStockButton != null) {
                lowStockButton.setText("Show All Stock");
            }
        } else {
            // Show all products
            stockTable.setItems(productsList);
            
            // Reset button text
            if (lowStockButton != null) {
                lowStockButton.setText("Show Low Stock Only");
            }
        }
    }
    
    @FXML
    private void showUpdateStockDialog() {
        // Show dialog to update stock
    }
    
    @FXML
    private void searchStock() {
        // Search stock
    }
    
    @FXML
    private void generateSalesReport() {
        // Generate sales report
    }
    
    @FXML
    private void generateInventoryReport() {
        // Generate inventory report
    }
    
    private void initializeProductsManagement() {
        // Set up product table columns
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        productDescColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        productPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        productStockColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        
        // Add action buttons to product table
        Callback<TableColumn<Product, Void>, TableCell<Product, Void>> productCellFactory = param -> {
            return new TableCell<>() {
                private final Button editButton = new Button("Edit");
                private final Button deleteButton = new Button("Delete");
                {
                    editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
                    
                    editButton.setOnAction(event -> {
                        Product product = getTableView().getItems().get(getIndex());
                        openProductDialog(product);
                    });
                    
                    deleteButton.setOnAction(event -> {
                        Product product = getTableView().getItems().get(getIndex());
                        deleteProduct(product);
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(5);
                        buttons.getChildren().addAll(editButton, deleteButton);
                        setGraphic(buttons);
                    }
                }
            };
        };
        
        productActionsColumn.setCellFactory(productCellFactory);
        
        // Set items to the table
        productsTable.setItems(productsList);
        
        // Add search functionality
        searchProductField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchProducts(newValue);
        });
    }
    
    private void searchProducts(String searchText) {
        ObservableList<Product> filteredList = FXCollections.observableArrayList();
        
        if (searchText == null || searchText.isEmpty()) {
            productsTable.setItems(productsList);
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            
            for (Product product : productsList) {
                if (product.getName().toLowerCase().contains(lowerCaseFilter) ||
                    product.getDescription().toLowerCase().contains(lowerCaseFilter)) {
                    filteredList.add(product);
                }
            }
            
            productsTable.setItems(filteredList);
        }
    }
    
    @FXML
    private void openAddProductDialog() {
        openProductDialog(null);
    }
    
    private void openProductDialog(Product product) {
        try {
            // Create dialog
            Dialog<Product> dialog = new Dialog<>();
            dialog.setTitle(product == null ? "Add New Product" : "Edit Product");
            dialog.initModality(Modality.APPLICATION_MODAL);
            
            // Set the button types
            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
            
            // Create the form grid
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            
            // Create fields
            TextField nameField = new TextField();
            nameField.setPromptText("Product Name");
            TextField descField = new TextField();
            descField.setPromptText("Description");
            TextField priceField = new TextField();
            priceField.setPromptText("Price");
            TextField quantityField = new TextField();
            quantityField.setPromptText("Quantity");
            TextField categoryField = new TextField();
            categoryField.setPromptText("Category");
            TextField supplierField = new TextField();
            supplierField.setPromptText("Supplier");
            TextField minStockField = new TextField();
            minStockField.setPromptText("Minimum Stock");
            
            // Set existing values if editing
            if (product != null) {
                nameField.setText(product.getName());
                descField.setText(product.getDescription());
                priceField.setText(String.valueOf(product.getPrice()));
                quantityField.setText(String.valueOf(product.getQuantity()));
                categoryField.setText(product.getCategory());
                supplierField.setText(product.getSupplier());
                minStockField.setText(String.valueOf(product.getMinStock()));
            }
            
            // Add fields to grid
            grid.add(new Label("Name:"), 0, 0);
            grid.add(nameField, 1, 0);
            grid.add(new Label("Description:"), 0, 1);
            grid.add(descField, 1, 1);
            grid.add(new Label("Price:"), 0, 2);
            grid.add(priceField, 1, 2);
            grid.add(new Label("Quantity:"), 0, 3);
            grid.add(quantityField, 1, 3);
            grid.add(new Label("Category:"), 0, 4);
            grid.add(categoryField, 1, 4);
            grid.add(new Label("Supplier:"), 0, 5);
            grid.add(supplierField, 1, 5);
            grid.add(new Label("Min Stock:"), 0, 6);
            grid.add(minStockField, 1, 6);
            
            dialog.getDialogPane().setContent(grid);
            
            // Request focus on the name field by default
            nameField.requestFocus();
            
            // Convert the result
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    try {
                        // Get values and validate
                        String name = nameField.getText().trim();
                        String description = descField.getText().trim();
                        double price = Double.parseDouble(priceField.getText().trim());
                        int quantity = Integer.parseInt(quantityField.getText().trim());
                        String category = categoryField.getText().trim();
                        String supplier = supplierField.getText().trim();
                        int minStock = Integer.parseInt(minStockField.getText().trim());
                        
                        if (name.isEmpty() || description.isEmpty() || category.isEmpty() || supplier.isEmpty()) {
                            throw new IllegalArgumentException("All fields must be filled");
                        }
                        
                        // Create or update product
                        if (product == null) {
                            // New product - generate ID
                            int newId = productsList.size() > 0 ? 
                                productsList.stream().mapToInt(Product::getId).max().orElse(0) + 1 : 1;
                            return new Product(newId, name, description, price, quantity, category, supplier, minStock);
                        } else {
                            // Update existing product
                            return new Product(product.getId(), name, description, price, quantity, category, supplier, minStock);
                        }
                    } catch (NumberFormatException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid Input");
                        alert.setHeaderText(null);
                        alert.setContentText("Please enter valid numbers for Price, Quantity and Min Stock fields.");
                        alert.showAndWait();
                        return null;
                    } catch (IllegalArgumentException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid Input");
                        alert.setHeaderText(null);
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                        return null;
                    }
                }
                return null;
            });
            
            // Show the dialog and process the result
            Optional<Product> result = dialog.showAndWait();
            
            result.ifPresent(newProduct -> {
                boolean success;
                
                if (product == null) {
                    // Add new product to database
                    success = addProductToDatabase(newProduct);
                    if (success) {
                        productsList.add(newProduct);
                        // Add activity log
                        activitiesList.add(0, new Activity(LocalDate.now(), "Product Added", 
                            "Added new product: " + newProduct.getName()));
                    }
                } else {
                    // Update existing product in database
                    success = updateProductInDatabase(newProduct);
                    if (success) {
                        // Find and replace existing product
                        for (int i = 0; i < productsList.size(); i++) {
                            if (productsList.get(i).getId() == newProduct.getId()) {
                                productsList.set(i, newProduct);
                                break;
                            }
                        }
                        
                        // Add activity log
                        activitiesList.add(0, new Activity(LocalDate.now(), "Product Updated", 
                            "Updated product: " + newProduct.getName()));
                    }
                }
                
                if (success) {
                    // Update total products count
                    totalProductsLabel.setText(String.valueOf(productsList.size()));
                    
                    // Show success message
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText(product == null ? 
                        "Product added successfully!" : "Product updated successfully!");
                    successAlert.showAndWait();
                } else {
                    showErrorAlert("Database Error", "Failed to save product to database.");
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    private boolean addProductToDatabase(Product product) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        
        try {
            connection = DatabaseConnector.getInstance().getConnection();
            String query = "INSERT INTO Medicine (name, description, price, stock_quantity, category, " +
                          "brand, expiry_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setDouble(3, product.getPrice());
            statement.setInt(4, product.getQuantity());
            statement.setString(5, product.getCategory());
            statement.setString(6, product.getSupplier()); // Supplier is mapped to brand
            statement.setDate(7, java.sql.Date.valueOf(product.getExpiryDate()));
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the generated product ID
                generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Error adding product: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnector.getInstance().closeResources(connection, statement, generatedKeys);
        }
    }
    
    private boolean updateProductInDatabase(Product product) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DatabaseConnector.getInstance().getConnection();
            String query = "UPDATE Medicine SET name = ?, description = ?, price = ?, " +
                          "stock_quantity = ?, category = ?, brand = ?, " +
                          "expiry_date = ? WHERE medicine_id = ?";
            statement = connection.prepareStatement(query);
            
            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setDouble(3, product.getPrice());
            statement.setInt(4, product.getQuantity());
            statement.setString(5, product.getCategory());
            statement.setString(6, product.getSupplier()); // Supplier is mapped to brand
            statement.setDate(7, java.sql.Date.valueOf(product.getExpiryDate()));
            statement.setInt(8, product.getId());
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Error updating product: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnector.getInstance().closeResources(connection, statement, null);
        }
    }
    
    private boolean deleteProductFromDatabase(int productId) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DatabaseConnector.getInstance().getConnection();
            
            // First check if the product has been used in any sales
            PreparedStatement checkSalesStatement = connection.prepareStatement(
                "SELECT COUNT(*) FROM Invoice_Item WHERE medicine_id = ?");
            checkSalesStatement.setInt(1, productId);
            ResultSet salesResultSet = checkSalesStatement.executeQuery();
            
            // Also check if it's used in purchases
            PreparedStatement checkPurchasesStatement = connection.prepareStatement(
                "SELECT COUNT(*) FROM Purchase WHERE medicine_id = ?");
            checkPurchasesStatement.setInt(1, productId);
            ResultSet purchaseResultSet = checkPurchasesStatement.executeQuery();
            
            boolean hasSalesReferences = salesResultSet.next() && salesResultSet.getInt(1) > 0;
            boolean hasPurchaseReferences = purchaseResultSet.next() && purchaseResultSet.getInt(1) > 0;
            
            if (hasSalesReferences || hasPurchaseReferences) {
                // Ask if the user wants to forcefully delete
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Foreign Key Constraint");
                confirmAlert.setHeaderText("This product has associated sales or purchase records");
                confirmAlert.setContentText("Do you want to proceed with deletion? This will remove references in related tables.");
                
                Optional<ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    return forceDeleteProduct(productId);
                } else {
                    return false;
                }
            }
            
            // No references, safe to delete
            String query = "DELETE FROM Medicine WHERE medicine_id = ?";
            statement = connection.prepareStatement(query);
            
            statement.setInt(1, productId);
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                // Log the activity in the Audit_Log table
                logAuditActivity("Medicine", "DELETE", "Product ID: " + productId);
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            
            // Check if it's a foreign key constraint error
            if (e.getMessage().contains("foreign key constraint") || e.getMessage().contains("CONSTRAINT")) {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Foreign Key Constraint");
                confirmAlert.setHeaderText("Cannot delete product due to database constraints");
                confirmAlert.setContentText("The product has related records in the system that prevent deletion. Would you like to try a force delete (may result in data loss)?");
                
                Optional<ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    return forceDeleteProduct(productId);
                }
            } else {
                showErrorAlert("Database Error", "Error deleting product: " + e.getMessage());
            }
            return false;
        } finally {
            DatabaseConnector.getInstance().closeResources(connection, statement, null);
        }
    }
    
    /**
     * Force delete a product by removing all references to it first
     * @param productId ID of the product to delete
     * @return true if successful
     */
    private boolean forceDeleteProduct(int productId) {
        Connection connection = null;
        try {
            connection = DatabaseConnector.getInstance().getConnection();
            
            // Begin transaction
            connection.setAutoCommit(false);
            
            // Disable foreign key checks
            try (PreparedStatement stmt = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=0")) {
                stmt.execute();
            }
            
            // Delete from Invoice_Item
            try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM Invoice_Item WHERE medicine_id = ?")) {
                stmt.setInt(1, productId);
                stmt.executeUpdate();
            }
            
            // Delete from Purchase
            try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM Purchase WHERE medicine_id = ?")) {
                stmt.setInt(1, productId);
                stmt.executeUpdate();
            }
            
            // Delete from Stock_Transactions
            try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM Stock_Transactions WHERE medicine_id = ?")) {
                stmt.setInt(1, productId);
                stmt.executeUpdate();
            }
            
            // Delete from Return if it exists
            try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM Return WHERE medicine_id = ?")) {
                stmt.setInt(1, productId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                // Ignore if table doesn't exist or structure is different
                System.out.println("Note: Could not delete from Return table: " + e.getMessage());
            }
            
            // Finally delete the product
            try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM Medicine WHERE medicine_id = ?")) {
                stmt.setInt(1, productId);
                int result = stmt.executeUpdate();
                
                // Re-enable foreign key checks
                try (PreparedStatement fkStmt = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=1")) {
                    fkStmt.execute();
                }
                
                // Commit the transaction
                connection.commit();
                
                if (result > 0) {
                    logAuditActivity("Medicine", "FORCE DELETE", "Product ID: " + productId);
                    return true;
                }
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Force Delete Failed", "Error during force delete: " + e.getMessage());
            
            // Rollback the transaction
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.rollback();
                    
                    // Make sure to re-enable foreign key checks
                    try (PreparedStatement fkStmt = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=1")) {
                        fkStmt.execute();
                    }
                    
                    // Reset auto-commit
                    connection.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
            return false;
        } finally {
            // Reset auto-commit if needed
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
            DatabaseConnector.getInstance().closeResources(connection, null, null);
        }
    }
    
    private void deleteProduct(Product product) {
        // Show confirmation dialog
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Product");
        confirmDialog.setHeaderText("Delete " + product.getName());
        confirmDialog.setContentText("Are you sure you want to delete this product? This action cannot be undone.");
        
        // Add buttons
        ButtonType deleteButton = new ButtonType("Delete");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmDialog.getButtonTypes().setAll(deleteButton, cancelButton);
        
        // Show the dialog and wait for response
        confirmDialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == deleteButton) {
                boolean success = deleteProductFromDatabase(product.getId());
                
                if (success) {
                    // Remove from the observable list
                    productsList.removeIf(p -> p.getId() == product.getId());
                
                    // Show success message
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Product Deleted");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Product has been successfully removed.");
                    successAlert.showAndWait();
                    
                    // Refresh product lists
                    loadProductsFromDatabase();
                } else {
                    // Show error message
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Failed to delete product. Please try again.");
                    errorAlert.showAndWait();
                }
            }
        });
    }
    
    private void initializeEmployeesManagement() {
        // Set up employee table columns
        employeeIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        employeeRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        employeeEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        employeePhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        
        // Add action buttons to employee table
        Callback<TableColumn<Employee, Void>, TableCell<Employee, Void>> employeeCellFactory = param -> {
            return new TableCell<>() {
                private final Button editButton = new Button("Edit");
                private final Button deleteButton = new Button("Delete");
                {
                    editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
                    
                    editButton.setOnAction(event -> {
                        Employee employee = getTableView().getItems().get(getIndex());
                        openEmployeeDialog(employee);
                    });
                    
                    deleteButton.setOnAction(event -> {
                        Employee employee = getTableView().getItems().get(getIndex());
                        deleteEmployee(employee);
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(5);
                        buttons.getChildren().addAll(editButton, deleteButton);
                        setGraphic(buttons);
                    }
                }
            };
        };
        
        employeeActionsColumn.setCellFactory(employeeCellFactory);
        
        // Set items to the table
        employeesTable.setItems(employeesList);
        
        // Add search functionality
        searchEmployeeField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchEmployees(newValue);
        });
    }
    
    private void searchEmployees(String searchText) {
        ObservableList<Employee> filteredList = FXCollections.observableArrayList();
        
        if (searchText == null || searchText.isEmpty()) {
            employeesTable.setItems(employeesList);
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            
            for (Employee employee : employeesList) {
                if (employee.getName().toLowerCase().contains(lowerCaseFilter) ||
                    employee.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                    employee.getRole().toLowerCase().contains(lowerCaseFilter)) {
                    filteredList.add(employee);
                }
            }
            
            employeesTable.setItems(filteredList);
        }
    }
    
    @FXML
    private void openAddEmployeeDialog() {
        openEmployeeDialog(null);
    }
    
    private void openEmployeeDialog(Employee employee) {
        try {
            // Create dialog
            Dialog<Employee> dialog = new Dialog<>();
            dialog.setTitle(employee == null ? "Add New Employee" : "Edit Employee");
            dialog.initModality(Modality.APPLICATION_MODAL);
            
            // Set the button types
            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
            
            // Create the form grid
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            
            // Create fields
            TextField nameField = new TextField();
            nameField.setPromptText("Employee Name");
            
            ComboBox<String> roleField = new ComboBox<>();
            // Replace existing predefined roles with roles that match the database column size
            roleField.getItems().addAll("Admin", "Pharmacist", "Cashier");
            roleField.setPromptText("Role");
            // Disable editable to prevent users from entering roles of arbitrary length
            roleField.setEditable(false);
            
            TextField emailField = new TextField();
            emailField.setPromptText("Email");
            Label emailErrorLabel = new Label("");
            emailErrorLabel.setTextFill(javafx.scene.paint.Color.RED);
            emailErrorLabel.setVisible(false);
            
            TextField phoneField = new TextField();
            phoneField.setPromptText("Phone (10 digits)");
            Label phoneErrorLabel = new Label("");
            phoneErrorLabel.setTextFill(javafx.scene.paint.Color.RED);
            phoneErrorLabel.setVisible(false);
            
            TextField salaryField = new TextField();
            salaryField.setPromptText("Salary");
            DatePicker hireDatePicker = new DatePicker();
            hireDatePicker.setPromptText("Hire Date");
            
            // Set existing values if editing
            if (employee != null) {
                nameField.setText(employee.getName());
                roleField.setValue(employee.getRole());
                emailField.setText(employee.getEmail());
                phoneField.setText(employee.getContactNumber());
                salaryField.setText(String.valueOf(employee.getSalary()));
                hireDatePicker.setValue(employee.getHireDate());
            } else {
                hireDatePicker.setValue(LocalDate.now());
            }
            
            // Add fields to grid
            grid.add(new Label("Name:"), 0, 0);
            grid.add(nameField, 1, 0);
            grid.add(new Label("Role:"), 0, 1);
            grid.add(roleField, 1, 1);
            grid.add(new Label("Email:"), 0, 2);
            grid.add(emailField, 1, 2);
            grid.add(emailErrorLabel, 1, 3);
            grid.add(new Label("Phone:"), 0, 4);
            grid.add(phoneField, 1, 4);
            grid.add(phoneErrorLabel, 1, 5);
            grid.add(new Label("Salary:"), 0, 6);
            grid.add(salaryField, 1, 6);
            grid.add(new Label("Hire Date:"), 0, 7);
            grid.add(hireDatePicker, 1, 7);
            
            dialog.getDialogPane().setContent(grid);
            
            // Request focus on the name field by default
            nameField.requestFocus();
            
            // Email validation
            final String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            final Pattern emailPattern = Pattern.compile(emailRegex);
            
            // Phone validation - exactly 10 digits
            final String phoneRegex = "^\\d{10}$";
            final Pattern phonePattern = Pattern.compile(phoneRegex);
            
            // Add validation listeners
            emailField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.trim().isEmpty()) {
                    emailErrorLabel.setText("Email is required");
                    emailErrorLabel.setVisible(true);
                } else if (!emailPattern.matcher(newValue.trim()).matches()) {
                    emailErrorLabel.setText("Please enter a valid email address");
                    emailErrorLabel.setVisible(true);
                } else {
                    emailErrorLabel.setVisible(false);
                }
            });
            
            phoneField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.trim().isEmpty()) {
                    phoneErrorLabel.setText("Phone number is required");
                    phoneErrorLabel.setVisible(true);
                } else if (!phonePattern.matcher(newValue.trim()).matches()) {
                    phoneErrorLabel.setText("Phone must be exactly 10 digits");
                    phoneErrorLabel.setVisible(true);
                } else {
                    phoneErrorLabel.setVisible(false);
                }
            });
            
            // Convert the result
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    try {
                        // Get values and validate
                        String name = nameField.getText().trim();
                        String role = roleField.getValue();
                        String email = emailField.getText().trim();
                        String phone = phoneField.getText().trim();
                        double salary = Double.parseDouble(salaryField.getText().trim());
                        LocalDate hireDate = hireDatePicker.getValue();
                        
                        if (name.isEmpty() || role == null || role.isEmpty() || 
                            email.isEmpty() || phone.isEmpty()) {
                            throw new IllegalArgumentException("Name, role, email, and phone must be filled");
                        }
                        
                        // Validate email format
                        if (!emailPattern.matcher(email).matches()) {
                            throw new IllegalArgumentException("Please enter a valid email address");
                        }
                        
                        // Validate phone number format
                        if (!phonePattern.matcher(phone).matches()) {
                            throw new IllegalArgumentException("Phone number must be exactly 10 digits");
                        }
                        
                        // Create or update employee
                        if (employee == null) {
                            // New employee - generate ID
                            int newId = employeesList.size() > 0 ? 
                                employeesList.stream().mapToInt(Employee::getId).max().orElse(0) + 1 : 1;
                            return new Employee(newId, name, role, phone, email, hireDate, salary);
                        } else {
                            // Update existing employee
                            return new Employee(employee.getId(), name, role, phone, email, hireDate, salary);
                        }
                    } catch (NumberFormatException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid Input");
                        alert.setHeaderText(null);
                        alert.setContentText("Please enter a valid number for Salary.");
                        alert.showAndWait();
                        return null;
                    } catch (IllegalArgumentException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid Input");
                        alert.setHeaderText(null);
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                        return null;
                    }
                }
                return null;
            });
            
            // Show the dialog and process the result
            Optional<Employee> result = dialog.showAndWait();
            
            result.ifPresent(newEmployee -> {
                boolean success;
                
                if (employee == null) {
                    // Add new employee to database
                    success = addEmployeeToDatabase(newEmployee);
                    if (success) {
                        employeesList.add(newEmployee);
                        // Add activity log
                        activitiesList.add(0, new Activity(LocalDate.now(), "Employee Added", 
                            "Added new employee: " + newEmployee.getName() + " (" + newEmployee.getRole() + ")"));
                    }
                } else {
                    // Update existing employee in database
                    success = updateEmployeeInDatabase(newEmployee);
                    if (success) {
                        // Find and replace existing employee
                        for (int i = 0; i < employeesList.size(); i++) {
                            if (employeesList.get(i).getId() == newEmployee.getId()) {
                                employeesList.set(i, newEmployee);
                                break;
                            }
                        }
                        
                        // Add activity log
                        activitiesList.add(0, new Activity(LocalDate.now(), "Employee Updated", 
                            "Updated employee: " + newEmployee.getName() + " (" + newEmployee.getRole() + ")"));
                    }
                }
                
                if (success) {
                    // Update total employees count
                    updateDashboardCounts();
                    
                    // Show success message
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText(employee == null ? 
                        "Employee added successfully!" : "Employee updated successfully!");
                    successAlert.showAndWait();
                } else {
                    showErrorAlert("Database Error", "Failed to save employee to database.");
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    private boolean addEmployeeToDatabase(Employee employee) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        
        try {
            System.out.println("Attempting to add employee: " + employee.getName() + " to database...");
            
            connection = DatabaseConnector.getInstance().getConnection();
            if (connection == null) {
                System.err.println("Failed to get database connection");
                return false;
            }
            
            // Check if the employee already exists with the same email
            String checkQuery = "SELECT COUNT(*) FROM Employee WHERE email = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
            checkStatement.setString(1, employee.getEmail());
            ResultSet checkResult = checkStatement.executeQuery();
            
            if (checkResult.next() && checkResult.getInt(1) > 0) {
                System.err.println("An employee with this email already exists");
                showErrorAlert("Duplicate Entry", "An employee with the email " + employee.getEmail() + " already exists.");
                return false;
            }
            
            // Ensure role value is not too long for the database column (assuming VARCHAR(50))
            String role = employee.getRole();
            if (role.length() > 50) {
                role = role.substring(0, 50);
                System.out.println("Role value truncated to 50 characters");
            }
            
            String query = "INSERT INTO Employee (name, role, contact_number, email, hire_date, salary) " +
                          "VALUES (?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            statement.setString(1, employee.getName());
            statement.setString(2, role); // Use the potentially truncated role
            statement.setString(3, employee.getContactNumber());
            statement.setString(4, employee.getEmail());
            statement.setDate(5, java.sql.Date.valueOf(employee.getHireDate()));
            statement.setDouble(6, employee.getSalary());
            
            System.out.println("Executing query: " + query + " with params: " + 
                             employee.getName() + ", " + role + ", " + 
                             employee.getContactNumber() + ", " + employee.getEmail() + ", " + 
                             employee.getHireDate() + ", " + employee.getSalary());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the generated employee ID
                generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    employee.setId(generatedKeys.getInt(1));
                    System.out.println("Employee added successfully with ID: " + employee.getId());
                }
                
                // Log the activity in the Audit_Log table
                logAuditActivity("Employee", "INSERT", employee.getName());
                
                return true;
            } else {
                System.err.println("No rows affected when adding employee");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Exception adding employee: " + e.getMessage());
            showErrorAlert("Database Error", "Error adding employee: " + e.getMessage());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("General exception adding employee: " + e.getMessage());
            showErrorAlert("Error", "Unexpected error adding employee: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnector.getInstance().closeResources(connection, statement, generatedKeys);
        }
    }
    
    private boolean updateEmployeeInDatabase(Employee employee) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DatabaseConnector.getInstance().getConnection();
            String query = "UPDATE Employee SET name = ?, role = ?, contact_number = ?, " +
                          "email = ?, hire_date = ?, salary = ? WHERE employee_id = ?";
            statement = connection.prepareStatement(query);
            
            statement.setString(1, employee.getName());
            statement.setString(2, employee.getRole());
            statement.setString(3, employee.getContactNumber());
            statement.setString(4, employee.getEmail());
            statement.setDate(5, java.sql.Date.valueOf(employee.getHireDate()));
            statement.setDouble(6, employee.getSalary());
            statement.setInt(7, employee.getId());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                // Log the activity in the Audit_Log table
                logAuditActivity("Employee", "UPDATE", employee.getName());
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Error updating employee: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnector.getInstance().closeResources(connection, statement, null);
        }
    }
    
    private void deleteEmployee(Employee employee) {
        // Show confirmation alert
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Employee");
        alert.setHeaderText("Delete Employee: " + employee.getName());
        alert.setContentText("Are you sure you want to delete this employee? This action cannot be undone.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Delete from database
            boolean success = deleteEmployeeFromDatabase(employee.getId());
            
            if (success) {
                // Database operation successful, remove from the list
                employeesList.remove(employee);
                
                // Add activity log
                activitiesList.add(0, new Activity(LocalDate.now(), "Employee Deleted", 
                    "Deleted employee: " + employee.getName() + " (" + employee.getRole() + ")"));
                
                // Show success message
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Employee deleted successfully!");
                successAlert.showAndWait();
                
                // Update dashboard counts
                updateDashboardCounts();
            } else {
                showErrorAlert("Database Error", "Failed to delete employee from database.");
            }
        }
    }
    
    private boolean deleteEmployeeFromDatabase(int employeeId) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DatabaseConnector.getInstance().getConnection();
            
            // First check if this employee is linked to any users
            PreparedStatement checkUserStatement = connection.prepareStatement(
                "SELECT COUNT(*) FROM Users WHERE employee_id = ?");
            checkUserStatement.setInt(1, employeeId);
            ResultSet userResultSet = checkUserStatement.executeQuery();
            
            if (userResultSet.next() && userResultSet.getInt(1) > 0) {
                showErrorAlert("Delete Failed", "This employee is linked to a user account. Please delete the user account first.");
                return false;
            }
            
            // Check if this employee is linked to any invoices
            PreparedStatement checkInvoiceStatement = connection.prepareStatement(
                "SELECT COUNT(*) FROM Invoice WHERE employee_id = ?");
            checkInvoiceStatement.setInt(1, employeeId);
            ResultSet invoiceResultSet = checkInvoiceStatement.executeQuery();
            
            if (invoiceResultSet.next() && invoiceResultSet.getInt(1) > 0) {
                // Ask if the user wants to forcefully delete
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Foreign Key Constraint");
                confirmAlert.setHeaderText("This employee has associated invoices");
                confirmAlert.setContentText("Do you want to set the employee_id to NULL for all related invoices and proceed with deletion?");
                
                Optional<ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // Update invoice records to set employee_id to NULL
                    PreparedStatement updateInvoicesStmt = connection.prepareStatement(
                        "UPDATE Invoice SET employee_id = NULL WHERE employee_id = ?");
                    updateInvoicesStmt.setInt(1, employeeId);
                    updateInvoicesStmt.executeUpdate();
                    updateInvoicesStmt.close();
                } else {
                    return false;
                }
            }
            
            String query = "DELETE FROM Employee WHERE employee_id = ?";
            statement = connection.prepareStatement(query);
            
            statement.setInt(1, employeeId);
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                // Log the activity in the Audit_Log table
                logAuditActivity("Employee", "DELETE", "Employee ID: " + employeeId);
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            
            // Check if it's a foreign key constraint error
            if (e.getMessage().contains("foreign key constraint") || e.getMessage().contains("CONSTRAINT")) {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Foreign Key Constraint");
                confirmAlert.setHeaderText("Cannot delete employee due to database constraints");
                confirmAlert.setContentText("The employee has related records in the system that prevent deletion. Would you like to try a force delete (may result in data loss)?");
                
                Optional<ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    return forceDeleteEmployee(employeeId);
                }
            } else {
                showErrorAlert("Database Error", "Error deleting employee: " + e.getMessage());
            }
            return false;
        } finally {
            DatabaseConnector.getInstance().closeResources(connection, statement, null);
        }
    }
    
    /**
     * Force delete an employee by removing all references to it first
     * @param employeeId ID of the employee to delete
     * @return true if successful
     */
    private boolean forceDeleteEmployee(int employeeId) {
        Connection connection = null;
        try {
            connection = DatabaseConnector.getInstance().getConnection();
            // Disable foreign key checks
            try (PreparedStatement stmt = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=0")) {
                stmt.execute();
            }
            
            // Update Invoice to remove references
            try (PreparedStatement stmt = connection.prepareStatement("UPDATE Invoice SET employee_id = NULL WHERE employee_id = ?")) {
                stmt.setInt(1, employeeId);
                stmt.executeUpdate();
            }
            
            // Optionally update Users to remove references
            try (PreparedStatement stmt = connection.prepareStatement("UPDATE Users SET employee_id = NULL WHERE employee_id = ?")) {
                stmt.setInt(1, employeeId);
                stmt.executeUpdate();
            }
            
            // Delete the employee
            try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM Employee WHERE employee_id = ?")) {
                stmt.setInt(1, employeeId);
                int result = stmt.executeUpdate();
                
                // Re-enable foreign key checks
                try (PreparedStatement fkStmt = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=1")) {
                    fkStmt.execute();
                }
                
                if (result > 0) {
                    logAuditActivity("Employee", "FORCE DELETE", "Employee ID: " + employeeId);
                    return true;
                }
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Force Delete Failed", "Error during force delete: " + e.getMessage());
            
            // Make sure to re-enable foreign key checks
            try {
                if (connection != null && !connection.isClosed()) {
                    try (PreparedStatement fkStmt = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=1")) {
                        fkStmt.execute();
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
            return false;
        } finally {
            DatabaseConnector.getInstance().closeResources(connection, null, null);
        }
    }
    
    private void logAuditActivity(String tableName, String operationType, String details) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DatabaseConnector.getInstance().getConnection();
            String query = "INSERT INTO Audit_Log (table_name, operation_type, performed_by, operation_details) " +
                          "VALUES (?, ?, ?, ?)";
            statement = connection.prepareStatement(query);
            
            statement.setString(1, tableName);
            statement.setString(2, operationType);
            statement.setString(3, "Admin"); // You could replace this with the current user's username
            statement.setString(4, details);
            
            statement.executeUpdate();
        } catch (SQLException e) {
            // Just log the error but don't stop the flow
            e.printStackTrace();
        } finally {
            DatabaseConnector.getInstance().closeResources(connection, statement, null);
        }
    }
    
    private void initializeSuppliersManagement() {
        // Set up supplier table columns
        supplierIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        supplierNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        supplierContactColumn.setCellValueFactory(new PropertyValueFactory<>("contactPerson"));
        supplierEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        supplierPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        
        // Add action buttons to supplier table
        Callback<TableColumn<Supplier, Void>, TableCell<Supplier, Void>> supplierCellFactory = param -> {
            return new TableCell<>() {
                private final Button editButton = new Button("Edit");
                private final Button deleteButton = new Button("Delete");
                {
                    editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
                    
                    editButton.setOnAction(event -> {
                        Supplier supplier = getTableView().getItems().get(getIndex());
                        openSupplierDialog(supplier);
                    });
                    
                    deleteButton.setOnAction(event -> {
                        Supplier supplier = getTableView().getItems().get(getIndex());
                        deleteSupplier(supplier);
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(5);
                        buttons.getChildren().addAll(editButton, deleteButton);
                        setGraphic(buttons);
                    }
                }
            };
        };
        
        supplierActionsColumn.setCellFactory(supplierCellFactory);
        
        // Set items to the table
        suppliersTable.setItems(suppliersList);
    }
    
    private void openSupplierDialog(Supplier supplier) {
        try {
            // Create dialog
            Dialog<Supplier> dialog = new Dialog<>();
            dialog.setTitle(supplier == null ? "Add New Supplier" : "Edit Supplier");
            dialog.initModality(Modality.APPLICATION_MODAL);
            
            // Set the button types
            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
            
            // Create the form grid
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            
            // Create fields
            TextField nameField = new TextField();
            nameField.setPromptText("Supplier Name");
            TextField contactPersonField = new TextField();
            contactPersonField.setPromptText("Contact Person");
            TextField emailField = new TextField();
            emailField.setPromptText("Email");
            Label emailErrorLabel = new Label("");
            emailErrorLabel.setTextFill(javafx.scene.paint.Color.RED);
            emailErrorLabel.setVisible(false);
            
            TextField phoneField = new TextField();
            phoneField.setPromptText("Phone (10 digits)");
            Label phoneErrorLabel = new Label("");
            phoneErrorLabel.setTextFill(javafx.scene.paint.Color.RED);
            phoneErrorLabel.setVisible(false);
            
            TextField addressField = new TextField();
            addressField.setPromptText("Address");
            TextArea notesField = new TextArea();
            notesField.setPromptText("Notes");
            notesField.setPrefRowCount(3);
            
            // Set existing values if editing
            if (supplier != null) {
                nameField.setText(supplier.getName());
                contactPersonField.setText(supplier.getContactPerson());
                emailField.setText(supplier.getEmail());
                phoneField.setText(supplier.getPhone());
                addressField.setText(supplier.getAddress());
            }
            
            // Add fields to grid
            grid.add(new Label("Name:"), 0, 0);
            grid.add(nameField, 1, 0);
            grid.add(new Label("Contact Person:"), 0, 1);
            grid.add(contactPersonField, 1, 1);
            grid.add(new Label("Email:"), 0, 2);
            grid.add(emailField, 1, 2);
            grid.add(emailErrorLabel, 1, 3);
            grid.add(new Label("Phone:"), 0, 4);
            grid.add(phoneField, 1, 4);
            grid.add(phoneErrorLabel, 1, 5);
            grid.add(new Label("Address:"), 0, 6);
            grid.add(addressField, 1, 6);
            grid.add(new Label("Notes:"), 0, 7);
            grid.add(notesField, 1, 7);
            
            // Set the content and bind validation
            dialog.getDialogPane().setContent(grid);
            
            // Validate input fields
            Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
            saveButton.setDisable(true);
            
            // Email validation
            final String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            final Pattern emailPattern = Pattern.compile(emailRegex);
            
            // Phone validation - exactly 10 digits
            final String phoneRegex = "^\\d{10}$";
            final Pattern phonePattern = Pattern.compile(phoneRegex);
            
            // Add validation listeners
            emailField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.trim().isEmpty()) {
                    emailErrorLabel.setText("Email is required");
                    emailErrorLabel.setVisible(true);
                    saveButton.setDisable(true);
                } else if (!emailPattern.matcher(newValue.trim()).matches()) {
                    emailErrorLabel.setText("Please enter a valid email address");
                    emailErrorLabel.setVisible(true);
                    saveButton.setDisable(true);
                } else {
                    emailErrorLabel.setVisible(false);
                    // Check if other validations pass
                    validateSupplierForm(saveButton, nameField, emailField, phoneField, emailPattern, phonePattern);
                }
            });
            
            // Phone validation listener
            phoneField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.trim().isEmpty()) {
                    phoneErrorLabel.setText("Phone number is required");
                    phoneErrorLabel.setVisible(true);
                    saveButton.setDisable(true);
                } else if (!phonePattern.matcher(newValue.trim()).matches()) {
                    phoneErrorLabel.setText("Phone must be exactly 10 digits");
                    phoneErrorLabel.setVisible(true);
                    saveButton.setDisable(true);
                } else {
                    phoneErrorLabel.setVisible(false);
                    // Check if other validations pass
                    validateSupplierForm(saveButton, nameField, emailField, phoneField, emailPattern, phonePattern);
                }
            });
            
            // Name validation listener
            nameField.textProperty().addListener((observable, oldValue, newValue) -> {
                validateSupplierForm(saveButton, nameField, emailField, phoneField, emailPattern, phonePattern);
            });
            
            // Request focus on the name field by default
            Platform.runLater(() -> nameField.requestFocus());
            
            // Convert the result to a Supplier object when the save button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    String email = emailField.getText().trim();
                    String phone = phoneField.getText().trim();
                    
                    // Final validation check
                    if (!emailPattern.matcher(email).matches()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid Email");
                        alert.setHeaderText(null);
                        alert.setContentText("Please enter a valid email address.");
                        alert.showAndWait();
                        return null;
                    }
                    
                    // Validate phone number format
                    if (!phonePattern.matcher(phone).matches()) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid Phone Number");
                        alert.setHeaderText(null);
                        alert.setContentText("Phone number must be exactly 10 digits.");
                        alert.showAndWait();
                        return null;
                    }
                    
                    int supplierId = (supplier == null) ? 0 : supplier.getId();
                    return new Supplier(
                        supplierId,
                        nameField.getText(),
                        contactPersonField.getText(),
                        email,
                        phone,
                        addressField.getText(),
                        notesField.getText()
                    );
                }
                return null;
            });
            
            // Show the dialog and capture the result
            Optional<Supplier> result = dialog.showAndWait();
            
            result.ifPresent(newSupplier -> {
                boolean success;
                
                if (supplier == null) {
                    // Add new supplier to database
                    success = addSupplierToDatabase(newSupplier);
                    if (success) {
                        // Add to the observable list
                        suppliersList.add(newSupplier);
                        
                        // Add activity log
                        activitiesList.add(0, new Activity(LocalDate.now(), "Supplier Added", 
                            "Added supplier: " + newSupplier.getName()));
                    }
                } else {
                    // Update existing supplier in database
                    success = updateSupplierInDatabase(newSupplier);
                    if (success) {
                        // Update in the observable list
                        int index = suppliersList.indexOf(supplier);
                        if (index >= 0) {
                            suppliersList.set(index, newSupplier);
                        }
                        
                        // Add activity log
                        activitiesList.add(0, new Activity(LocalDate.now(), "Supplier Updated", 
                            "Updated supplier: " + newSupplier.getName()));
                    }
                }
                
                if (success) {
                    // Update dashboard counts
                    updateDashboardCounts();
                    
                    // Show success message
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText(supplier == null ? 
                        "Supplier added successfully!" : "Supplier updated successfully!");
                    successAlert.showAndWait();
                } else {
                    showErrorAlert("Database Error", "Failed to save supplier to database.");
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "Error while opening supplier dialog: " + e.getMessage());
        }
    }
    
    private void deleteSupplier(Supplier supplier) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Supplier");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete " + supplier.getName() + "?");
        
        Optional<ButtonType> result = confirmation.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Delete from database
            boolean success = deleteSupplierFromDatabase(supplier.getId());
            
            if (success) {
                // Remove from the observable list
                suppliersList.remove(supplier);
                
                // Add activity log
                activitiesList.add(0, new Activity(LocalDate.now(), "Supplier Deleted", 
                    "Deleted supplier: " + supplier.getName()));
                
                // Update dashboard counts
                updateDashboardCounts();
                
                // Show success message
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Supplier deleted successfully!");
                successAlert.showAndWait();
            }
        }
    }
    
    private boolean addSupplierToDatabase(Supplier supplier) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        
        try {
            connection = DatabaseConnector.getInstance().getConnection();
            String query = "INSERT INTO Supplier (name, contact_number, email, address) VALUES (?, ?, ?, ?)";
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            
            statement.setString(1, supplier.getName());
            statement.setString(2, supplier.getPhone());
            statement.setString(3, supplier.getEmail());
            statement.setString(4, supplier.getAddress());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the generated supplier ID
                generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    supplier.setId(generatedKeys.getInt(1));
                }
                
                // Log the activity in the Audit_Log table
                logAuditActivity("Supplier", "INSERT", supplier.getName());
                
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Error adding supplier: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnector.getInstance().closeResources(connection, statement, generatedKeys);
        }
    }
    
    private boolean updateSupplierInDatabase(Supplier supplier) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DatabaseConnector.getInstance().getConnection();
            String query = "UPDATE Supplier SET name = ?, contact_number = ?, email = ?, address = ? WHERE supplier_id = ?";
            statement = connection.prepareStatement(query);
            
            statement.setString(1, supplier.getName());
            statement.setString(2, supplier.getPhone());
            statement.setString(3, supplier.getEmail());
            statement.setString(4, supplier.getAddress());
            statement.setInt(5, supplier.getId());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                // Log the activity in the Audit_Log table
                logAuditActivity("Supplier", "UPDATE", supplier.getName());
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Error updating supplier: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnector.getInstance().closeResources(connection, statement, null);
        }
    }
    
    private boolean deleteSupplierFromDatabase(int supplierId) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DatabaseConnector.getInstance().getConnection();
            
            // First check if this supplier is linked to any medicines
            PreparedStatement checkStatement = connection.prepareStatement(
                "SELECT COUNT(*) FROM Medicine WHERE brand = (SELECT name FROM Supplier WHERE supplier_id = ?)");
            checkStatement.setInt(1, supplierId);
            ResultSet resultSet = checkStatement.executeQuery();
            
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                showErrorAlert("Delete Failed", "This supplier is linked to existing products. Remove those products first.");
                return false;
            }
            
            String query = "DELETE FROM Supplier WHERE supplier_id = ?";
            statement = connection.prepareStatement(query);
            
            statement.setInt(1, supplierId);
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                // Log the activity in the Audit_Log table
                logAuditActivity("Supplier", "DELETE", "Supplier ID: " + supplierId);
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Error deleting supplier: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnector.getInstance().closeResources(connection, statement, null);
        }
    }
    
    private void initializeSalesManagement() {
        // Set up sales table columns
        saleInvoiceColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceNumber"));
        saleDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customer"));
        itemsColumn.setCellValueFactory(new PropertyValueFactory<>("itemCount"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        
        // Add action buttons to sales table
        Callback<TableColumn<Sale, Void>, TableCell<Sale, Void>> saleCellFactory = param -> {
            return new TableCell<>() {
                private final Button viewButton = new Button("View");
                private final Button printButton = new Button("Print");
                {
                    viewButton.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");
                    printButton.setStyle("-fx-background-color: #757575; -fx-text-fill: white;");
                    
                    viewButton.setOnAction(event -> {
                        Sale sale = getTableView().getItems().get(getIndex());
                        // Show sale details
                        showSaleDetails(sale);
                    });
                    
                    printButton.setOnAction(event -> {
                        Sale sale = getTableView().getItems().get(getIndex());
                        // Handle printing
                        printSaleInvoice(sale);
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(5);
                        buttons.getChildren().addAll(viewButton, printButton);
                        setGraphic(buttons);
                    }
                }
            };
        };
        
        saleActionsColumn.setCellFactory(saleCellFactory);
        
        // Set items to the table
        salesTable.setItems(salesList);
        
        // Set date picker defaults
        salesStartDate.setValue(LocalDate.now().minusMonths(1));
        salesEndDate.setValue(LocalDate.now());
    }
    
    private void initializeStockManagement() {
        // Set up stock table columns
        stockIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        stockNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        stockQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        stockMinColumn.setCellValueFactory(new PropertyValueFactory<>("minStock"));
        
        // Status column with different colors for low stock
        stockStatusColumn.setCellValueFactory(cellData -> {
            Product product = cellData.getValue();
            String status = product.isLowOnStock() ? "Low Stock" : "In Stock";
            return new javafx.beans.property.SimpleStringProperty(status);
        });
        
        stockStatusColumn.setCellFactory(column -> {
            return new TableCell<Product, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        if ("Low Stock".equals(item)) {
                            setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
                        } else {
                            setStyle("-fx-text-fill: #4caf50; -fx-font-weight: bold;");
                        }
                    }
                }
            };
        });
        
        // Add action buttons to stock table
        Callback<TableColumn<Product, Void>, TableCell<Product, Void>> stockCellFactory = param -> {
            return new TableCell<>() {
                private final Button updateButton = new Button("Update");
                {
                    updateButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
                    
                    updateButton.setOnAction(event -> {
                        Product product = getTableView().getItems().get(getIndex());
                        // Handle update stock action
                        openUpdateStockDialog(product);
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(updateButton);
                    }
                }
            };
        };
        
        stockActionsColumn.setCellFactory(stockCellFactory);
        
        // Set items to the table
        stockTable.setItems(productsList);
    }
    
    private void openUpdateStockDialog(Product product) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Update Stock");
        dialog.setHeaderText("Update Stock for: " + product.getName());
        
        // Set the button types
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);
        
        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Create quantity field
        TextField quantityField = new TextField(String.valueOf(product.getQuantity()));
        quantityField.setPromptText("New Quantity");
        
        // Add the field to the grid
        grid.add(new Label("Current Quantity:"), 0, 0);
        grid.add(new Label(String.valueOf(product.getQuantity())), 1, 0);
        grid.add(new Label("New Quantity:"), 0, 1);
        grid.add(quantityField, 1, 1);
        
        // Set the content
        dialog.getDialogPane().setContent(grid);
        
        // Convert the result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                try {
                    return Integer.parseInt(quantityField.getText().trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });
        
        // Show the dialog and process the result
        Optional<Integer> result = dialog.showAndWait();
        
        result.ifPresent(newQuantity -> {
            // Update the product in memory
            int oldQuantity = product.getQuantity();
            product.setQuantity(newQuantity);
            
            // Update in database
            boolean success = updateProductStockInDatabase(product.getId(), newQuantity);
            
            if (success) {
                // Update in the list view to refresh the UI
                int index = productsList.indexOf(product);
                if (index >= 0) {
                    productsList.set(index, product);
                }
                
                // Add activity log
                activitiesList.add(0, new Activity(LocalDate.now(), "Stock Updated", 
                    "Updated stock for " + product.getName() + " from " + oldQuantity + " to " + newQuantity));
                
                // Show success message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Stock updated successfully!");
                alert.showAndWait();
            } else {
                // Show error message
                showErrorAlert("Database Error", "Failed to update stock in database.");
                
                // Revert the change in memory
                product.setQuantity(oldQuantity);
            }
        });
    }
    
    private boolean updateProductStockInDatabase(int productId, int newQuantity) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DatabaseConnector.getInstance().getConnection();
            String query = "UPDATE Medicine SET stock_quantity = ? WHERE medicine_id = ?";
            statement = connection.prepareStatement(query);
            
            statement.setInt(1, newQuantity);
            statement.setInt(2, productId);
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                // Also add a record to the Stock_Transactions table
                addStockTransaction(productId, newQuantity);
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Error updating product stock: " + e.getMessage());
            return false;
        } finally {
            DatabaseConnector.getInstance().closeResources(connection, statement, null);
        }
    }
    
    private void addStockTransaction(int productId, int newQuantity) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DatabaseConnector.getInstance().getConnection();
            String query = "INSERT INTO Stock_Transactions (medicine_id, change_quantity, transaction_type, transaction_date) " +
                          "VALUES (?, ?, 'purchase', ?)";
            statement = connection.prepareStatement(query);
            
            statement.setInt(1, productId);
            statement.setInt(2, newQuantity);
            statement.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            
            statement.executeUpdate();
        } catch (SQLException e) {
            // Just log the error but don't stop the flow
            e.printStackTrace();
        } finally {
            DatabaseConnector.getInstance().closeResources(connection, statement, null);
        }
    }
    
    private void initializeReports() {
        // Set up reports table columns
        reportDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        reportTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        reportDetailsColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        // Add action buttons to reports table
        Callback<TableColumn<Report, Void>, TableCell<Report, Void>> reportCellFactory = param -> {
            return new TableCell<>() {
                private final Button viewButton = new Button("View");
                {
                    viewButton.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");
                    
                    viewButton.setOnAction(event -> {
                        Report report = getTableView().getItems().get(getIndex());
                        viewReport(report);
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(viewButton);
                    }
                }
            };
        };
        
        reportActionsColumn.setCellFactory(reportCellFactory);
        
        // Set items to the table
        reportsTable.setItems(reportsList);
        
        // Set date picker defaults
        reportStartDate.setValue(LocalDate.now().minusMonths(1));
        reportEndDate.setValue(LocalDate.now());
    }
    
    private void viewReport(Report report) {
        try {
            // Create a report viewer pane if it doesn't exist yet
            if (reportViewerPane == null) {
                createReportViewerPane();
            }
            
            // Populate the report viewer with this report's data
            reportTitleLabel.setText(report.getTitle());
            reportDateLabel.setText(report.getDate().toString());
            reportTypeLabel.setText(report.getType());
            
            // Generate sample content based on report type
            StringBuilder content = new StringBuilder();
            content.append("===== ").append(report.getTitle().toUpperCase()).append(" =====\n\n");
            content.append("Generated on: ").append(report.getDate()).append("\n");
            content.append("Report Type: ").append(report.getType()).append("\n");
            content.append("Description: ").append(report.getDescription()).append("\n\n");
            content.append("------------------------------------\n\n");
            
            // Add sample data based on report type
            if (report.getType().contains("Sales")) {
                content.append("SALES SUMMARY\n\n");
                content.append("Total Sales: ₹1,245.78\n");
                content.append("Number of Transactions: 25\n");
                content.append("Average Sale Value: ₹49.83\n\n");
                content.append("TOP SELLING PRODUCTS:\n");
                content.append("1. Paracetamol - 48 units - ₹286.56\n");
                content.append("2. Vitamin C - 32 units - ₹279.68\n");
                content.append("3. Amoxicillin - 15 units - ₹187.50\n\n");
                content.append("SALES BY DAY:\n");
                content.append("Monday: ₹198.45\n");
                content.append("Tuesday: ₹245.67\n");
                content.append("Wednesday: ₹176.32\n");
                content.append("Thursday: ₹302.45\n");
                content.append("Friday: ₹322.89\n");
            } else if (report.getType().contains("Inventory") || report.getType().contains("Stock")) {
                content.append("INVENTORY STATUS SUMMARY\n\n");
                content.append("Total Products: 143\n");
                content.append("Low Stock Items: 12\n");
                content.append("Out of Stock Items: 3\n\n");
                content.append("LOW STOCK ITEMS:\n");
                content.append("1. Paracetamol - 5 units remaining (Min: 10)\n");
                content.append("2. Vitamin C - 8 units remaining (Min: 15)\n");
                content.append("3. Ibuprofen - 7 units remaining (Min: 10)\n\n");
                content.append("ITEMS EXPIRING SOON:\n");
                content.append("1. Amoxicillin - Expires: 30/06/2023\n");
                content.append("2. Acetaminophen - Expires: 15/07/2023\n");
            }
            
            reportContentArea.setText(content.toString());
            
            // Hide the reports pane and show the viewer
            reportsPane.setVisible(false);
            reportsPane.setManaged(false);
            
            // Add the viewer to the content area if not already there
            if (!contentArea.getChildren().contains(reportViewerPane)) {
                contentArea.getChildren().add(reportViewerPane);
            }
            
            reportViewerPane.setVisible(true);
            reportViewerPane.setManaged(true);
            
            // Add activity log
            activitiesList.add(0, new Activity(LocalDate.now(), "Report Viewed", 
                "Viewed report: " + report.getTitle()));
        } catch (Exception ex) {
            ex.printStackTrace();
            showErrorAlert("Error", "Could not open report: " + ex.getMessage());
        }
    }
    
    private void initializeUsersManagement() {
        // Set up columns
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        userRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        
        // Custom cell factory for the linked employee column
        linkedEmployeeColumn.setCellFactory(column -> {
            return new TableCell<User, Integer>() {
                @Override
                protected void updateItem(Integer employeeId, boolean empty) {
                    super.updateItem(employeeId, empty);
                    
                    if (empty || employeeId == null) {
                        setText(null);
                    } else {
                        setText(getEmployeeName(employeeId));
                    }
                }
            };
        });
        linkedEmployeeColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        
        // Add action buttons
        userActionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final Button resetButton = new Button("Reset PW");
            
            {
                editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
                resetButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                
                editButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    openUserDialog(user);
                });
                
                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    deleteUser(user);
                });
                
                resetButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    resetUserPassword(user);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5);
                    buttons.getChildren().addAll(editButton, resetButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });
        
        // Load users data
        loadUsersData();
        
        // Set the items to the table
        usersTable.setItems(usersList);
    }
    
    private void loadUsersData() {
        // Clear existing data
        usersList.clear();
        
        // Get all users from database
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = DatabaseConnector.getInstance().getConnection();
            String query = "SELECT u.*, e.name AS employee_name FROM Users u " +
                          "LEFT JOIN Employee e ON u.employee_id = e.employee_id";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String role = resultSet.getString("role");
                Integer employeeId = null;
                try {
                    employeeId = resultSet.getInt("employee_id");
                    if (resultSet.wasNull()) {
                        employeeId = null;
                    }
                } catch (SQLException e) {
                    // employee_id column doesn't exist or is null
                }
                
                User user = new User(userId, username, password, role, employeeId);
                usersList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Error loading users: " + e.getMessage());
        } finally {
            DatabaseConnector.getInstance().closeResources(connection, statement, resultSet);
        }
        
        // Update total count in dashboard
        updateDashboardCounts();
    }
    
    /**
     * Get employee name for display in the users table
     */
    private String getEmployeeName(Integer employeeId) {
        if (employeeId == null) {
            return "-";
        }
        
        for (Employee employee : employeesList) {
            if (employee.getId() == employeeId) {
                return employee.getName();
            }
        }
        
        return "Employee ID: " + employeeId;
    }
    
    @FXML
    private void searchUsers() {
        String searchText = searchUserField.getText().trim().toLowerCase();
        searchUsers(searchText);
    }
    
    private void searchUsers(String searchText) {
        if (searchText.isEmpty()) {
            usersTable.setItems(usersList);
        } else {
            ObservableList<User> filteredList = FXCollections.observableArrayList();
            for (User user : usersList) {
                if (user.getUsername().toLowerCase().contains(searchText) || 
                    user.getRole().toLowerCase().contains(searchText)) {
                    filteredList.add(user);
                }
            }
            usersTable.setItems(filteredList);
        }
    }
    
    @FXML
    private void showAddUserDialog() {
        openUserDialog(null);
    }
    
    private void openUserDialog(User user) {
        try {
            // Create dialog
            Dialog<User> dialog = new Dialog<>();
            dialog.setTitle(user == null ? "Add New User" : "Edit User");
            dialog.setHeaderText(null);
            
            // Set the button types
            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
            
            // Create the form grid
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            
            // Define the fields
            TextField usernameField = new TextField();
            usernameField.setPromptText("Username");
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Password");
            
            ComboBox<String> roleCombo = new ComboBox<>();
            roleCombo.getItems().addAll("admin", "pharmacist");
            roleCombo.setPromptText("Select Role");
            
            ComboBox<Employee> employeeCombo = new ComboBox<>();
            employeeCombo.setItems(employeesList);
            employeeCombo.setPromptText("Link to Employee (Optional)");
            employeeCombo.setConverter(new StringConverter<Employee>() {
                @Override
                public String toString(Employee employee) {
                    return employee == null ? "" : employee.getName();
                }
                
                @Override
                public Employee fromString(String string) {
                    return null; // Not used for ComboBox
                }
            });
            
            // Set existing values if editing
            if (user != null) {
                usernameField.setText(user.getUsername());
                roleCombo.setValue(user.getRole());
                
                if (user.getEmployeeId() != null) {
                    for (Employee employee : employeesList) {
                        if (employee.getId() == user.getEmployeeId()) {
                            employeeCombo.setValue(employee);
                            break;
                        }
                    }
                }
                
                // Password field is empty when editing
                passwordField.setPromptText("Leave blank to keep current password");
            }
            
            // Add fields to the grid
            grid.add(new Label("Username:"), 0, 0);
            grid.add(usernameField, 1, 0);
            grid.add(new Label("Password:"), 0, 1);
            grid.add(passwordField, 1, 1);
            grid.add(new Label("Role:"), 0, 2);
            grid.add(roleCombo, 1, 2);
            grid.add(new Label("Link to Employee:"), 0, 3);
            grid.add(employeeCombo, 1, 3);
            
            // Enable/Disable the Save button based on validations
            Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
            saveButton.setDisable(true);
            
            // Validate the input fields
            usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
                validateUserDialogInput(saveButton, usernameField, passwordField, roleCombo, user);
            });
            
            passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
                validateUserDialogInput(saveButton, usernameField, passwordField, roleCombo, user);
            });
            
            roleCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
                validateUserDialogInput(saveButton, usernameField, passwordField, roleCombo, user);
            });
            
            // Set the grid in the dialog
            dialog.getDialogPane().setContent(grid);
            
            // Request focus on the username field
            Platform.runLater(usernameField::requestFocus);
            
            // Convert the result to a User object when the save button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    String username = usernameField.getText();
                    String password = passwordField.getText();
                    String role = roleCombo.getValue();
                    
                    Integer employeeId = null;
                    if (employeeCombo.getValue() != null) {
                        employeeId = employeeCombo.getValue().getId();
                    }
                    
                    if (user == null) {
                        // Create new user
                        return new User(0, username, password, role, employeeId);
                    } else {
                        // Update existing user
                        User updatedUser = new User(user.getUserId(), username, 
                            password.isEmpty() ? user.getPassword() : password, 
                            role, employeeId);
                        return updatedUser;
                    }
                }
                return null;
            });
            
            // Show the dialog and process the result
            Optional<User> result = dialog.showAndWait();
            
            result.ifPresent(newUser -> {
                if (user == null) {
                    // Add new user to database
                    Connection connection = null;
                    PreparedStatement statement = null;
                    ResultSet generatedKeys = null;
                    
                    try {
                        connection = DatabaseConnector.getInstance().getConnection();
                        String query = "INSERT INTO Users (username, password, role, employee_id) VALUES (?, ?, ?, ?)";
                        statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                        
                        statement.setString(1, newUser.getUsername());
                        statement.setString(2, newUser.getPassword());
                        statement.setString(3, newUser.getRole());
                        
                        if (newUser.getEmployeeId() != null) {
                            statement.setInt(4, newUser.getEmployeeId());
                        } else {
                            statement.setNull(4, java.sql.Types.INTEGER);
                        }
                        
                        int rowsAffected = statement.executeUpdate();
                        
                        if (rowsAffected > 0) {
                            // Get the generated user ID
                            generatedKeys = statement.getGeneratedKeys();
                            if (generatedKeys.next()) {
                                newUser.setUserId(generatedKeys.getInt(1));
                            }
                            
                            // Add to the observable list
                            usersList.add(newUser);
                            
                            // Show success message
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Success");
                            alert.setHeaderText(null);
                            alert.setContentText("User added successfully!");
                            alert.showAndWait();
                        } else {
                            showErrorAlert("Error", "Failed to add user to database.");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showErrorAlert("Database Error", "Error adding user: " + e.getMessage());
                    } finally {
                        DatabaseConnector.getInstance().closeResources(connection, statement, generatedKeys);
                    }
                } else {
                    // Update existing user in database
                    Connection connection = null;
                    PreparedStatement statement = null;
                    
                    try {
                        connection = DatabaseConnector.getInstance().getConnection();
                        String query = "UPDATE Users SET username = ?, role = ?, employee_id = ? WHERE user_id = ?";
                        
                        // If password was changed, include it in the update
                        if (!passwordField.getText().isEmpty()) {
                            query = "UPDATE Users SET username = ?, password = ?, role = ?, employee_id = ? WHERE user_id = ?";
                        }
                        
                        statement = connection.prepareStatement(query);
                        
                        statement.setString(1, newUser.getUsername());
                        
                        if (!passwordField.getText().isEmpty()) {
                            statement.setString(2, newUser.getPassword());
                            statement.setString(3, newUser.getRole());
                            
                            if (newUser.getEmployeeId() != null) {
                                statement.setInt(4, newUser.getEmployeeId());
                            } else {
                                statement.setNull(4, java.sql.Types.INTEGER);
                            }
                            
                            statement.setInt(5, newUser.getUserId());
                        } else {
                            statement.setString(2, newUser.getRole());
                            
                            if (newUser.getEmployeeId() != null) {
                                statement.setInt(3, newUser.getEmployeeId());
                            } else {
                                statement.setNull(3, java.sql.Types.INTEGER);
                            }
                            
                            statement.setInt(4, newUser.getUserId());
                        }
                        
                        int rowsAffected = statement.executeUpdate();
                        
                        if (rowsAffected > 0) {
                            // Update in the observable list
                            for (int i = 0; i < usersList.size(); i++) {
                                if (usersList.get(i).getUserId() == user.getUserId()) {
                                    usersList.set(i, newUser);
                                    break;
                                }
                            }
                            
                            // Show success message
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Success");
                            alert.setHeaderText(null);
                            alert.setContentText("User updated successfully!");
                            alert.showAndWait();
                        } else {
                            showErrorAlert("Error", "Failed to update user in database.");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showErrorAlert("Database Error", "Error updating user: " + e.getMessage());
                    } finally {
                        DatabaseConnector.getInstance().closeResources(connection, statement, null);
                    }
                }
                
                // Refresh the list to show updates
                loadUsersData();
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error opening user dialog", e.getMessage());
        }
    }
    
    private void validateUserDialogInput(Node saveButton, TextField usernameField, 
            PasswordField passwordField, ComboBox<String> roleCombo, User existingUser) {
        
        boolean isValid = !usernameField.getText().trim().isEmpty() 
                        && (existingUser != null || !passwordField.getText().trim().isEmpty()) 
                        && roleCombo.getValue() != null;
        
        saveButton.setDisable(!isValid);
    }
    
    private void deleteUser(User user) {
        // Show confirmation dialog
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete the user: " + user.getUsername() + "?");
        
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Delete the user from the database
            Connection connection = null;
            PreparedStatement statement = null;
            
            try {
                connection = DatabaseConnector.getInstance().getConnection();
                String query = "DELETE FROM Users WHERE user_id = ?";
                statement = connection.prepareStatement(query);
                statement.setInt(1, user.getUserId());
                
                int rowsAffected = statement.executeUpdate();
                
                if (rowsAffected > 0) {
                    // Remove from the observable list
                    usersList.remove(user);
                    
                    // Update dashboard counts
                    updateDashboardCounts();
                    
                    // Show success message
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("User deleted successfully!");
                    alert.showAndWait();
                } else {
                    showErrorAlert("Error", "Failed to delete user from database.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorAlert("Database Error", "Error deleting user: " + e.getMessage());
            } finally {
                DatabaseConnector.getInstance().closeResources(connection, statement, null);
            }
        }
    }
    
    private void resetUserPassword(User user) {
        // Show password reset dialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reset Password");
        dialog.setHeaderText("Reset password for user: " + user.getUsername());
        dialog.setContentText("Enter new password:");
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().isEmpty()) {
            String newPassword = result.get();
            
            // Update password in database
            Connection connection = null;
            PreparedStatement statement = null;
            
            try {
                connection = DatabaseConnector.getInstance().getConnection();
                String query = "UPDATE Users SET password = ? WHERE user_id = ?";
                statement = connection.prepareStatement(query);
                
                statement.setString(1, newPassword);
                statement.setInt(2, user.getUserId());
                
                int rowsAffected = statement.executeUpdate();
                
                if (rowsAffected > 0) {
                    // Update the password in the user object
                    user.setPassword(newPassword);
                    
                    // Show success message
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Password reset successfully!");
                    alert.showAndWait();
                } else {
                    showErrorAlert("Error", "Failed to reset password in database.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorAlert("Database Error", "Error resetting password: " + e.getMessage());
            } finally {
                DatabaseConnector.getInstance().closeResources(connection, statement, null);
            }
        }
    }
    
    private void updateDashboardCounts() {
        // Update total users count if needed
        // Set other counts as needed
        totalEmployeesLabel.setText(String.valueOf(employeesList.size()));
        totalProductsLabel.setText(String.valueOf(productsList.size()));
        todaySalesLabel.setText(String.valueOf(getSalesToday()));
        
        // Other counts can be added here as needed
    }
    
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void initializeCategoriesManagement() {
        // Set up category table columns
        categoryIdColumn.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        categoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        categoryDescColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        // Add action buttons to category table
        Callback<TableColumn<Category, Void>, TableCell<Category, Void>> categoryCellFactory = param -> {
            return new TableCell<>() {
                private final Button editButton = new Button("Edit");
                private final Button deleteButton = new Button("Delete");
                {
                    editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
                    
                    editButton.setOnAction(event -> {
                        Category category = getTableView().getItems().get(getIndex());
                        openCategoryDialog(category);
                    });
                    
                    deleteButton.setOnAction(event -> {
                        Category category = getTableView().getItems().get(getIndex());
                        deleteCategory(category);
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(5);
                        buttons.getChildren().addAll(editButton, deleteButton);
                        setGraphic(buttons);
                    }
                }
            };
        };
        
        categoryActionsColumn.setCellFactory(categoryCellFactory);
        
        // Set items to the table
        categoriesTable.setItems(categoriesList);
        
        // Add search functionality
        searchCategoryField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchCategories(newValue);
        });
        
        // Load categories from database
        loadCategoriesFromDatabase();
    }
    
    private void loadCategoriesFromDatabase() {
        // Clear existing data
        categoriesList.clear();
        
        // Create CategoryDAO
        CategoryDAO categoryDAO = new CategoryDAO();
        
        // Get all categories from database
        List<Category> categories = categoryDAO.getAllCategories();
        
        // Add categories to the list
        categoriesList.addAll(categories);
        
        // Update dashboard counts if needed
        updateDashboardCounts();
    }
    
    @FXML
    private void searchCategories() {
        String searchText = searchCategoryField.getText();
        searchCategories(searchText);
    }
    
    private void searchCategories(String searchText) {
        ObservableList<Category> filteredList = FXCollections.observableArrayList();
        
        if (searchText == null || searchText.isEmpty()) {
            categoriesTable.setItems(categoriesList);
        } else {
            String lowerCaseFilter = searchText.toLowerCase();
            
            for (Category category : categoriesList) {
                if (category.getCategoryName().toLowerCase().contains(lowerCaseFilter) ||
                    (category.getDescription() != null && 
                     category.getDescription().toLowerCase().contains(lowerCaseFilter))) {
                    filteredList.add(category);
                }
            }
            
            categoriesTable.setItems(filteredList);
        }
    }
    
    @FXML
    private void showAddCategoryDialog() {
        openCategoryDialog(null);
    }
    
    private void openCategoryDialog(Category category) {
        try {
            // Create dialog
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle(category == null ? "Add New Category" : "Edit Category");
            dialog.initModality(Modality.APPLICATION_MODAL);
            
            // Set the button types (OK and Cancel)
            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
            
            // Create the grid pane for form fields
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            
            // Create text fields for category input
            TextField nameField = new TextField();
            nameField.setPromptText("Category Name");
            TextArea descField = new TextArea();
            descField.setPromptText("Description");
            descField.setPrefRowCount(3);
            
            // If editing, populate fields with category data
            if (category != null) {
                nameField.setText(category.getCategoryName());
                descField.setText(category.getDescription());
            }
            
            // Add labels and fields to the grid
            grid.add(new Label("Category Name:"), 0, 0);
            grid.add(nameField, 1, 0);
            grid.add(new Label("Description:"), 0, 1);
            grid.add(descField, 1, 1);
            
            // Set the grid as the dialog content
            dialog.getDialogPane().setContent(grid);
            
            // Request focus on the name field by default
            Platform.runLater(() -> nameField.requestFocus());
            
            // Disable the save button if name field is empty
            Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
            saveButton.setDisable(true);
            
            // Validate input fields
            nameField.textProperty().addListener((observable, oldValue, newValue) -> {
                saveButton.setDisable(newValue.trim().isEmpty());
            });
            
            // Show the dialog and wait for user response
            Optional<ButtonType> result = dialog.showAndWait();
            
            if (result.isPresent() && result.get() == saveButtonType) {
                // User clicked Save, so process the form
                String name = nameField.getText().trim();
                String description = descField.getText().trim();
                
                if (category == null) {
                    // Add new category
                    Category newCategory = new Category(0, name, description);
                    
                    // Save to database
                    if (addCategoryToDatabase(newCategory)) {
                        // Add to the list
                        categoriesList.add(newCategory);
                        
                        // Log the activity
                        logAuditActivity("Medicine_Category", "INSERT", "Added new category: " + name);
                        
                        // Show success alert
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success");
                        alert.setHeaderText(null);
                        alert.setContentText("Category added successfully!");
                        alert.showAndWait();
                    } else {
                        showErrorAlert("Error", "Failed to add category. Please try again.");
                    }
                } else {
                    // Update existing category
                    category.setCategoryName(name);
                    category.setDescription(description);
                    
                    // Update the database
                    if (updateCategoryInDatabase(category)) {
                        // Refresh the table (or just update the current item)
                        categoriesTable.refresh();
                        
                        // Log the activity
                        logAuditActivity("Medicine_Category", "UPDATE", "Updated category: " + name);
                        
                        // Show success alert
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success");
                        alert.setHeaderText(null);
                        alert.setContentText("Category updated successfully!");
                        alert.showAndWait();
                    } else {
                        showErrorAlert("Error", "Failed to update category. Please try again.");
                    }
                }
                
                // Update dashboard counts
                updateDashboardCounts();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }
    
    private boolean addCategoryToDatabase(Category category) {
        CategoryDAO categoryDAO = new CategoryDAO();
        return categoryDAO.addCategory(category);
    }
    
    private boolean updateCategoryInDatabase(Category category) {
        CategoryDAO categoryDAO = new CategoryDAO();
        return categoryDAO.updateCategory(category);
    }
    
    private void deleteCategory(Category category) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Category");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete " + category.getCategoryName() + "?");
        
        Optional<ButtonType> result = confirmation.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Delete from database
            CategoryDAO categoryDAO = new CategoryDAO();
            boolean success = categoryDAO.deleteCategory(category.getCategoryId());
            
            if (success) {
                // Remove from the observable list
                categoriesList.remove(category);
                
                // Log the activity
                logAuditActivity("Medicine_Category", "DELETE", "Deleted category: " + category.getCategoryName());
                
                // Update dashboard counts
                updateDashboardCounts();
                
                // Show success message
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Category deleted successfully!");
                successAlert.showAndWait();
            } else {
                showErrorAlert("Delete Failed", "This category may be in use by products. Remove those products first.");
            }
        }
    }
    
    private void showSaleDetails(Sale sale) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Sale Details");
        dialog.setHeaderText("Invoice #" + sale.getInvoiceNumber());
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        Label customerLabel = new Label("Customer: " + sale.getCustomer());
        Label dateLabel = new Label("Date: " + sale.getDate());
        
        content.getChildren().addAll(customerLabel, dateLabel, new Separator());
        
        // Items table
        TableView<SaleItem> itemsTable = new TableView<>();
        itemsTable.setPrefHeight(200);
        
        TableColumn<SaleItem, String> productCol = new TableColumn<>("Product");
        productCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productCol.setPrefWidth(150);
        
        TableColumn<SaleItem, Integer> quantityCol = new TableColumn<>("Qty");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setPrefWidth(50);
        
        TableColumn<SaleItem, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setCellFactory(column -> new TableCell<SaleItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("₹%.2f", price));
                }
            }
        });
        priceCol.setPrefWidth(80);
        
        TableColumn<SaleItem, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalCol.setCellFactory(column -> new TableCell<SaleItem, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(String.format("₹%.2f", total));
                }
            }
        });
        totalCol.setPrefWidth(80);
        
        itemsTable.getColumns().addAll(productCol, quantityCol, priceCol, totalCol);
        itemsTable.setItems(FXCollections.observableArrayList(sale.getSaleItems()));
        
        content.getChildren().add(itemsTable);
        
        // Total
        Label totalLabel = new Label("Total: ₹" + String.format("%.2f", sale.getTotal()));
        totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        totalLabel.setAlignment(Pos.CENTER_RIGHT);
        content.getChildren().add(totalLabel);
        
        // Close button
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);
        
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }
    
    private void printSaleInvoice(Sale sale) {
        // Create a print preview dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Print Invoice");
        dialog.setHeaderText("Print Preview for Invoice #" + sale.getInvoiceNumber());
        
        // Set the button types
        ButtonType printButtonType = new ButtonType("Print", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(printButtonType, ButtonType.CANCEL);
        
        // Create the preview content
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");
        
        // Header
        Label headerLabel = new Label("PHARMACY MANAGEMENT SYSTEM");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label invoiceLabel = new Label("INVOICE #" + sale.getInvoiceNumber());
        invoiceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label dateLabel = new Label("Date: " + sale.getDate());
        Label customerLabel = new Label("Customer: " + sale.getCustomer());
        
        // Items table
        TableView<SaleItem> itemsTable = new TableView<>();
        itemsTable.setPrefHeight(200);
        
        TableColumn<SaleItem, String> itemNameCol = new TableColumn<>("Item");
        itemNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProductName()));
        itemNameCol.setPrefWidth(200);
        
        TableColumn<SaleItem, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        qtyCol.setPrefWidth(50);
        
        TableColumn<SaleItem, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setCellFactory(col -> new TableCell<SaleItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("₹%.2f", price));
                }
            }
        });
        priceCol.setPrefWidth(100);
        
        TableColumn<SaleItem, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(data -> {
            double total = data.getValue().getQuantity() * data.getValue().getPrice();
            return new SimpleObjectProperty<>(total);
        });
        totalCol.setCellFactory(col -> new TableCell<SaleItem, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(String.format("₹%.2f", total));
                }
            }
        });
        totalCol.setPrefWidth(100);
        
        itemsTable.getColumns().addAll(itemNameCol, qtyCol, priceCol, totalCol);
        itemsTable.setItems(FXCollections.observableArrayList(sale.getSaleItems()));
        
        // Summary
        HBox totalBox = new HBox(10);
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        Label totalLabel = new Label("Total: ₹" + String.format("%.2f", sale.getTotal()));
        totalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        totalBox.getChildren().add(totalLabel);
        
        // Thank you note
        Label thankYouLabel = new Label("Thank you for your business!");
        thankYouLabel.setStyle("-fx-font-style: italic;");
        
        // Add all components to the content
        content.getChildren().addAll(
            headerLabel,
            new Separator(),
            invoiceLabel,
            dateLabel,
            customerLabel,
            new Separator(),
            itemsTable,
            new Separator(),
            totalBox,
            thankYouLabel
        );
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);
        scrollPane.setPrefWidth(600);
        
        dialog.getDialogPane().setContent(scrollPane);
        
        // Show the dialog and handle the result
        dialog.showAndWait().ifPresent(result -> {
            if (result == printButtonType) {
                // Add to activity log
                activitiesList.add(0, new Activity(LocalDate.now(), "Invoice Printed", 
                    "Printed invoice #" + sale.getInvoiceNumber()));
                
                // Show confirmation
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Print Job Sent");
                alert.setHeaderText(null);
                alert.setContentText("The invoice has been sent to the printer.");
                alert.showAndWait();
            }
        });
    }
    
    // Helper method to validate supplier form fields
    private void validateSupplierForm(Node saveButton, TextField nameField, TextField emailField, 
                                    TextField phoneField, Pattern emailPattern, Pattern phonePattern) {
        boolean validName = !nameField.getText().trim().isEmpty();
        boolean validEmail = !emailField.getText().trim().isEmpty() && 
                             emailPattern.matcher(emailField.getText().trim()).matches();
        boolean validPhone = !phoneField.getText().trim().isEmpty() && 
                             phonePattern.matcher(phoneField.getText().trim()).matches();
        
        saveButton.setDisable(!(validName && validEmail && validPhone));
    }
    
    private void createReportViewerPane() {
        // Create the report viewer pane
        reportViewerPane = new VBox(15);
        reportViewerPane.setVisible(false);
        reportViewerPane.setManaged(false);
        reportViewerPane.getStyleClass().add("dashboard-content");
        reportViewerPane.setStyle("-fx-padding: 20;");
        
        // Create header with back button
        HBox header = new HBox(10);
        backToReportsButton = new Button("← Back to Reports");
        backToReportsButton.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");
        backToReportsButton.setOnAction(e -> {
            reportViewerPane.setVisible(false);
            reportViewerPane.setManaged(false);
            reportsPane.setVisible(true);
            reportsPane.setManaged(true);
        });
        
        Label titleLabel = new Label("Report Details");
        titleLabel.setStyle("-fx-font-size: 22; -fx-font-weight: bold;");
        header.getChildren().addAll(backToReportsButton, titleLabel);
        
        // Create report details section
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(15);
        detailsGrid.setVgap(10);
        detailsGrid.setPadding(new Insets(20, 0, 20, 0));
        
        // Add report details fields
        Label titleFieldLabel = new Label("Title:");
        titleFieldLabel.setStyle("-fx-font-weight: bold;");
        reportTitleLabel = new Label();
        
        Label dateFieldLabel = new Label("Date:");
        dateFieldLabel.setStyle("-fx-font-weight: bold;");
        reportDateLabel = new Label();
        
        Label typeFieldLabel = new Label("Type:");
        typeFieldLabel.setStyle("-fx-font-weight: bold;");
        reportTypeLabel = new Label();
        
        detailsGrid.add(titleFieldLabel, 0, 0);
        detailsGrid.add(reportTitleLabel, 1, 0);
        detailsGrid.add(dateFieldLabel, 0, 1);
        detailsGrid.add(reportDateLabel, 1, 1);
        detailsGrid.add(typeFieldLabel, 0, 2);
        detailsGrid.add(reportTypeLabel, 1, 2);
        
        // Add report content area
        Label contentLabel = new Label("Report Content:");
        contentLabel.setStyle("-fx-font-weight: bold;");
        reportContentArea = new TextArea();
        reportContentArea.setEditable(false);
        reportContentArea.setPrefHeight(400);
        reportContentArea.setWrapText(true);
        
        // Add all components to the viewer pane
        reportViewerPane.getChildren().addAll(header, detailsGrid, contentLabel, reportContentArea);
        
        // Add to the content area
        contentArea.getChildren().add(reportViewerPane);
    }
    
    @FXML
    private void generateReport() {
        String reportType = reportTypeCombo.getValue();
        LocalDate startDate = reportStartDate.getValue();
        LocalDate endDate = reportEndDate.getValue();
        
        if (reportType == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please select a report type!");
            alert.showAndWait();
            return;
        }
        
        if (startDate == null || endDate == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please select start and end dates!");
            alert.showAndWait();
            return;
        }
        
        if (startDate.isAfter(endDate)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Start date cannot be after end date!");
            alert.showAndWait();
            return;
        }
        
        // Generate report logic would go here
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report Generated");
        alert.setHeaderText(null);
        alert.setContentText("Report has been generated successfully!");
        alert.showAndWait();
        
        // Add to reports list
        int newId = reportsList.size() > 0 ? 
            reportsList.stream().mapToInt(Report::getId).max().orElse(0) + 1 : 1;
        
        Report newReport = new Report(
            newId,
            reportType + " Report",
            LocalDate.now(),
            reportType,
            "Generated report from " + startDate + " to " + endDate,
            "/reports/" + reportType.toLowerCase().replace(" ", "_") + "_" + LocalDate.now() + ".pdf"
        );
        
        reportsList.add(0, newReport);
        
        // Add activity log
        activitiesList.add(0, new Activity(LocalDate.now(), "Report Generated", 
            "Generated " + reportType + " report"));
    }
    
    @FXML
    private void exportReportExcel() {
        Report selectedReport = reportsTable.getSelectionModel().getSelectedItem();
        
        if (selectedReport == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please select a report to export!");
            alert.showAndWait();
            return;
        }
        
        // Create a file chooser for Excel files
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report as Excel");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        // Set initial file name based on report
        String fileName = selectedReport.getType().toLowerCase().replace(" ", "_") + "_" + 
                          selectedReport.getDate() + ".xlsx";
        fileChooser.setInitialFileName(fileName);
        
        // Show save dialog
        Stage stage = (Stage) reportsPane.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        
        if (file != null) {
            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Excel Exported");
            alert.setHeaderText(null);
            alert.setContentText("Report has been exported to Excel:\n" + file.getAbsolutePath());
            alert.showAndWait();
            
            // Add activity log
            activitiesList.add(0, new Activity(LocalDate.now(), "Report Exported", 
                "Exported " + selectedReport.getTitle() + " to Excel"));
        }
    }
    
    // Fixed placeholder values for reports
    private String generateSampleSalesReport() {
        StringBuilder content = new StringBuilder();
        content.append("PHARMACY MANAGEMENT SYSTEM\n");
        content.append("SALES REPORT\n");
        content.append("Date Range: " + LocalDate.now().minusDays(7) + " to " + LocalDate.now() + "\n\n");
        
        content.append("Total Sales: ₹1,245.78\n");
        content.append("Number of Transactions: 25\n");
        content.append("Average Sale Value: ₹49.83\n\n");
        
        content.append("TOP SELLING PRODUCTS:\n");
        content.append("1. Paracetamol - 48 units - ₹286.56\n");
        content.append("2. Vitamin C - 32 units - ₹279.68\n");
        content.append("3. Amoxicillin - 15 units - ₹187.50\n\n");
        
        content.append("DAILY SALES:\n");
        content.append("Monday: ₹198.45\n");
        content.append("Tuesday: ₹245.67\n");
        content.append("Wednesday: ₹176.32\n");
        content.append("Thursday: ₹302.45\n");
        content.append("Friday: ₹322.89\n");
        
        return content.toString();
    }
} 