package com.automation.examples.stepdefinitions;

import com.automation.cucumber.BaseStepDefinitions;
import com.automation.examples.workflows.LoginWorkflow;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step Definitions for Saucedemo Login Tests
 * Step definitions should interact with Workflows, not Page Objects directly
 */
public class LoginStepDefinitions extends BaseStepDefinitions {
    
    private LoginWorkflow loginWorkflow;
    
    public LoginStepDefinitions() {
        super();
        this.loginWorkflow = new LoginWorkflow();
    }
    
    @Given("I am on the Saucedemo login page")
    public void iAmOnTheSaucedemoLoginPage() {
        logStep("Navigating to Saucedemo login page");
        loginWorkflow.navigateToLogin();
    }
    
    @When("I login with username {string} and password {string}")
    public void iLoginWithUsernameAndPassword(String username, String password) {
        logStep(String.format("Logging in with username: %s", username));
        loginWorkflow.login(username, password);
        storeInContext("username", username);
    }
    
    @Then("I should be on the products page")
    public void iShouldBeOnTheProductsPage() {
        logStep("Verifying products page is displayed");
        boolean productsPageLoaded = loginWorkflow.verifyProductsPageLoaded();
        assertThat(productsPageLoaded)
            .as("Products page should be displayed")
            .isTrue();
    }
    
    @Then("the page title should be {string}")
    public void thePageTitleShouldBe(String expectedTitle) {
        logStep("Verifying page title is: " + expectedTitle);
        String actualTitle = loginWorkflow.getProductsPageTitle();
        assertThat(actualTitle)
            .as("Page title should match")
            .isEqualTo(expectedTitle);
    }
    
    @Then("I should see an error message")
    public void iShouldSeeAnErrorMessage() {
        logStep("Verifying error message is displayed");
        boolean errorDisplayed = loginWorkflow.isErrorMessageDisplayed();
        assertThat(errorDisplayed)
            .as("Error message should be displayed")
            .isTrue();
    }
    
    @Then("the error message should contain {string}")
    public void theErrorMessageShouldContain(String expectedText) {
        logStep("Verifying error message contains: " + expectedText);
        String errorMessage = loginWorkflow.getLoginErrorMessage();
        assertThat(errorMessage)
            .as("Error message should contain expected text")
            .containsIgnoringCase(expectedText);
    }
    
    @Then("I should see {int} products displayed")
    public void iShouldSeeProductsDisplayed(int expectedCount) {
        logStep("Verifying product count is: " + expectedCount);
        int actualCount = loginWorkflow.getProductCount();
        assertThat(actualCount)
            .as("Product count should match")
            .isEqualTo(expectedCount);
    }
}
