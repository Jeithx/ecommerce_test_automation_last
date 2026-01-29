package com.ecommerce.tests.ui;

import com.ecommerce.listeners.RetryAnalyzer;
import com.ecommerce.pages.CartPage;
import com.ecommerce.pages.HomePage;
import com.ecommerce.pages.LoginPage;
import com.ecommerce.tests.base.BaseTest;
import io.qameta.allure.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Remove From Cart Test Class
 * Tests for removing items from shopping cart
 * 
 * Test Count: 8
 * 
 * @author QA Team
 * @version 1.0
 */
@Epic("Shopping")
@Feature("Remove from Cart")
public class RemoveFromCartTest extends BaseTest {
    
    private static final Logger log = LogManager.getLogger(RemoveFromCartTest.class);
    
    private LoginPage loginPage;
    private HomePage homePage;
    private CartPage cartPage;
    
    @BeforeMethod(alwaysRun = true)
    public void setUpTest() {
        loginPage = new LoginPage(getDriver());
        homePage = loginPage.login(
            config.getProperty("standard.user", "standard_user"),
            config.getProperty("test.password", "secret_sauce")
        );
        log.info("Remove from cart test setup completed");
    }
    
    @Test(priority = 1, groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify product can be removed from cart on product page")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Remove from Cart")
    public void testRemoveProductFromProductPage() {
        log.info("TC-REM-001: Testing remove from product page");
        
        homePage.addProductToCart("Sauce Labs Backpack");
        Assert.assertEquals(homePage.getCartItemCount(), 1, "Cart should have 1 item");
        
        homePage.removeProductFromCart("Sauce Labs Backpack");
        Assert.assertEquals(homePage.getCartItemCount(), 0, "Cart should be empty after removal");
    }
    
    @Test(priority = 2, groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify product can be removed from cart page")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Remove from Cart")
    public void testRemoveProductFromCartPage() {
        log.info("TC-REM-002: Testing remove from cart page");
        
        homePage.addProductToCart("Sauce Labs Backpack");
        cartPage = homePage.goToCart();
        
        Assert.assertTrue(cartPage.isProductInCart("Sauce Labs Backpack"), "Product should be in cart");
        
        cartPage.removeProduct("Sauce Labs Backpack");
        
        Assert.assertFalse(cartPage.isProductInCart("Sauce Labs Backpack"), 
            "Product should be removed from cart");
    }
    
    @Test(priority = 3, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify removing one product doesn't affect others")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Remove from Cart")
    public void testRemoveOneProductKeepsOthers() {
        log.info("TC-REM-003: Testing remove one keeps others");
        
        homePage.addProductToCart("Sauce Labs Backpack");
        homePage.addProductToCart("Sauce Labs Bike Light");
        homePage.addProductToCart("Sauce Labs Bolt T-Shirt");
        
        Assert.assertEquals(homePage.getCartItemCount(), 3, "Cart should have 3 items");
        
        homePage.removeProductFromCart("Sauce Labs Bike Light");
        
        Assert.assertEquals(homePage.getCartItemCount(), 2, "Cart should have 2 items");
        Assert.assertTrue(homePage.isProductInCart("Sauce Labs Backpack"), "Backpack should remain");
        Assert.assertTrue(homePage.isProductInCart("Sauce Labs Bolt T-Shirt"), "T-Shirt should remain");
    }
    
    @Test(priority = 4, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify removing all products empties cart")
    @Severity(SeverityLevel.NORMAL)
    @Story("Remove from Cart")
    public void testRemoveAllProducts() {
        log.info("TC-REM-004: Testing remove all products");
        
        homePage.addProductToCart("Sauce Labs Backpack");
        homePage.addProductToCart("Sauce Labs Bike Light");
        
        homePage.removeProductFromCart("Sauce Labs Backpack");
        homePage.removeProductFromCart("Sauce Labs Bike Light");
        
        Assert.assertEquals(homePage.getCartItemCount(), 0, "Cart should be empty");
    }
    
    @Test(priority = 5, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify cart total updates after removal")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Remove from Cart")
    public void testCartTotalUpdatesAfterRemoval() {
        log.info("TC-REM-005: Testing cart total updates");
        
        // Backpack = $29.99, Bike Light = $9.99
        homePage.addProductToCart("Sauce Labs Backpack");
        homePage.addProductToCart("Sauce Labs Bike Light");
        
        cartPage = homePage.goToCart();
        double totalBefore = cartPage.getCartTotal();
        Assert.assertEquals(totalBefore, 39.98, 0.01, "Total should be $39.98");
        
        cartPage.removeProduct("Sauce Labs Bike Light");
        double totalAfter = cartPage.getCartTotal();
        
        Assert.assertEquals(totalAfter, 29.99, 0.01, "Total should be $29.99 after removal");
    }
    
    @Test(priority = 6, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify remove button changes to add after removal")
    @Severity(SeverityLevel.NORMAL)
    @Story("Remove from Cart")
    public void testButtonChangesAfterRemoval() {
        log.info("TC-REM-006: Testing button changes after removal");
        
        homePage.addProductToCart("Sauce Labs Backpack");
        Assert.assertTrue(homePage.isProductInCart("Sauce Labs Backpack"), 
            "Should show Remove button");
        
        homePage.removeProductFromCart("Sauce Labs Backpack");
        Assert.assertFalse(homePage.isProductInCart("Sauce Labs Backpack"), 
            "Should show Add button again");
    }
    
    @Test(priority = 7, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify can re-add product after removal")
    @Severity(SeverityLevel.NORMAL)
    @Story("Remove from Cart")
    public void testReAddAfterRemoval() {
        log.info("TC-REM-007: Testing re-add after removal");
        
        homePage.addProductToCart("Sauce Labs Backpack");
        Assert.assertEquals(homePage.getCartItemCount(), 1, "Cart should have 1 item");
        
        homePage.removeProductFromCart("Sauce Labs Backpack");
        Assert.assertEquals(homePage.getCartItemCount(), 0, "Cart should be empty");
        
        homePage.addProductToCart("Sauce Labs Backpack");
        Assert.assertEquals(homePage.getCartItemCount(), 1, "Cart should have 1 item again");
    }
    
    @Test(priority = 8, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify cart persists removal after navigation")
    @Severity(SeverityLevel.NORMAL)
    @Story("Remove from Cart")
    public void testRemovalPersistsAfterNavigation() {
        log.info("TC-REM-008: Testing removal persists");
        
        homePage.addProductToCart("Sauce Labs Backpack");
        homePage.addProductToCart("Sauce Labs Bike Light");
        
        cartPage = homePage.goToCart();
        cartPage.removeProduct("Sauce Labs Backpack");
        
        homePage = cartPage.continueShopping();
        cartPage = homePage.goToCart();
        
        Assert.assertEquals(cartPage.getCartItemCount(), 1, "Cart should still have 1 item");
        Assert.assertFalse(cartPage.isProductInCart("Sauce Labs Backpack"), 
            "Removed product should stay removed");
        Assert.assertTrue(cartPage.isProductInCart("Sauce Labs Bike Light"), 
            "Other product should remain");
    }
}
