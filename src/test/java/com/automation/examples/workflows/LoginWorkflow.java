package com.automation.examples.workflows;

import com.automation.examples.pages.LoginPage;
import com.automation.examples.pages.ProductsPage;
import com.automation.workflows.BaseWorkflow;

/**
 * Workflow - Saucedemo Login Operations
 * Demonstrates the 3-layer architecture: Test → Workflow → Page Objects
 * Contains business logic for login scenarios
 */
public class LoginWorkflow extends BaseWorkflow {
    
    private LoginPage loginPage;
    private ProductsPage productsPage;
    
    public LoginWorkflow() {
        super();
        this.loginPage = new LoginPage();
        this.productsPage = new ProductsPage();
    }
    
    /**
     * Navigate to login page
     */
    public void navigateToLogin() {
        logStep("Navigating to Saucedemo login page");
        loginPage.open();
        verifyLoginPageLoaded();
    }
    
    /**
     * Verify login page is loaded
     */
    public boolean verifyLoginPageLoaded() {
        logStep("Verifying login page is loaded");
        return loginPage.isPageLoaded();
    }
    
    /**
     * Perform login with credentials
     */
    public void login(String username, String password) {
        logStep("Logging in with username: " + username);
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLogin();
    }
    
    /**
     * Complete login flow from navigation to products page
     */
    public void performCompleteLogin(String username, String password) {
        navigateToLogin();
        login(username, password);
    }
    
    /**
     * Verify successful login by checking products page
     */
    public boolean verifySuccessfulLogin() {
        logStep("Verifying successful login");
        return productsPage.isPageLoaded();
    }
    
    /**
     * Get error message from failed login
     */
    public String getLoginErrorMessage() {
        logStep("Getting login error message");
        return loginPage.getErrorMessage();
    }
    
    /**
     * Check if error message is displayed
     */
    public boolean isErrorMessageDisplayed() {
        logStep("Checking if error message is displayed");
        return loginPage.isErrorDisplayed();
    }
    
    /**
     * Verify products page is loaded after login
     */
    public boolean verifyProductsPageLoaded() {
        logStep("Verifying products page is loaded");
        return productsPage.isPageLoaded();
    }
    
    /**
     * Get products page title
     */
    public String getProductsPageTitle() {
        logStep("Getting products page title");
        return productsPage.getPageTitle();
    }
    
    /**
     * Get count of products on page
     */
    public int getProductCount() {
        logStep("Getting product count");
        return productsPage.getProductCount();
    }
    
    /**
     * Add product to cart
     */
    public void addProductToCart() {
        logStep("Adding first product to cart");
        productsPage.addFirstProductToCart();
    }
    
    /**
     * Get cart item count
     */
    public String getCartItemCount() {
        logStep("Getting cart item count");
        return productsPage.getCartBadgeCount();
    }
    
    /**
     * Verify cart badge is displayed
     */
    public boolean isCartBadgeDisplayed() {
        logStep("Verifying cart badge is displayed");
        return productsPage.isCartBadgeDisplayed();
    }
    
    /**
     * Logout from application
     */
    public void logout() {
        logStep("Logging out");
        productsPage.clickLogout();
    }
}
