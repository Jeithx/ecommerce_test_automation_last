package com.ecommerce.pages;

import com.ecommerce.pages.base.BasePage;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Login Page Object
 * Handles all login page interactions
 * 
 * @author QA Team
 * @version 2.0
 */
public class LoginPage extends BasePage {
    
    // ==================== LOCATORS ====================
    
    @FindBy(id = "user-name")
    private WebElement usernameField;
    
    @FindBy(id = "password")
    private WebElement passwordField;
    
    @FindBy(id = "login-button")
    private WebElement loginButton;
    
    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;
    
    @FindBy(css = ".error-button")
    private WebElement errorCloseButton;
    
    @FindBy(css = ".login_logo")
    private WebElement loginLogo;
    
    // ==================== CONSTRUCTOR ====================
    
    public LoginPage(WebDriver driver) {
        super(driver);
        log.info("LoginPage initialized");
    }
    
    // ==================== PAGE ACTIONS ====================
    
    public LoginPage enterUsername(String username) {
        type(usernameField, username);
        log.info("Entered username: {}", username);
        return this;
    }
    
    public LoginPage enterPassword(String password) {
        type(passwordField, password);
        log.info("Entered password: ****");
        return this;
    }
    
    public LoginPage clickLoginButton() {
        click(loginButton);
        log.info("Clicked login button");
        return this;
    }
    
    public HomePage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
        
        if (isLoginSuccessful()) {
            log.info("Login successful for user: {}", username);
            return new HomePage(driver);
        } else {
            log.warn("Login failed for user: {}", username);
            return null;
        }
    }
    
    public void attemptEmptyLogin() {
        clickLoginButton();
        log.info("Attempted login with empty credentials");
    }
    
    public void attemptLoginWithOnlyUsername(String username) {
        enterUsername(username);
        clickLoginButton();
        log.info("Attempted login with only username");
    }
    
    public void attemptLoginWithOnlyPassword(String password) {
        enterPassword(password);
        clickLoginButton();
        log.info("Attempted login with only password");
    }
    
    public void closeErrorMessage() {
        if (isErrorMessageDisplayed()) {
            click(errorCloseButton);
            log.info("Closed error message");
        }
    }
    
    // ==================== VALIDATIONS ====================
    
    public boolean isLoginSuccessful() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            shortWait.until(ExpectedConditions.urlContains("inventory"));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
    
    public boolean isErrorMessageDisplayed() {
        try {
            return isDisplayed(errorMessage);
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getErrorMessage() {
        if (isErrorMessageDisplayed()) {
            return getText(errorMessage);
        }
        return "";
    }
    
    public boolean isUsernameFieldDisplayed() {
        return isDisplayed(usernameField);
    }
    
    public boolean isPasswordFieldDisplayed() {
        return isDisplayed(passwordField);
    }
    
    public boolean isLoginButtonEnabled() {
        return isEnabled(loginButton);
    }
    
    public WebElement getPasswordField() {
        return passwordField;
    }
    
    @Override
    public boolean isPageLoaded() {
        try {
            return isDisplayed(loginLogo) && isDisplayed(loginButton);
        } catch (Exception e) {
            return false;
        }
    }
}
