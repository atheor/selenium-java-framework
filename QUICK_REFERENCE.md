# Framework Quick Reference Guide

## 🎯 Architecture Overview

```
Test Layer (Tests)
    ↓ (calls only)
Workflow Layer (Business Logic)
    ↓ (calls only)
Page Object Layer (UI Elements)
```

## 📁 Key Files & Locations

### Configuration
- **Main Config**: `src/main/resources/config.properties`
- **Logging**: `src/main/resources/log4j2.xml`
- **TestNG Suite**: `src/test/resources/testng.xml`

### Core Framework (`src/main/java/com/automation/`)
```
core/
├── actions/WebActions.java          # Browser actions (alerts, frames, navigation)
├── api/APIClient.java               # HTTP client for API testing
├── api/APIResponse.java             # API response wrapper
├── browser/DriverManager.java       # Thread-safe driver management
├── browser/DriverFactory.java       # Multi-browser support
├── config/ConfigManager.java        # Centralized configuration
├── elements/Element.java            # Auto-waiting element wrapper
└── utils/
    ├── ScreenshotUtil.java          # Screenshot utilities
    └── WaitUtil.java                # Wait and retry utilities

pages/BasePage.java                  # Base class for page objects
workflows/BaseWorkflow.java          # Base class for workflows
tests/BaseTest.java                  # Base class for tests

cucumber/
├── CucumberHooks.java               # Scenario setup/teardown
├── ScenarioContext.java             # Thread-safe data sharing
└── BaseStepDefinitions.java         # Base for step definitions
```

## 🔧 Common Tasks

### 1. Create a New Page Object

```java
package com.automation.pages;

import com.automation.core.elements.Element;
import com.automation.pages.BasePage;
import org.openqa.selenium.By;

public class YourPage extends BasePage {
    
    // Define elements
    private final Element yourElement = new Element(
        By.id("element-id"), 
        "Element Name"
    );
    
    @Override
    public boolean isPageLoaded() {
        return yourElement.isDisplayed();
    }
    
    // Add page-specific methods
    public void clickElement() {
        yourElement.click();
    }
}
```

### 2. Create a New Workflow

```java
package com.automation.workflows;

import com.automation.pages.YourPage;
import com.automation.workflows.BaseWorkflow;

public class YourWorkflow extends BaseWorkflow {
    
    private YourPage yourPage;
    
    public YourWorkflow() {
        super();
        this.yourPage = new YourPage();
    }
    
    public void performAction() {
        logStep("Performing action");
        yourPage.clickElement();
        // Add business logic here
    }
}
```

### 3. Create a New Test

```java
package com.automation.tests;

import com.automation.workflows.YourWorkflow;
import com.automation.tests.BaseTest;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class YourTest extends BaseTest {
    
    private YourWorkflow workflow;
    
    @Test
    public void testYourFeature() {
        workflow = new YourWorkflow();
        
        logInfo("Starting test");
        workflow.performAction();
        
        // Add assertions
        logPass("Test completed");
    }
}
```

### 4. Create API Test

```java
import com.automation.core.api.APIClient;
import com.automation.core.api.APIResponse;

public class YourAPITest {
    
    private APIClient apiClient = new APIClient();
    
    @Test
    public void testAPI() {
        apiClient.setBaseUrl("https://api.example.com");
        
        APIResponse response = apiClient.get("/endpoint");
        
        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.getStatusCode()).isEqualTo(200);
    }
}
```

### 5. Create Cucumber Feature

```gherkin
Feature: Your Feature
  
  Scenario: Your Scenario
    Given precondition
    When action
    Then expected result
```

### 6. Create Step Definitions

```java
package com.automation.stepdefinitions;

import com.automation.cucumber.BaseStepDefinitions;
import com.automation.workflows.YourWorkflow;
import io.cucumber.java.en.*;

public class YourStepDefinitions extends BaseStepDefinitions {
    
    private YourWorkflow workflow = new YourWorkflow();
    
    @Given("precondition")
    public void precondition() {
        logStep("Setting up precondition");
        // Implementation
    }
    
    @When("action")
    public void action() {
        logStep("Performing action");
        workflow.performAction();
    }
    
    @Then("expected result")
    public void expectedResult() {
        logStep("Verifying result");
        // Assertions
    }
}
```

## 🎨 Element Interaction Examples

```java
Element element = new Element(By.id("my-id"), "My Element");

// Basic interactions (with auto-waiting)
element.click();
element.type("text");
element.clear();

// Get information
String text = element.getText();
String attr = element.getAttribute("class");
boolean visible = element.isDisplayed();

// Advanced actions
element.hover();
element.doubleClick();
element.scrollIntoView();
element.jsClick();

// Dropdowns
element.selectByText("Option Text");
element.selectByValue("option-value");
element.selectByIndex(0);

// Explicit waits
element.waitForVisible();
element.waitForClickable();
element.waitForText("expected text");
```

## 🌐 WebActions Examples

```java
WebActions actions = new WebActions();

// Navigation
actions.navigateTo("https://example.com");
actions.refresh();
actions.back();
actions.forward();

// Scrolling
actions.scrollToTop();
actions.scrollToBottom();
actions.scrollBy(0, 500);

// Alerts
actions.acceptAlert();
actions.dismissAlert();
String alertText = actions.getAlertText();

// Windows/Tabs
actions.switchToNewWindow();
actions.closeCurrentWindow();
actions.openNewTab();

// Frames
actions.switchToFrame("frame-name");
actions.switchToDefaultContent();

// Cookies
actions.addCookie(cookie);
actions.deleteAllCookies();

// Screenshots
byte[] screenshot = actions.takeScreenshot();
```

## 🔌 API Testing Examples

```java
APIClient client = new APIClient();
client.setBaseUrl("https://api.example.com");

// GET request
APIResponse response = client.get("/users");

// POST request
Map<String, Object> body = new HashMap<>();
body.put("name", "John");
APIResponse response = client.post("/users", body);

// PUT request
client.put("/users/1", updateData);

// DELETE request
client.delete("/users/1");

// With custom headers
Map<String, String> headers = new HashMap<>();
headers.put("Authorization", "Bearer token");
client.get("/secure-endpoint", headers);

// Response handling
int status = response.getStatusCode();
String body = response.getBody();
boolean success = response.isSuccessful();
User user = response.getBodyAs(User.class);
```

## ⚙️ Configuration Properties

```properties
# Browser
browser=chrome|firefox|edge|safari
headless=true|false
browser.maximize=true|false

# Timeouts (seconds)
timeout.implicit=10
timeout.explicit=20
timeout.page.load=30
wait.element.visible=20
wait.element.clickable=15

# API
api.base.url=https://api.example.com
api.timeout=30000
api.retry.count=3

# Reporting
screenshot.on.failure=true
screenshot.on.success=false
report.path=test-output/reports
```

## 🚀 Running Tests - Command Line

```bash
# All tests
mvn test

# Specific class
mvn test -Dtest=YourTest

# Specific method
mvn test -Dtest=YourTest#testMethod

# With system properties
mvn test -Dbrowser=firefox -Dheadless=true

# Cucumber tests
mvn test -Dtest=CucumberRunner

# Clean and test
mvn clean test

# Skip tests
mvn clean install -DskipTests
```

## 📊 Assertions (AssertJ)

```java
import static org.assertj.core.api.Assertions.*;

// Basic assertions
assertThat(actual).isEqualTo(expected);
assertThat(actual).isNotNull();
assertThat(text).contains("substring");
assertThat(number).isGreaterThan(10);
assertThat(list).hasSize(5);
assertThat(value).isTrue();

// With descriptions
assertThat(actual)
    .as("Descriptive message")
    .isEqualTo(expected);
```

## 🐛 Debugging Tips

1. **Enable verbose logging**: Set `log.level=DEBUG` in config.properties
2. **Take screenshots**: Enable `screenshot.on.success=true`
3. **Check reports**: Look in `test-output/reports/ExtentReport.html`
4. **View logs**: Check `test-output/logs/automation.log`
5. **Use breakpoints**: Debug tests in IDE
6. **Highlight elements**: Use `webActions.highlightElement(element)`

## 📝 Best Practices Checklist

- [ ] Tests only call Workflows
- [ ] Workflows only call Page Objects
- [ ] Elements use meaningful names
- [ ] All pages implement `isPageLoaded()`
- [ ] Use Element wrapper, not WebElement
- [ ] Assertions only in tests
- [ ] Log important steps
- [ ] Handle waits properly
- [ ] Clean up resources (quit driver)
- [ ] Use proper naming conventions

## 🆘 Common Issues

### Issue: Element not found
**Solution**: 
- Verify locator is correct
- Check if element is in iframe
- Increase timeout in config.properties
- Use `element.waitForVisible()`

### Issue: StaleElementReferenceException
**Solution**:
- Re-find element after page refresh
- Use Element wrapper (handles this automatically)

### Issue: Tests fail randomly
**Solution**:
- Increase waits in config.properties
- Use proper synchronization
- Check for race conditions

### Issue: Driver not starting
**Solution**:
- Check browser is installed
- WebDriverManager will auto-download driver
- Verify Java version (17+)

## 📚 Additional Resources

- Framework code documentation (Javadoc in classes)
- Example tests in `src/test/java/com/automation/examples/`
- TestNG documentation: https://testng.org/
- Cucumber documentation: https://cucumber.io/
- Selenium documentation: https://www.selenium.dev/

---
**For more details, see README.md**
