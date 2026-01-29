package com.ecommerce.listeners;

import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Enhanced Retry Analyzer for handling flaky tests
 * Provides detailed logging and Allure integration
 * 
 * Features:
 * - Configurable retry count
 * - Thread-safe retry tracking
 * - Allure reporting integration
 * - Flaky test detection
 * 
 * @author QA Team
 * @version 2.0
 */
public class RetryAnalyzer implements IRetryAnalyzer {
    
    private static final Logger log = LogManager.getLogger(RetryAnalyzer.class);
    
    private static final int MAX_RETRY_COUNT = 2;
    
    // Thread-safe map to track retries per test method
    private static final ConcurrentHashMap<String, AtomicInteger> retryCountMap = new ConcurrentHashMap<>();
    
    // Track flaky tests for reporting
    private static final ConcurrentHashMap<String, Boolean> flakyTestMap = new ConcurrentHashMap<>();
    
    @Override
    public boolean retry(ITestResult result) {
        String testKey = getTestKey(result);
        
        AtomicInteger retryCount = retryCountMap.computeIfAbsent(testKey, k -> new AtomicInteger(0));
        int currentRetry = retryCount.get();
        
        if (currentRetry < MAX_RETRY_COUNT) {
            retryCount.incrementAndGet();
            
            log.warn("┌─────────────────────────────────────────────");
            log.warn("│ 🔄 RETRY TRIGGERED");
            log.warn("│ Test: {}", result.getMethod().getMethodName());
            log.warn("│ Attempt: {}/{}", retryCount.get(), MAX_RETRY_COUNT);
            log.warn("│ Reason: {}", getFailureReason(result));
            log.warn("└─────────────────────────────────────────────");
            
            // Mark as potentially flaky
            flakyTestMap.put(testKey, true);
            
            // Add to Allure
            Allure.addAttachment("Retry Info", 
                String.format("Test '%s' - Retry attempt %d/%d\nReason: %s",
                    result.getMethod().getMethodName(),
                    retryCount.get(),
                    MAX_RETRY_COUNT,
                    getFailureReason(result)));
            
            return true;
        }
        
        // Max retries reached
        log.error("┌─────────────────────────────────────────────");
        log.error("│ ❌ MAX RETRIES REACHED");
        log.error("│ Test: {}", result.getMethod().getMethodName());
        log.error("│ Total Attempts: {}", MAX_RETRY_COUNT + 1);
        log.error("│ Final Status: FAILED");
        log.error("└─────────────────────────────────────────────");
        
        return false;
    }
    
    /**
     * Generate unique key for each test method
     */
    private String getTestKey(ITestResult result) {
        return result.getTestClass().getName() + "." + result.getMethod().getMethodName();
    }
    
    /**
     * Extract failure reason from test result
     */
    private String getFailureReason(ITestResult result) {
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            String message = throwable.getMessage();
            if (message != null && message.length() > 100) {
                return message.substring(0, 100) + "...";
            }
            return message != null ? message : throwable.getClass().getSimpleName();
        }
        return "Unknown";
    }
    
    /**
     * Check if a test was marked as flaky
     */
    public static boolean isFlaky(String testKey) {
        return flakyTestMap.getOrDefault(testKey, false);
    }
    
    /**
     * Get retry count for a test
     */
    public static int getRetryCount(String testKey) {
        AtomicInteger count = retryCountMap.get(testKey);
        return count != null ? count.get() : 0;
    }
    
    /**
     * Reset retry tracking (call before suite)
     */
    public static void resetTracking() {
        retryCountMap.clear();
        flakyTestMap.clear();
    }
    
    /**
     * Get all flaky tests for reporting
     */
    public static ConcurrentHashMap<String, Boolean> getFlakyTests() {
        return new ConcurrentHashMap<>(flakyTestMap);
    }
    
    /**
     * Get flaky test count
     */
    public static int getFlakyTestCount() {
        return (int) flakyTestMap.values().stream().filter(v -> v).count();
    }
    
    /**
     * Calculate suite reliability percentage
     * Formula: (Total - Flaky) / Total * 100
     */
    public static double calculateReliability(int totalTests) {
        if (totalTests == 0) return 100.0;
        int flakyCount = getFlakyTestCount();
        return ((double) (totalTests - flakyCount) / totalTests) * 100;
    }
}
