package com.automation.examples.tests;

import com.automation.examples.workflows.LoginWorkflow;
import com.automation.tests.BaseTest;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test Class - Saucedemo Tests
 * Demonstrates the 3-layer architecture where tests only interact with Workflows
 * Tests should NEVER directly call Page Object methods
 */
public class SauceDemoTest extends BaseTest {
    
    private LoginWorkflow loginWorkflow;
    
    @Test(description = "Verify successful login with valid credentials", priority = 1)
    public void testSuccessfulLogin() {
        loginWorkflow = new LoginWorkflow();
        
        logInfo("Starting successful login test");
        
        // Perform login - Test only calls workflow methods
        loginWorkflow.performCompleteLogin("standard_user", "secret_sauce");
        
        // Verify successful login
        boolean loginSuccessful = loginWorkflow.verifySuccessfulLogin();
        assertThat(loginSuccessful)
            .as("User should be logged in successfully")
            .isTrue();
        
        String pageTitle = loginWorkflow.getProductsPageTitle();
        assertThat(pageTitle)
            .as("Products page title should be 'Products'")
            .isEqualTo("Products");
        
        logPass("Login test completed successfully");
    }
    
    @Test(description = "Verify login fails with invalid credentials", priority = 2)
    public void testInvalidLogin() {
        loginWorkflow = new LoginWorkflow();
        
        logInfo("Starting invalid login test");
        
        // Attempt login with invalid credentials
        loginWorkflow.performCompleteLogin("invalid_user", "wrong_password");
        
        // Verify error message is displayed
        boolean errorDisplayed = loginWorkflow.isErrorMessageDisplayed();
        assertThat(errorDisplayed)
            .as("Error message should be displayed")
            .isTrue();
        
        String errorMessage = loginWorkflow.getLoginErrorMessage();
        assertThat(errorMessage)
            .as("Error message should contain 'Username and password do not match'")
            .contains("Username and password do not match");
        
        logPass("Invalid login test completed successfully");
    }
    
    @Test(description = "Verify locked out user cannot login", priority = 3)
    public void testLockedOutUser() {
        loginWorkflow = new LoginWorkflow();
        
        logInfo("Starting locked out user test");
        
        // Attempt login with locked out user
        loginWorkflow.performCompleteLogin("locked_out_user", "secret_sauce");
        
        // Verify error message
        boolean errorDisplayed = loginWorkflow.isErrorMessageDisplayed();
        assertThat(errorDisplayed)
            .as("Error message should be displayed for locked out user")
            .isTrue();
        
        String errorMessage = loginWorkflow.getLoginErrorMessage();
        assertThat(errorMessage)
            .as("Error message should contain 'locked out'")
            .containsIgnoringCase("locked out");
        
        logPass("Locked out user test completed successfully");
    }
    
    @Test(description = "Verify product count on products page", priority = 4)
    public void testProductCount() {
        loginWorkflow = new LoginWorkflow();
        
        logInfo("Starting product count test");
        
        // Login and navigate to products page
        loginWorkflow.performCompleteLogin("standard_user", "secret_sauce");
        
        // Verify products are displayed
        int productCount = loginWorkflow.getProductCount();
        assertThat(productCount)
            .as("Product count should be 6")
            .isEqualTo(6);
        
        logPass("Product count: " + productCount);
    }
    
    @Test(description = "Verify adding product to cart", priority = 5)
    public void testAddToCart() {
        loginWorkflow = new LoginWorkflow();
        
        logInfo("Starting add to cart test");
        
        // Login
        loginWorkflow.performCompleteLogin("standard_user", "secret_sauce");
        
        // Add product to cart
        loginWorkflow.addProductToCart();
        
        // Verify cart badge is displayed
        boolean cartBadgeDisplayed = loginWorkflow.isCartBadgeDisplayed();
        assertThat(cartBadgeDisplayed)
            .as("Cart badge should be displayed after adding product")
            .isTrue();
        
        String cartCount = loginWorkflow.getCartItemCount();
        assertThat(cartCount)
            .as("Cart count should be '1'")
            .isEqualTo("1");
        
        logPass("Add to cart test completed successfully");
    }
}
