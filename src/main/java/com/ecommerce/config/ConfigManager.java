package com.ecommerce.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration Manager - Singleton pattern
 * Handles all configuration properties
 * 
 * @author QA Team
 * @version 2.0
 */
public class ConfigManager {
    
    private static final Logger log = LogManager.getLogger(ConfigManager.class);
    
    private static ConfigManager instance;
    private Properties properties;
    
    private static final String CONFIG_PATH = "src/test/resources/config/config.properties";
    
    private ConfigManager() {
        properties = new Properties();
        loadProperties();
    }
    
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    private void loadProperties() {
        try (InputStream input = new FileInputStream(CONFIG_PATH)) {
            properties.load(input);
            log.info("Configuration loaded successfully");
        } catch (IOException e) {
            log.warn("Could not load config file, using defaults: {}", e.getMessage());
            setDefaults();
        }
        
        // Override with environment variables if present
        overrideWithEnvironmentVariables();
        
        // Override with system properties if present
        overrideWithSystemProperties();
    }
    
    private void setDefaults() {
        properties.setProperty("base.url", "https://www.saucedemo.com");
        properties.setProperty("browser", "chrome");
        properties.setProperty("headless", "false");
        properties.setProperty("implicit.wait", "10");
        properties.setProperty("explicit.wait", "20");
        properties.setProperty("page.load.timeout", "30");
        properties.setProperty("standard.user", "standard_user");
        properties.setProperty("test.password", "secret_sauce");
        properties.setProperty("screenshot.on.failure", "true");
        properties.setProperty("selenium.grid", "false");
        properties.setProperty("selenium.grid.url", "http://localhost:4444/wd/hub");
    }
    
    private void overrideWithEnvironmentVariables() {
        String[] envVars = {"BROWSER", "HEADLESS", "SELENIUM_GRID_URL", "TEST_ENV", "THREAD_COUNT"};
        
        for (String var : envVars) {
            String value = System.getenv(var);
            if (value != null && !value.isEmpty()) {
                String propertyKey = var.toLowerCase().replace("_", ".");
                properties.setProperty(propertyKey, value);
                log.info("Overridden from ENV: {} = {}", propertyKey, value);
            }
        }
        
        // Special handling for Selenium Grid URL
        String gridUrl = System.getenv("SELENIUM_GRID_URL");
        if (gridUrl != null && !gridUrl.isEmpty()) {
            properties.setProperty("selenium.grid", "true");
            properties.setProperty("selenium.grid.url", gridUrl);
        }
    }
    
    private void overrideWithSystemProperties() {
        for (String key : properties.stringPropertyNames()) {
            String systemValue = System.getProperty(key);
            if (systemValue != null && !systemValue.isEmpty()) {
                properties.setProperty(key, systemValue);
                log.debug("Overridden from System Property: {} = {}", key, systemValue);
            }
        }
    }
    
    // ==================== GETTERS ====================
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                log.warn("Invalid integer for key {}: {}", key, value);
            }
        }
        return defaultValue;
    }
    
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }
    
    public long getLong(String key, long defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                log.warn("Invalid long for key {}: {}", key, value);
            }
        }
        return defaultValue;
    }
    
    // ==================== SETTERS ====================
    
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
    
    // ==================== UTILITY ====================
    
    public void reload() {
        loadProperties();
        log.info("Configuration reloaded");
    }
}
