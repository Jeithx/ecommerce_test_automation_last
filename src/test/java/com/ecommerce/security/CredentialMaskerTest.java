package com.ecommerce.security;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Unit tests for CredentialMasker utility
 *
 * @author QA Team
 * @version 2.0
 */
public class CredentialMaskerTest {

    @Test(description = "Test masking of normal length credentials")
    public void testMaskCredential_NormalLength() {
        String credential = "secret_sauce";
        String masked = CredentialMasker.maskCredential(credential);

        assertEquals(masked, "se*********ce", "Credential should be masked with first and last 2 chars visible");
        assertNotEquals(masked, credential, "Masked value should differ from original");
    }

    @Test(description = "Test masking of short credentials")
    public void testMaskCredential_ShortString() {
        String credential = "ab";
        String masked = CredentialMasker.maskCredential(credential);

        assertEquals(masked, "**", "Short credentials should be fully masked");
    }

    @Test(description = "Test masking of null credential")
    public void testMaskCredential_Null() {
        String masked = CredentialMasker.maskCredential(null);
        assertNull(masked, "Null input should return null");
    }

    @Test(description = "Test masking of empty credential")
    public void testMaskCredential_Empty() {
        String masked = CredentialMasker.maskCredential("");
        assertEquals(masked, "", "Empty string should return empty string");
    }

    @Test(description = "Test masking of single character")
    public void testMaskCredential_SingleChar() {
        String credential = "a";
        String masked = CredentialMasker.maskCredential(credential);
        assertEquals(masked, "*", "Single character should be masked");
    }

    @Test(description = "Test masking of medium length credential")
    public void testMaskCredential_MediumLength() {
        String credential = "admin123";
        String masked = CredentialMasker.maskCredential(credential);

        assertTrue(masked.startsWith("ad"), "Should show first 2 chars");
        assertTrue(masked.endsWith("23"), "Should show last 2 chars");
        assertTrue(masked.contains("*"), "Should contain masked characters");
    }

    @Test(description = "Test masking credentials in text - password pattern")
    public void testMaskCredentialsInText_Password() {
        String text = "Failed login with password=secret_sauce for user admin";
        String masked = CredentialMasker.maskCredentialsInText(text);

        assertTrue(masked.contains("password=***MASKED***"), "Password value should be masked");
        assertFalse(masked.contains("secret_sauce"), "Original password should not appear");
    }

    @Test(description = "Test masking credentials in text - API key pattern")
    public void testMaskCredentialsInText_ApiKey() {
        String text = "Using api_key: 1234567890abcdef";
        String masked = CredentialMasker.maskCredentialsInText(text);

        assertTrue(masked.contains("api_key: ***MASKED***"), "API key should be masked");
        assertFalse(masked.contains("1234567890abcdef"), "Original API key should not appear");
    }

    @Test(description = "Test masking credentials in text - token pattern")
    public void testMaskCredentialsInText_Token() {
        String text = "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        String masked = CredentialMasker.maskCredentialsInText(text);

        assertTrue(masked.contains("Bearer ***MASKED***"), "Token should be masked");
    }

    @Test(description = "Test masking credentials in text - secret pattern")
    public void testMaskCredentialsInText_Secret() {
        String text = "client_secret=abcd1234efgh5678";
        String masked = CredentialMasker.maskCredentialsInText(text);

        assertTrue(masked.contains("client_secret=***MASKED***"), "Secret should be masked");
        assertFalse(masked.contains("abcd1234efgh5678"), "Original secret should not appear");
    }

    @Test(description = "Test masking credentials in text - null input")
    public void testMaskCredentialsInText_Null() {
        String masked = CredentialMasker.maskCredentialsInText(null);
        assertNull(masked, "Null input should return null");
    }

    @Test(description = "Test masking credentials in text - no credentials")
    public void testMaskCredentialsInText_NoCredentials() {
        String text = "This is a normal log message with no credentials";
        String masked = CredentialMasker.maskCredentialsInText(text);

        assertEquals(masked, text, "Text without credentials should remain unchanged");
    }

    @Test(description = "Test masking URL credentials")
    public void testMaskUrlCredentials() {
        String url = "https://admin:password123@example.com/api";
        String masked = CredentialMasker.maskUrlCredentials(url);

        assertEquals(masked, "https://***:***@example.com/api", "URL credentials should be masked");
        assertFalse(masked.contains("admin"), "Username should not appear");
        assertFalse(masked.contains("password123"), "Password should not appear");
    }

    @Test(description = "Test masking URL without credentials")
    public void testMaskUrlCredentials_NoCredentials() {
        String url = "https://example.com/api";
        String masked = CredentialMasker.maskUrlCredentials(url);

        assertEquals(masked, url, "URL without credentials should remain unchanged");
    }

    @Test(description = "Test masking URL credentials - null input")
    public void testMaskUrlCredentials_Null() {
        String masked = CredentialMasker.maskUrlCredentials(null);
        assertNull(masked, "Null input should return null");
    }

    @Test(description = "Test masking multiple credential types in same text")
    public void testMaskCredentialsInText_MultipleTypes() {
        String text = "Login failed: password=secret123 api_key=xyz789 token=abc456";
        String masked = CredentialMasker.maskCredentialsInText(text);

        assertTrue(masked.contains("password=***MASKED***"), "Password should be masked");
        assertTrue(masked.contains("api_key=***MASKED***"), "API key should be masked");
        assertTrue(masked.contains("token=***MASKED***"), "Token should be masked");
        assertFalse(masked.contains("secret123"), "Original password should not appear");
        assertFalse(masked.contains("xyz789"), "Original API key should not appear");
        assertFalse(masked.contains("abc456"), "Original token should not appear");
    }
}
