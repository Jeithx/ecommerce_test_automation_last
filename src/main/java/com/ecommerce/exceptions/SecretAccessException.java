package com.ecommerce.exceptions;

/**
 * Exception thrown when secrets cannot be accessed or loaded
 *
 * @author QA Team
 * @version 2.0
 */
public class SecretAccessException extends RuntimeException {

    public SecretAccessException(String message) {
        super(message);
    }

    public SecretAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create an exception with setup instructions for the user
     *
     * @param secretKey the secret key that failed to load
     * @return a new SecretAccessException with helpful error message
     */
    public static SecretAccessException missingSecret(String secretKey) {
        String message = String.format(
            "Required secret '%s' not found!%n%n" +
            "Please set up your environment:%n" +
            "  1. Copy .env.template to .env%n" +
            "  2. Fill in the required values%n" +
            "  OR%n" +
            "  Set environment variable: %s%n%n" +
            "For CI/CD, ensure GitHub Secrets are configured.%n" +
            "See docs/SECURITY.md for details.",
            secretKey, secretKey
        );
        return new SecretAccessException(message);
    }
}
