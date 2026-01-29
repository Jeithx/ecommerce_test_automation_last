package com.ecommerce.security;

import com.ecommerce.exceptions.SecretAccessException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Facade for managing secrets from multiple providers
 *
 * This class orchestrates secret loading from multiple sources
 * and provides a unified interface for the application.
 *
 * Thread-safe singleton pattern.
 *
 * @author QA Team
 * @version 2.0
 */
public class SecretsManager {

    private static final Logger log = LogManager.getLogger(SecretsManager.class);

    private static volatile SecretsManager instance;
    private final List<SecretsProvider> providers;
    private final Object lock = new Object();

    private SecretsManager() {
        this.providers = new ArrayList<>();
        initializeProviders();
    }

    /**
     * Get the singleton instance of SecretsManager
     *
     * @return the SecretsManager instance
     */
    public static SecretsManager getInstance() {
        if (instance == null) {
            synchronized (SecretsManager.class) {
                if (instance == null) {
                    instance = new SecretsManager();
                }
            }
        }
        return instance;
    }

    private void initializeProviders() {
        // Add EnvironmentSecretsProvider (env vars + .env file)
        providers.add(new EnvironmentSecretsProvider());

        // Future: Add AzureKeyVaultSecretsProvider based on config
        // Future: Add AwsSecretsManagerProvider based on config

        log.info("SecretsManager initialized with {} provider(s)", providers.size());
    }

    /**
     * Get a secret value by key
     *
     * Tries each provider in order until a value is found
     *
     * @param key the secret key
     * @return the secret value
     * @throws SecretAccessException if the secret is not found
     */
    public String getSecret(String key) {
        return getSecret(key, true);
    }

    /**
     * Get a secret value by key
     *
     * @param key the secret key
     * @param required if true, throws exception if not found; if false, returns null
     * @return the secret value, or null if not found and not required
     * @throws SecretAccessException if required=true and secret is not found
     */
    public String getSecret(String key, boolean required) {
        validateKey(key);

        for (SecretsProvider provider : providers) {
            String value = provider.getSecret(key);
            if (value != null && !value.isEmpty()) {
                log.debug("Secret '{}' retrieved from provider: {}", key, provider.getProviderName());
                return value;
            }
        }

        if (required) {
            log.error("Required secret '{}' not found in any provider", key);
            throw SecretAccessException.missingSecret(key);
        }

        return null;
    }

    /**
     * Get a secret with a masked value for logging
     *
     * @param key the secret key
     * @return the masked secret value
     */
    public String getSecretMasked(String key) {
        String value = getSecret(key);
        return CredentialMasker.maskCredential(value);
    }

    /**
     * Check if a secret exists in any provider
     *
     * @param key the secret key
     * @return true if the secret exists, false otherwise
     */
    public boolean hasSecret(String key) {
        if (key == null || key.isEmpty()) {
            return false;
        }

        for (SecretsProvider provider : providers) {
            if (provider.hasSecret(key)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Reload secrets from all providers
     */
    public void reload() {
        synchronized (lock) {
            for (SecretsProvider provider : providers) {
                provider.reload();
            }
            log.info("All secrets providers reloaded");
        }
    }

    /**
     * Validate a secret value
     *
     * @param key the secret key (for error messages)
     * @param value the secret value to validate
     * @throws SecretAccessException if validation fails
     */
    public void validateSecret(String key, String value) {
        if (value == null || value.isEmpty()) {
            throw new SecretAccessException("Secret '" + key + "' is null or empty");
        }

        if (value.trim().isEmpty()) {
            throw new SecretAccessException("Secret '" + key + "' contains only whitespace");
        }

        // Add more validation rules as needed
        // For example: min/max length, allowed characters, etc.
    }

    private void validateKey(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Secret key cannot be null or empty");
        }
    }

    /**
     * Get all active provider names (for debugging/logging)
     *
     * @return list of provider names
     */
    public List<String> getActiveProviders() {
        List<String> names = new ArrayList<>();
        for (SecretsProvider provider : providers) {
            names.add(provider.getProviderName());
        }
        return names;
    }
}
