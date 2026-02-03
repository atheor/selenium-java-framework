package com.automation.cucumber;

import com.automation.core.browser.DriverManager;
import com.automation.core.config.ConfigManager;
import com.automation.core.utils.ScreenshotUtil;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import lombok.extern.slf4j.Slf4j;

/**
 * Cucumber hooks for setup and teardown operations.
 * Manages driver lifecycle and screenshot capture for Cucumber scenarios.
 */
@Slf4j
public class CucumberHooks {
    
    private ConfigManager config;
    
    @Before
    public void beforeScenario(Scenario scenario) {
        config = ConfigManager.getInstance();
        log.info("Starting scenario: {}", scenario.getName());
        
        // Initialize driver
        DriverManager.getDriver();
        
        // Store scenario in context
        ScenarioContext.setScenario(scenario);
    }
    
    @After
    public void afterScenario(Scenario scenario) {
        log.info("Finished scenario: {} - Status: {}", scenario.getName(), scenario.getStatus());
        
        // Capture screenshot on failure
        if (scenario.isFailed() && config.isScreenshotOnFailure()) {
            captureScreenshot(scenario);
        }
        
        // Quit driver
        DriverManager.quitDriver();
        
        // Clear scenario context
        ScenarioContext.clear();
    }
    
    @AfterStep
    public void afterStep(Scenario scenario) {
        // Optionally capture screenshot after each step
        if (config.get("screenshot.on.step", "false").equalsIgnoreCase("true")) {
            captureScreenshot(scenario);
        }
    }
    
    private void captureScreenshot(Scenario scenario) {
        try {
            if (DriverManager.hasDriver()) {
                byte[] screenshot = ScreenshotUtil.captureScreenshotAsBytes(DriverManager.getDriver());
                scenario.attach(screenshot, "image/png", scenario.getName());
                log.info("Screenshot attached to scenario: {}", scenario.getName());
            }
        } catch (Exception e) {
            log.error("Failed to capture screenshot: {}", e.getMessage());
        }
    }
}
