package com.automation.core.config;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Centralized configuration manager for the framework.
 * Provides access to all configuration properties with type-safe methods.
 * Supports overriding properties via system properties for CI/CD environments.
 */
@Slf4j
public class ConfigManager {
    
    private static ConfigManager instance;
    private final Properties properties;
    
    private ConfigManager() {
        properties = new Properties();
        loadProperties();
    }
    
    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }
    
    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) {
                log.warn("Unable to find config.properties. Using defaults.");
                return;
            }
            properties.load(input);
            log.info("Configuration loaded successfully");
        } catch (IOException e) {
            log.error("Error loading configuration: {}", e.getMessage());
            throw new RuntimeException("Failed to load configuration", e);
        }
    }
    
    /**
     * Get property value with system property override support
     */
    private String getProperty(String key, String defaultValue) {
        // First check system property
        String systemValue = System.getProperty(key);
        if (systemValue != null) {
            return systemValue;
        }
        // Then check properties file
        return properties.getProperty(key, defaultValue);
    }
    
    // Browser Configuration
    public String getBrowser() {
        return getProperty("browser", "chrome");
    }
    
    public boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless", "false"));
    }
    
    public boolean shouldMaximizeBrowser() {
        return Boolean.parseBoolean(getProperty("browser.maximize", "true"));
    }
    
    public boolean shouldDeleteCookies() {
        return Boolean.parseBoolean(getProperty("browser.delete.cookies", "true"));
    }
    
    // Timeout Configuration
    public int getImplicitTimeout() {
        return Integer.parseInt(getProperty("timeout.implicit", "10"));
    }
    
    public int getExplicitTimeout() {
        return Integer.parseInt(getProperty("timeout.explicit", "20"));
    }
    
    public int getPageLoadTimeout() {
        return Integer.parseInt(getProperty("timeout.page.load", "30"));
    }
    
    public int getScriptTimeout() {
        return Integer.parseInt(getProperty("timeout.script", "30"));
    }
    
    public int getPollingInterval() {
        return Integer.parseInt(getProperty("timeout.polling", "500"));
    }
    
    // Wait Configuration
    public int getElementVisibleTimeout() {
        return Integer.parseInt(getProperty("wait.element.visible", "20"));
    }
    
    public int getElementClickableTimeout() {
        return Integer.parseInt(getProperty("wait.element.clickable", "15"));
    }
    
    public int getElementPresenceTimeout() {
        return Integer.parseInt(getProperty("wait.element.presence", "10"));
    }
    
    // API Configuration
    public String getApiBaseUrl() {
        return getProperty("api.base.url", "https://api.example.com");
    }
    
    public int getApiTimeout() {
        return Integer.parseInt(getProperty("api.timeout", "30000"));
    }
    
    public int getApiRetryCount() {
        return Integer.parseInt(getProperty("api.retry.count", "3"));
    }
    
    // Test Configuration
    public boolean isScreenshotOnFailure() {
        return Boolean.parseBoolean(getProperty("screenshot.on.failure", "true"));
    }
    
    public boolean isScreenshotOnSuccess() {
        return Boolean.parseBoolean(getProperty("screenshot.on.success", "false"));
    }
    
    public boolean isVideoRecording() {
        return Boolean.parseBoolean(getProperty("video.recording", "false"));
    }
    
    // Reporting
    public String getReportPath() {
        return getProperty("report.path", "test-output/reports");
    }
    
    public String getReportTitle() {
        return getProperty("report.title", "Test Automation Report");
    }
    
    public String getReportName() {
        return getProperty("report.name", "Selenium Framework Report");
    }
    
    // Environment
    public String getEnvironment() {
        return getProperty("environment", "qa");
    }
    
    public String getBaseUrl() {
        return getProperty("base.url", "https://www.example.com");
    }
    
    // Logging
    public String getLogLevel() {
        return getProperty("log.level", "INFO");
    }
    
    public String getLogPath() {
        return getProperty("log.path", "test-output/logs");
    }
    
    /**
     * Get any custom property by key
     */
    public String get(String key) {
        return getProperty(key, "");
    }
    
    /**
     * Get property with default value
     */
    public String get(String key, String defaultValue) {
        return getProperty(key, defaultValue);
    }
}
