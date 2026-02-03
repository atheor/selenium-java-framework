package com.automation.core.browser;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

/**
 * Thread-safe WebDriver manager using ThreadLocal for parallel test execution.
 * Provides centralized driver lifecycle management.
 */
@Slf4j
public class DriverManager {
    
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    
    /**
     * Get the WebDriver instance for the current thread
     */
    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            log.debug("No driver found for current thread. Creating new driver.");
            setDriver(DriverFactory.createDriver());
        }
        return driverThreadLocal.get();
    }
    
    /**
     * Set the WebDriver instance for the current thread
     */
    public static void setDriver(WebDriver driver) {
        driverThreadLocal.set(driver);
        log.debug("Driver set for thread: {}", Thread.currentThread().getId());
    }
    
    /**
     * Quit the driver and remove it from ThreadLocal
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                log.info("Driver quit successfully for thread: {}", Thread.currentThread().getId());
            } catch (Exception e) {
                log.error("Error quitting driver: {}", e.getMessage());
            } finally {
                driverThreadLocal.remove();
            }
        }
    }
    
    /**
     * Check if driver exists for current thread
     */
    public static boolean hasDriver() {
        return driverThreadLocal.get() != null;
    }
}
