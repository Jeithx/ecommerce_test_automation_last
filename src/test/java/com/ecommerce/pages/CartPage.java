package com.ecommerce.pages;

import com.ecommerce.pages.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Cart Page Object
 * Handles shopping cart interactions
 * 
 * @author QA Team
 * @version 2.0
 */
public class CartPage extends BasePage {
    
    // ==================== LOCATORS ====================
    
    @FindBy(css = ".cart_list")
    private WebElement cartList;
    
    @FindBy(css = ".cart_item")
    private List<WebElement> cartItems;
    
    @FindBy(css = ".cart_item .inventory_item_name")
    private List<WebElement> cartItemNames;
    
    @FindBy(css = ".cart_item .inventory_item_price")
    private List<WebElement> cartItemPrices;
    
    @FindBy(id = "checkout")
    private WebElement checkoutButton;
    
    @FindBy(id = "continue-shopping")
    private WebElement continueShoppingButton;
    
    @FindBy(css = ".title")
    private WebElement pageTitle;
    
    // ==================== CONSTRUCTOR ====================
    
    public CartPage(WebDriver driver) {
        super(driver);
        log.info("CartPage initialized");
    }
    
    // ==================== CART ACTIONS ====================
    
    public void removeProduct(String productName) {
        String buttonId = "remove-" + productName.toLowerCase()
            .replace(" ", "-");
        WebElement removeButton = driver.findElement(By.id(buttonId));
        click(removeButton);
        log.info("Removed product from cart: {}", productName);
    }
    
    public CheckoutPage proceedToCheckout() {
        click(checkoutButton);
        log.info("Proceeded to checkout");
        return new CheckoutPage(driver);
    }
    
    public HomePage continueShopping() {
        click(continueShoppingButton);
        log.info("Continued shopping");
        return new HomePage(driver);
    }
    
    // ==================== GETTERS ====================
    
    public int getCartItemCount() {
        return cartItems.size();
    }
    
    public List<String> getCartItemNames() {
        return cartItemNames.stream()
            .map(WebElement::getText)
            .collect(Collectors.toList());
    }
    
    public double getCartTotal() {
        return cartItemPrices.stream()
            .map(el -> el.getText().replace("$", ""))
            .mapToDouble(Double::parseDouble)
            .sum();
    }
    
    public boolean isProductInCart(String productName) {
        return getCartItemNames().contains(productName);
    }
    
    public boolean isCartEmpty() {
        return cartItems.isEmpty();
    }
    
    // ==================== VALIDATIONS ====================
    
    @Override
    public boolean isPageLoaded() {
        try {
            return getCurrentUrl().contains("cart") && isDisplayed(cartList);
        } catch (Exception e) {
            return false;
        }
    }
}
