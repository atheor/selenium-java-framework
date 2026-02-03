package com.automation.tests;

import com.automation.core.browser.DriverManager;
import com.automation.core.config.ConfigManager;
import com.automation.core.utils.ScreenshotUtil;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Base Test class that all test classes should extend.
 * Handles driver lifecycle, reporting, and screenshot capture.
 * Tests should only interact with Workflows, never directly with Page Objects.
 */
@Slf4j
public abstract class BaseTest {
    
    protected ConfigManager config;
    protected static ExtentReports extent;
    protected static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    
    @BeforeSuite(alwaysRun = true)
    public void setupSuite() {
        config = ConfigManager.getInstance();
        setupReporting();
        log.info("Test suite started");
    }
    
    @BeforeClass(alwaysRun = true)
    public void setupClass() {
        log.info("Starting test class: {}", this.getClass().getSimpleName());
    }
    
    @BeforeMethod(alwaysRun = true)
    public void setupTest(Method method) {
        log.info("Starting test: {}", method.getName());
        
        // Create ExtentTest for this test method
        ExtentTest test = extent.createTest(method.getName());
        extentTest.set(test);
        
        // Initialize driver for this test
        DriverManager.getDriver();
    }
    
    @AfterMethod(alwaysRun = true)
    public void tearDownTest(ITestResult result) {
        ExtentTest test = extentTest.get();
        
        if (result.getStatus() == ITestResult.FAILURE) {
            log.error("Test failed: {}", result.getName());
            test.log(Status.FAIL, "Test Failed: " + result.getThrowable());
            
            if (config != null && config.isScreenshotOnFailure()) {
                captureScreenshot(result.getName());
            }
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            log.info("Test passed: {}", result.getName());
            test.log(Status.PASS, "Test Passed");
            
            if (config != null && config.isScreenshotOnSuccess()) {
                captureScreenshot(result.getName());
            }
        } else if (result.getStatus() == ITestResult.SKIP) {
            log.warn("Test skipped: {}", result.getName());
            test.log(Status.SKIP, "Test Skipped: " + result.getThrowable());
        }
        
        // Quit driver after test
        DriverManager.quitDriver();
        log.info("Finished test: {}", result.getName());
    }
    
    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        log.info("Finished test class: {}", this.getClass().getSimpleName());
    }
    
    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        if (extent != null) {
            extent.flush();
        }
        log.info("Test suite completed");
    }
    
    private void setupReporting() {
        String reportPath = config.getReportPath();
        new File(reportPath).mkdirs();
        
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath + "/ExtentReport.html");
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setDocumentTitle(config.getReportTitle());
        sparkReporter.config().setReportName(config.getReportName());
        sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
        
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Environment", config.getEnvironment());
        extent.setSystemInfo("Browser", config.getBrowser());
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        
        log.info("Reporting configured");
    }
    
    private void captureScreenshot(String testName) {
        try {
            String screenshotBase64 = ScreenshotUtil.captureScreenshotAsBase64(DriverManager.getDriver());
            if (!screenshotBase64.isEmpty()) {
                extentTest.get().addScreenCaptureFromBase64String(screenshotBase64, testName);
                log.info("Screenshot captured for test: {}", testName);
            }
        } catch (Exception e) {
            log.error("Failed to capture screenshot: {}", e.getMessage());
        }
    }
    
    /**
     * Log a message to the test report
     */
    protected void logInfo(String message) {
        extentTest.get().log(Status.INFO, message);
        log.info(message);
    }
    
    /**
     * Log a pass message to the test report
     */
    protected void logPass(String message) {
        extentTest.get().log(Status.PASS, message);
        log.info(message);
    }
    
    /**
     * Log a fail message to the test report
     */
    protected void logFail(String message) {
        extentTest.get().log(Status.FAIL, message);
        log.error(message);
    }
}
