package com.automation.core.elements;

import com.automation.core.browser.DriverManager;
import com.automation.core.config.ConfigManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Enhanced WebElement wrapper with built-in waits and auto-retry capabilities.
 * Provides Playwright-like auto-waiting behavior for all element interactions.
 */
@Slf4j
public class Element {
    
    private final By locator;
    private final String elementName;
    private final ConfigManager config;
    private WebDriverWait wait;
    
    public Element(By locator, String elementName) {
        this.locator = locator;
        this.elementName = elementName;
        this.config = ConfigManager.getInstance();
        initializeWait();
    }
    
    public Element(By locator) {
        this(locator, locator.toString());
    }
    
    private void initializeWait() {
        WebDriver driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(config.getExplicitTimeout()),
                Duration.ofMillis(config.getPollingInterval())
        );
    }
    
    /**
     * Get the underlying WebElement with auto-wait for presence
     */
    private WebElement getElement() {
        try {
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException e) {
            log.error("Element '{}' not found within {} seconds", elementName, config.getExplicitTimeout());
            throw new NoSuchElementException("Element '" + elementName + "' not found: " + locator);
        }
    }
    
    /**
     * Get all matching elements
     */
    public List<WebElement> getElements() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return DriverManager.getDriver().findElements(locator);
        } catch (TimeoutException e) {
            log.error("Elements '{}' not found within {} seconds", elementName, config.getExplicitTimeout());
            throw new NoSuchElementException("Elements '" + elementName + "' not found: " + locator);
        }
    }
    
    /**
     * Wait for element to be visible
     */
    public Element waitForVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            log.debug("Element '{}' is visible", elementName);
        } catch (TimeoutException e) {
            log.error("Element '{}' not visible within {} seconds", elementName, config.getExplicitTimeout());
            throw new TimeoutException("Element '" + elementName + "' not visible: " + locator);
        }
        return this;
    }
    
    /**
     * Wait for element to be clickable
     */
    public Element waitForClickable() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(locator));
            log.debug("Element '{}' is clickable", elementName);
        } catch (TimeoutException e) {
            log.error("Element '{}' not clickable within {} seconds", elementName, config.getExplicitTimeout());
            throw new TimeoutException("Element '" + elementName + "' not clickable: " + locator);
        }
        return this;
    }
    
    /**
     * Wait for element to be invisible
     */
    public Element waitForInvisible() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
            log.debug("Element '{}' is invisible", elementName);
        } catch (TimeoutException e) {
            log.error("Element '{}' still visible after {} seconds", elementName, config.getExplicitTimeout());
            throw new TimeoutException("Element '" + elementName + "' still visible: " + locator);
        }
        return this;
    }
    
    /**
     * Click with auto-wait for clickable
     */
    public void click() {
        try {
            waitForClickable();
            getElement().click();
            log.info("Clicked on element '{}'", elementName);
        } catch (ElementClickInterceptedException e) {
            log.warn("Click intercepted, trying JavaScript click for '{}'", elementName);
            jsClick();
        }
    }
    
    /**
     * Click using JavaScript
     */
    public void jsClick() {
        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
        js.executeScript("arguments[0].click();", getElement());
        log.info("JavaScript clicked on element '{}'", elementName);
    }
    
    /**
     * Double click
     */
    public void doubleClick() {
        waitForClickable();
        Actions actions = new Actions(DriverManager.getDriver());
        actions.doubleClick(getElement()).perform();
        log.info("Double clicked on element '{}'", elementName);
    }
    
    /**
     * Right click (context click)
     */
    public void rightClick() {
        waitForClickable();
        Actions actions = new Actions(DriverManager.getDriver());
        actions.contextClick(getElement()).perform();
        log.info("Right clicked on element '{}'", elementName);
    }
    
    /**
     * Type text with auto-wait for visible
     */
    public void type(String text) {
        waitForVisible();
        WebElement element = getElement();
        element.clear();
        element.sendKeys(text);
        log.info("Typed '{}' into element '{}'", text, elementName);
    }
    
    /**
     * Type text without clearing existing text
     */
    public void append(String text) {
        waitForVisible();
        getElement().sendKeys(text);
        log.info("Appended '{}' to element '{}'", text, elementName);
    }
    
    /**
     * Send keys (e.g., ENTER, TAB)
     */
    public void sendKeys(Keys key) {
        waitForVisible();
        getElement().sendKeys(key);
        log.info("Sent key '{}' to element '{}'", key, elementName);
    }
    
    /**
     * Clear element
     */
    public void clear() {
        waitForVisible();
        getElement().clear();
        log.info("Cleared element '{}'", elementName);
    }
    
    /**
     * Get text with auto-wait
     */
    public String getText() {
        waitForVisible();
        String text = getElement().getText();
        log.debug("Got text '{}' from element '{}'", text, elementName);
        return text;
    }
    
    /**
     * Get attribute value
     */
    public String getAttribute(String attributeName) {
        String value = getElement().getAttribute(attributeName);
        log.debug("Got attribute '{}' = '{}' from element '{}'", attributeName, value, elementName);
        return value;
    }
    
    /**
     * Get CSS value
     */
    public String getCssValue(String propertyName) {
        String value = getElement().getCssValue(propertyName);
        log.debug("Got CSS '{}' = '{}' from element '{}'", propertyName, value, elementName);
        return value;
    }
    
    /**
     * Check if element is displayed
     */
    public boolean isDisplayed() {
        try {
            boolean displayed = getElement().isDisplayed();
            log.debug("Element '{}' displayed: {}", elementName, displayed);
            return displayed;
        } catch (NoSuchElementException e) {
            log.debug("Element '{}' not found", elementName);
            return false;
        }
    }
    
    /**
     * Check if element is enabled
     */
    public boolean isEnabled() {
        boolean enabled = getElement().isEnabled();
        log.debug("Element '{}' enabled: {}", elementName, enabled);
        return enabled;
    }
    
    /**
     * Check if element is selected (for checkboxes and radio buttons)
     */
    public boolean isSelected() {
        boolean selected = getElement().isSelected();
        log.debug("Element '{}' selected: {}", elementName, selected);
        return selected;
    }
    
    /**
     * Hover over element
     */
    public void hover() {
        waitForVisible();
        Actions actions = new Actions(DriverManager.getDriver());
        actions.moveToElement(getElement()).perform();
        log.info("Hovered over element '{}'", elementName);
    }
    
    /**
     * Scroll element into view
     */
    public void scrollIntoView() {
        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
        js.executeScript("arguments[0].scrollIntoView(true);", getElement());
        log.info("Scrolled element '{}' into view", elementName);
    }
    
    /**
     * Select dropdown option by visible text
     */
    public void selectByText(String text) {
        waitForVisible();
        Select select = new Select(getElement());
        select.selectByVisibleText(text);
        log.info("Selected option '{}' from dropdown '{}'", text, elementName);
    }
    
    /**
     * Select dropdown option by value
     */
    public void selectByValue(String value) {
        waitForVisible();
        Select select = new Select(getElement());
        select.selectByValue(value);
        log.info("Selected value '{}' from dropdown '{}'", value, elementName);
    }
    
    /**
     * Select dropdown option by index
     */
    public void selectByIndex(int index) {
        waitForVisible();
        Select select = new Select(getElement());
        select.selectByIndex(index);
        log.info("Selected index '{}' from dropdown '{}'", index, elementName);
    }
    
    /**
     * Get all selected options from dropdown
     */
    public List<WebElement> getAllSelectedOptions() {
        Select select = new Select(getElement());
        return select.getAllSelectedOptions();
    }
    
    /**
     * Get all options from dropdown
     */
    public List<WebElement> getAllOptions() {
        Select select = new Select(getElement());
        return select.getOptions();
    }
    
    /**
     * Wait for specific text to be present in element
     */
    public Element waitForText(String text) {
        try {
            wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
            log.debug("Text '{}' present in element '{}'", text, elementName);
        } catch (TimeoutException e) {
            log.error("Text '{}' not present in element '{}' within {} seconds", 
                    text, elementName, config.getExplicitTimeout());
            throw new TimeoutException("Text '" + text + "' not present in element: " + elementName);
        }
        return this;
    }
    
    /**
     * Wait for attribute to contain specific value
     */
    public Element waitForAttributeContains(String attribute, String value) {
        try {
            wait.until(ExpectedConditions.attributeContains(locator, attribute, value));
            log.debug("Attribute '{}' contains '{}' in element '{}'", attribute, value, elementName);
        } catch (TimeoutException e) {
            log.error("Attribute '{}' doesn't contain '{}' in element '{}' within {} seconds",
                    attribute, value, elementName, config.getExplicitTimeout());
            throw new TimeoutException("Attribute condition not met for element: " + elementName);
        }
        return this;
    }
    
    /**
     * Get the locator
     */
    public By getLocator() {
        return locator;
    }
    
    /**
     * Get the element name
     */
    public String getElementName() {
        return elementName;
    }
}
