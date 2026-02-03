Feature: Saucedemo Login Functionality
  As a user of Saucedemo
  I want to login to the application
  So that I can access the products

  Background:
    Given I am on the Saucedemo login page

  Scenario: Successful login with valid credentials
    When I login with username "standard_user" and password "secret_sauce"
    Then I should be on the products page
    And the page title should be "Products"

  Scenario: Failed login with invalid credentials
    When I login with username "invalid_user" and password "wrong_password"
    Then I should see an error message
    And the error message should contain "Username and password do not match"

  Scenario: Locked out user cannot login
    When I login with username "locked_out_user" and password "secret_sauce"
    Then I should see an error message
    And the error message should contain "locked out"

  Scenario: Verify product count after successful login
    When I login with username "standard_user" and password "secret_sauce"
    Then I should be on the products page
    And I should see 6 products displayed

  Scenario Outline: Login with different user types
    When I login with username "<username>" and password "secret_sauce"
    Then I should be on the products page

    Examples:
      | username                |
      | standard_user           |
      | problem_user            |
      | performance_glitch_user |
