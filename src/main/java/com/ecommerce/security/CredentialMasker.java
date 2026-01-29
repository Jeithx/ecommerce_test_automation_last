package com.ecommerce.security;

import java.util.regex.Pattern;

/**
 * Utility class for masking credentials in logs and error messages
 *
 * @author QA Team
 * @version 2.0
 */
public class CredentialMasker {

    private static final int VISIBLE_CHARS = 2;
    private static final String MASK_CHAR = "*";
    private static final int MIN_LENGTH_TO_MASK = 3;

    // Patterns for common secret formats
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("(password|passwd|pwd)([=:\\s]+)([^\\s&]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern API_KEY_PATTERN = Pattern.compile("(api[_-]?key|apikey)([=:\\s]+)([^\\s&]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern TOKEN_PATTERN = Pattern.compile("(token|auth|bearer)([=:\\s]+)([^\\s&]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern SECRET_PATTERN = Pattern.compile("(secret|private[_-]?key)([=:\\s]+)([^\\s&]+)", Pattern.CASE_INSENSITIVE);

    /**
     * Mask a credential value for safe logging
     *
     * Examples:
     *   "secret_sauce" → "se***ce"
     *   "admin" → "ad***in" (if >= 5 chars)
     *   "ab" → "**" (too short, fully masked)
     *
     * @param credential the credential to mask
     * @return masked credential, or null if input is null
     */
    public static String maskCredential(String credential) {
        if (credential == null) {
            return null;
        }

        if (credential.isEmpty()) {
            return "";
        }

        int length = credential.length();

        // If too short, return all mask characters
        if (length < MIN_LENGTH_TO_MASK) {
            return MASK_CHAR.repeat(length);
        }

        // Show first and last VISIBLE_CHARS characters
        if (length <= (VISIBLE_CHARS * 2)) {
            // For very short strings, show first char and mask rest
            return credential.charAt(0) + MASK_CHAR.repeat(length - 1);
        }

        String prefix = credential.substring(0, VISIBLE_CHARS);
        String suffix = credential.substring(length - VISIBLE_CHARS);
        int maskLength = length - (VISIBLE_CHARS * 2);

        return prefix + MASK_CHAR.repeat(maskLength) + suffix;
    }

    /**
     * Mask credentials in a text string (e.g., log message or error message)
     *
     * This will find common credential patterns and mask their values
     *
     * @param text the text to sanitize
     * @return sanitized text with credentials masked
     */
    public static String maskCredentialsInText(String text) {
        if (text == null) {
            return null;
        }

        String sanitized = text;

        // Mask various credential patterns
        sanitized = PASSWORD_PATTERN.matcher(sanitized).replaceAll("$1$2***MASKED***");
        sanitized = API_KEY_PATTERN.matcher(sanitized).replaceAll("$1$2***MASKED***");
        sanitized = TOKEN_PATTERN.matcher(sanitized).replaceAll("$1$2***MASKED***");
        sanitized = SECRET_PATTERN.matcher(sanitized).replaceAll("$1$2***MASKED***");

        return sanitized;
    }

    /**
     * Mask a URL that might contain credentials
     *
     * Example: "https://user:password@example.com" → "https://***:***@example.com"
     *
     * @param url the URL to mask
     * @return masked URL
     */
    public static String maskUrlCredentials(String url) {
        if (url == null) {
            return null;
        }

        // Pattern to find user:password in URLs
        Pattern urlCredPattern = Pattern.compile("(https?://)([^:]+):([^@]+)@");
        return urlCredPattern.matcher(url).replaceAll("$1***:***@");
    }
}
