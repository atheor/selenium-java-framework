package com.automation.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for capturing screenshots during test execution.
 */
@Slf4j
public class ScreenshotUtil {
    
    private static final String SCREENSHOT_DIR = "test-output/screenshots";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    
    static {
        createScreenshotDirectory();
    }
    
    private static void createScreenshotDirectory() {
        try {
            Path path = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Created screenshot directory: {}", SCREENSHOT_DIR);
            }
        } catch (IOException e) {
            log.error("Failed to create screenshot directory: {}", e.getMessage());
        }
    }
    
    /**
     * Take screenshot and save to file
     */
    public static String captureScreenshot(WebDriver driver, String testName) {
        try {
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            File source = screenshot.getScreenshotAs(OutputType.FILE);
            
            String timestamp = dateFormat.format(new Date());
            String fileName = String.format("%s_%s.png", testName, timestamp);
            Path destination = Paths.get(SCREENSHOT_DIR, fileName);
            
            Files.copy(source.toPath(), destination);
            log.info("Screenshot saved: {}", destination);
            
            return destination.toString();
        } catch (Exception e) {
            log.error("Failed to capture screenshot: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Take screenshot and return as byte array
     */
    public static byte[] captureScreenshotAsBytes(WebDriver driver) {
        try {
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            return screenshot.getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            log.error("Failed to capture screenshot as bytes: {}", e.getMessage());
            return new byte[0];
        }
    }
    
    /**
     * Take screenshot and return as Base64 string
     */
    public static String captureScreenshotAsBase64(WebDriver driver) {
        try {
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            return screenshot.getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            log.error("Failed to capture screenshot as Base64: {}", e.getMessage());
            return "";
        }
    }
}
