package com.pharmacy.models;

import java.time.LocalDate;

public class Report {
    private int id;
    private String title;
    private LocalDate date;
    private String type;
    private String description;
    private String filePath;

    // Constructor
    public Report(int id, String title, LocalDate date, String type, String description, String filePath) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.type = type;
        this.description = description;
        this.filePath = filePath;
    }

    // Default constructor
    public Report() {
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return title;
    }
} 