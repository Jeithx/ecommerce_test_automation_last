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

import java.util.List;

/**
 * Cart Test Class - Shopping cart functionality tests
 * 
 * Test Count: 10
 * 
 * @author QA Team
 * @version 1.0
 */
@Epic("Shopping")
@Feature("Cart")
public class CartTest extends BaseTest {
    
    private static final Logger log = LogManager.getLogger(CartTest.class);
    
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
        log.info("Cart test setup completed - User logged in");
    }
    
    // ==================== SMOKE TESTS ====================
    
    @Test(priority = 1, groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify product can be added to cart")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Add to Cart")
    public void testAddProductToCart() {
        log.info("TC-CART-001: Testing add product to cart");
        
        String productName = "Sauce Labs Backpack";
        homePage.addProductToCart(productName);
        
        Assert.assertEquals(homePage.getCartItemCount(), 1, 
            "Cart should have 1 item");
        Assert.assertTrue(homePage.isProductInCart(productName), 
            "Product should show 'Remove' button");
    }
    
    @Test(priority = 2, groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify cart displays added products correctly")
    @Severity(SeverityLevel.CRITICAL)
    @Story("View Cart")
    public void testViewCartWithProducts() {
        log.info("TC-CART-002: Testing view cart with products");
        
        String productName = "Sauce Labs Backpack";
        homePage.addProductToCart(productName);
        cartPage = homePage.goToCart();
        
        Assert.assertTrue(cartPage.isPageLoaded(), "Cart page should be loaded");
        Assert.assertTrue(cartPage.isProductInCart(productName), 
            "Product should be in cart");
    }
    
    // ==================== REGRESSION TESTS ====================
    
    @Test(priority = 3, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify multiple products can be added to cart")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Add to Cart")
    public void testAddMultipleProductsToCart() {
        log.info("TC-CART-003: Testing add multiple products");
        
        homePage.addProductToCart("Sauce Labs Backpack");
        homePage.addProductToCart("Sauce Labs Bike Light");
        homePage.addProductToCart("Sauce Labs Bolt T-Shirt");
        
        Assert.assertEquals(homePage.getCartItemCount(), 3, 
            "Cart should have 3 items");
    }
    
    @Test(priority = 4, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify empty cart shows no items")
    @Severity(SeverityLevel.NORMAL)
    @Story("View Cart")
    public void testEmptyCart() {
        log.info("TC-CART-004: Testing empty cart");
        
        cartPage = homePage.goToCart();
        
        Assert.assertTrue(cartPage.isPageLoaded(), "Cart page should be loaded");
        Assert.assertTrue(cartPage.isCartEmpty(), "Cart should be empty");
        Assert.assertEquals(cartPage.getCartItemCount(), 0, "Cart count should be 0");
    }
    
    @Test(priority = 5, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify cart badge updates when adding products")
    @Severity(SeverityLevel.NORMAL)
    @Story("Add to Cart")
    public void testCartBadgeUpdates() {
        log.info("TC-CART-005: Testing cart badge updates");
        
        Assert.assertEquals(homePage.getCartItemCount(), 0, "Initial cart should be empty");
        
        homePage.addProductToCart(0);
        Assert.assertEquals(homePage.getCartItemCount(), 1, "Badge should show 1");
        
        homePage.addProductToCart(1);
        Assert.assertEquals(homePage.getCartItemCount(), 2, "Badge should show 2");
    }
    
    @Test(priority = 6, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify continue shopping returns to products page")
    @Severity(SeverityLevel.NORMAL)
    @Story("Navigation")
    public void testContinueShopping() {
        log.info("TC-CART-006: Testing continue shopping");
        
        homePage.addProductToCart("Sauce Labs Backpack");
        cartPage = homePage.goToCart();
        
        HomePage returnedHomePage = cartPage.continueShopping();
        
        Assert.assertTrue(returnedHomePage.isPageLoaded(), 
            "Should return to products page");
    }
    
    @Test(priority = 7, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify cart preserves items after navigation")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cart Persistence")
    public void testCartPersistsAfterNavigation() {
        log.info("TC-CART-007: Testing cart persistence");
        
        homePage.addProductToCart("Sauce Labs Backpack");
        homePage.addProductToCart("Sauce Labs Bike Light");
        
        cartPage = homePage.goToCart();
        homePage = cartPage.continueShopping();
        
        Assert.assertEquals(homePage.getCartItemCount(), 2, 
            "Cart should still have 2 items after navigation");
    }
    
    @Test(priority = 8, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify cart displays correct product names")
    @Severity(SeverityLevel.NORMAL)
    @Story("View Cart")
    public void testCartDisplaysCorrectProductNames() {
        log.info("TC-CART-008: Testing cart product names");
        
        String product1 = "Sauce Labs Backpack";
        String product2 = "Sauce Labs Bike Light";
        
        homePage.addProductToCart(product1);
        homePage.addProductToCart(product2);
        cartPage = homePage.goToCart();
        
        List<String> cartItems = cartPage.getCartItemNames();
        
        Assert.assertTrue(cartItems.contains(product1), "Cart should contain " + product1);
        Assert.assertTrue(cartItems.contains(product2), "Cart should contain " + product2);
    }
    
    @Test(priority = 9, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify cart calculates total correctly")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cart Calculations")
    public void testCartTotalCalculation() {
        log.info("TC-CART-009: Testing cart total calculation");
        
        // Sauce Labs Backpack = $29.99
        // Sauce Labs Bike Light = $9.99
        homePage.addProductToCart("Sauce Labs Backpack");
        homePage.addProductToCart("Sauce Labs Bike Light");
        cartPage = homePage.goToCart();
        
        double expectedTotal = 29.99 + 9.99;
        double actualTotal = cartPage.getCartTotal();
        
        Assert.assertEquals(actualTotal, expectedTotal, 0.01, 
            "Cart total should be calculated correctly");
    }
    
    @Test(priority = 10, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify all products can be added to cart")
    @Severity(SeverityLevel.NORMAL)
    @Story("Add to Cart")
    public void testAddAllProductsToCart() {
        log.info("TC-CART-010: Testing add all products");
        
        int productCount = homePage.getProductCount();
        
        for (int i = 0; i < productCount; i++) {
            homePage.addProductToCart(i);
        }
        
        Assert.assertEquals(homePage.getCartItemCount(), productCount, 
            "All products should be in cart");
    }
}
