package com.ecommerce.security;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Secrets provider that loads from environment variables and .env files
 *
 * Loading hierarchy:
 *   1. System environment variables (highest priority)
 *   2. .env file in project root
 *
 * @author QA Team
 * @version 2.0
 */
public class EnvironmentSecretsProvider implements SecretsProvider {

    private static final Logger log = LogManager.getLogger(EnvironmentSecretsProvider.class);

    private Dotenv dotenv;
    private final Map<String, String> cache;
    private final Object lock = new Object();

    public EnvironmentSecretsProvider() {
        this.cache = new ConcurrentHashMap<>();
        loadDotenv();
    }

    private void loadDotenv() {
        try {
            // Try to load .env file from project root
            // configure() with ignoreIfMissing will not throw exception if .env doesn't exist
            dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            if (dotenv != null && dotenv.entries().size() > 0) {
                log.info("Loaded {} secrets from .env file", dotenv.entries().size());
            } else {
                log.info("No .env file found, will use environment variables only");
            }
        } catch (DotenvException e) {
            log.warn("Failed to load .env file: {}. Will use environment variables only.", e.getMessage());
            dotenv = null;
        }
    }

    @Override
    public String getSecret(String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }

        // Check cache first
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        String value = null;

        // 1. Try system environment variable first (highest priority)
        value = System.getenv(key);
        if (value != null && !value.isEmpty()) {
            log.debug("Secret '{}' loaded from environment variable", key);
            cache.put(key, value);
            return value;
        }

        // 2. Try .env file
        if (dotenv != null) {
            value = dotenv.get(key);
            if (value != null && !value.isEmpty()) {
                log.debug("Secret '{}' loaded from .env file", key);
                cache.put(key, value);
                return value;
            }
        }

        log.debug("Secret '{}' not found in environment variables or .env file", key);
        return null;
    }

    @Override
    public boolean hasSecret(String key) {
        return getSecret(key) != null;
    }

    @Override
    public void reload() {
        synchronized (lock) {
            cache.clear();
            loadDotenv();
            log.info("Secrets reloaded from environment and .env file");
        }
    }

    @Override
    public String getProviderName() {
        return "EnvironmentSecretsProvider";
    }
}
