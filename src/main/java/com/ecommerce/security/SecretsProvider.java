package com.ecommerce.security;

/**
 * Interface for providing secrets from various sources
 *
 * This abstraction allows for multiple implementations:
 * - EnvironmentSecretsProvider (env vars + .env files)
 * - AzureKeyVaultSecretsProvider (future)
 * - AwsSecretsManagerProvider (future)
 *
 * @author QA Team
 * @version 2.0
 */
public interface SecretsProvider {

    /**
     * Retrieve a secret value by key
     *
     * @param key the secret key to retrieve
     * @return the secret value, or null if not found
     */
    String getSecret(String key);

    /**
     * Check if a secret exists
     *
     * @param key the secret key to check
     * @return true if the secret exists, false otherwise
     */
    boolean hasSecret(String key);

    /**
     * Reload secrets from the source
     * This allows for refreshing cached secrets or picking up changes
     */
    void reload();

    /**
     * Get the name of this secrets provider (for logging/debugging)
     *
     * @return the provider name
     */
    String getProviderName();
}
