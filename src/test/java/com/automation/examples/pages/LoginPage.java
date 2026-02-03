package com.automation.examples.pages;

import com.automation.core.elements.Element;
import com.automation.pages.BasePage;
import org.openqa.selenium.By;

/**
 * Page Object - Saucedemo Login Page
 * Demonstrates element locators and page interactions
 */
public class LoginPage extends BasePage {
    
    // Page Elements using Element wrapper with auto-waiting
    private final Element usernameField = new Element(By.id("user-name"), "Username Field");
    private final Element passwordField = new Element(By.id("password"), "Password Field");
    private final Element loginButton = new Element(By.id("login-button"), "Login Button");
    private final Element errorMessage = new Element(By.cssSelector(".error-message-container"), "Error Message");
    private final Element sauceLogo = new Element(By.className("login_logo"), "Sauce Labs Logo");
    
    /**
     * Navigate to Saucedemo login page
     */
    public void open() {
        String url = config.get("saucedemo.url", "https://www.saucedemo.com");
        navigateTo(url);
    }
    
    /**
     * Verify if login page is loaded
     */
    @Override
    public boolean isPageLoaded() {
        return usernameField.isDisplayed() && loginButton.isDisplayed();
    }
    
    /**
     * Enter username
     */
    public void enterUsername(String username) {
        usernameField.type(username);
    }
    
    /**
     * Enter password
     */
    public void enterPassword(String password) {
        passwordField.type(password);
    }
    
    /**
     * Click login button
     */
    public void clickLogin() {
        loginButton.click();
    }
    
    /**
     * Get error message text
     */
    public String getErrorMessage() {
        return errorMessage.getText();
    }
    
    /**
     * Check if error message is displayed
     */
    public boolean isErrorDisplayed() {
        return errorMessage.isDisplayed();
    }
    
    /**
     * Clear username field
     */
    public void clearUsername() {
        usernameField.clear();
    }
    
    /**
     * Clear password field
     */
    public void clearPassword() {
        passwordField.clear();
    }
}
