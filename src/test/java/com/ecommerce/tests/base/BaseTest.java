package com.ecommerce.tests.base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.ecommerce.config.ConfigManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Base Test Class - Foundation for all test classes
 * Supports both local and Selenium Grid execution
 * Thread-safe implementation with proper cleanup
 * 
 * @author QA Team
 * @version 2.0
 */
public class BaseTest {
    
    private static final Logger log = LogManager.getLogger(BaseTest.class);
    
    // ThreadLocal for thread-safe driver management
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<WebDriverWait> waitThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();
    
    // Test metrics for reliability tracking
    private static final Map<String, TestMetrics> testMetricsMap = new HashMap<>();
    
    protected ConfigManager config;
    protected static ExtentReports extent;
    
    private static final String TIMESTAMP = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
    
    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        log.info("╔════════════════════════════════════════════╗");
        log.info("║     E-COMMERCE TEST SUITE STARTED          ║");
        log.info("╚════════════════════════════════════════════╝");
        config = ConfigManager.getInstance();
        setupExtentReports();
    }
    
    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser", "environment"})
    public void setUp(@Optional("chrome") String browser,
                      @Optional("staging") String environment,
                      ITestResult result) {
        
        log.info("┌─────────────────────────────────────────────");
        log.info("│ Setting up test: {}", result.getMethod().getMethodName());
        log.info("│ Browser: {} | Environment: {}", browser, environment);
        log.info("└─────────────────────────────────────────────");
        
        config = ConfigManager.getInstance();
        
        // Create ExtentTest
        ExtentTest test = extent.createTest(result.getMethod().getMethodName());
        test.assignCategory(getTestGroups(result));
        extentTestThreadLocal.set(test);
        
        // Initialize WebDriver (Local or Grid)
        initializeDriver(browser);
        
        // Navigate to base URL
        getDriver().get(config.getProperty("base.url"));
        log.info("Navigated to: {}", config.getProperty("base.url"));
    }
    
    /**
     * Initialize WebDriver - supports both local and Selenium Grid
     */
    private void initializeDriver(String browserName) {
        WebDriver webDriver;
        boolean isHeadless = config.getBoolean("headless", false);
        boolean useGrid = config.getBoolean("selenium.grid", false);
        String gridUrl = config.getProperty("selenium.grid.url", "http://localhost:4444/wd/hub");
        
        // Check for environment variable override (Docker)
        String envGridUrl = System.getenv("SELENIUM_GRID_URL");
        if (envGridUrl != null && !envGridUrl.isEmpty()) {
            useGrid = true;
            gridUrl = envGridUrl;
        }
        
        if (useGrid) {
            webDriver = createRemoteDriver(browserName, gridUrl, isHeadless);
            log.info("Using Selenium Grid at: {}", gridUrl);
        } else {
            webDriver = createLocalDriver(browserName, isHeadless);
            log.info("Using local WebDriver");
        }
        
        // Configure driver
        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(
            Duration.ofSeconds(config.getInt("implicit.wait", 10)));
        webDriver.manage().timeouts().pageLoadTimeout(
            Duration.ofSeconds(config.getInt("page.load.timeout", 30)));
        
        // Store in ThreadLocal
        driverThreadLocal.set(webDriver);
        waitThreadLocal.set(new WebDriverWait(webDriver, 
            Duration.ofSeconds(config.getInt("explicit.wait", 20))));
        
        log.info("Driver initialized: {} (Grid: {})", browserName, useGrid);
    }
    
    /**
     * Create Remote WebDriver for Selenium Grid
     */
    private WebDriver createRemoteDriver(String browserName, String gridUrl, boolean isHeadless) {
        try {
            URL hubUrl = new URL(gridUrl);
            
            switch (browserName.toLowerCase()) {
                case "firefox":
                    FirefoxOptions ffOptions = new FirefoxOptions();
                    if (isHeadless) ffOptions.addArguments("--headless");
                    return new RemoteWebDriver(hubUrl, ffOptions);
                    
                case "edge":
                    EdgeOptions edgeOptions = new EdgeOptions();
                    if (isHeadless) edgeOptions.addArguments("--headless");
                    return new RemoteWebDriver(hubUrl, edgeOptions);
                    
                case "chrome":
                default:
                    ChromeOptions chromeOptions = new ChromeOptions();
                    if (isHeadless) chromeOptions.addArguments("--headless");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--window-size=1920,1080");
                    chromeOptions.addArguments("--disable-gpu");
                    return new RemoteWebDriver(hubUrl, chromeOptions);
            }
        } catch (MalformedURLException e) {
            log.error("Invalid Selenium Grid URL: {}", gridUrl);
            throw new RuntimeException("Invalid Selenium Grid URL", e);
        }
    }
    
    /**
     * Create Local WebDriver
     */
    private WebDriver createLocalDriver(String browserName, boolean isHeadless) {
        switch (browserName.toLowerCase()) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions ffOptions = new FirefoxOptions();
                if (isHeadless) ffOptions.addArguments("--headless");
                return new FirefoxDriver(ffOptions);
                
            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                if (isHeadless) edgeOptions.addArguments("--headless");
                return new EdgeDriver(edgeOptions);
                
            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (isHeadless) chromeOptions.addArguments("--headless");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--window-size=1920,1080");
                return new ChromeDriver(chromeOptions);
        }
    }
    
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        try {
            String testName = result.getMethod().getMethodName();
            long duration = result.getEndMillis() - result.getStartMillis();
            
            // Track metrics
            trackTestMetrics(testName, result.getStatus(), duration);
            
            // Handle test result
            if (result.getStatus() == ITestResult.FAILURE) {
                captureScreenshot(result);
                logFailure(result);
            } else if (result.getStatus() == ITestResult.SUCCESS) {
                logSuccess(result);
            } else if (result.getStatus() == ITestResult.SKIP) {
                logSkip(result);
            }
        } finally {
            // CRITICAL: Clean up resources
            cleanupDriver();
        }
    }
    
    /**
     * Track test metrics for reliability calculation
     */
    private void trackTestMetrics(String testName, int status, long duration) {
        TestMetrics metrics = testMetricsMap.computeIfAbsent(testName, k -> new TestMetrics());
        metrics.totalRuns++;
        metrics.totalDuration += duration;
        
        if (status == ITestResult.SUCCESS) {
            metrics.passCount++;
        } else if (status == ITestResult.FAILURE) {
            metrics.failCount++;
        }
    }
    
    /**
     * Clean up driver and ThreadLocal references
     */
    private void cleanupDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                log.info("Driver closed");
            } catch (Exception e) {
                log.warn("Error closing driver: {}", e.getMessage());
            } finally {
                // IMPORTANT: Remove ThreadLocal references to prevent memory leak
                driverThreadLocal.remove();
                waitThreadLocal.remove();
                extentTestThreadLocal.remove();
            }
        }
    }
    
    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        // Log reliability metrics
        logReliabilityMetrics();
        
        log.info("╔════════════════════════════════════════════╗");
        log.info("║     E-COMMERCE TEST SUITE COMPLETED        ║");
        log.info("╚════════════════════════════════════════════╝");
        
        if (extent != null) {
            extent.flush();
        }
    }
    
    /**
     * Log suite reliability metrics
     */
    private void logReliabilityMetrics() {
        int totalTests = 0;
        int totalPassed = 0;
        long totalDuration = 0;
        
        for (TestMetrics metrics : testMetricsMap.values()) {
            totalTests += metrics.totalRuns;
            totalPassed += metrics.passCount;
            totalDuration += metrics.totalDuration;
        }
        
        double reliability = totalTests > 0 ? (double) totalPassed / totalTests * 100 : 0;
        
        log.info("┌─────────────────────────────────────────────");
        log.info("│ RELIABILITY METRICS");
        log.info("├─────────────────────────────────────────────");
        log.info("│ Total Test Runs: {}", totalTests);
        log.info("│ Passed: {} | Failed: {}", totalPassed, totalTests - totalPassed);
        log.info("│ Suite Reliability: {:.2f}%", reliability);
        log.info("│ Total Duration: {} ms", totalDuration);
        log.info("└─────────────────────────────────────────────");
        
        // Add to Allure
        Allure.addAttachment("Suite Reliability", 
            String.format("%.2f%% (%d/%d tests passed)", reliability, totalPassed, totalTests));
    }
    
    private void setupExtentReports() {
        String reportPath = "reports/extent/ExtentReport_" + TIMESTAMP + ".html";
        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        spark.config().setDocumentTitle("E-Commerce Test Report");
        spark.config().setReportName("Test Execution Report - " + TIMESTAMP);
        spark.config().setTheme(com.aventstack.extentreports.reporter.configuration.Theme.DARK);
        
        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java", System.getProperty("java.version"));
        extent.setSystemInfo("Browser", config.getProperty("browser", "chrome"));
        extent.setSystemInfo("Environment", config.getProperty("environment", "staging"));
        extent.setSystemInfo("Selenium Grid", config.getBoolean("selenium.grid", false) ? "Enabled" : "Disabled");
    }
    
    private void captureScreenshot(ITestResult result) {
        WebDriver driver = getDriver();
        if (driver != null && config.getBoolean("screenshot.on.failure", true)) {
            try {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment("Screenshot - " + result.getMethod().getMethodName(), 
                    new ByteArrayInputStream(screenshot));
                
                ExtentTest test = getExtentTest();
                if (test != null) {
                    test.addScreenCaptureFromBase64String(
                        java.util.Base64.getEncoder().encodeToString(screenshot));
                }
                log.info("Screenshot captured for: {}", result.getMethod().getMethodName());
            } catch (Exception e) {
                log.error("Failed to capture screenshot: {}", e.getMessage());
            }
        }
    }
    
    private String[] getTestGroups(ITestResult result) {
        return result.getMethod().getGroups();
    }
    
    private void logSuccess(ITestResult result) {
        ExtentTest test = getExtentTest();
        if (test != null) {
            test.log(Status.PASS, "✅ Test passed in " + 
                (result.getEndMillis() - result.getStartMillis()) + "ms");
        }
        log.info("✅ TEST PASSED: {} ({}ms)", 
            result.getMethod().getMethodName(),
            result.getEndMillis() - result.getStartMillis());
    }
    
    private void logFailure(ITestResult result) {
        ExtentTest test = getExtentTest();
        if (test != null) {
            test.log(Status.FAIL, "❌ Test failed: " + result.getThrowable().getMessage());
            test.fail(result.getThrowable());
        }
        log.error("❌ TEST FAILED: {} - {}", 
            result.getMethod().getMethodName(), 
            result.getThrowable().getMessage());
    }
    
    private void logSkip(ITestResult result) {
        ExtentTest test = getExtentTest();
        if (test != null) {
            test.log(Status.SKIP, "⚠️ Test skipped");
        }
        log.warn("⚠️ TEST SKIPPED: {}", result.getMethod().getMethodName());
    }
    
    // Static getters for thread-safe access
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }
    
    public static WebDriverWait getWait() {
        return waitThreadLocal.get();
    }
    
    public static ExtentTest getExtentTest() {
        return extentTestThreadLocal.get();
    }
    
    /**
     * Inner class for tracking test metrics
     */
    private static class TestMetrics {
        int totalRuns = 0;
        int passCount = 0;
        int failCount = 0;
        long totalDuration = 0;
    }
}
