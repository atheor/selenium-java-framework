# Selenium Java Test Automation Framework

A production-ready, modular test automation framework built with Selenium, Java, TestNG, and Cucumber. Designed with separation of concerns and a clean 3-layer architecture.

## 🏗️ Architecture

The framework follows a strict **3-layer architecture** to enforce separation of concerns:

```
┌─────────────────────────────────────────────┐
│              TEST LAYER                      │
│  (Tests interact only with Workflows)        │
└─────────────────┬───────────────────────────┘
                  │
┌─────────────────▼───────────────────────────┐
│           WORKFLOW LAYER                     │
│  (Business logic & orchestration)            │
│  (Workflows interact with Page Objects)      │
└─────────────────┬───────────────────────────┘
                  │
┌─────────────────▼───────────────────────────┐
│         PAGE OBJECT LAYER                    │
│  (UI element definitions & basic actions)    │
└─────────────────────────────────────────────┘
```

### Key Principles:
- ✅ **Tests** → Can call **Workflows** only
- ✅ **Workflows** → Can call **Page Objects** only
- ❌ **Tests** → Cannot call **Page Objects** directly

## 🚀 Features

### Core Capabilities
- ✨ **Selenium WebDriver** - UI automation with auto-waiting (Playwright-like behavior)
- 🔌 **API Testing** - HTTP Client for RESTful API testing
- 🥒 **Cucumber BDD** - Behavior-driven development support
- 📊 **TestNG** - Powerful test orchestration and parallel execution
- 📝 **ExtentReports** - Beautiful HTML test reports
- 🎯 **Centralized Configuration** - Single place to manage timeouts, URLs, etc.

### Framework Components

#### 1. Configuration Management
- `ConfigManager` - Centralized configuration with property override support
- All timeouts, URLs, and settings in one place
- Environment-specific configurations
- System property override for CI/CD

#### 2. Browser Management
- `DriverManager` - Thread-safe WebDriver lifecycle management
- `DriverFactory` - Multi-browser support (Chrome, Firefox, Edge, Safari)
- Automatic driver setup with WebDriverManager
- Parallel execution support with ThreadLocal

#### 3. Element Wrapper
- `Element` class - Enhanced WebElement with built-in waits
- Auto-waiting for all interactions (click, type, etc.)
- Retry logic for flaky elements
- Rich API for all element interactions

#### 4. Actions & Utilities
- `WebActions` - High-level browser actions (navigation, alerts, frames, etc.)
- `APIClient` - RESTful API testing with retry logic
- `ScreenshotUtil` - Screenshot capture utilities
- `WaitUtil` - Custom wait and retry utilities

#### 5. Base Classes
- `BasePage` - Foundation for all page objects
- `BaseWorkflow` - Foundation for all workflows
- `BaseTest` - Foundation for all tests with reporting integration
- `BaseStepDefinitions` - Foundation for Cucumber step definitions

#### 6. Cucumber Integration
- `CucumberHooks` - Setup/teardown for scenarios
- `ScenarioContext` - Thread-safe context sharing between steps
- `CucumberRunner` - TestNG runner with parallel execution

## 📦 Project Structure

```
selenium-java-framework/
├── src/
│   ├── main/
│   │   ├── java/com/automation/
│   │   │   ├── core/
│   │   │   │   ├── actions/        # WebActions
│   │   │   │   ├── api/            # APIClient, APIResponse
│   │   │   │   ├── browser/        # DriverManager, DriverFactory
│   │   │   │   ├── config/         # ConfigManager
│   │   │   │   ├── elements/       # Element wrapper
│   │   │   │   └── utils/          # Utilities
│   │   │   ├── cucumber/           # Cucumber support
│   │   │   ├── pages/              # BasePage
│   │   │   ├── tests/              # BaseTest
│   │   │   └── workflows/          # BaseWorkflow
│   │   └── resources/
│   │       ├── config.properties   # Framework configuration
│   │       └── log4j2.xml          # Logging configuration
│   └── test/
│       ├── java/com/automation/
│       │   └── examples/           # Example implementations
│       │       ├── api/            # API test examples
│       │       ├── pages/          # Page object examples
│       │       ├── stepdefinitions/ # Cucumber step definitions
│       │       ├── tests/          # Test examples
│       │       └── workflows/      # Workflow examples
│       └── resources/
│           ├── features/           # Cucumber feature files
│           └── testng.xml          # TestNG suite configuration
├── test-output/                    # Test results, reports, screenshots
├── pom.xml                         # Maven dependencies
└── README.md                       # This file
```

## 🛠️ Setup & Installation

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Chrome/Firefox/Edge browser installed

### Installation
1. Clone or download the framework
2. Install dependencies:
```bash
mvn clean install
```

## 🎯 Usage Examples

See the `src/test/java/com/automation/examples/` directory for complete examples including:
- Page Objects (GoogleHomePage, GoogleSearchResultsPage)
- Workflows (GoogleSearchWorkflow)
- Tests (GoogleSearchTest)
- API Tests (ExampleAPITest)
- Cucumber Features and Step Definitions

## ⚙️ Configuration

Edit `src/main/resources/config.properties`:

```properties
# Browser Configuration
browser=chrome
headless=false

# Timeouts (in seconds)
timeout.implicit=10
timeout.explicit=20
wait.element.clickable=15

# API Configuration
api.base.url=https://api.example.com

# Test Configuration
screenshot.on.failure=true
```

Override properties via system properties:
```bash
mvn test -Dbrowser=firefox -Dheadless=true
```

## 🏃 Running Tests

### Run all tests:
```bash
mvn test
```

### Run specific test class:
```bash
mvn test -Dtest=GoogleSearchTest
```

### Run with custom browser:
```bash
mvn test -Dbrowser=firefox
```

### Run in headless mode:
```bash
mvn test -Dheadless=true
```

### Run Cucumber tests:
```bash
mvn test -Dtest=CucumberRunner
```

### Run with parallel execution:
Edit `testng.xml` to set `parallel="tests"` or `parallel="methods"`

## 📊 Reports

After test execution, reports are available at:
- **ExtentReports**: `test-output/reports/ExtentReport.html`
- **Cucumber Reports**: `test-output/cucumber-reports/cucumber.html`
- **Screenshots**: `test-output/screenshots/`
- **Logs**: `test-output/logs/automation.log`

## 🔧 Advanced Features

### Auto-Waiting (Playwright-like)
All element interactions have built-in waits:
```java
element.click();  // Automatically waits for clickable
element.type("text");  // Automatically waits for visible
```

### Centralized Timeout Management
Change timeouts in one place (`config.properties`):
```properties
timeout.explicit=20
wait.element.clickable=15
```

### Parallel Execution
Thread-safe design with ThreadLocal:
```xml
<suite parallel="tests" thread-count="3">
```

### Retry Logic
Built-in retry for API calls:
```java
// Configured in config.properties
api.retry.count=3
```

### Screenshot on Failure
Automatic screenshot capture:
```properties
screenshot.on.failure=true
```

## 📚 Best Practices

1. **Follow the 3-layer architecture strictly**
   - Tests → Workflows → Pages
   
2. **Use meaningful element names**
   ```java
   new Element(By.id("submit"), "Submit Button")
   ```

3. **Implement `isPageLoaded()` in all pages**
   ```java
   @Override
   public boolean isPageLoaded() {
       return keyElement.isDisplayed();
   }
   ```

4. **Keep workflows business-focused**
   - Workflows should represent user journeys
   - Keep page objects technical

5. **Use assertions in tests, not in workflows or pages**

6. **Leverage the Element wrapper**
   - Don't use WebElement directly
   - Use Element for auto-waiting

## 🔌 Using as a Library

This framework is designed to be a standalone, reusable library. To use it in your test projects:

### Option 1: Install to Local Maven Repository
```bash
mvn clean install
```

Then add the dependency in your test project's `pom.xml`:
```xml
<dependency>
    <groupId>com.automation</groupId>
    <artifactId>selenium-java-framework</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Option 2: Deploy to Artifactory/Nexus
Deploy this framework to your organization's Maven repository and consume it across multiple projects.

## 📄 License

This framework is provided as-is for educational and commercial use.

---

**Happy Testing! 🚀**