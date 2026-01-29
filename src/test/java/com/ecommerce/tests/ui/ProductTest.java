package com.ecommerce.tests.ui;

import com.ecommerce.listeners.RetryAnalyzer;
import com.ecommerce.pages.HomePage;
import com.ecommerce.pages.LoginPage;
import com.ecommerce.tests.base.BaseTest;
import io.qameta.allure.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Product Test Class - Product listing and display tests
 * 
 * Test Count: 10
 * 
 * @author QA Team
 * @version 1.0
 */
@Epic("Shopping")
@Feature("Products")
public class ProductTest extends BaseTest {
    
    private static final Logger log = LogManager.getLogger(ProductTest.class);
    
    private LoginPage loginPage;
    private HomePage homePage;
    
    @BeforeMethod(alwaysRun = true)
    public void setUpTest() {
        loginPage = new LoginPage(getDriver());
        homePage = loginPage.login(
            config.getProperty("standard.user", "standard_user"),
            config.getProperty("test.password", "secret_sauce")
        );
        log.info("Product test setup completed");
    }
    
    // ==================== SMOKE TESTS ====================
    
    @Test(priority = 1, groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify products page displays products")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Product Display")
    public void testProductsDisplayed() {
        log.info("TC-PROD-001: Testing products displayed");
        
        Assert.assertTrue(homePage.isPageLoaded(), "Products page should be loaded");
        Assert.assertTrue(homePage.getProductCount() > 0, "Should display products");
    }
    
    @Test(priority = 2, groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify correct number of products displayed")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Product Display")
    public void testCorrectProductCount() {
        log.info("TC-PROD-002: Testing correct product count");
        
        int expectedCount = 6; // SauceDemo has 6 products
        int actualCount = homePage.getProductCount();
        
        Assert.assertEquals(actualCount, expectedCount, 
            "Should display " + expectedCount + " products");
    }
    
    // ==================== REGRESSION TESTS ====================
    
    @Test(priority = 3, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify all products have names")
    @Severity(SeverityLevel.NORMAL)
    @Story("Product Display")
    public void testAllProductsHaveNames() {
        log.info("TC-PROD-003: Testing all products have names");
        
        List<String> productNames = homePage.getAllProductNames();
        
        Assert.assertFalse(productNames.isEmpty(), "Product names list should not be empty");
        
        for (String name : productNames) {
            Assert.assertNotNull(name, "Product name should not be null");
            Assert.assertFalse(name.trim().isEmpty(), "Product name should not be empty");
        }
    }
    
    @Test(priority = 4, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify all products have prices")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Product Display")
    public void testAllProductsHavePrices() {
        log.info("TC-PROD-004: Testing all products have prices");
        
        List<Double> prices = homePage.getAllProductPrices();
        
        Assert.assertFalse(prices.isEmpty(), "Prices list should not be empty");
        
        for (Double price : prices) {
            Assert.assertNotNull(price, "Price should not be null");
            Assert.assertTrue(price > 0, "Price should be positive: " + price);
        }
    }
    
    @Test(priority = 5, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify products can be sorted A to Z")
    @Severity(SeverityLevel.NORMAL)
    @Story("Product Sorting")
    public void testSortProductsAtoZ() {
        log.info("TC-PROD-005: Testing sort A to Z");
        
        homePage.sortProducts(HomePage.SortOption.NAME_A_TO_Z);
        
        List<String> productNames = homePage.getAllProductNames();
        List<String> sortedNames = productNames.stream()
            .sorted()
            .collect(Collectors.toList());
        
        Assert.assertEquals(productNames, sortedNames, 
            "Products should be sorted A to Z");
    }
    
    @Test(priority = 6, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify products can be sorted Z to A")
    @Severity(SeverityLevel.NORMAL)
    @Story("Product Sorting")
    public void testSortProductsZtoA() {
        log.info("TC-PROD-006: Testing sort Z to A");
        
        homePage.sortProducts(HomePage.SortOption.NAME_Z_TO_A);
        
        List<String> productNames = homePage.getAllProductNames();
        List<String> sortedNames = productNames.stream()
            .sorted(Collections.reverseOrder())
            .collect(Collectors.toList());
        
        Assert.assertEquals(productNames, sortedNames, 
            "Products should be sorted Z to A");
    }
    
    @Test(priority = 7, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify products can be sorted price low to high")
    @Severity(SeverityLevel.NORMAL)
    @Story("Product Sorting")
    public void testSortProductsPriceLowToHigh() {
        log.info("TC-PROD-007: Testing sort price low to high");
        
        homePage.sortProducts(HomePage.SortOption.PRICE_LOW_TO_HIGH);
        
        List<Double> prices = homePage.getAllProductPrices();
        List<Double> sortedPrices = prices.stream()
            .sorted()
            .collect(Collectors.toList());
        
        Assert.assertEquals(prices, sortedPrices, 
            "Products should be sorted by price low to high");
    }
    
    @Test(priority = 8, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify products can be sorted price high to low")
    @Severity(SeverityLevel.NORMAL)
    @Story("Product Sorting")
    public void testSortProductsPriceHighToLow() {
        log.info("TC-PROD-008: Testing sort price high to low");
        
        homePage.sortProducts(HomePage.SortOption.PRICE_HIGH_TO_LOW);
        
        List<Double> prices = homePage.getAllProductPrices();
        List<Double> sortedPrices = prices.stream()
            .sorted(Collections.reverseOrder())
            .collect(Collectors.toList());
        
        Assert.assertEquals(prices, sortedPrices, 
            "Products should be sorted by price high to low");
    }
    
    @Test(priority = 9, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify specific products are available")
    @Severity(SeverityLevel.NORMAL)
    @Story("Product Display")
    public void testSpecificProductsAvailable() {
        log.info("TC-PROD-009: Testing specific products available");
        
        List<String> productNames = homePage.getAllProductNames();
        
        String[] expectedProducts = {
            "Sauce Labs Backpack",
            "Sauce Labs Bike Light",
            "Sauce Labs Bolt T-Shirt",
            "Sauce Labs Fleece Jacket",
            "Sauce Labs Onesie"
        };
        
        for (String expectedProduct : expectedProducts) {
            Assert.assertTrue(productNames.contains(expectedProduct), 
                "Product should be available: " + expectedProduct);
        }
    }
    
    @Test(priority = 10, groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Description("Verify product prices are within expected range")
    @Severity(SeverityLevel.MINOR)
    @Story("Product Display")
    public void testProductPricesInRange() {
        log.info("TC-PROD-010: Testing product prices in range");
        
        List<Double> prices = homePage.getAllProductPrices();
        
        double minExpectedPrice = 5.0;
        double maxExpectedPrice = 100.0;
        
        for (Double price : prices) {
            Assert.assertTrue(price >= minExpectedPrice && price <= maxExpectedPrice,
                "Price should be between $" + minExpectedPrice + " and $" + maxExpectedPrice + 
                ": $" + price);
        }
    }
}
