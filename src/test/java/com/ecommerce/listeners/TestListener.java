package com.ecommerce.listeners;

import com.ecommerce.tests.base.BaseTest;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

/**
 * Test Listener - Handles test lifecycle events
 * Provides screenshots on failure and Allure integration
 * 
 * @author QA Team
 * @version 2.0
 */
public class TestListener implements ITestListener {
    
    private static final Logger log = LogManager.getLogger(TestListener.class);
    
    @Override
    public void onStart(ITestContext context) {
        log.info("╔════════════════════════════════════════════╗");
        log.info("║     TEST SUITE STARTED: {}          ", context.getName());
        log.info("╚════════════════════════════════════════════╝");
    }
    
    @Override
    public void onFinish(ITestContext context) {
        int passed = context.getPassedTests().size();
        int failed = context.getFailedTests().size();
        int skipped = context.getSkippedTests().size();
        int total = passed + failed + skipped;
        double reliability = total > 0 ? (double) passed / total * 100 : 0;
        
        log.info("╔════════════════════════════════════════════╗");
        log.info("║     TEST SUITE COMPLETED: {}        ", context.getName());
        log.info("╠════════════════════════════════════════════╣");
        log.info("║ ✅ Passed:  {}                              ", passed);
        log.info("║ ❌ Failed:  {}                              ", failed);
        log.info("║ ⚠️  Skipped: {}                              ", skipped);
        log.info("║ 📊 Reliability: {:.2f}%                     ", reliability);
        log.info("╚════════════════════════════════════════════╝");
        
        // Add to Allure
        Allure.addAttachment("Suite Summary", 
            String.format("Passed: %d, Failed: %d, Skipped: %d, Reliability: %.2f%%",
                passed, failed, skipped, reliability));
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        log.info("▶ Starting test: {}", result.getMethod().getMethodName());
        Allure.getLifecycle().updateTestCase(testCase -> 
            testCase.setName(result.getMethod().getMethodName()));
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        log.info("✅ PASSED: {} ({}ms)", result.getMethod().getMethodName(), duration);
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        log.error("❌ FAILED: {} ({}ms)", result.getMethod().getMethodName(), duration);
        log.error("   Reason: {}", result.getThrowable().getMessage());
        
        // Capture screenshot
        captureScreenshot(result);
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("⚠️ SKIPPED: {}", result.getMethod().getMethodName());
        if (result.getThrowable() != null) {
            log.warn("   Reason: {}", result.getThrowable().getMessage());
        }
    }
    
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        log.warn("⚠️ PARTIALLY FAILED: {}", result.getMethod().getMethodName());
    }
    
    /**
     * Capture screenshot on failure
     * Thread-safe implementation
     */
    private void captureScreenshot(ITestResult result) {
        try {
            // Get driver from ThreadLocal in BaseTest
            WebDriver driver = BaseTest.getDriver();
            
            if (driver != null) {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                
                // Add to Allure
                Allure.addAttachment(
                    "Screenshot - " + result.getMethod().getMethodName(),
                    new ByteArrayInputStream(screenshot)
                );
                
                log.info("📸 Screenshot captured for: {}", result.getMethod().getMethodName());
            } else {
                log.warn("Could not capture screenshot: Driver is null");
            }
        } catch (Exception e) {
            log.error("Failed to capture screenshot: {}", e.getMessage());
        }
    }
}
