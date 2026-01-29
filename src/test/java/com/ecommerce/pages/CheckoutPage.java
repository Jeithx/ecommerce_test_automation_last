package com.ecommerce.pages;

import com.ecommerce.pages.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Checkout Page Object
 * Handles checkout form and process
 * 
 * @author QA Team
 * @version 2.0
 */
public class CheckoutPage extends BasePage {
    
    // ==================== LOCATORS ====================
    
    @FindBy(id = "first-name")
    private WebElement firstNameField;
    
    @FindBy(id = "last-name")
    private WebElement lastNameField;
    
    @FindBy(id = "postal-code")
    private WebElement zipCodeField;
    
    @FindBy(id = "continue")
    private WebElement continueButton;
    
    @FindBy(id = "cancel")
    private WebElement cancelButton;
    
    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;
    
    @FindBy(css = ".title")
    private WebElement pageTitle;
    
    @FindBy(css = ".checkout_info")
    private WebElement checkoutInfo;
    
    // ==================== CONSTRUCTOR ====================
    
    public CheckoutPage(WebDriver driver) {
        super(driver);
        log.info("CheckoutPage initialized");
    }
    
    // ==================== FORM ACTIONS ====================
    
    public CheckoutPage enterFirstName(String firstName) {
        type(firstNameField, firstName);
        log.info("Entered first name: {}", firstName);
        return this;
    }
    
    public CheckoutPage enterLastName(String lastName) {
        type(lastNameField, lastName);
        log.info("Entered last name: {}", lastName);
        return this;
    }
    
    public CheckoutPage enterZipCode(String zipCode) {
        type(zipCodeField, zipCode);
        log.info("Entered zip code: {}", zipCode);
        return this;
    }
    
    public CheckoutPage fillCheckoutInfo(String firstName, String lastName, String zipCode) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterZipCode(zipCode);
        return this;
    }
    
    public void continueToOverview() {
        click(continueButton);
        log.info("Clicked continue button");
    }
    
    public CartPage cancelCheckout() {
        click(cancelButton);
        log.info("Cancelled checkout");
        return new CartPage(driver);
    }
    
    // ==================== VALIDATIONS ====================
    
    public boolean isFormValid() {
        String firstName = firstNameField.getAttribute("value");
        String lastName = lastNameField.getAttribute("value");
        String zipCode = zipCodeField.getAttribute("value");
        
        return !firstName.isEmpty() && !lastName.isEmpty() && !zipCode.isEmpty();
    }
    
    public boolean isErrorDisplayed() {
        try {
            return isDisplayed(errorMessage);
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getErrorMessage() {
        if (isErrorDisplayed()) {
            return getText(errorMessage);
        }
        return "";
    }
    
    @Override
    public boolean isPageLoaded() {
        try {
            return getCurrentUrl().contains("checkout") && 
                   (isDisplayed(checkoutInfo) || isDisplayed(firstNameField));
        } catch (Exception e) {
            return false;
        }
    }
}
