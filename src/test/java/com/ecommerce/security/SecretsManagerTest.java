package com.ecommerce.security;

import com.ecommerce.exceptions.SecretAccessException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Unit tests for SecretsManager
 *
 * NOTE: These tests require environment variables or .env file to be set up
 *
 * @author QA Team
 * @version 2.0
 */
public class SecretsManagerTest {

    private SecretsManager secretsManager;

    @BeforeMethod
    public void setUp() {
        secretsManager = SecretsManager.getInstance();
        // Reload to ensure fresh state
        secretsManager.reload();
    }

    @Test(description = "Test getting a valid secret from environment")
    public void testGetSecret_ValidSecret() {
        // These secrets should be available from .env file or environment variables
        String standardUser = secretsManager.getSecret("STANDARD_USER");

        assertNotNull(standardUser, "STANDARD_USER secret should be available");
        assertFalse(standardUser.isEmpty(), "Secret should not be empty");
    }

    @Test(description = "Test hasSecret returns true for existing secret")
    public void testHasSecret_ExistingSecret() {
        boolean hasSecret = secretsManager.hasSecret("STANDARD_USER");
        assertTrue(hasSecret, "Should return true for existing secret");
    }

    @Test(description = "Test hasSecret returns false for non-existing secret")
    public void testHasSecret_NonExistingSecret() {
        boolean hasSecret = secretsManager.hasSecret("NON_EXISTENT_SECRET_KEY_12345");
        assertFalse(hasSecret, "Should return false for non-existing secret");
    }

    @Test(description = "Test getSecret with null key throws IllegalArgumentException", expectedExceptions = IllegalArgumentException.class)
    public void testGetSecret_NullKey() {
        secretsManager.getSecret(null);
    }

    @Test(description = "Test getSecret with empty key throws IllegalArgumentException", expectedExceptions = IllegalArgumentException.class)
    public void testGetSecret_EmptyKey() {
        secretsManager.getSecret("");
    }

    @Test(description = "Test getSecret for non-existing required secret throws SecretAccessException", expectedExceptions = SecretAccessException.class)
    public void testGetSecret_NonExistingRequired() {
        secretsManager.getSecret("NON_EXISTENT_SECRET_KEY_12345", true);
    }

    @Test(description = "Test getSecret for non-existing optional secret returns null")
    public void testGetSecret_NonExistingOptional() {
        String value = secretsManager.getSecret("NON_EXISTENT_SECRET_KEY_12345", false);
        assertNull(value, "Should return null for non-existing optional secret");
    }

    @Test(description = "Test getSecretMasked masks the secret value")
    public void testGetSecretMasked() {
        String masked = secretsManager.getSecretMasked("TEST_PASSWORD");

        assertNotNull(masked, "Masked value should not be null");
        assertTrue(masked.contains("*"), "Masked value should contain asterisks");
        assertFalse(masked.contains("secret_sauce"), "Should not contain full password");
    }

    @Test(description = "Test reload refreshes secrets")
    public void testReload() {
        // Get a secret before reload
        String before = secretsManager.getSecret("STANDARD_USER", false);

        // Reload
        secretsManager.reload();

        // Get the same secret after reload
        String after = secretsManager.getSecret("STANDARD_USER", false);

        // Values should be equal (no change unless environment changed)
        assertEquals(before, after, "Secret should be available after reload");
    }

    @Test(description = "Test getActiveProviders returns at least one provider")
    public void testGetActiveProviders() {
        var providers = secretsManager.getActiveProviders();

        assertNotNull(providers, "Provider list should not be null");
        assertFalse(providers.isEmpty(), "Should have at least one provider");
        assertTrue(providers.contains("EnvironmentSecretsProvider"), "Should have EnvironmentSecretsProvider");
    }

    @Test(description = "Test validateSecret accepts valid secret")
    public void testValidateSecret_Valid() {
        // Should not throw exception
        secretsManager.validateSecret("TEST_KEY", "valid_value");
    }

    @Test(description = "Test validateSecret rejects null value", expectedExceptions = SecretAccessException.class)
    public void testValidateSecret_Null() {
        secretsManager.validateSecret("TEST_KEY", null);
    }

    @Test(description = "Test validateSecret rejects empty value", expectedExceptions = SecretAccessException.class)
    public void testValidateSecret_Empty() {
        secretsManager.validateSecret("TEST_KEY", "");
    }

    @Test(description = "Test validateSecret rejects whitespace-only value", expectedExceptions = SecretAccessException.class)
    public void testValidateSecret_Whitespace() {
        secretsManager.validateSecret("TEST_KEY", "   ");
    }

    @Test(description = "Test SecretAccessException has helpful message")
    public void testSecretAccessException_HelpfulMessage() {
        try {
            secretsManager.getSecret("MISSING_SECRET_KEY_FOR_TEST", true);
            fail("Should have thrown SecretAccessException");
        } catch (SecretAccessException e) {
            String message = e.getMessage();

            // Verify message contains helpful information
            assertTrue(message.contains("MISSING_SECRET_KEY_FOR_TEST"), "Should mention the missing key");
            assertTrue(message.contains(".env"), "Should mention .env file");
            assertTrue(message.contains("GitHub Secrets"), "Should mention GitHub Secrets");
            assertTrue(message.contains("SECURITY.md"), "Should reference security docs");
        }
    }

    @Test(description = "Test hasSecret with null key returns false")
    public void testHasSecret_NullKey() {
        boolean hasSecret = secretsManager.hasSecret(null);
        assertFalse(hasSecret, "Should return false for null key");
    }

    @Test(description = "Test hasSecret with empty key returns false")
    public void testHasSecret_EmptyKey() {
        boolean hasSecret = secretsManager.hasSecret("");
        assertFalse(hasSecret, "Should return false for empty key");
    }

    @Test(description = "Test singleton pattern returns same instance")
    public void testSingleton() {
        SecretsManager instance1 = SecretsManager.getInstance();
        SecretsManager instance2 = SecretsManager.getInstance();

        assertSame(instance1, instance2, "Should return same instance (singleton)");
    }

    @Test(description = "Test all required test credentials are available")
    public void testAllRequiredCredentials() {
        String[] requiredSecrets = {
            "STANDARD_USER",
            "LOCKED_USER",
            "PROBLEM_USER",
            "PERFORMANCE_USER",
            "TEST_PASSWORD"
        };

        for (String secretKey : requiredSecrets) {
            assertTrue(secretsManager.hasSecret(secretKey),
                "Required secret '" + secretKey + "' should be available");

            String value = secretsManager.getSecret(secretKey);
            assertNotNull(value, "Secret '" + secretKey + "' should not be null");
            assertFalse(value.isEmpty(), "Secret '" + secretKey + "' should not be empty");
        }
    }
}
