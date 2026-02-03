# Framework Architecture & Design Document

## 1. Overview

This is a production-ready test automation framework for UI and API testing, built with enterprise-grade patterns and best practices.

### Design Goals
1. **Separation of Concerns**: Strict 3-layer architecture
2. **Maintainability**: Changes in one layer don't affect others
3. **Reusability**: Framework as a standalone library
4. **Scalability**: Thread-safe parallel execution
5. **Reliability**: Auto-waiting and retry mechanisms
6. **Flexibility**: Support for multiple browsers, test types, and runners

## 2. Architecture Layers

### Layer 1: Page Objects
**Purpose**: Encapsulate UI element definitions and basic interactions

**Responsibilities**:
- Define page elements using Element wrapper
- Implement `isPageLoaded()` verification
- Provide simple, atomic actions (click, type, getText)
- No business logic or assertions

**Rules**:
- ✅ Can use Element, WebActions, WebDriver
- ❌ Cannot call other pages
- ❌ Cannot contain assertions
- ❌ Cannot contain business logic

**Example**:
```java
public class LoginPage extends BasePage {
    private final Element username = new Element(By.id("user"), "Username");
    
    public void enterUsername(String user) {
        username.type(user);  // Simple, atomic action
    }
}
```

### Layer 2: Workflows
**Purpose**: Orchestrate page objects and implement business logic

**Responsibilities**:
- Combine multiple page actions into business flows
- Navigate between pages
- Implement user journeys and scenarios
- Provide verification methods (return booleans)
- No assertions (leave to tests)

**Rules**:
- ✅ Can call multiple page objects
- ✅ Can navigate between pages
- ✅ Can implement business logic
- ❌ Cannot be called by other workflows
- ❌ Cannot contain assertions

**Example**:
```java
public class LoginWorkflow extends BaseWorkflow {
    private LoginPage loginPage;
    private DashboardPage dashboard;
    
    public void performLogin(String user, String pass) {
        logStep("Performing login");
        loginPage.enterUsername(user);
        loginPage.enterPassword(pass);
        loginPage.clickLogin();
    }
    
    public boolean verifyLoginSuccess() {
        return dashboard.isPageLoaded();
    }
}
```

### Layer 3: Tests
**Purpose**: Define test scenarios and validate outcomes

**Responsibilities**:
- Call workflow methods only
- Perform assertions
- Handle test data
- Report test results

**Rules**:
- ✅ Can call workflow methods
- ✅ Must contain assertions
- ✅ Can use test data
- ❌ Cannot call page object methods directly
- ❌ Cannot contain business logic

**Example**:
```java
public class LoginTest extends BaseTest {
    private LoginWorkflow workflow = new LoginWorkflow();
    
    @Test
    public void testLogin() {
        workflow.performLogin("user", "pass");
        assertThat(workflow.verifyLoginSuccess()).isTrue();
    }
}
```

## 3. Core Components

### 3.1 Configuration Management

**ConfigManager**
- Singleton pattern for centralized configuration
- Loads from config.properties
- Supports system property overrides
- Thread-safe implementation

**Benefits**:
- Single source of truth for all settings
- Easy to modify timeouts globally
- Environment-specific configurations
- CI/CD integration via system properties

### 3.2 Driver Management

**DriverManager**
- ThreadLocal for thread-safe driver storage
- Automatic driver initialization
- Proper cleanup on test completion
- Supports parallel test execution

**DriverFactory**
- Creates WebDriver instances
- Multi-browser support
- Automatic driver download (WebDriverManager)
- Configures browser options and timeouts

**Benefits**:
- No manual driver setup required
- Parallel execution support out-of-the-box
- Easy to switch browsers
- Centralized driver configuration

### 3.3 Element Wrapper

**Element Class**
- Wraps Selenium WebElement
- Built-in explicit waits for all actions
- Auto-retry on StaleElementReferenceException
- Rich API for element interactions

**Key Features**:
```java
// Auto-waiting on every interaction
element.click();           // Waits for clickable
element.type("text");      // Waits for visible
element.getText();         // Waits for presence

// Explicit waits available
element.waitForVisible();
element.waitForClickable();
element.waitForText("expected");

// Advanced interactions
element.hover();
element.doubleClick();
element.scrollIntoView();
element.jsClick();

// Dropdown support
element.selectByText("option");
element.selectByValue("value");
```

**Benefits**:
- Playwright-like auto-waiting behavior
- Reduces flaky tests
- Cleaner test code
- Handles most sync issues automatically

### 3.4 Actions Layer

**WebActions**
- High-level browser operations
- Navigation, alerts, frames, windows
- JavaScript execution
- Cookie management
- Screenshot capture

**APIClient**
- RESTful API testing support
- All HTTP methods (GET, POST, PUT, PATCH, DELETE)
- Automatic retry with exponential backoff
- JSON serialization/deserialization
- Custom headers support

**Benefits**:
- Consistent API across framework
- Built-in error handling
- Retry logic for flaky APIs
- Easy to use and extend

### 3.5 Base Classes

**BasePage**
- Foundation for all page objects
- Provides WebDriver, WebActions, ConfigManager
- Enforces isPageLoaded() implementation
- PageFactory initialization

**BaseWorkflow**
- Foundation for all workflows
- Provides ConfigManager
- Step logging utilities

**BaseTest**
- Foundation for all tests
- Driver lifecycle management
- ExtentReports integration
- Screenshot on failure
- TestNG hooks

**BaseStepDefinitions**
- Foundation for Cucumber steps
- ScenarioContext access
- Step logging utilities

## 4. Testing Support

### 4.1 TestNG Integration
- Parallel execution at test/class/method level
- Data-driven testing with @DataProvider
- Test dependencies and priorities
- Flexible test suite organization
- XML-based configuration

### 4.2 Cucumber Integration
- BDD-style test writing
- Gherkin feature files
- Step definition organization
- Scenario context for data sharing
- Automatic hooks for setup/teardown
- Parallel scenario execution

### 4.3 API Testing
- Full HTTP client implementation
- Request/response logging
- JSON handling
- Authentication support
- Response validation utilities

## 5. Reporting & Logging

### 5.1 ExtentReports
- HTML reports with screenshots
- Test execution timeline
- Pass/fail statistics
- System information
- Embedded screenshots on failure

### 5.2 Cucumber Reports
- HTML, JSON, XML formats
- Step-by-step execution details
- Scenario pass/fail rates
- Tag-based filtering

### 5.3 Logging
- Log4j2 configuration
- Console and file logging
- Configurable log levels
- Automatic log rotation

## 6. Design Patterns Used

### 6.1 Singleton Pattern
- ConfigManager
- Ensures single instance of configuration

### 6.2 Factory Pattern
- DriverFactory
- Creates different browser instances

### 6.3 Page Object Pattern
- All page classes
- Encapsulates page structure

### 6.4 ThreadLocal Pattern
- DriverManager
- ScenarioContext
- Thread-safe parallel execution

### 6.5 Builder Pattern
- Element configuration
- Fluent API for element interactions

## 7. Best Practices Enforced

### 7.1 Naming Conventions
- Pages: `*Page.java` (e.g., LoginPage)
- Workflows: `*Workflow.java` (e.g., LoginWorkflow)
- Tests: `*Test.java` (e.g., LoginTest)
- Step Definitions: `*StepDefinitions.java`

### 7.2 Code Organization
```
com.automation/
├── core/           # Framework internals (don't modify)
├── pages/          # Page objects (extend BasePage)
├── workflows/      # Business logic (extend BaseWorkflow)
├── tests/          # Test classes (extend BaseTest)
└── cucumber/       # BDD support
```

### 7.3 Element Locator Strategy
1. Prefer ID over other locators
2. Use CSS selectors over XPath when possible
3. Avoid brittle locators (indexes, positions)
4. Use data attributes for test automation
5. Give elements meaningful names

### 7.4 Wait Strategy
1. Use Element wrapper (auto-waiting)
2. Configure timeouts in config.properties
3. Avoid Thread.sleep() (use WaitUtil)
4. Implement proper page load verification

### 7.5 Test Data Management
1. Externalize test data
2. Use properties files or JSON
3. Don't hardcode credentials
4. Use environment-specific data

## 8. Framework Extension Guide

### 8.1 Adding New Browsers
Edit DriverFactory.java:
```java
private static WebDriver createNewBrowser() {
    WebDriverManager.newbrowser().setup();
    return new NewBrowserDriver(options);
}
```

### 8.2 Adding Custom Waits
Extend WaitUtil.java:
```java
public static void waitForCustomCondition() {
    // Implementation
}
```

### 8.3 Adding New Utilities
Create in `core/utils/` package:
```java
public class CustomUtil {
    // Utility methods
}
```

### 8.4 Custom Report Listeners
Implement TestNG listener:
```java
public class CustomListener implements ITestListener {
    // Override methods
}
```

## 9. CI/CD Integration

### 9.1 Jenkins Integration
```groovy
stage('Test') {
    steps {
        sh 'mvn clean test -Dbrowser=chrome -Dheadless=true'
    }
}
```

### 9.2 GitHub Actions
```yaml
- name: Run Tests
  run: mvn test -Dbrowser=firefox
```

### 9.3 Docker Support
```dockerfile
FROM maven:3.8-openjdk-17
COPY . /app
WORKDIR /app
RUN mvn clean install
CMD ["mvn", "test"]
```

## 10. Performance Considerations

### 10.1 Parallel Execution
- Tests are thread-safe
- Use ThreadLocal for driver instances
- Configure in testng.xml

### 10.2 Browser Reuse
- Each test gets fresh browser
- Can be optimized for test classes

### 10.3 Timeout Optimization
- Tune timeouts based on environment
- Lower for fast systems
- Higher for slow CI/CD

## 11. Security Considerations

### 11.1 Credentials Management
- Never hardcode credentials
- Use environment variables
- Consider secret management tools

### 11.2 API Keys
- Store in external configuration
- Use CI/CD secret management
- Rotate regularly

## 12. Maintenance Guidelines

### 12.1 Framework Updates
1. Review dependency versions regularly
2. Test framework changes thoroughly
3. Document breaking changes
4. Version the framework

### 12.2 Code Review Checklist
- [ ] Follows 3-layer architecture
- [ ] Uses Element wrapper
- [ ] No hardcoded waits
- [ ] Proper error handling
- [ ] Meaningful names
- [ ] Adequate logging
- [ ] Tests are independent

## 13. Troubleshooting Guide

### Issue: Tests fail on CI but pass locally
**Possible Causes**:
- Different browser versions
- Different screen resolutions
- Network latency
- Missing dependencies

**Solutions**:
- Use same browser versions
- Run in headless mode
- Increase timeouts
- Check CI logs

### Issue: Parallel tests interfere
**Possible Causes**:
- Shared test data
- Global state
- Browser profile conflicts

**Solutions**:
- Use unique test data
- Avoid shared state
- Ensure ThreadLocal usage

### Issue: Flaky tests
**Possible Causes**:
- Insufficient waits
- Timing issues
- External dependencies

**Solutions**:
- Use Element auto-waiting
- Increase explicit timeouts
- Mock external services

## 14. Success Metrics

### Framework Quality Indicators
- Test execution time < 30min for full suite
- Test flakiness rate < 2%
- Code coverage > 70%
- Build success rate > 95%
- Average bug detection time < 24hr

### Team Adoption Metrics
- Time to write new test < 30min
- Framework learning curve < 1 week
- Code review approval time < 1 day
- Test maintenance time < 10% of test writing

---

**Document Version**: 1.0  
**Last Updated**: 2026-02-04  
**Maintained By**: Test Automation Team
