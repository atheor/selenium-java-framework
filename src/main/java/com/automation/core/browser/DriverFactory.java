package com.automation.core.browser;

import com.automation.core.config.ConfigManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.time.Duration;

/**
 * Factory class for creating WebDriver instances with proper configuration.
 * Supports Chrome, Firefox, Edge, and Safari browsers.
 */
@Slf4j
public class DriverFactory {
    
    private static final ConfigManager config = ConfigManager.getInstance();
    
    /**
     * Create a new WebDriver instance based on configuration
     */
    public static WebDriver createDriver() {
        String browser = config.getBrowser().toLowerCase();
        log.info("Creating {} driver instance", browser);
        
        WebDriver driver = switch (browser) {
            case "chrome" -> createChromeDriver();
            case "firefox" -> createFirefoxDriver();
            case "edge" -> createEdgeDriver();
            case "safari" -> createSafariDriver();
            default -> {
                log.warn("Unknown browser: {}. Defaulting to Chrome", browser);
                yield createChromeDriver();
            }
        };
        
        configureDriver(driver);
        return driver;
    }
    
    private static WebDriver createChromeDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        
        if (config.isHeadless()) {
            options.addArguments("--headless=new");
        }
        
        // Common Chrome options for stability
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-extensions");
        options.addArguments("--remote-allow-origins=*");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        
        return new ChromeDriver(options);
    }
    
    private static WebDriver createFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        
        if (config.isHeadless()) {
            options.addArguments("--headless");
        }
        
        options.addArguments("--disable-gpu");
        options.addPreference("dom.webdriver.enabled", false);
        options.addPreference("useAutomationExtension", false);
        
        return new FirefoxDriver(options);
    }
    
    private static WebDriver createEdgeDriver() {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        
        if (config.isHeadless()) {
            options.addArguments("--headless=new");
        }
        
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        
        return new EdgeDriver(options);
    }
    
    private static WebDriver createSafariDriver() {
        SafariOptions options = new SafariOptions();
        options.setAutomaticInspection(false);
        
        return new SafariDriver(options);
    }
    
    /**
     * Configure driver with timeouts and window settings
     */
    private static void configureDriver(WebDriver driver) {
        // Set timeouts
        driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(config.getImplicitTimeout()));
        driver.manage().timeouts()
                .pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));
        driver.manage().timeouts()
                .scriptTimeout(Duration.ofSeconds(config.getScriptTimeout()));
        
        // Maximize window if configured
        if (config.shouldMaximizeBrowser()) {
            driver.manage().window().maximize();
        }
        
        // Delete cookies if configured
        if (config.shouldDeleteCookies()) {
            driver.manage().deleteAllCookies();
        }
        
        log.info("Driver configured successfully");
    }
}
