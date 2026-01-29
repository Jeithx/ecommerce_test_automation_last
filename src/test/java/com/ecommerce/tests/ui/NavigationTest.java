package com.ecommerce.tests.ui;

import com.ecommerce.listeners.RetryAnalyzer;
import com.ecommerce.pages.*;
import com.ecommerce.tests.base.BaseTest;
import io.qameta.allure.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Navigation Test Class - Navigation and menu tests
 * 
 * Test Count: 8
 * 
 * @author QA Team
 * @version 1.0
 */
@Epic("Navigation")
@Feature("Menu & Navigation")
public class NavigationTest extends BaseTest {
    
    private static final Logger log = LogManager.getLogger(NavigationTest.class);
    
    private LoginPage loginPage;
    private HomePage homePage;
    
    @BeforeMethod(alwaysRun = true)
    public void setUpTest() {
        loginPage = new LoginPage(getDriver());
        homePage = loginPage.login(
            config.getProperty("standard.user", "standard_user"),
            config.getProperty("test.password", "secret_sauce")
        );
        log.info("Navigation test setup completed");
    }
    
    @Test(priority = 1, groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify hamburger menu opens")
    @Severity(SeverityLevel.NORMAL)
    @Story("Menu Navigation")
    public void testMenuOpens() {
        log.info("TC-NAV-001: Testing menu opens");
        
        homePage.openMenu();
        
        // Menu should be visible (logout link indicates menu is open)
        Assert.assertTrue(homePage.isPageLoaded(), "Page should be loaded with menu");
    }
    
    @Test(priority = 2, groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify logout functionality")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Authentication")
    public void testLogout() {
        log.info("TC-NAV-002: Testing logout");
        
        LoginPage returnedLoginPage = homePage.logout();
        
        Assert.assertTrue(returnedLoginPage.isPageLoaded(), 
            "Should return to login page after logout");
    }
    
    @Test(priority = 3, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify cart icon navigates to cart")
    @Severity(SeverityLevel.NORMAL)
    @Story("Cart Navigation")
    public void testCartIconNavigation() {
        log.info("TC-NAV-003: Testing cart icon navigation");
        
        CartPage cartPage = homePage.goToCart();
        
        Assert.assertTrue(cartPage.isPageLoaded(), "Cart page should load");
    }
    
    @Test(priority = 4, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify continue shopping returns to products")
    @Severity(SeverityLevel.NORMAL)
    @Story("Cart Navigation")
    public void testContinueShoppingNavigation() {
        log.info("TC-NAV-004: Testing continue shopping navigation");
        
        CartPage cartPage = homePage.goToCart();
        HomePage returnedHomePage = cartPage.continueShopping();
        
        Assert.assertTrue(returnedHomePage.isPageLoaded(), 
            "Should return to products page");
    }
    
    @Test(priority = 5, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify checkout navigation from cart")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Checkout Navigation")
    public void testCheckoutNavigation() {
        log.info("TC-NAV-005: Testing checkout navigation");
        
        homePage.addProductToCart("Sauce Labs Backpack");
        CartPage cartPage = homePage.goToCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        
        Assert.assertTrue(checkoutPage.isPageLoaded(), 
            "Checkout page should load");
    }
    
    @Test(priority = 6, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify cancel checkout returns to cart")
    @Severity(SeverityLevel.NORMAL)
    @Story("Checkout Navigation")
    public void testCancelCheckoutNavigation() {
        log.info("TC-NAV-006: Testing cancel checkout navigation");
        
        homePage.addProductToCart("Sauce Labs Backpack");
        CartPage cartPage = homePage.goToCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        CartPage returnedCart = checkoutPage.cancelCheckout();
        
        Assert.assertTrue(returnedCart.isPageLoaded(), 
            "Should return to cart page after cancel");
    }
    
    @Test(priority = 7, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify page refresh maintains cart state")
    @Severity(SeverityLevel.NORMAL)
    @Story("Session Persistence")
    public void testRefreshMaintainsCartState() {
        log.info("TC-NAV-007: Testing refresh maintains cart state");
        
        homePage.addProductToCart("Sauce Labs Backpack");
        int cartCountBefore = homePage.getCartItemCount();
        
        getDriver().navigate().refresh();
        homePage = new HomePage(getDriver());
        
        int cartCountAfter = homePage.getCartItemCount();
        
        Assert.assertEquals(cartCountAfter, cartCountBefore, 
            "Cart should persist after refresh");
    }
    
    @Test(priority = 8, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify browser back button behavior")
    @Severity(SeverityLevel.MINOR)
    @Story("Browser Navigation")
    public void testBrowserBackButton() {
        log.info("TC-NAV-008: Testing browser back button");
        
        CartPage cartPage = homePage.goToCart();
        Assert.assertTrue(cartPage.isPageLoaded(), "Cart page should load");
        
        getDriver().navigate().back();
        homePage = new HomePage(getDriver());
        
        Assert.assertTrue(homePage.isPageLoaded(), 
            "Should return to products page on back");
    }
}
