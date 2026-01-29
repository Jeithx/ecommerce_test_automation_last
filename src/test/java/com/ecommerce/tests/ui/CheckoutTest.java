package com.ecommerce.tests.ui;

import com.ecommerce.listeners.RetryAnalyzer;
import com.ecommerce.pages.*;
import com.ecommerce.tests.base.BaseTest;
import com.ecommerce.utils.TestDataProvider;
import io.qameta.allure.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Checkout Test Class - Complete checkout flow tests
 * 
 * Test Count: 12
 * 
 * @author QA Team
 * @version 1.0
 */
@Epic("Shopping")
@Feature("Checkout")
public class CheckoutTest extends BaseTest {
    
    private static final Logger log = LogManager.getLogger(CheckoutTest.class);
    
    private LoginPage loginPage;
    private HomePage homePage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;
    
    @BeforeMethod(alwaysRun = true)
    public void setUpTest() {
        loginPage = new LoginPage(getDriver());
        homePage = loginPage.login(
            config.getProperty("standard.user", "standard_user"),
            config.getProperty("test.password", "secret_sauce")
        );
        
        // Add a product to cart for checkout tests
        homePage.addProductToCart("Sauce Labs Backpack");
        cartPage = homePage.goToCart();
        checkoutPage = cartPage.proceedToCheckout();
        
        log.info("Checkout test setup completed");
    }
    
    // ==================== SMOKE TESTS ====================
    
    @Test(priority = 1, groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify checkout page loads correctly")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Checkout Flow")
    public void testCheckoutPageLoads() {
        log.info("TC-CHK-001: Testing checkout page loads");
        
        Assert.assertTrue(checkoutPage.isPageLoaded(), 
            "Checkout page should be loaded");
    }
    
    @Test(priority = 2, groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify successful checkout with valid information")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Checkout Flow")
    public void testSuccessfulCheckout() {
        log.info("TC-CHK-002: Testing successful checkout");
        
        checkoutPage.enterFirstName("John")
                   .enterLastName("Doe")
                   .enterZipCode("12345");
        
        Assert.assertTrue(checkoutPage.isFormValid(), 
            "Form should be valid with all fields filled");
    }
    
    // ==================== REGRESSION TESTS ====================
    
    @Test(priority = 3, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify checkout fails with empty first name")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Form Validation")
    public void testCheckoutWithEmptyFirstName() {
        log.info("TC-CHK-003: Testing checkout with empty first name");
        
        checkoutPage.enterLastName("Doe")
                   .enterZipCode("12345")
                   .continueToOverview();
        
        Assert.assertTrue(checkoutPage.isErrorDisplayed(), 
            "Error should be displayed");
        Assert.assertTrue(checkoutPage.getErrorMessage().contains("First Name"), 
            "Error should mention First Name");
    }
    
    @Test(priority = 4, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify checkout fails with empty last name")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Form Validation")
    public void testCheckoutWithEmptyLastName() {
        log.info("TC-CHK-004: Testing checkout with empty last name");
        
        checkoutPage.enterFirstName("John")
                   .enterZipCode("12345")
                   .continueToOverview();
        
        Assert.assertTrue(checkoutPage.isErrorDisplayed(), 
            "Error should be displayed");
        Assert.assertTrue(checkoutPage.getErrorMessage().contains("Last Name"), 
            "Error should mention Last Name");
    }
    
    @Test(priority = 5, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify checkout fails with empty zip code")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Form Validation")
    public void testCheckoutWithEmptyZipCode() {
        log.info("TC-CHK-005: Testing checkout with empty zip code");
        
        checkoutPage.enterFirstName("John")
                   .enterLastName("Doe")
                   .continueToOverview();
        
        Assert.assertTrue(checkoutPage.isErrorDisplayed(), 
            "Error should be displayed");
        Assert.assertTrue(checkoutPage.getErrorMessage().contains("Postal Code"), 
            "Error should mention Postal Code");
    }
    
    @Test(priority = 6, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify checkout fails with all empty fields")
    @Severity(SeverityLevel.NORMAL)
    @Story("Form Validation")
    public void testCheckoutWithAllEmptyFields() {
        log.info("TC-CHK-006: Testing checkout with all empty fields");
        
        checkoutPage.continueToOverview();
        
        Assert.assertTrue(checkoutPage.isErrorDisplayed(), 
            "Error should be displayed");
    }
    
    @Test(priority = 7, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify cancel returns to cart")
    @Severity(SeverityLevel.NORMAL)
    @Story("Navigation")
    public void testCancelCheckout() {
        log.info("TC-CHK-007: Testing cancel checkout");
        
        CartPage returnedCart = checkoutPage.cancelCheckout();
        
        Assert.assertTrue(returnedCart.isPageLoaded(), 
            "Should return to cart page");
    }
    
    @Test(priority = 8, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify checkout with special characters in name")
    @Severity(SeverityLevel.NORMAL)
    @Story("Form Validation")
    public void testCheckoutWithSpecialCharacters() {
        log.info("TC-CHK-008: Testing checkout with special characters");
        
        checkoutPage.enterFirstName("José")
                   .enterLastName("O'Connor")
                   .enterZipCode("12345");
        
        Assert.assertTrue(checkoutPage.isFormValid(), 
            "Form should accept special characters");
    }
    
    @Test(priority = 9, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify checkout with numeric zip codes")
    @Severity(SeverityLevel.NORMAL)
    @Story("Form Validation")
    public void testCheckoutWithNumericZipCode() {
        log.info("TC-CHK-009: Testing checkout with numeric zip code");
        
        checkoutPage.enterFirstName("John")
                   .enterLastName("Doe")
                   .enterZipCode("90210");
        
        Assert.assertTrue(checkoutPage.isFormValid(), 
            "Form should accept numeric zip code");
    }
    
    @Test(priority = 10, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify checkout with alphanumeric zip codes")
    @Severity(SeverityLevel.NORMAL)
    @Story("Form Validation")
    public void testCheckoutWithAlphanumericZipCode() {
        log.info("TC-CHK-010: Testing checkout with alphanumeric zip code");
        
        checkoutPage.enterFirstName("John")
                   .enterLastName("Doe")
                   .enterZipCode("SW1A 1AA"); // UK postal code format
        
        Assert.assertTrue(checkoutPage.isFormValid(), 
            "Form should accept alphanumeric zip code");
    }
    
    @Test(priority = 11, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify checkout with very long names")
    @Severity(SeverityLevel.MINOR)
    @Story("Form Validation")
    public void testCheckoutWithLongNames() {
        log.info("TC-CHK-011: Testing checkout with long names");
        
        String longName = "Bartholomew Christopher Alexander Montgomery";
        
        checkoutPage.enterFirstName(longName)
                   .enterLastName(longName)
                   .enterZipCode("12345");
        
        Assert.assertTrue(checkoutPage.isFormValid(), 
            "Form should accept long names");
    }
    
    @Test(priority = 12, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class,
          dataProvider = "checkoutData", dataProviderClass = TestDataProvider.class)
    @Description("Data-driven checkout validation test")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Form Validation")
    public void testCheckoutWithVariousData(String firstName, String lastName, 
                                            String zipCode, boolean shouldPass) {
        log.info("TC-CHK-012-DD: Testing checkout with data - {}, {}, {}", 
            firstName, lastName, zipCode);
        
        // Navigate to checkout again for fresh form
        getDriver().navigate().refresh();
        checkoutPage = new CheckoutPage(getDriver());
        
        if (!firstName.isEmpty()) checkoutPage.enterFirstName(firstName);
        if (!lastName.isEmpty()) checkoutPage.enterLastName(lastName);
        if (!zipCode.isEmpty()) checkoutPage.enterZipCode(zipCode);
        
        if (shouldPass) {
            Assert.assertTrue(checkoutPage.isFormValid(), 
                "Form should be valid");
        } else {
            checkoutPage.continueToOverview();
            Assert.assertTrue(checkoutPage.isErrorDisplayed(), 
                "Error should be displayed for invalid data");
        }
    }
}
