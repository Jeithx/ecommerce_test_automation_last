package com.ecommerce.utils;

import org.testng.annotations.DataProvider;

/**
 * Test Data Provider
 * Provides test data for data-driven tests
 * 
 * @author QA Team
 * @version 2.0
 */
public class TestDataProvider {
    
    /**
     * Valid user credentials
     */
    @DataProvider(name = "validUsers")
    public static Object[][] validUsers() {
        return new Object[][] {
            {"standard_user", "secret_sauce", "Standard User"},
            {"problem_user", "secret_sauce", "Problem User"},
            {"performance_glitch_user", "secret_sauce", "Performance Glitch User"}
        };
    }
    
    /**
     * Invalid user credentials for negative testing
     */
    @DataProvider(name = "invalidUsers")
    public static Object[][] invalidUsers() {
        return new Object[][] {
            {"invalid_user", "secret_sauce", "Invalid Username"},
            {"standard_user", "wrong_password", "Invalid Password"},
            {"", "secret_sauce", "Empty Username"},
            {"standard_user", "", "Empty Password"},
            {"", "", "Empty Credentials"},
            {"locked_out_user", "secret_sauce", "Locked Out User"},
            {"test@#$%", "secret_sauce", "Special Characters Username"},
            {"standard_user", "test@#$%", "Special Characters Password"},
            {" ", "secret_sauce", "Whitespace Username"},
            {"standard_user", " ", "Whitespace Password"}
        };
    }
    
    /**
     * Checkout form data
     */
    @DataProvider(name = "checkoutData")
    public static Object[][] checkoutData() {
        return new Object[][] {
            // firstName, lastName, zipCode, shouldPass
            {"John", "Doe", "12345", true},
            {"Jane", "Smith", "90210", true},
            {"José", "García", "54321", true},
            {"O'Brien", "McDonald", "11111", true},
            {"", "Doe", "12345", false},
            {"John", "", "12345", false},
            {"John", "Doe", "", false},
            {"", "", "", false},
            {"A", "B", "1", true},
            {"VeryLongFirstNameThatExceedsNormalLength", "VeryLongLastNameThatExceedsNormalLength", "12345-6789", true}
        };
    }
    
    /**
     * Product names for testing
     */
    @DataProvider(name = "productNames")
    public static Object[][] productNames() {
        return new Object[][] {
            {"Sauce Labs Backpack"},
            {"Sauce Labs Bike Light"},
            {"Sauce Labs Bolt T-Shirt"},
            {"Sauce Labs Fleece Jacket"},
            {"Sauce Labs Onesie"},
            {"Test.allTheThings() T-Shirt (Red)"}
        };
    }
    
    /**
     * Sort options for product listing
     */
    @DataProvider(name = "sortOptions")
    public static Object[][] sortOptions() {
        return new Object[][] {
            {"az", "Name (A to Z)"},
            {"za", "Name (Z to A)"},
            {"lohi", "Price (low to high)"},
            {"hilo", "Price (high to low)"}
        };
    }
    
    /**
     * Browser configurations for cross-browser testing
     */
    @DataProvider(name = "browsers", parallel = true)
    public static Object[][] browsers() {
        return new Object[][] {
            {"chrome"},
            {"firefox"},
            {"edge"}
        };
    }
    
    /**
     * User types with expected behaviors
     */
    @DataProvider(name = "userTypes")
    public static Object[][] userTypes() {
        return new Object[][] {
            // username, password, expectedBehavior
            {"standard_user", "secret_sauce", "normal"},
            {"locked_out_user", "secret_sauce", "locked"},
            {"problem_user", "secret_sauce", "buggy"},
            {"performance_glitch_user", "secret_sauce", "slow"},
            {"error_user", "secret_sauce", "errors"},
            {"visual_user", "secret_sauce", "visual_bugs"}
        };
    }
}
