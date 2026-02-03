package com.automation.core.actions;

import com.automation.core.browser.DriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Centralized web actions for common browser interactions.
 * Provides high-level methods for navigation, alerts, windows, frames, etc.
 */
@Slf4j
public class WebActions {
    
    private final WebDriver driver;
    private final Actions actions;
    private final JavascriptExecutor jsExecutor;
    
    public WebActions() {
        this.driver = DriverManager.getDriver();
        this.actions = new Actions(driver);
        this.jsExecutor = (JavascriptExecutor) driver;
    }
    
    // ========== Navigation ==========
    
    public void navigateTo(String url) {
        driver.get(url);
        log.info("Navigated to URL: {}", url);
    }
    
    public void refresh() {
        driver.navigate().refresh();
        log.info("Page refreshed");
    }
    
    public void back() {
        driver.navigate().back();
        log.info("Navigated back");
    }
    
    public void forward() {
        driver.navigate().forward();
        log.info("Navigated forward");
    }
    
    public String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        log.debug("Current URL: {}", url);
        return url;
    }
    
    public String getTitle() {
        String title = driver.getTitle();
        log.debug("Page title: {}", title);
        return title;
    }
    
    // ========== JavaScript Execution ==========
    
    public Object executeScript(String script, Object... args) {
        Object result = jsExecutor.executeScript(script, args);
        log.debug("Executed JavaScript: {}", script);
        return result;
    }
    
    public Object executeAsyncScript(String script, Object... args) {
        Object result = jsExecutor.executeAsyncScript(script, args);
        log.debug("Executed async JavaScript: {}", script);
        return result;
    }
    
    public void scrollToTop() {
        executeScript("window.scrollTo(0, 0)");
        log.info("Scrolled to top of page");
    }
    
    public void scrollToBottom() {
        executeScript("window.scrollTo(0, document.body.scrollHeight)");
        log.info("Scrolled to bottom of page");
    }
    
    public void scrollBy(int x, int y) {
        executeScript(String.format("window.scrollBy(%d, %d)", x, y));
        log.info("Scrolled by x:{}, y:{}", x, y);
    }
    
    public void highlightElement(WebElement element) {
        String originalStyle = element.getAttribute("style");
        executeScript("arguments[0].setAttribute('style', 'border: 3px solid red;');", element);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        executeScript("arguments[0].setAttribute('style', '" + originalStyle + "');", element);
    }
    
    // ========== Alerts ==========
    
    public Alert getAlert(Duration timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        return wait.until(ExpectedConditions.alertIsPresent());
    }
    
    public void acceptAlert() {
        Alert alert = getAlert(Duration.ofSeconds(10));
        alert.accept();
        log.info("Alert accepted");
    }
    
    public void dismissAlert() {
        Alert alert = getAlert(Duration.ofSeconds(10));
        alert.dismiss();
        log.info("Alert dismissed");
    }
    
    public String getAlertText() {
        Alert alert = getAlert(Duration.ofSeconds(10));
        String text = alert.getText();
        log.info("Alert text: {}", text);
        return text;
    }
    
    public void sendKeysToAlert(String text) {
        Alert alert = getAlert(Duration.ofSeconds(10));
        alert.sendKeys(text);
        log.info("Sent text to alert: {}", text);
    }
    
    // ========== Windows and Tabs ==========
    
    public String getWindowHandle() {
        return driver.getWindowHandle();
    }
    
    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }
    
    public void switchToWindow(String windowHandle) {
        driver.switchTo().window(windowHandle);
        log.info("Switched to window: {}", windowHandle);
    }
    
    public void switchToNewWindow() {
        String originalWindow = driver.getWindowHandle();
        Set<String> allWindows = driver.getWindowHandles();
        
        for (String window : allWindows) {
            if (!window.equals(originalWindow)) {
                driver.switchTo().window(window);
                log.info("Switched to new window");
                break;
            }
        }
    }
    
    public void closeCurrentWindow() {
        driver.close();
        log.info("Closed current window");
    }
    
    public void switchToWindowByTitle(String title) {
        Set<String> windows = driver.getWindowHandles();
        for (String window : windows) {
            driver.switchTo().window(window);
            if (driver.getTitle().equals(title)) {
                log.info("Switched to window with title: {}", title);
                return;
            }
        }
        log.warn("No window found with title: {}", title);
    }
    
    public void openNewTab() {
        executeScript("window.open()");
        log.info("Opened new tab");
    }
    
    // ========== Frames ==========
    
    public void switchToFrame(int index) {
        driver.switchTo().frame(index);
        log.info("Switched to frame by index: {}", index);
    }
    
    public void switchToFrame(String nameOrId) {
        driver.switchTo().frame(nameOrId);
        log.info("Switched to frame: {}", nameOrId);
    }
    
    public void switchToFrame(WebElement frameElement) {
        driver.switchTo().frame(frameElement);
        log.info("Switched to frame element");
    }
    
    public void switchToDefaultContent() {
        driver.switchTo().defaultContent();
        log.info("Switched to default content");
    }
    
    public void switchToParentFrame() {
        driver.switchTo().parentFrame();
        log.info("Switched to parent frame");
    }
    
    // ========== Cookies ==========
    
    public void addCookie(Cookie cookie) {
        driver.manage().addCookie(cookie);
        log.info("Added cookie: {}", cookie.getName());
    }
    
    public Cookie getCookie(String name) {
        Cookie cookie = driver.manage().getCookieNamed(name);
        log.debug("Retrieved cookie: {}", name);
        return cookie;
    }
    
    public Set<Cookie> getAllCookies() {
        Set<Cookie> cookies = driver.manage().getCookies();
        log.debug("Retrieved all cookies, count: {}", cookies.size());
        return cookies;
    }
    
    public void deleteCookie(String name) {
        driver.manage().deleteCookieNamed(name);
        log.info("Deleted cookie: {}", name);
    }
    
    public void deleteAllCookies() {
        driver.manage().deleteAllCookies();
        log.info("Deleted all cookies");
    }
    
    // ========== Screenshots ==========
    
    public byte[] takeScreenshot() {
        TakesScreenshot screenshot = (TakesScreenshot) driver;
        byte[] bytes = screenshot.getScreenshotAs(OutputType.BYTES);
        log.info("Screenshot captured");
        return bytes;
    }
    
    public String takeScreenshotAsBase64() {
        TakesScreenshot screenshot = (TakesScreenshot) driver;
        String base64 = screenshot.getScreenshotAs(OutputType.BASE64);
        log.info("Screenshot captured as Base64");
        return base64;
    }
    
    // ========== Mouse Actions ==========
    
    public void dragAndDrop(WebElement source, WebElement target) {
        actions.dragAndDrop(source, target).perform();
        log.info("Performed drag and drop");
    }
    
    public void dragAndDropBy(WebElement source, int xOffset, int yOffset) {
        actions.dragAndDropBy(source, xOffset, yOffset).perform();
        log.info("Performed drag and drop by offset x:{}, y:{}", xOffset, yOffset);
    }
    
    public void moveToElement(WebElement element) {
        actions.moveToElement(element).perform();
        log.info("Moved to element");
    }
    
    public void moveToElementWithOffset(WebElement element, int xOffset, int yOffset) {
        actions.moveToElement(element, xOffset, yOffset).perform();
        log.info("Moved to element with offset x:{}, y:{}", xOffset, yOffset);
    }
    
    // ========== Keyboard Actions ==========
    
    public void pressKey(Keys key) {
        actions.sendKeys(key).perform();
        log.info("Pressed key: {}", key);
    }
    
    public void pressKeys(CharSequence... keys) {
        actions.sendKeys(keys).perform();
        log.info("Pressed multiple keys");
    }
    
    public void keyDown(Keys key) {
        actions.keyDown(key).perform();
        log.info("Key down: {}", key);
    }
    
    public void keyUp(Keys key) {
        actions.keyUp(key).perform();
        log.info("Key up: {}", key);
    }
    
    // ========== Wait Utilities ==========
    
    public void waitForPageLoad(Duration timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(webDriver -> 
            jsExecutor.executeScript("return document.readyState").equals("complete")
        );
        log.info("Page loaded completely");
    }
    
    public void waitForAjax(Duration timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(webDriver -> 
            (Boolean) jsExecutor.executeScript("return jQuery.active == 0")
        );
        log.info("AJAX requests completed");
    }
    
    public void hardWait(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
            log.debug("Hard wait for {} ms", milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Hard wait interrupted: {}", e.getMessage());
        }
    }
}
