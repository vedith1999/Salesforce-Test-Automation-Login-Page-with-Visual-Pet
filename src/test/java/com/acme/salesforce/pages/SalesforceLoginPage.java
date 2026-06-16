package com.acme.salesforce.pages;

import com.acme.salesforce.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class SalesforceLoginPage extends BasePage {
    private static final By USERNAME = By.id("username");
    private static final By USERNAME_BY_NAME = By.name("username");
    private static final By PASSWORD = By.id("password");
    private static final By PASSWORD_BY_NAME = By.name("pw");
    private static final By LOGIN_BUTTON = By.id("Login");
    private static final By LOGIN_BUTTON_BY_NAME = By.name("Login");
    private static final By FORGOT_PASSWORD = By.id("forgot_password_link");
    private static final By FORGOT_PASSWORD_BY_TEXT = By.partialLinkText("Forgot");
    private static final By LOGIN_ERROR = By.id("error");
    private static final By ALERT = By.cssSelector("[role='alert']");

    public SalesforceLoginPage(WebDriver driver) {
        super(driver);
    }

    public SalesforceLoginPage open() {
        announce("Opening the Salesforce login page.");
        driver.get(TestConfig.baseUrl());
        wait.until(ExpectedConditions.urlContains("salesforce"));
        waitForLoaded();
        announce("Login page loaded. Checking HTTPS and visible controls.");
        return this;
    }

    public SalesforceLoginPage waitForLoaded() {
        firstVisible(USERNAME, USERNAME_BY_NAME);
        firstVisible(PASSWORD, PASSWORD_BY_NAME);
        firstVisible(LOGIN_BUTTON, LOGIN_BUTTON_BY_NAME);
        return this;
    }

    public boolean isLoaded() {
        return isVisible(USERNAME, USERNAME_BY_NAME)
                && isVisible(PASSWORD, PASSWORD_BY_NAME)
                && isVisible(LOGIN_BUTTON, LOGIN_BUTTON_BY_NAME);
    }

    public boolean forgotPasswordLinkIsVisible() {
        return isVisible(FORGOT_PASSWORD, FORGOT_PASSWORD_BY_TEXT);
    }

    public SalesforceLoginPage login(String username, String password) {
        announce("Entering username and password for the login scenario.");
        typeFirst(username, USERNAME, USERNAME_BY_NAME);
        typeFirst(password, PASSWORD, PASSWORD_BY_NAME);
        announce("Submitting the Salesforce login form.");
        clickFirst(LOGIN_BUTTON, LOGIN_BUTTON_BY_NAME);
        return this;
    }

    public ResetPasswordPage openForgotPassword() {
        announce("Opening the forgot-password user flow.");
        clickFirst(FORGOT_PASSWORD, FORGOT_PASSWORD_BY_TEXT);
        return new ResetPasswordPage(driver).waitForLoaded();
    }

    public boolean hasLoginError() {
        return isVisible(LOGIN_ERROR, ALERT);
    }

    public String loginErrorText() {
        return textIfVisible(LOGIN_ERROR, ALERT);
    }
}
