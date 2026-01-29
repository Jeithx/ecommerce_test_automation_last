package com.ecommerce.pages;

import com.ecommerce.pages.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Home Page Object (Products Page)
 * Handles product listing, cart, and navigation
 * 
 * @author QA Team
 * @version 2.0
 */
public class HomePage extends BasePage {
    
    // ==================== LOCATORS ====================
    
    @FindBy(css = ".inventory_list")
    private WebElement inventoryList;
    
    @FindBy(css = ".inventory_item")
    private List<WebElement> inventoryItems;
    
    @FindBy(css = ".shopping_cart_link")
    private WebElement cartLink;
    
    @FindBy(css = ".shopping_cart_badge")
    private WebElement cartBadge;
    
    @FindBy(id = "react-burger-menu-btn")
    private WebElement menuButton;
    
    @FindBy(id = "logout_sidebar_link")
    private WebElement logoutLink;
    
    @FindBy(css = ".product_sort_container")
    private WebElement sortDropdown;
    
    @FindBy(css = ".inventory_item_name")
    private List<WebElement> productNames;
    
    @FindBy(css = ".inventory_item_price")
    private List<WebElement> productPrices;
    
    @FindBy(css = ".title")
    private WebElement pageTitle;
    
    // ==================== SORT OPTIONS ====================
    
    public enum SortOption {
        NAME_A_TO_Z("az"),
        NAME_Z_TO_A("za"),
        PRICE_LOW_TO_HIGH("lohi"),
        PRICE_HIGH_TO_LOW("hilo");
        
        private final String value;
        
        SortOption(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
    
    // ==================== CONSTRUCTOR ====================
    
    public HomePage(WebDriver driver) {
        super(driver);
        log.info("HomePage initialized");
    }
    
    // ==================== PRODUCT ACTIONS ====================
    
    public void addProductToCart(String productName) {
        String buttonId = "add-to-cart-" + productName.toLowerCase()
            .replace(" ", "-");
        WebElement addButton = driver.findElement(By.id(buttonId));
        click(addButton);
        log.info("Added product to cart: {}", productName);
    }
    
    public void addProductToCart(int index) {
        if (index < inventoryItems.size()) {
            WebElement item = inventoryItems.get(index);
            WebElement addButton = item.findElement(By.cssSelector("button[id^='add-to-cart']"));
            click(addButton);
            log.info("Added product at index {} to cart", index);
        }
    }
    
    public void removeProductFromCart(String productName) {
        String buttonId = "remove-" + productName.toLowerCase()
            .replace(" ", "-");
        WebElement removeButton = driver.findElement(By.id(buttonId));
        click(removeButton);
        log.info("Removed product from cart: {}", productName);
    }
    
    public boolean isProductInCart(String productName) {
        String buttonId = "remove-" + productName.toLowerCase()
            .replace(" ", "-");
        try {
            return driver.findElement(By.id(buttonId)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    // ==================== SORTING ====================
    
    public void sortProducts(SortOption option) {
        selectByValue(sortDropdown, option.getValue());
        log.info("Sorted products by: {}", option);
    }
    
    // ==================== GETTERS ====================
    
    public int getProductCount() {
        return inventoryItems.size();
    }
    
    public List<String> getAllProductNames() {
        return productNames.stream()
            .map(WebElement::getText)
            .collect(Collectors.toList());
    }
    
    public List<Double> getAllProductPrices() {
        return productPrices.stream()
            .map(el -> el.getText().replace("$", ""))
            .map(Double::parseDouble)
            .collect(Collectors.toList());
    }
    
    public int getCartItemCount() {
        try {
            if (isDisplayed(cartBadge)) {
                return Integer.parseInt(cartBadge.getText());
            }
        } catch (Exception e) {
            // Badge not visible means 0 items
        }
        return 0;
    }
    
    // ==================== NAVIGATION ====================
    
    public CartPage goToCart() {
        click(cartLink);
        log.info("Navigated to cart");
        return new CartPage(driver);
    }
    
    public void openMenu() {
        click(menuButton);
        log.info("Opened menu");
    }
    
    public LoginPage logout() {
        openMenu();
        waitForElementClickable(logoutLink);
        click(logoutLink);
        log.info("Logged out");
        return new LoginPage(driver);
    }
    
    // ==================== VALIDATIONS ====================
    
    public boolean isUserLoggedIn() {
        return getCurrentUrl().contains("inventory");
    }
    
    @Override
    public boolean isPageLoaded() {
        try {
            return isDisplayed(inventoryList) && inventoryItems.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
