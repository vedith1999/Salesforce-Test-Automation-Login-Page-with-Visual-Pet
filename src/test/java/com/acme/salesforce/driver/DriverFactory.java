package com.acme.salesforce.driver;

import com.acme.salesforce.config.TestConfig;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Locale;

public final class DriverFactory {
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        if (DRIVER.get() != null) {
            return DRIVER.get();
        }

        WebDriver driver = buildDriver();
        configureTimeouts(driver);
        DRIVER.set(driver);
        return driver;
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver has not been created for this thread.");
        }
        return driver;
    }

    public static WebDriver getDriverOrNull() {
        return DRIVER.get();
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }

    private static WebDriver buildDriver() {
        String browser = TestConfig.browser();
        String remoteUrl = TestConfig.remoteUrl();

        return switch (browser) {
            case "firefox" -> remoteUrl == null
                    ? new FirefoxDriver(firefoxOptions())
                    : new RemoteWebDriver(toUrl(remoteUrl), firefoxOptions());
            case "safari" -> remoteUrl == null
                    ? new SafariDriver(safariOptions())
                    : new RemoteWebDriver(toUrl(remoteUrl), safariOptions());
            case "chrome" -> remoteUrl == null
                    ? new ChromeDriver(chromeOptions())
                    : new RemoteWebDriver(toUrl(remoteUrl), chromeOptions());
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser
                    + ". Use chrome, firefox, or safari.");
        };
    }

    private static ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1440,1000");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        if (TestConfig.headless()) {
            options.addArguments("--headless=new");
        }
        return options;
    }

    private static FirefoxOptions firefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        if (TestConfig.headless()) {
            options.addArguments("-headless");
        }
        return options;
    }

    private static SafariOptions safariOptions() {
        if (TestConfig.headless()) {
            throw new IllegalArgumentException("Safari does not support headless mode. Re-run with -Dheadless=false.");
        }
        return new SafariOptions();
    }

    private static URL toUrl(String remoteUrl) {
        try {
            return new URL(remoteUrl);
        } catch (MalformedURLException exception) {
            throw new IllegalArgumentException("Invalid remoteUrl: " + remoteUrl, exception);
        }
    }

    private static void configureTimeouts(WebDriver driver) {
        Duration timeout = TestConfig.timeout();
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        driver.manage().timeouts().pageLoadTimeout(timeout.plusSeconds(20));
        driver.manage().timeouts().scriptTimeout(timeout);

        if (!TestConfig.browser().equals("safari")) {
            driver.manage().window().maximize();
        }
    }
}
