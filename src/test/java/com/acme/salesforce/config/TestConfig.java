package com.acme.salesforce.config;

import java.time.Duration;
import java.util.Locale;

public final class TestConfig {
    private static final String DEFAULT_BASE_URL = "https://login.salesforce.com";

    private TestConfig() {
    }

    public static String browser() {
        return get("browser", "BROWSER", "chrome").toLowerCase(Locale.ROOT);
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("headless", "HEADLESS", "true"));
    }

    public static String baseUrl() {
        return trimTrailingSlash(get("baseUrl", "BASE_URL", DEFAULT_BASE_URL));
    }

    public static Duration timeout() {
        String rawTimeout = get("timeoutSeconds", "TIMEOUT_SECONDS", "20");
        return Duration.ofSeconds(Long.parseLong(rawTimeout));
    }

    public static String remoteUrl() {
        return blankToNull(get("remoteUrl", "REMOTE_URL", null));
    }

    public static String salesforceUsername() {
        return blankToNull(get("sf.username", "SF_USERNAME", null));
    }

    public static String salesforcePassword() {
        return blankToNull(get("sf.password", "SF_PASSWORD", null));
    }

    public static String resetUsername() {
        return blankToNull(get("sf.reset.username", "SF_RESET_USERNAME", null));
    }

    public static boolean resetSubmissionEnabled() {
        return Boolean.parseBoolean(get("sf.reset.submit.enabled", "SF_RESET_SUBMIT_ENABLED", "false"));
    }

    public static boolean visualAgentEnabled() {
        return Boolean.parseBoolean(get("visualAgent", "VISUAL_AGENT", "false"));
    }

    public static Duration visualStepDelay() {
        String rawDelay = get("visualStepDelayMillis", "VISUAL_STEP_DELAY_MILLIS", "900");
        return Duration.ofMillis(Long.parseLong(rawDelay));
    }

    public static boolean credentialsAvailable() {
        return salesforceUsername() != null && salesforcePassword() != null;
    }

    private static String get(String propertyName, String environmentName, String defaultValue) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue.trim();
        }

        String environmentValue = System.getenv(environmentName);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue.trim();
        }

        return defaultValue;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return DEFAULT_BASE_URL;
        }

        String trimmed = value.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }

        return trimmed;
    }
}
