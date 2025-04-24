# PharmaCare Management System - Validation Tools

This document explains how to use the validation tools provided to test and verify all functionality of the PharmaCare Management System.

## Overview

The validation tools consist of several components:

1. **SystemValidator** - Core validation of database connectivity and data operations
2. **UIValidator** - Validation of UI components and user interfaces
3. **BusinessLogicValidator** - Validation of business logic operations
4. **ValidationRunner** - Main runner that orchestrates all validations

Additionally, a comprehensive validation plan (`validation_plan.md`) is provided as a checklist for manual validation.

## Prerequisites

Before running the validation tools, ensure you have:

1. Java 11 or higher installed
2. MySQL database server running
3. Database created and schema applied (using `database_setup.sql`)
4. Database credentials configured in `DatabaseConnector.java`

## Running Validations

### 1. Using ValidationRunner

The `ValidationRunner` class is the primary way to run validations. To use it:

```bash
# Navigate to the project root
cd PharmacyManagementSystem

# Compile the project
mvn clean compile

# Run the validator with all checks
mvn exec:java -Dexec.mainClass="com.pharmacy.utils.ValidationRunner"
```

### Command-Line Options

The ValidationRunner supports several command-line options:

- `--skip-ui` - Skip UI validations
- `--skip-db` - Skip database validations
- `--skip-business` - Skip business logic validations

Example:
```bash
# Run only database validations
mvn exec:java -Dexec.mainClass="com.pharmacy.utils.ValidationRunner" -Dexec.args="--skip-ui --skip-business"
```

### 2. Individual Validation Classes

You can also run each validation component individually:

#### Database Validation
```bash
mvn exec:java -Dexec.mainClass="com.pharmacy.utils.SystemValidator"
```

#### Business Logic Validation
```bash
mvn exec:java -Dexec.mainClass="com.pharmacy.utils.BusinessLogicValidator"
```

## Validation Output

The validation tools will print the results of each test to the console. Each test will be marked as either PASSED or FAILED.

Example output:
```
PharmaCare Management System Validation
======================================

Running database validations...
Database connection validation: PASSED
User retrieval validation: PASSED
Product retrieval validation: PASSED
Category retrieval validation: PASSED
Medicine retrieval validation: PASSED
Database validations: PASSED

Running UI validations...
Login screen loading validation: PASSED
Admin dashboard loading validation: PASSED
Pharmacist dashboard loading validation: PASSED
CSS theme loading validation: PASSED
Login form validation: PASSED
UI validations: PASSED

Running authentication validations...
Admin authentication: PASSED
Pharmacist authentication: PASSED
Invalid credentials rejected: PASSED

Running business logic validations...
Running product creation validation...
Product creation validation: PASSED
Running product update validation...
Product update validation: PASSED
Running sales processing validation...
Sales processing validation: PASSED
Business logic validation summary: ALL PASSED

PHARMACARE VALIDATION COMPLETE
```

## Manual Validation

In addition to automated validation, refer to the `validation_plan.md` file for a comprehensive checklist of functionality to validate manually.

## Troubleshooting

### Database Connection Issues

If you encounter database connection errors:

1. Verify MySQL is running
2. Check database credentials in `DatabaseConnector.java`
3. Ensure the database schema has been applied correctly
4. Check network connectivity if using a remote database

### UI Validation Issues

If UI validation fails:

1. Ensure JavaFX is properly configured in your project
2. Check that all FXML files exist in the correct location
3. Verify CSS files are present
4. Make sure all required libraries are in the classpath

### Business Logic Validation Issues

If business logic validation fails:

1. Check that all required tables exist in the database
2. Verify that DAOs are properly implemented
3. Check for any database constraint violations
4. Examine log files for detailed error information

## Extending Validations

You can extend the validation tools by:

1. Adding new methods to existing validator classes
2. Creating new validator classes for specific functionality
3. Adding new command-line options to ValidationRunner
4. Expanding the validation plan with additional test cases 