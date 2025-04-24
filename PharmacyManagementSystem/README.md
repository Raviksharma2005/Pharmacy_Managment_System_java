# Pharmacy Management System

A comprehensive JavaFX-based system for pharmacy management.

## Features

- Product/Medicine Management
- Employee Management
- Supplier Management
- Sales and Billing
- Stock Management
- Reports Generation

## Database Setup

The application uses MySQL for data storage. Follow these steps to set up the database:

1. Install MySQL Server if you don't have it already.
2. Create a new database named `pharmacy_db` or run the complete setup script:

```sql
source database_setup.sql
```

3. Configure the database connection in the application:
   - Open `src/main/java/com/pharmacy/util/DatabaseConnector.java`
   - Update the following fields with your MySQL credentials:
     ```java
     private static final String DB_URL = "jdbc:mysql://localhost:3306/pharmacy_db";
     private static final String DB_USER = "your_username";
     private static final String DB_PASSWORD = "your_password";
     ```

## System Requirements

- Java 17 or higher
- JavaFX
- MySQL 8.0 or higher
- Maven (for building)

## Running the Application

1. Ensure Maven is installed on your system.
2. Run the following command from the project directory:

```
mvn clean compile exec:java -Dexec.mainClass="com.pharmacy.Main"
```

## Default Login Credentials

- Admin:
  - Username: admin
  - Password: admin123
  - Role: Admin

- Pharmacist:
  - Username: pharmacist
  - Password: pharm123
  - Role: Pharmacist

## Dependencies

- JavaFX
- MySQL Connector/J
- JFoenix (for UI components)
- iText (for PDF generation)

## Project Structure

- `src/main/java/com/pharmacy/models` - Data models
- `src/main/java/com/pharmacy/controllers` - JavaFX controllers
- `src/main/java/com/pharmacy/dao` - Data Access Objects
- `src/main/java/com/pharmacy/util` - Utility classes
- `src/main/resources/fxml` - JavaFX FXML files
- `src/main/resources/css` - CSS stylesheets
- `src/main/resources/images` - Application images

## License

This project is licensed under the MIT License - see the LICENSE file for details. 