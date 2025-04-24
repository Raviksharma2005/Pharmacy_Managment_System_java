-- Create Customer table if it doesn't exist
CREATE TABLE IF NOT EXISTS Customer (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    address TEXT
);

-- Create Employee table if it doesn't exist
CREATE TABLE IF NOT EXISTS Employee (
    employee_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL,
    contact_number VARCHAR(15),
    email VARCHAR(100),
    hire_date DATE,
    salary DECIMAL(10,2)
);

-- Create Medicine table if it doesn't exist
CREATE TABLE IF NOT EXISTS Medicine (
    medicine_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category VARCHAR(50),
    brand VARCHAR(100),
    stock_quantity INT NOT NULL DEFAULT 0,
    price DECIMAL(10,2) NOT NULL,
    expiry_date DATE
);

-- Create Invoice table if it doesn't exist
CREATE TABLE IF NOT EXISTS Invoice (
    invoice_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT,
    customer_id INT,
    invoice_date DATE,
    total_amount DECIMAL(10,2),
    discount_amount DECIMAL(10,2) DEFAULT 0,
    tax_amount DECIMAL(10,2) DEFAULT 0,
    final_amount DECIMAL(10,2),
    FOREIGN KEY (employee_id) REFERENCES Employee(employee_id) ON DELETE SET NULL,
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id) ON DELETE SET NULL
);

-- Create Invoice_Item table if it doesn't exist
CREATE TABLE IF NOT EXISTS Invoice_Item (
    invoice_item_id INT PRIMARY KEY AUTO_INCREMENT,
    invoice_id INT,
    medicine_id INT,
    quantity INT,
    price_per_unit DECIMAL(10,2),
    total_price DECIMAL(10,2),
    FOREIGN KEY (invoice_id) REFERENCES Invoice(invoice_id) ON DELETE CASCADE,
    FOREIGN KEY (medicine_id) REFERENCES Medicine(medicine_id) ON DELETE CASCADE
);

-- Create Stock_Transactions table if it doesn't exist
CREATE TABLE IF NOT EXISTS Stock_Transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    medicine_id INT,
    change_quantity INT,
    transaction_type VARCHAR(10),
    transaction_date DATE,
    reference_id INT,
    FOREIGN KEY (medicine_id) REFERENCES Medicine(medicine_id) ON DELETE CASCADE
);

-- Insert a default customer if none exists
INSERT INTO Customer (name, email, phone, address)
SELECT 'Walk-in Customer', 'customer@pharmacy.com', '0000000000', 'Default customer'
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM Customer LIMIT 1);

-- Insert a default employee if none exists
INSERT INTO Employee (name, role, contact_number, email, hire_date, salary)
SELECT 'Default Admin', 'Admin', '0000000000', 'admin@pharmacy.com', CURDATE(), 50000
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM Employee LIMIT 1); 