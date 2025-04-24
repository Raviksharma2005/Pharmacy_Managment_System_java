package com.pharmacy.utils;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import java.util.prefs.Preferences;

/**
 * A utility class to manage application themes
 */
public class ThemeManager {
    // Theme constants
    public static final String LIGHT_THEME = "light";
    public static final String DARK_THEME = "dark";
    public static final String MODERN_THEME = "modern";
    
    // CSS file paths
    private static final String LIGHT_THEME_CSS = "/css/light-theme.css";
    private static final String DARK_THEME_CSS = "/css/dark-theme.css";
    private static final String MODERN_THEME_CSS = "/css/modern-theme.css";
    
    // Preferences key for saving user's theme selection
    private static final String PREF_THEME = "pharmacy_app_theme";
    
    // Singleton instance
    private static ThemeManager instance;
    
    // Preferences object to store user preferences
    private final Preferences prefs;
    
    // Current theme
    private String currentTheme;
    
    /**
     * Private constructor - use getInstance() instead
     */
    private ThemeManager() {
        prefs = Preferences.userNodeForPackage(ThemeManager.class);
        currentTheme = prefs.get(PREF_THEME, LIGHT_THEME); // Default to light theme
    }
    
    /**
     * Get the ThemeManager instance
     * @return The ThemeManager instance
     */
    public static synchronized ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
    
    /**
     * Apply a theme to a scene
     * @param scene The scene to apply the theme to
     * @param themeName The theme to apply (use the constants LIGHT_THEME, DARK_THEME, MODERN_THEME)
     */
    public void applyTheme(Scene scene, String themeName) {
        try {
            // Clear existing theme stylesheets
            scene.getStylesheets().clear();
            
            // Apply the selected theme
            switch (themeName) {
                case LIGHT_THEME:
                    scene.getStylesheets().add(getClass().getResource(LIGHT_THEME_CSS).toExternalForm());
                    break;
                case DARK_THEME:
                    scene.getStylesheets().add(getClass().getResource(DARK_THEME_CSS).toExternalForm());
                    break;
                case MODERN_THEME:
                    scene.getStylesheets().add(getClass().getResource(MODERN_THEME_CSS).toExternalForm());
                    break;
                default:
                    // Default to light theme if an invalid theme is specified
                    scene.getStylesheets().add(getClass().getResource(LIGHT_THEME_CSS).toExternalForm());
                    themeName = LIGHT_THEME;
                    break;
            }
            
            // Update current theme and save to preferences
            currentTheme = themeName;
            prefs.put(PREF_THEME, themeName);
            
        } catch (Exception e) {
            showErrorAlert("Failed to apply theme: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Apply the saved theme to a scene
     * @param scene The scene to apply the theme to
     */
    public void applySavedTheme(Scene scene) {
        applyTheme(scene, currentTheme);
    }
    
    /**
     * Get the current theme name
     * @return The current theme name
     */
    public String getCurrentTheme() {
        return currentTheme;
    }
    
    /**
     * Cycle to the next theme
     * @param scene The scene to apply the new theme to
     */
    public void cycleTheme(Scene scene) {
        String nextTheme;
        
        switch (currentTheme) {
            case LIGHT_THEME:
                nextTheme = DARK_THEME;
                break;
            case DARK_THEME:
                nextTheme = MODERN_THEME;
                break;
            case MODERN_THEME:
            default:
                nextTheme = LIGHT_THEME;
                break;
        }
        
        applyTheme(scene, nextTheme);
    }
    
    /**
     * Shows an error alert with the given message
     * @param message The error message to show
     */
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Theme Error");
        alert.setHeaderText("Error Applying Theme");
        alert.setContentText(message);
        alert.showAndWait();
    }
} 