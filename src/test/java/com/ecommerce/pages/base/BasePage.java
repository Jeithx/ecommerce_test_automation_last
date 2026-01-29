package com.ecommerce.pages.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Base Page - Foundation for all Page Objects
 * Contains common methods for all pages
 * 
 * @author QA Team
 * @version 2.0
 */
public abstract class BasePage {
    
    protected static final Logger log = LogManager.getLogger(BasePage.class);
    
    protected WebDriver driver;
    protected WebDriverWait wait;
    
    private static final int DEFAULT_TIMEOUT = 20;
    
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        PageFactory.initElements(driver, this);
    }
    
    // ==================== WAIT METHODS ====================
    
    protected void waitForElementVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }
    
    protected void waitForElementClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }
    
    protected void waitForElementPresent(By locator) {
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }
    
    protected void waitForUrlContains(String urlPart) {
        wait.until(ExpectedConditions.urlContains(urlPart));
    }
    
    protected void waitForPageLoad() {
        wait.until(driver -> ((JavascriptExecutor) driver)
            .executeScript("return document.readyState").equals("complete"));
    }
    
    // ==================== ELEMENT INTERACTIONS ====================
    
    protected void click(WebElement element) {
        waitForElementClickable(element);
        element.click();
        log.debug("Clicked on element: {}", element);
    }
    
    protected void type(WebElement element, String text) {
        waitForElementVisible(element);
        element.clear();
        element.sendKeys(text);
        log.debug("Typed '{}' into element", text);
    }
    
    protected String getText(WebElement element) {
        waitForElementVisible(element);
        return element.getText();
    }
    
    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }
    
    protected boolean isEnabled(WebElement element) {
        try {
            return element.isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    protected void selectByVisibleText(WebElement element, String text) {
        waitForElementVisible(element);
        Select select = new Select(element);
        select.selectByVisibleText(text);
        log.debug("Selected '{}' from dropdown", text);
    }
    
    protected void selectByValue(WebElement element, String value) {
        waitForElementVisible(element);
        Select select = new Select(element);
        select.selectByValue(value);
    }
    
    // ==================== JAVASCRIPT METHODS ====================
    
    protected void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView(true);", element);
    }
    
    protected void clickWithJS(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].click();", element);
    }
    
    // ==================== UTILITY METHODS ====================
    
    protected List<WebElement> getElements(By locator) {
        return driver.findElements(locator);
    }
    
    protected WebElement getElement(By locator) {
        return driver.findElement(locator);
    }
    
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
    
    public String getPageTitle() {
        return driver.getTitle();
    }
    
    public abstract boolean isPageLoaded();
}
