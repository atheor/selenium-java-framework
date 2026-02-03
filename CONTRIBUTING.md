# Contributing to the Framework

## 🎯 Getting Started

Thank you for contributing to the test automation framework! This guide will help you understand how to work with the framework effectively.

## 📋 Prerequisites

Before contributing, ensure you have:
- Java 17 or higher installed
- Maven 3.6+ installed
- An IDE (IntelliJ IDEA, Eclipse, or VS Code)
- Git installed and configured
- Basic understanding of Selenium and TestNG

## 🏗️ Framework Architecture Understanding

**CRITICAL**: Understand the 3-layer architecture before contributing:

```
Layer 3: Tests         → Can ONLY call Workflows
Layer 2: Workflows     → Can ONLY call Page Objects  
Layer 1: Page Objects  → Can ONLY interact with UI elements
```

**Read these first:**
1. README.md - Framework overview
2. ARCHITECTURE.md - Detailed design
3. QUICK_REFERENCE.md - Common patterns

## 📁 Project Structure

```
src/main/java/com/automation/
├── core/           ← DON'T MODIFY (framework internals)
├── pages/          ← Add page objects here
├── workflows/      ← Add workflows here
├── tests/          ← Add tests here
└── cucumber/       ← Add step definitions here
```

## ✅ Code Review Checklist

Before submitting your code, ensure:

### Architecture Compliance
- [ ] Tests only call workflow methods
- [ ] Workflows only call page object methods
- [ ] No direct page object calls from tests
- [ ] Proper inheritance (extends BasePage, BaseWorkflow, BaseTest)

### Element Usage
- [ ] Using `Element` wrapper, not `WebElement`
- [ ] Elements have meaningful names
- [ ] No hardcoded `Thread.sleep()`
- [ ] Proper locator strategies (ID > CSS > XPath)

### Code Quality
- [ ] No hardcoded values (use config.properties)
- [ ] Proper exception handling
- [ ] Adequate logging
- [ ] Javadoc for public methods
- [ ] No code duplication

### Testing
- [ ] Tests are independent
- [ ] Tests can run in parallel
- [ ] Assertions use AssertJ
- [ ] Test names are descriptive
- [ ] Tests clean up after themselves

## 🎨 Coding Standards

### Naming Conventions

```java
// Classes
public class LoginPage extends BasePage { }
public class LoginWorkflow extends BaseWorkflow { }
public class LoginTest extends BaseTest { }

// Methods - use descriptive names
public void enterUsername(String username) { }
public void performLogin(String user, String pass) { }
public void testSuccessfulLogin() { }

// Variables
private final Element submitButton = new Element(...);
private LoginWorkflow loginWorkflow;
```

### Package Structure

```
com.automation.pages        - Page objects
com.automation.workflows    - Workflows
com.automation.tests        - Tests
com.automation.stepdefinitions - Cucumber steps
com.automation.utils        - Project-specific utilities
```

### File Organization

```java
// Page Object Template
public class YourPage extends BasePage {
    // 1. Element declarations (private final)
    private final Element element = new Element(...);
    
    // 2. isPageLoaded() implementation
    @Override
    public boolean isPageLoaded() {
        return element.isDisplayed();
    }
    
    // 3. Page actions (public methods)
    public void clickElement() {
        element.click();
    }
    
    // 4. Helper methods (private if needed)
}
```

## 📝 Writing New Components

### 1. Creating a Page Object

```java
package com.automation.pages;

import com.automation.core.elements.Element;
import com.automation.pages.BasePage;
import org.openqa.selenium.By;

/**
 * Page Object for [Page Name]
 * URL: [page URL]
 */
public class YourPage extends BasePage {
    
    // Define elements with meaningful names
    private final Element username = new Element(
        By.id("username"), 
        "Username Field"
    );
    
    private final Element password = new Element(
        By.id("password"),
        "Password Field"
    );
    
    private final Element loginButton = new Element(
        By.id("login-btn"),
        "Login Button"
    );
    
    /**
     * Verify if the page is loaded
     */
    @Override
    public boolean isPageLoaded() {
        return loginButton.isDisplayed();
    }
    
    /**
     * Navigate to this page (if applicable)
     */
    public void open() {
        navigateTo(config.get("login.url"));
    }
    
    /**
     * Enter username
     */
    public void enterUsername(String user) {
        username.type(user);
    }
    
    /**
     * Enter password
     */
    public void enterPassword(String pass) {
        password.type(pass);
    }
    
    /**
     * Click login button
     */
    public void clickLogin() {
        loginButton.click();
    }
}
```

### 2. Creating a Workflow

```java
package com.automation.workflows;

import com.automation.pages.YourPage;
import com.automation.workflows.BaseWorkflow;

/**
 * Workflow for [Feature Name]
 * Orchestrates page objects to implement business logic
 */
public class YourWorkflow extends BaseWorkflow {
    
    private YourPage yourPage;
    private AnotherPage anotherPage;
    
    public YourWorkflow() {
        super();
        this.yourPage = new YourPage();
        this.anotherPage = new AnotherPage();
    }
    
    /**
     * Perform complete business operation
     */
    public void performBusinessOperation(String param1, String param2) {
        logStep("Performing operation with: " + param1);
        
        // Orchestrate page objects
        yourPage.open();
        yourPage.enterData(param1);
        yourPage.submit();
        
        // Navigate to next page
        anotherPage.verifyResult();
    }
    
    /**
     * Verify operation success
     * @return true if successful
     */
    public boolean verifySuccess() {
        logStep("Verifying operation success");
        return anotherPage.isPageLoaded();
    }
}
```

### 3. Creating a Test

```java
package com.automation.tests;

import com.automation.workflows.YourWorkflow;
import com.automation.tests.BaseTest;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * Test class for [Feature Name]
 */
public class YourTest extends BaseTest {
    
    private YourWorkflow workflow;
    
    @Test(description = "Test description here")
    public void testYourFeature() {
        // Setup
        workflow = new YourWorkflow();
        logInfo("Starting test: testYourFeature");
        
        // Execute
        workflow.performBusinessOperation("value1", "value2");
        
        // Verify
        boolean success = workflow.verifySuccess();
        assertThat(success)
            .as("Operation should be successful")
            .isTrue();
        
        logPass("Test completed successfully");
    }
    
    @Test(description = "Another test", priority = 2)
    public void testAnotherScenario() {
        workflow = new YourWorkflow();
        
        // Test logic
        
        logPass("Test passed");
    }
}
```

### 4. Creating Cucumber Feature & Steps

**Feature File** (`src/test/resources/features/YourFeature.feature`):
```gherkin
Feature: Your Feature Name
  As a user
  I want to perform an action
  So that I achieve a goal

  Background:
    Given the application is running
    And I am logged in

  Scenario: Successful operation
    Given I am on the main page
    When I perform an action
    Then I should see the expected result

  Scenario Outline: Multiple scenarios
    Given I am on the main page
    When I enter "<input>"
    Then I should see "<output>"

    Examples:
      | input   | output  |
      | value1  | result1 |
      | value2  | result2 |
```

**Step Definitions**:
```java
package com.automation.stepdefinitions;

import com.automation.cucumber.BaseStepDefinitions;
import com.automation.workflows.YourWorkflow;
import io.cucumber.java.en.*;
import static org.assertj.core.api.Assertions.*;

public class YourStepDefinitions extends BaseStepDefinitions {
    
    private YourWorkflow workflow = new YourWorkflow();
    
    @Given("the application is running")
    public void theApplicationIsRunning() {
        logStep("Verifying application is running");
        // Implementation
    }
    
    @When("I perform an action")
    public void iPerformAnAction() {
        logStep("Performing action");
        workflow.performBusinessOperation("param1", "param2");
    }
    
    @Then("I should see the expected result")
    public void iShouldSeeTheExpectedResult() {
        logStep("Verifying expected result");
        boolean success = workflow.verifySuccess();
        assertThat(success).isTrue();
    }
}
```

## 🔧 Configuration Guidelines

### Adding New Properties

1. Add to `config.properties`:
```properties
# Your Feature Configuration
your.feature.url=https://example.com
your.feature.timeout=30
```

2. Add getter to ConfigManager:
```java
public String getYourFeatureUrl() {
    return getProperty("your.feature.url", "default-value");
}
```

## 🧪 Testing Your Changes

### Run All Tests
```bash
mvn clean test
```

### Run Specific Test
```bash
mvn test -Dtest=YourTest
```

### Run with Different Browser
```bash
mvn test -Dbrowser=firefox
```

### Verify Build
```bash
mvn clean verify
```

## 📊 Code Quality Standards

### Use AssertJ for Assertions
```java
// ✅ Good
assertThat(actual).isEqualTo(expected);
assertThat(list).hasSize(5).contains("item");

// ❌ Avoid
assertEquals(expected, actual);
assertTrue(condition);
```

### Use Element Wrapper
```java
// ✅ Good
Element button = new Element(By.id("btn"), "Button");
button.click();

// ❌ Avoid
WebElement button = driver.findElement(By.id("btn"));
button.click();
```

### Avoid Hard Waits
```java
// ✅ Good
element.waitForVisible();
element.click();

// ❌ Avoid
Thread.sleep(5000);
element.click();
```

### Use Meaningful Names
```java
// ✅ Good
Element submitOrderButton = new Element(By.id("submit"), "Submit Order Button");

// ❌ Avoid
Element btn1 = new Element(By.id("submit"), "btn");
```

## 🚀 Pull Request Process

1. **Create Feature Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make Changes**
   - Follow coding standards
   - Add tests for new features
   - Update documentation if needed

3. **Test Locally**
   ```bash
   mvn clean test
   ```

4. **Commit Changes**
   ```bash
   git add .
   git commit -m "feat: add new feature"
   ```

5. **Push to Remote**
   ```bash
   git push origin feature/your-feature-name
   ```

6. **Create Pull Request**
   - Provide clear description
   - Reference related issues
   - Wait for code review

## 📋 Commit Message Format

Follow conventional commits:

```
feat: add new login workflow
fix: resolve timeout issue in checkout page
docs: update README with API examples
refactor: simplify user registration workflow
test: add tests for payment feature
chore: update dependencies
```

## 🐛 Reporting Issues

When reporting issues, include:
1. Framework version
2. Java version
3. Browser and version
4. Operating system
5. Steps to reproduce
6. Expected vs actual behavior
7. Relevant logs/screenshots

## ⚠️ What NOT to Do

### Don't Modify Core Framework
```
❌ DON'T modify files in src/main/java/com/automation/core/
✅ DO extend framework using base classes
```

### Don't Break the Architecture
```
❌ DON'T call page objects from tests
✅ DO call workflows from tests
```

### Don't Use WebElement Directly
```
❌ DON'T use WebElement
✅ DO use Element wrapper
```

### Don't Hardcode Values
```
❌ DON'T hardcode URLs, timeouts, test data
✅ DO use config.properties
```

### Don't Create Shared State
```
❌ DON'T use static variables for test data
✅ DO keep tests independent
```

## 💡 Best Practices

### 1. Keep Tests Independent
Each test should be able to run independently and in any order.

### 2. Use Proper Logging
```java
logStep("Performing important action");
logInfo("Additional information");
logPass("Test passed with result: " + result);
```

### 3. Implement Page Load Verification
```java
@Override
public boolean isPageLoaded() {
    return criticalElement.isDisplayed();
}
```

### 4. Use Descriptive Test Names
```java
// ✅ Good
@Test(description = "Verify user can login with valid credentials")
public void testLoginWithValidCredentials() { }

// ❌ Avoid
@Test
public void test1() { }
```

### 5. Add Javadoc for Public Methods
```java
/**
 * Performs user login with provided credentials
 * @param username the username to login with
 * @param password the password to login with
 */
public void performLogin(String username, String password) { }
```

## 📚 Learning Resources

### Internal Documentation
- README.md - Framework overview
- ARCHITECTURE.md - Design details
- QUICK_REFERENCE.md - Common tasks
- Example code in `src/test/java/com/automation/examples/`

### External Resources
- [Selenium Documentation](https://www.selenium.dev/documentation/)
- [TestNG Documentation](https://testng.org/doc/)
- [Cucumber Documentation](https://cucumber.io/docs/cucumber/)
- [AssertJ Documentation](https://assertj.github.io/doc/)

## 🤝 Getting Help

If you need help:
1. Check the documentation files
2. Review example code
3. Ask in team chat
4. Create an issue with details

## ✨ Recognition

Contributors who follow these guidelines and produce quality code will be recognized in:
- Project documentation
- Team meetings
- Code review feedback

---

**Thank you for contributing to the framework!** Your efforts help make testing better for everyone. 🚀
