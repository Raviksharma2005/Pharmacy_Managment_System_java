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
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import com.pharmacy.models.*;
import com.pharmacy.dao.*;
import com.pharmacy.util.DatabaseConnector;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PharmacistDashboardController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(PharmacistDashboardController.class.getName());
    
    @FXML private StackPane contentArea;
    @FXML private VBox dashboardPane;
    @FXML private VBox productsPane;
    @FXML private VBox sellMedicinesPane;
    @FXML private VBox salesHistoryPane;
    @FXML private VBox manageStockPane;
    
    @FXML private Button dashboardBtn;
    @FXML private Button viewProductsBtn;
    @FXML private Button sellMedicinesBtn;
    @FXML private Button salesHistoryBtn;
    @FXML private Button manageStockBtn;
    
    // Dashboard components
    @FXML private Label totalProductsLabel;
    @FXML private Label todaySalesLabel;
    @FXML private Label todayRevenueLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label totalCategoriesLabel;
    @FXML private TableView<Sale> recentSalesTable;
    @FXML private TableColumn<Sale, Integer> invoiceColumn;
    @FXML private TableColumn<Sale, LocalDate> dateColumn;
    @FXML private TableColumn<Sale, String> customerColumn;
    @FXML private TableColumn<Sale, Double> amountColumn;
    @FXML private TableColumn<Sale, String> statusColumn;
    
    // Products View components
    @FXML private TextField searchProduct;
    @FXML private ToggleButton showLowStockBtn;
    @FXML private TableView<Product> productsTable;
    @FXML private TableColumn<Product, Integer> productIdColumn;
    @FXML private TableColumn<Product, String> productNameColumn;
    @FXML private TableColumn<Product, String> productDescColumn;
    @FXML private TableColumn<Product, Double> productPriceColumn;
    @FXML private TableColumn<Product, Integer> productStockColumn;
    @FXML private TableColumn<Product, String> productCategoryColumn;
    @FXML private TableColumn<Product, String> productStatusColumn;
    @FXML private TableColumn<Product, Void> productActionColumn;
    
    // Sell Medicines components
    @FXML private TextField searchMedicine;
    @FXML private TableView<Product> medicinesTable;
    @FXML private TableColumn<Product, Integer> medicineIdColumn;
    @FXML private TableColumn<Product, String> medicineNameColumn;
    @FXML private TableColumn<Product, Double> medicinePriceColumn;
    @FXML private TableColumn<Product, Integer> medicineStockColumn;
    @FXML private TableColumn<Product, String> medicineCategoryColumn;
    @FXML private TableColumn<Product, Void> medicineActionColumn;
    
    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> cartProductColumn;
    @FXML private TableColumn<CartItem, Integer> cartQuantityColumn;
    @FXML private TableColumn<CartItem, Double> cartPriceColumn;
    @FXML private TableColumn<CartItem, Double> cartTotalColumn;
    @FXML private TableColumn<CartItem, Void> cartRemoveColumn;
    
    @FXML private TextField customerNameField;
    @FXML private Label totalAmountLabel;
    @FXML private TextField discountPercentField;
    @FXML private TextField taxPercentField;
    @FXML private Label discountAmountLabel;
    @FXML private Label taxAmountLabel;
    @FXML private Label finalAmountLabel;
    
    // Sales History components
    @FXML private DatePicker salesStartDate;
    @FXML private DatePicker salesEndDate;
    @FXML private TableView<Sale> salesHistoryTable;
    @FXML private TableColumn<Sale, Integer> salesInvoiceColumn;
    @FXML private TableColumn<Sale, LocalDate> salesDateColumn;
    @FXML private TableColumn<Sale, String> salesCustomerColumn;
    @FXML private TableColumn<Sale, Integer> salesItemsColumn;
    @FXML private TableColumn<Sale, Double> salesTotalColumn;
    @FXML private TableColumn<Sale, Void> salesDetailsColumn;
    @FXML private Label totalSalesAmountLabel;
    
    // Stock Management components
    @FXML private TextField searchStock;
    @FXML private ToggleButton showLowStockBtn2;
    @FXML private TableView<Product> stockTable;
    @FXML private TableColumn<Product, Integer> stockIdColumn;
    @FXML private TableColumn<Product, String> stockNameColumn;
    @FXML private TableColumn<Product, Integer> stockQuantityColumn;
    @FXML private TableColumn<Product, Integer> stockMinColumn;
    @FXML private TableColumn<Product, String> stockStatusColumn;
    @FXML private TableColumn<Product, Void> stockUpdateColumn;
    
    // Database DAOs
    private MedicineDAO medicineDAO;
    private SaleDAO saleDAO;
    
    // Observable lists for UI binding
    private ObservableList<Product> productsList = FXCollections.observableArrayList();
    private ObservableList<Sale> salesList = FXCollections.observableArrayList();
    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Initialize DAOs
            medicineDAO = new MedicineDAO();
            saleDAO = new SaleDAO();
            
            // Set dashboard as active by default
            setActiveButton(dashboardBtn);
            
            // Ensure database is properly set up
            ensureDatabaseSetup();
            
            // Ensure default customer exists
            ensureDefaultCustomer();
            
            // Load data from database
            loadDataFromDatabase();
            
            // Initialize tables and UI components
            initializeDashboard();
            initializeProductsView();
            initializeSellMedicinesView();
            initializeSalesHistoryView();
            initializeStockManagementView();
            
            // Show the dashboard initially
            showPane(dashboardPane);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing dashboard", e);
            showAlert("Initialization Error", "Failed to initialize dashboard: " + e.getMessage());
        }
    }
    
    /**
     * Ensures that all required database tables exist
     */
    private void ensureDatabaseSetup() {
        Connection connection = null;
        Statement statement = null;
        
        try {
            LOGGER.info("Checking and ensuring required database tables exist");
            connection = DatabaseConnector.getInstance().getConnection();
            statement = connection.createStatement();
            
            // Ensure the Customer table exists
            statement.execute(
                "CREATE TABLE IF NOT EXISTS Customer (" +
                "customer_id INT PRIMARY KEY AUTO_INCREMENT, " +
                "name VARCHAR(100) NOT NULL, " +
                "email VARCHAR(100) UNIQUE NOT NULL, " +
                "phone VARCHAR(15) NOT NULL, " +
                "address TEXT)");
            
            // Ensure the Invoice table exists
            statement.execute(
                "CREATE TABLE IF NOT EXISTS Invoice (" +
                "invoice_id INT PRIMARY KEY AUTO_INCREMENT, " +
                "employee_id INT, " +
                "customer_id INT, " +
                "invoice_date DATE, " +
                "total_amount DECIMAL(10,2), " +
                "discount_amount DECIMAL(10,2), " +
                "tax_amount DECIMAL(10,2), " +
                "final_amount DECIMAL(10,2))");
            
            // Ensure the Invoice_Item table exists
            statement.execute(
                "CREATE TABLE IF NOT EXISTS Invoice_Item (" +
                "invoice_item_id INT PRIMARY KEY AUTO_INCREMENT, " +
                "invoice_id INT, " +
                "medicine_id INT, " +
                "quantity INT, " +
                "price_per_unit DECIMAL(10,2), " +
                "total_price DECIMAL(10,2))");
            
            // Ensure the Stock_Transactions table exists
            statement.execute(
                "CREATE TABLE IF NOT EXISTS Stock_Transactions (" +
                "transaction_id INT PRIMARY KEY AUTO_INCREMENT, " +
                "medicine_id INT, " +
                "change_quantity INT, " +
                "transaction_type VARCHAR(10), " +
                "transaction_date DATE, " +
                "reference_id INT)");
            
            LOGGER.info("Database tables verified and created if needed");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error ensuring database tables exist", e);
            showAlert("Database Error", "Error setting up database tables: " + e.getMessage());
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing database resources", e);
            }
        }
    }
    
    /**
     * Ensures that a default customer exists in the database
     * This is critical for sales functionality
     */
    private void ensureDefaultCustomer() {
        try {
            CustomerDAO customerDAO = new CustomerDAO();
            Customer defaultCustomer = customerDAO.getCustomerByName("Walk-in Customer");
            
            if (defaultCustomer == null) {
                LOGGER.info("Creating default customer for the system");
                Customer customer = new Customer();
                customer.setName("Walk-in Customer");
                customer.setEmail("customer@pharmacy.com");
                customer.setPhone("0000000000");
                customer.setAddress("Default customer for walk-in sales");
                
                boolean added = customerDAO.addCustomer(customer);
                if (added) {
                    LOGGER.info("Default customer created successfully");
                } else {
                    LOGGER.severe("Failed to create default customer");
                    showAlert("Database Warning", "Could not create default customer. Some functionality may be limited.");
                }
            } else {
                LOGGER.info("Default customer already exists with ID: " + defaultCustomer.getId());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error ensuring default customer exists", e);
            showAlert("Database Error", "Error initializing customer data: " + e.getMessage());
        }
    }
    
    private void loadDataFromDatabase() {
        try {
            // Load products
            List<Product> medicines = medicineDAO.getAllMedicines();
            productsList.setAll(medicines);
            
            // Load sales
            List<Sale> sales = saleDAO.getAllSales();
            salesList.setAll(sales);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading data from database", e);
            showAlert("Database Error", "Failed to load data from database: " + e.getMessage());
        }
    }
    
    private void initializeDashboard() {
        // Set dashboard summary stats
        totalProductsLabel.setText(String.valueOf(productsList.size()));
        todaySalesLabel.setText(String.valueOf(countTodaySales()));
        todayRevenueLabel.setText(String.format("₹%.2f", calculateTodayRevenue()));
        lowStockLabel.setText(String.valueOf(countLowStockItems()));
        
        // Count unique categories
        long uniqueCategories = productsList.stream()
                .map(Product::getCategory)
                .distinct()
                .count();
        totalCategoriesLabel.setText(String.valueOf(uniqueCategories));
        
        // Initialize recent sales table
        invoiceColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceNumber"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customer"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Show only recent sales (limit to 10)
        ObservableList<Sale> recentSales = FXCollections.observableArrayList(
            salesList.stream()
                .sorted((s1, s2) -> s2.getDate().compareTo(s1.getDate()))
                .limit(10)
                .collect(Collectors.toList())
        );
        recentSalesTable.setItems(recentSales);
    }
    
    private void initializeProductsView() {
        // Initialize products table
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        productDescColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        productPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        productStockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        productCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        
        // Custom cell factory for status column
        productStatusColumn.setCellFactory(column -> {
            return new TableCell<Product, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        Product product = getTableView().getItems().get(getIndex());
                        if (product.isLowOnStock()) {
                            setText("LOW STOCK");
                            setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
                        } else {
                            setText("IN STOCK");
                            setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                        }
                    }
                }
            };
        });
        
        // Custom cell factory for action column
        productActionColumn.setCellFactory(column -> {
            return new TableCell<Product, Void>() {
                private final Button sellButton = new Button("Sell");
                
                {
                    sellButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    sellButton.setOnAction(event -> {
                        Product product = getTableView().getItems().get(getIndex());
                        showSellMedicines();
                        searchMedicine(product.getName());
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(sellButton);
                    }
                }
            };
        });
        
        productsTable.setItems(productsList);
    }
    
    private void initializeSellMedicinesView() {
        // Initialize medicines table
        medicineIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        medicineNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        medicinePriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        medicineStockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        medicineCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        
        // Add to cart button
        medicineActionColumn.setCellFactory(column -> {
            return new TableCell<Product, Void>() {
                private final Button addButton = new Button("Add to Cart");
                
                {
                    addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    addButton.setOnAction(event -> {
                        Product product = getTableView().getItems().get(getIndex());
                        addToCart(product);
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Product product = getTableView().getItems().get(getIndex());
                        addButton.setDisable(product.getStock() <= 0);
                        setGraphic(addButton);
                    }
                }
            };
        });
        
        medicinesTable.setItems(productsList);
        
        // Initialize cart table
        cartProductColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        cartQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        cartPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        cartTotalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        
        // Quantity adjustment and remove button
        cartRemoveColumn.setCellFactory(column -> {
            return new TableCell<CartItem, Void>() {
                private final Button removeButton = new Button("Remove");
                
                {
                    removeButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                    removeButton.setOnAction(event -> {
                        CartItem cartItem = getTableView().getItems().get(getIndex());
                        removeFromCart(cartItem);
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(removeButton);
                    }
                }
            };
        });
        
        // Create a custom cell factory for the quantity column to allow adjusting
        cartQuantityColumn.setCellFactory(column -> {
            return new TableCell<CartItem, Integer>() {
                private final Spinner<Integer> spinner = new Spinner<>(1, 100, 1);
                
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (empty) {
                        setGraphic(null);
                    } else {
                        CartItem cartItem = getTableView().getItems().get(getIndex());
                        spinner.getValueFactory().setValue(cartItem.getQuantity());
                        
                        // Get the product for stock checking
                        final Product product = productsList.stream()
                            .filter(p -> p.getId() == cartItem.getProductId())
                            .findFirst()
                            .orElse(null);
                        
                        // Update quantity when spinner value changes
                        spinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                            // Check if new quantity exceeds available stock
                            if (product != null && newValue > product.getStock()) {
                                // Revert to old value
                                spinner.getValueFactory().setValue(oldValue);
                                
                                // Show error message
                                showAlert("Insufficient Stock", 
                                    "Not enough stock available for " + cartItem.getProductName() + 
                                    ". Available: " + product.getStock());
                            } else {
                                cartItem.setQuantity(newValue);
                                updateTotalAmount();
                                getTableView().refresh();
                            }
                        });
                        
                        setGraphic(spinner);
                    }
                }
            };
        });
        
        cartTable.setItems(cartItems);
        
        // Initialize discount and tax fields
        discountPercentField.setText("0");
        taxPercentField.setText("0");
        
        // Add listeners to discount and tax fields to update total
        discountPercentField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                discountPercentField.setText(oldValue);
            } else {
                updateTotalAmount();
            }
        });
        
        taxPercentField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                taxPercentField.setText(oldValue);
            } else {
                updateTotalAmount();
            }
        });
        
        // Initialize total amount
        updateTotalAmount();
    }
    
    private void initializeSalesHistoryView() {
        // Initialize sales history table
        salesInvoiceColumn.setCellValueFactory(new PropertyValueFactory<>("invoiceNumber"));
        salesDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        salesCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("customer"));
        salesItemsColumn.setCellValueFactory(new PropertyValueFactory<>("items"));
        salesTotalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        
        // View details button
        salesDetailsColumn.setCellFactory(column -> {
            return new TableCell<Sale, Void>() {
                private final Button viewButton = new Button("View");
                
                {
                    viewButton.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");
                    viewButton.setOnAction(event -> {
                        Sale sale = getTableView().getItems().get(getIndex());
                        showSaleDetails(sale);
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
        });
        
        salesHistoryTable.setItems(salesList);
        
        // Initialize date pickers
        salesStartDate.setValue(LocalDate.now().minusMonths(1));
        salesEndDate.setValue(LocalDate.now());
        
        // Calculate total sales amount
        updateTotalSalesAmount();
    }
    
    private void initializeStockManagementView() {
        // Initialize stock table
        stockIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        stockNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        stockQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        stockMinColumn.setCellValueFactory(new PropertyValueFactory<>("minStock"));
        
        // Custom status column like in products view
        stockStatusColumn.setCellFactory(column -> {
            return new TableCell<Product, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        Product product = getTableView().getItems().get(getIndex());
                        if (product.isLowOnStock()) {
                            setText("LOW STOCK");
                            setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
                        } else {
                            setText("IN STOCK");
                            setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                        }
                    }
                }
            };
        });
        
        // Update stock button
        stockUpdateColumn.setCellFactory(column -> {
            return new TableCell<Product, Void>() {
                private final Button updateButton = new Button("Update");
                
                {
                    updateButton.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");
                    updateButton.setOnAction(event -> {
                        Product product = getTableView().getItems().get(getIndex());
                        showUpdateStockDialog(product);
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
        });
        
        stockTable.setItems(productsList);
    }
    
    // Cart management methods
    private void addToCart(Product product) {
        // Check if the product is already in the cart
        for (CartItem item : cartItems) {
            if (item.getProductId() == product.getId()) {
                // Check if there's enough stock before incrementing
                if (item.getQuantity() + 1 > product.getStock()) {
                    showAlert("Insufficient Stock", 
                        "Not enough stock available for " + product.getName() + 
                        ". Available: " + product.getStock() + 
                        ", Already in cart: " + item.getQuantity());
                    return;
                }
                
                // If product already in cart and enough stock, increment quantity
                item.incrementQuantity();
                cartTable.refresh();
                updateTotalAmount();
                return;
            }
        }
        
        // Check if there's stock available for a new item
        if (product.getStock() < 1) {
            showAlert("Out of Stock", "This product is out of stock.");
            return;
        }
        
        // Otherwise add as new item
        CartItem newItem = new CartItem(cartItems.size() + 1, product, 1, product.getPrice());
        cartItems.add(newItem);
        updateTotalAmount();
    }
    
    private void removeFromCart(CartItem cartItem) {
        cartItems.remove(cartItem);
        updateTotalAmount();
    }
    
    private void updateTotalAmount() {
        double subtotal = cartItems.stream()
                .mapToDouble(CartItem::getTotal)
                .sum();
        
        // Get discount percentage (default to 0 if empty or invalid)
        double discountPercent = 0;
        try {
            if (!discountPercentField.getText().isEmpty()) {
                discountPercent = Double.parseDouble(discountPercentField.getText());
            }
        } catch (NumberFormatException e) {
            discountPercentField.setText("0");
        }
        
        // Get tax percentage (default to 0 if empty or invalid)
        double taxPercent = 0;
        try {
            if (!taxPercentField.getText().isEmpty()) {
                taxPercent = Double.parseDouble(taxPercentField.getText());
            }
        } catch (NumberFormatException e) {
            taxPercentField.setText("0");
        }
        
        // Calculate discount and tax amounts
        double discountAmount = subtotal * (discountPercent / 100);
        double afterDiscount = subtotal - discountAmount;
        double taxAmount = afterDiscount * (taxPercent / 100);
        double finalAmount = afterDiscount + taxAmount;
        
        // Update labels
        totalAmountLabel.setText(String.format("₹%.2f", subtotal));
        discountAmountLabel.setText(String.format("₹%.2f", discountAmount));
        taxAmountLabel.setText(String.format("₹%.2f", taxAmount));
        finalAmountLabel.setText(String.format("₹%.2f", finalAmount));
    }
    
    // Sales methods
    private void showSaleDetails(Sale sale) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sale Details");
        alert.setHeaderText("Invoice #" + sale.getInvoiceNumber());
        
        StringBuilder content = new StringBuilder();
        content.append("Customer: ").append(sale.getCustomer()).append("\n");
        content.append("Date: ").append(sale.getDate()).append("\n\n");
        content.append("Items:\n");
        
        for (SaleItem item : sale.getSaleItems()) {
            content.append("- ")
                  .append(item.getProductName())
                  .append(" × ")
                  .append(item.getQuantity())
                  .append(" @ ₹")
                  .append(String.format("%.2f", item.getPrice()))
                  .append(" = ₹")
                  .append(String.format("%.2f", item.getTotal()))
                  .append("\n");
        }
        
        content.append("\nTotal: ₹").append(String.format("%.2f", sale.getTotal()));
        
        alert.setContentText(content.toString());
        alert.showAndWait();
    }
    
    private void updateTotalSalesAmount() {
        double total = salesHistoryTable.getItems().stream()
                .mapToDouble(Sale::getTotal)
                .sum();
        totalSalesAmountLabel.setText(String.format("₹%.2f", total));
    }
    
    // Stock management methods
    private void showUpdateStockDialog(Product product) {
        // Create a dialog
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Update Stock");
        dialog.setHeaderText("Update Stock for " + product.getName());
        
        // Set the button types
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);
        
        // Create the content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        // Add current stock information
        Label currentStockLabel = new Label("Current Stock: " + product.getStock());
        Spinner<Integer> quantitySpinner = new Spinner<>(0, 1000, product.getStock());
        quantitySpinner.setEditable(true);
        
        // Add controls to adjust stock
        Label adjustLabel = new Label("New Stock Level:");
        
        grid.add(currentStockLabel, 0, 0, 2, 1);
        grid.add(adjustLabel, 0, 1);
        grid.add(quantitySpinner, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the quantity spinner
        quantitySpinner.requestFocus();
        
        // Convert the result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                return quantitySpinner.getValue();
            }
            return null;
        });
        
        // Show the dialog and process the result
        dialog.showAndWait().ifPresent(newStock -> {
            try {
                // Save the previous stock value
                int originalStock = product.getStock();
                
                // Update the stock in the product model
                product.setStock(newStock);
                
                // Update in database
                boolean success = medicineDAO.updateMedicine(product);
                
                if (success) {
                    // Refresh product lists
                    List<Product> updatedProducts = medicineDAO.getAllMedicines();
                    productsList.setAll(updatedProducts);
                    
                    // Update tables
                    productsTable.refresh();
                    medicinesTable.refresh();
                    stockTable.refresh();
                    
                    // Show confirmation
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Stock Updated");
                    alert.setHeaderText(null);
                    alert.setContentText("Stock for " + product.getName() + " has been updated to " + newStock);
                    alert.showAndWait();
                } else {
                    // Restore original stock value on failure
                    product.setStock(originalStock);
                    
                    // Show error
                    showAlert("Database Error", "Failed to update stock in database.");
                }
                
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error updating stock", e);
                showAlert("Error", "An error occurred while updating stock: " + e.getMessage());
            }
        });
    }
    
    @FXML
    private void searchMedicines() {
        String searchText = searchMedicine.getText().toLowerCase();
        searchMedicine(searchText);
    }
    
    private void searchMedicine(String searchText) {
        try {
            if (searchText == null || searchText.isEmpty()) {
                medicinesTable.setItems(productsList);
            } else {
                // Search medicines using database
                List<Product> searchResults = medicineDAO.searchMedicines(searchText);
                medicinesTable.setItems(FXCollections.observableArrayList(searchResults));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error searching medicines", e);
            showAlert("Database Error", "Failed to search medicines: " + e.getMessage());
        }
    }
    
    @FXML
    private void clearCart() {
        cartItems.clear();
        updateTotalAmount();
    }
    
    @FXML
    private void generateInvoice() {
        // Check if cart is empty
        if (cartItems.isEmpty()) {
            showAlert("Error", "Cart is empty. Please add products before generating an invoice.");
            return;
        }
        
        // Check if customer name is provided
        String customerName = customerNameField.getText().trim();
        if (customerName.isEmpty()) {
            showAlert("Error", "Please enter a customer name.");
            return;
        }
        
        // Check stock availability for all items in cart
        for (CartItem cartItem : cartItems) {
            Product product = productsList.stream()
                .filter(p -> p.getId() == cartItem.getProductId())
                .findFirst()
                .orElse(null);
                
            if (product != null && product.getStock() < cartItem.getQuantity()) {
                showAlert("Insufficient Stock", 
                    "Not enough stock available for " + cartItem.getProductName() + 
                    ". Available: " + product.getStock() + 
                    ", Requested: " + cartItem.getQuantity());
                return;
            }
        }
        
        try {
            LOGGER.info("Starting invoice generation for customer: " + customerName);
            
            // First, ensure a customer exists for this transaction
            CustomerDAO customerDAO = new CustomerDAO();
            int customerId = 0;
            
            // Try to find an existing customer with this name
            Customer customer = customerDAO.getCustomerByName(customerName);
            
            if (customer == null) {
                LOGGER.info("Customer not found, creating new customer record for: " + customerName);
                // Create a new customer with default values
                customer = new Customer();
                customer.setName(customerName);
                customer.setEmail(customerName.toLowerCase().replace(" ", ".") + "@example.com");
                customer.setPhone("0000000000"); // Default phone
                customer.setAddress("Walk-in Customer"); // Default address
                
                // Save the new customer
                boolean customerAdded = customerDAO.addCustomer(customer);
                if (!customerAdded) {
                    LOGGER.severe("Failed to create new customer: " + customerName);
                    showAlert("Error", "Failed to create customer record. Please try again.");
                    return;
                }
                
                // Refresh customer object to get assigned ID
                customer = customerDAO.getCustomerByName(customerName);
                if (customer == null) {
                    LOGGER.severe("Created customer but can't retrieve it: " + customerName);
                    showAlert("Error", "Customer created but unable to retrieve ID. Please try again.");
                    return;
                }
            }
            
            customerId = customer.getId();
            LOGGER.info("Using customer ID: " + customerId + " for invoice");
            
            // Convert cart items to sale items
            List<SaleItem> saleItems = new ArrayList<>();
            for (CartItem cartItem : cartItems) {
                SaleItem saleItem = new SaleItem(
                    0, // ID will be set by database
                    0, // sale ID will be set by database
                    cartItem.getProductId(),
                    cartItem.getProductName(),
                    cartItem.getQuantity(),
                    cartItem.getPrice()
                );
                saleItems.add(saleItem);
                LOGGER.info("Added item to invoice: " + cartItem.getProductName() + " x " + cartItem.getQuantity());
            }
            
            // Calculate totals
            double subtotal = cartItems.stream().mapToDouble(CartItem::getTotal).sum();
            
            // Parse discount and tax percentages
            double discountPercent = 0;
            try {
                discountPercent = Double.parseDouble(discountPercentField.getText());
            } catch (NumberFormatException e) {
                discountPercentField.setText("0");
            }
            
            double taxPercent = 0;
            try {
                taxPercent = Double.parseDouble(taxPercentField.getText());
            } catch (NumberFormatException e) {
                taxPercentField.setText("0");
            }
            
            // Calculate discount and tax amounts
            double discountAmount = subtotal * (discountPercent / 100);
            double afterDiscount = subtotal - discountAmount;
            double taxAmount = afterDiscount * (taxPercent / 100);
            double finalAmount = afterDiscount + taxAmount;
            
            LOGGER.info("Invoice calculations - Subtotal: " + subtotal + 
                      ", Discount: " + discountAmount + 
                      ", Tax: " + taxAmount + 
                      ", Final: " + finalAmount);
            
            // Create new sale
            Sale sale = new Sale();
            sale.setCustomerId(customerId); // Set the customer ID
            sale.setInvoiceDate(LocalDate.now());
            sale.setCustomerName(customerName);
            sale.setTotalAmount(subtotal);
            sale.setDiscountAmount(discountAmount);
            sale.setTaxAmount(taxAmount);
            sale.setFinalAmount(finalAmount);
            sale.setItems(saleItems);
            
            // Save to database
            LOGGER.info("Attempting to save invoice to database...");
            boolean success = saleDAO.addSale(sale);
            
            if (success) {
                LOGGER.info("Invoice successfully saved with ID: " + sale.getInvoiceId());
                // Add to UI list
                salesList.add(sale);
                
                // Refresh product list to get updated stock quantities
                List<Product> updatedProducts = medicineDAO.getAllMedicines();
                productsList.setAll(updatedProducts);
                
                // Update tables
                productsTable.refresh();
                medicinesTable.refresh();
                stockTable.refresh();
                salesHistoryTable.refresh();
                recentSalesTable.refresh();
                
                // Clear cart
                cartItems.clear();
                updateTotalAmount();
                customerNameField.clear();
                discountPercentField.setText("0");
                taxPercentField.setText("0");
                
                // Update dashboard stats
                initializeDashboard();
                
                // Show success message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Invoice Generated");
                alert.setHeaderText(null);
                alert.setContentText("Invoice #" + sale.getInvoiceId() + " has been generated successfully!" +
                        "\nSubtotal: ₹" + String.format("%.2f", subtotal) +
                        "\nDiscount: ₹" + String.format("%.2f", discountAmount) +
                        "\nTax: ₹" + String.format("%.2f", taxAmount) +
                        "\nTotal: ₹" + String.format("%.2f", finalAmount));
                alert.showAndWait();
            } else {
                LOGGER.severe("Failed to save invoice in database");
                showAlert("Database Error", "Failed to save the invoice. Please check database connection and try again.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating invoice", e);
            showAlert("Error", "An error occurred while generating the invoice: " + e.getMessage() + 
                    "\nStack trace: " + java.util.Arrays.toString(e.getStackTrace()));
        }
    }
    
    @FXML
    private void filterSales() {
        LocalDate startDate = salesStartDate.getValue();
        LocalDate endDate = salesEndDate.getValue();
        
        if (startDate == null || endDate == null) {
            showAlert("Error", "Please select both start and end dates.");
            return;
        }
        
        if (startDate.isAfter(endDate)) {
            showAlert("Error", "Start date cannot be after end date.");
            return;
        }
        
        try {
            // Get filtered sales from database
            List<Sale> filteredSales = saleDAO.getSalesByDateRange(startDate, endDate);
            salesHistoryTable.setItems(FXCollections.observableArrayList(filteredSales));
            
            // Update total amount
            updateTotalSalesAmount();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error filtering sales", e);
            showAlert("Database Error", "Failed to filter sales: " + e.getMessage());
        }
    }
    
    @FXML
    private void toggleLowStockInManage() {
        boolean showOnlyLowStock = showLowStockBtn2.isSelected();
        
        try {
            if (showOnlyLowStock) {
                // Get low stock products from database
                List<Product> lowStockProducts = medicineDAO.getLowStockMedicines();
                stockTable.setItems(FXCollections.observableArrayList(lowStockProducts));
            } else {
                // Show all products
                stockTable.setItems(productsList);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error toggling low stock display in stock management", e);
            showAlert("Database Error", "Failed to fetch low stock products: " + e.getMessage());
        }
    }
    
    @FXML
    private void searchStock() {
        String searchText = searchStock.getText().toLowerCase();
        
        try {
            if (searchText.isEmpty()) {
                stockTable.setItems(productsList);
            } else {
                // Search products using database
                List<Product> searchResults = medicineDAO.searchMedicines(searchText);
                stockTable.setItems(FXCollections.observableArrayList(searchResults));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error searching stock", e);
            showAlert("Database Error", "Failed to search products in stock: " + e.getMessage());
        }
    }
    
    @FXML
    private void toggleLowStock() {
        boolean showOnlyLowStock = showLowStockBtn.isSelected();
        
        try {
            if (showOnlyLowStock) {
                // Get low stock products from database
                List<Product> lowStockProducts = medicineDAO.getLowStockMedicines();
                productsTable.setItems(FXCollections.observableArrayList(lowStockProducts));
            } else {
                // Show all products
                productsTable.setItems(productsList);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error toggling low stock display", e);
            showAlert("Database Error", "Failed to fetch low stock products: " + e.getMessage());
        }
    }
    
    @FXML
    private void searchProducts() {
        String searchText = searchProduct.getText().toLowerCase();
        
        try {
            if (searchText.isEmpty()) {
                productsTable.setItems(productsList);
            } else {
                // Search products using database
                List<Product> searchResults = medicineDAO.searchMedicines(searchText);
                productsTable.setItems(FXCollections.observableArrayList(searchResults));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error searching products", e);
            showAlert("Database Error", "Failed to search products: " + e.getMessage());
        }
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    @FXML
    private void showDashboard() {
        setActiveButton(dashboardBtn);
        showPane(dashboardPane);
    }
    
    @FXML
    private void showProducts() {
        setActiveButton(viewProductsBtn);
        showPane(productsPane);
    }
    
    @FXML
    private void showSellMedicines() {
        setActiveButton(sellMedicinesBtn);
        showPane(sellMedicinesPane);
    }
    
    @FXML
    private void showSalesHistory() {
        setActiveButton(salesHistoryBtn);
        showPane(salesHistoryPane);
    }
    
    @FXML
    private void showManageStock() {
        setActiveButton(manageStockBtn);
        showPane(manageStockPane);
    }
    
    @FXML
    private void handleLogout() {
        try {
            // Load the login screen
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Stage stage = (Stage) dashboardBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Pharmacy Management System");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Error returning to login screen: " + e.getMessage());
        }
    }
    
    private void showPane(VBox pane) {
        // Hide all panes
        dashboardPane.setVisible(false);
        dashboardPane.setManaged(false);
        productsPane.setVisible(false);
        productsPane.setManaged(false);
        sellMedicinesPane.setVisible(false);
        sellMedicinesPane.setManaged(false);
        salesHistoryPane.setVisible(false);
        salesHistoryPane.setManaged(false);
        manageStockPane.setVisible(false);
        manageStockPane.setManaged(false);
        
        // Show the selected pane
        pane.setVisible(true);
        pane.setManaged(true);
    }
    
    private int countTodaySales() {
        try {
            return saleDAO.getTodaySales().size();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error counting today's sales", e);
            return 0;
        }
    }
    
    private double calculateTodayRevenue() {
        try {
            return saleDAO.getTodaySales().stream()
                .mapToDouble(Sale::getTotal)
                .sum();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating today's revenue", e);
            return 0.0;
        }
    }
    
    private int countLowStockItems() {
        try {
            return medicineDAO.getLowStockMedicines().size();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error counting low stock items", e);
            return 0;
        }
    }
    
    private void setActiveButton(Button button) {
        // Reset all buttons to default style
        String defaultStyle = "-fx-background-color: #263238; -fx-text-fill: white; " +
                             "-fx-alignment: CENTER_LEFT; -fx-font-size: 14;";
        String activeStyle = "-fx-background-color: #1E88E5; -fx-text-fill: white; " +
                            "-fx-alignment: CENTER_LEFT; -fx-font-size: 14;";
        
        dashboardBtn.setStyle(defaultStyle);
        viewProductsBtn.setStyle(defaultStyle);
        sellMedicinesBtn.setStyle(defaultStyle);
        salesHistoryBtn.setStyle(defaultStyle);
        manageStockBtn.setStyle(defaultStyle);
        
        // Set active button style
        button.setStyle(activeStyle);
    }
} 