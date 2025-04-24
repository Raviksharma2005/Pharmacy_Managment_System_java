CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'pharmacist') NOT NULL,
    employee_id INT,
    FOREIGN KEY (employee_id) REFERENCES Employee(employee_id)
);

CREATE TABLE Customer (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15) NOT NULL,
    address TEXT
);

-- Employee Table
CREATE TABLE Employee (
    employee_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    role ENUM('Pharmacist', 'Cashier') NOT NULL,
    contact_number VARCHAR(15),
    email VARCHAR(100) UNIQUE,
    hire_date DATE,
    salary DECIMAL(10,2)
);

-- Supplier Table
CREATE TABLE Supplier (
    supplier_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    contact_number VARCHAR(15),
    email VARCHAR(100),
    address TEXT
);

-- Medicine_Category Table
CREATE TABLE Medicine_Category (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(50) NOT NULL,
    description TEXT
);

-- Medicine Table
CREATE TABLE Medicine (
    medicine_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    brand VARCHAR(100),
    category VARCHAR(50),
    stock_quantity INT CHECK (stock_quantity >= 0),
    expiry_date DATE,
    price DECIMAL(10,2),
    description TEXT,
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES Medicine_Category(category_id)
);

-- Purchase Table
CREATE TABLE Purchase (
    purchase_id INT PRIMARY KEY AUTO_INCREMENT,
    supplier_id INT,
    medicine_id INT,
    purchase_price DECIMAL(10,2),
    quantity INT,
    purchase_date DATE,
    FOREIGN KEY (supplier_id) REFERENCES Supplier(supplier_id),
    FOREIGN KEY (medicine_id) REFERENCES Medicine(medicine_id)
);

-- Doctor Table
CREATE TABLE Doctor (
    doctor_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100),
    contact_number VARCHAR(15),
    email VARCHAR(100)
);

-- Prescription Table
CREATE TABLE Prescription (
    prescription_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT,
    doctor_name VARCHAR(100),
    diagnosis TEXT,
    notes TEXT,
    prescription_date DATE,
    doctor_id INT,
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id),
    FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id)
);

-- Invoice Table
CREATE TABLE Invoice (
    invoice_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT,
    customer_id INT,
    invoice_date DATE,
    total_amount DECIMAL(10,2),
    discount_amount DECIMAL(10,2),
    tax_amount DECIMAL(10,2),
    final_amount DECIMAL(10,2),
    FOREIGN KEY (employee_id) REFERENCES Employee(employee_id),
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id)
);

-- Invoice_Item Table
CREATE TABLE Invoice_Item (
    invoice_item_id INT PRIMARY KEY AUTO_INCREMENT,
    invoice_id INT,
    medicine_id INT,
    quantity INT,
    price_per_unit DECIMAL(10,2),
    total_price DECIMAL(10,2),
    FOREIGN KEY (invoice_id) REFERENCES Invoice(invoice_id),
    FOREIGN KEY (medicine_id) REFERENCES Medicine(medicine_id)
);

-- Return Table
CREATE TABLE ⁠ Return ⁠ (
    return_id INT PRIMARY KEY AUTO_INCREMENT,
    invoice_id INT,
    medicine_id INT,
    quantity_returned INT,
    return_date DATE,
    reason TEXT,
    refund_amount DECIMAL(10,2),
    FOREIGN KEY (invoice_id) REFERENCES Invoice(invoice_id),
    FOREIGN KEY (medicine_id) REFERENCES Medicine(medicine_id)
);

-- Refund Table
CREATE TABLE Refund (
    refund_id INT PRIMARY KEY AUTO_INCREMENT,
    return_id INT,
    refund_status VARCHAR(50),
    refund_date DATE,
    payment_method VARCHAR(50),
    FOREIGN KEY (return_id) REFERENCES ⁠ Return ⁠(return_id)
);


-- Stock_Transactions Table
CREATE TABLE Stock_Transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    medicine_id INT,
    change_quantity INT,
    transaction_type ENUM('purchase', 'sale', 'return') NOT NULL,
    transaction_date DATE,
    reference_id INT,
    FOREIGN KEY (medicine_id) REFERENCES Medicine(medicine_id)
);

-- Payment_Details Table
CREATE TABLE Payment_Details (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    invoice_id INT,
    payment_method VARCHAR(50),
    transaction_date DATE,
    amount_paid DECIMAL(10,2),
    payment_status ENUM('completed', 'pending', 'failed'),
    FOREIGN KEY (invoice_id) REFERENCES Invoice(invoice_id)
);

-- Audit_Log Table
CREATE TABLE Audit_Log (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    table_name VARCHAR(100),
    operation_type ENUM('INSERT', 'UPDATE', 'DELETE'),
    performed_by VARCHAR(100),
    operation_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    operation_details TEXT
);