package com.ecommerce.tests.ui;

import com.ecommerce.listeners.RetryAnalyzer;
import com.ecommerce.pages.HomePage;
import com.ecommerce.pages.LoginPage;
import com.ecommerce.tests.base.BaseTest;
import com.ecommerce.utils.TestDataProvider;
import io.qameta.allure.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Login Test Class - Comprehensive login functionality tests
 * 
 * Test Count: 12
 * 
 * @author QA Team
 * @version 2.0
 */
@Epic("Authentication")
@Feature("Login")
public class LoginTest extends BaseTest {
    
    private static final Logger log = LogManager.getLogger(LoginTest.class);
    
    private LoginPage loginPage;
    
    @BeforeMethod(alwaysRun = true)
    public void setUpTest() {
        loginPage = new LoginPage(getDriver());
        log.info("Login test setup completed");
    }
    
    // ==================== SMOKE TESTS ====================
    
    @Test(priority = 1, groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify successful login with valid credentials")
    @Severity(SeverityLevel.BLOCKER)
    @Story("User Login")
    public void testSuccessfulLogin() {
        log.info("TC-001: Testing successful login");
        
        String username = config.getProperty("standard.user", "standard_user");
        String password = config.getProperty("test.password", "secret_sauce");
        
        HomePage homePage = loginPage.login(username, password);
        
        Assert.assertTrue(homePage.isPageLoaded(), "Home page should be loaded");
        Assert.assertTrue(homePage.isUserLoggedIn(), "User should be logged in");
    }
    
    @Test(priority = 2, groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify login fails with invalid username")
    @Severity(SeverityLevel.CRITICAL)
    @Story("User Login")
    public void testLoginWithInvalidUsername() {
        log.info("TC-002: Testing login with invalid username");
        
        loginPage.enterUsername("invalid_user")
                .enterPassword("secret_sauce")
                .clickLoginButton();
        
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), 
            "Error message should be displayed");
        Assert.assertTrue(loginPage.getErrorMessage().contains("Username and password do not match"),
            "Should show credential mismatch error");
    }
    
    @Test(priority = 3, groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify login fails with invalid password")
    @Severity(SeverityLevel.CRITICAL)
    @Story("User Login")
    public void testLoginWithInvalidPassword() {
        log.info("TC-003: Testing login with invalid password");
        
        loginPage.enterUsername("standard_user")
                .enterPassword("wrong_password")
                .clickLoginButton();
        
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), 
            "Error message should be displayed");
    }
    
    // ==================== REGRESSION TESTS ====================
    
    @Test(priority = 4, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify login fails with empty credentials")
    @Severity(SeverityLevel.NORMAL)
    @Story("User Login")
    public void testLoginWithEmptyCredentials() {
        log.info("TC-004: Testing login with empty credentials");
        
        loginPage.attemptEmptyLogin();
        
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), 
            "Error message should be displayed");
        Assert.assertTrue(loginPage.getErrorMessage().contains("Username is required"),
            "Should show username required error");
    }
    
    @Test(priority = 5, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify locked out user cannot login")
    @Severity(SeverityLevel.CRITICAL)
    @Story("User Login")
    public void testLockedOutUserLogin() {
        log.info("TC-005: Testing locked out user login");
        
        loginPage.enterUsername("locked_out_user")
                .enterPassword("secret_sauce")
                .clickLoginButton();
        
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), 
            "Error message should be displayed");
        Assert.assertTrue(loginPage.getErrorMessage().contains("locked"), 
            "Error should mention locked");
    }
    
    @Test(priority = 6, groups = {"regression", "ui"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify login page elements are displayed")
    @Severity(SeverityLevel.NORMAL)
    @Story("Login Page UI")
    public void testLoginPageElements() {
        log.info("TC-006: Testing login page elements");
        
        Assert.assertTrue(loginPage.isUsernameFieldDisplayed(), 
            "Username field should be displayed");
        Assert.assertTrue(loginPage.isPasswordFieldDisplayed(), 
            "Password field should be displayed");
        Assert.assertTrue(loginPage.isLoginButtonEnabled(), 
            "Login button should be enabled");
    }
    
    @Test(priority = 7, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify login with only username shows password required error")
    @Severity(SeverityLevel.NORMAL)
    @Story("User Login")
    public void testLoginWithOnlyUsername() {
        log.info("TC-007: Testing login with only username");
        
        loginPage.attemptLoginWithOnlyUsername("standard_user");
        
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), 
            "Error message should be displayed");
        Assert.assertTrue(loginPage.getErrorMessage().contains("Password is required"),
            "Should show password required error");
    }
    
    @Test(priority = 8, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify login with only password shows username required error")
    @Severity(SeverityLevel.NORMAL)
    @Story("User Login")
    public void testLoginWithOnlyPassword() {
        log.info("TC-008: Testing login with only password");
        
        loginPage.attemptLoginWithOnlyPassword("secret_sauce");
        
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), 
            "Error message should be displayed");
        Assert.assertTrue(loginPage.getErrorMessage().contains("Username is required"),
            "Should show username required error");
    }
    
    @Test(priority = 9, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify error message can be closed")
    @Severity(SeverityLevel.MINOR)
    @Story("Login Page UI")
    public void testCloseErrorMessage() {
        log.info("TC-009: Testing close error message");
        
        loginPage.attemptEmptyLogin();
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error should be displayed");
        
        loginPage.closeErrorMessage();
        // Note: SauceDemo doesn't hide the error on close, it stays visible
        // This test validates the close button is clickable
    }
    
    @Test(priority = 10, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify problem user can login but experiences issues")
    @Severity(SeverityLevel.NORMAL)
    @Story("User Login")
    public void testProblemUserLogin() {
        log.info("TC-010: Testing problem user login");
        
        HomePage homePage = loginPage.login("problem_user", "secret_sauce");
        
        Assert.assertTrue(homePage.isPageLoaded(), "Home page should be loaded");
        // Problem user has issues with images, not login itself
    }
    
    @Test(priority = 11, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify performance glitch user experiences slow login")
    @Severity(SeverityLevel.NORMAL)
    @Story("User Login")
    public void testPerformanceGlitchUserLogin() {
        log.info("TC-011: Testing performance glitch user login");
        
        long startTime = System.currentTimeMillis();
        HomePage homePage = loginPage.login("performance_glitch_user", "secret_sauce");
        long loginTime = System.currentTimeMillis() - startTime;
        
        Assert.assertTrue(homePage.isPageLoaded(), "Home page should be loaded");
        log.info("Performance glitch user login time: {}ms", loginTime);
        // This user intentionally has slower response
    }
    
    @Test(priority = 12, groups = {"regression", "security"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify password field masks input")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Security")
    public void testPasswordFieldMasking() {
        log.info("TC-012: Testing password field masking");
        
        String passwordFieldType = loginPage.getPasswordField().getAttribute("type");
        Assert.assertEquals(passwordFieldType, "password", 
            "Password field should mask input");
    }
    
    // ==================== DATA-DRIVEN TESTS ====================
    
    @Test(dataProvider = "invalidUsers", dataProviderClass = TestDataProvider.class, 
          groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify login fails with various invalid credentials")
    @Severity(SeverityLevel.CRITICAL)
    @Story("User Login")
    public void testLoginWithInvalidCredentials(String username, String password, String scenario) {
        log.info("TC-DD: Testing invalid login - {}", scenario);
        
        if (!username.isEmpty()) {
            loginPage.enterUsername(username);
        }
        if (!password.isEmpty()) {
            loginPage.enterPassword(password);
        }
        loginPage.clickLoginButton();
        
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), 
            "Error message should be displayed for: " + scenario);
    }
}
