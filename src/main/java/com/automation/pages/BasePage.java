package com.automation.pages;

import com.automation.core.actions.WebActions;
import com.automation.core.browser.DriverManager;
import com.automation.core.config.ConfigManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

/**
 * Base Page Object class that all page objects should extend.
 * Provides common functionality and ensures proper initialization.
 * Pages should only expose methods that represent user actions on that page.
 */
@Slf4j
public abstract class BasePage {
    
    protected WebDriver driver;
    protected WebActions webActions;
    protected ConfigManager config;
    
    public BasePage() {
        this.driver = DriverManager.getDriver();
        this.webActions = new WebActions();
        this.config = ConfigManager.getInstance();
        PageFactory.initElements(driver, this);
        log.debug("Initialized page: {}", this.getClass().getSimpleName());
    }
    
    /**
     * Verify if the page is loaded.
     * Each page should implement this to verify page-specific elements.
     */
    public abstract boolean isPageLoaded();
    
    /**
     * Navigate to the page URL.
     * Override this in pages that can be directly navigated to.
     */
    public void navigateTo(String url) {
        webActions.navigateTo(url);
        log.info("Navigated to: {}", url);
    }
    
    /**
     * Get the page title
     */
    public String getPageTitle() {
        return webActions.getTitle();
    }
    
    /**
     * Get the current URL
     */
    public String getCurrentUrl() {
        return webActions.getCurrentUrl();
    }
    
    /**
     * Refresh the page
     */
    public void refresh() {
        webActions.refresh();
    }
}
