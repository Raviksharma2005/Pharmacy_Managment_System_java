package com.pharmacy.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.File;
import java.time.LocalDate;
import java.util.logging.Logger;

import com.pharmacy.models.Report;

public class ReportViewerController {
    private static final Logger LOGGER = Logger.getLogger(ReportViewerController.class.getName());
    
    @FXML private Button backButton;
    @FXML private Label titleLabel;
    @FXML private Label dateLabel;
    @FXML private Label typeLabel;
    @FXML private TextArea contentArea;
    @FXML private Button exportPdfButton;
    @FXML private Button exportExcelButton;
    
    private Report currentReport;
    private Runnable onBackAction;
    
    @FXML
    private void initialize() {
        backButton.setOnAction(this::handleBackButton);
        exportPdfButton.setOnAction(this::handleExportPdf);
        exportExcelButton.setOnAction(this::handleExportExcel);
        
        // Set initial content
        StringBuilder content = new StringBuilder();
        content.append("PHARMACY MANAGEMENT SYSTEM\n");
        content.append("SAMPLE SALES REPORT\n");
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
        
        contentArea.setText(content.toString());
    }
    
    public void setReport(Report report) {
        this.currentReport = report;
        
        // Update UI with report details
        titleLabel.setText(report.getTitle());
        dateLabel.setText(report.getDate().toString());
        typeLabel.setText(report.getType());
        
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
        
        contentArea.setText(content.toString());
    }
    
    public void setOnBackAction(Runnable action) {
        this.onBackAction = action;
    }
    
    private void handleBackButton(ActionEvent event) {
        if (onBackAction != null) {
            onBackAction.run();
        }
    }
    
    private void handleExportPdf(ActionEvent event) {
        if (currentReport == null) return;
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report as PDF");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        // Set initial file name based on report
        String fileName = currentReport.getType().toLowerCase().replace(" ", "_") + "_" + 
                          currentReport.getDate() + ".pdf";
        fileChooser.setInitialFileName(fileName);
        
        // Show save dialog
        Stage stage = (Stage) exportPdfButton.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        
        if (file != null) {
            // In a real app, this would generate and save the actual PDF
            LOGGER.info("Report saved as PDF: " + file.getAbsolutePath());
            // Show confirmation
            showExportSuccessMessage("PDF", file.getAbsolutePath());
        }
    }
    
    private void handleExportExcel(ActionEvent event) {
        if (currentReport == null) return;
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report as Excel");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        // Set initial file name based on report
        String fileName = currentReport.getType().toLowerCase().replace(" ", "_") + "_" + 
                          currentReport.getDate() + ".xlsx";
        fileChooser.setInitialFileName(fileName);
        
        // Show save dialog
        Stage stage = (Stage) exportExcelButton.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        
        if (file != null) {
            // In a real app, this would generate and save the actual Excel file
            LOGGER.info("Report saved as Excel: " + file.getAbsolutePath());
            // Show confirmation
            showExportSuccessMessage("Excel", file.getAbsolutePath());
        }
    }
    
    private void showExportSuccessMessage(String type, String path) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Export Successful");
        alert.setHeaderText(null);
        alert.setContentText("Report has been exported as " + type + " to:\n" + path);
        alert.showAndWait();
    }
} 