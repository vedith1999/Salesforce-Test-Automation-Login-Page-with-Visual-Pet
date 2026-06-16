package com.acme.salesforce.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ResetPasswordPage extends BasePage {
    private static final By PAGE_HEADING = By.xpath("//*[normalize-space()='Reset Your Password']");
    private static final By USERNAME = By.id("un");
    private static final By USERNAME_BY_NAME = By.name("un");
    private static final By CONTINUE_BUTTON = By.id("continue");
    private static final By CONTINUE_BY_NAME = By.name("continue");
    private static final By ERROR = By.id("error");
    private static final By ALERT = By.cssSelector("[role='alert']");
    private static final By CONFIRMATION = By.cssSelector(".message, .success, #forgotPassForm .mb16");

    public ResetPasswordPage(WebDriver driver) {
        super(driver);
    }

    public ResetPasswordPage waitForLoaded() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("forgotpassword"),
                ExpectedConditions.visibilityOfElementLocated(PAGE_HEADING)
        ));
        firstVisible(USERNAME, USERNAME_BY_NAME);
        firstVisible(CONTINUE_BUTTON, CONTINUE_BY_NAME);
        announce("Reset-password page loaded. Username field and Continue button are visible.");
        return this;
    }

    public boolean isLoaded() {
        return isVisible(USERNAME, USERNAME_BY_NAME) && isVisible(CONTINUE_BUTTON, CONTINUE_BY_NAME);
    }

    public ResetPasswordPage submitBlankUsername() {
        announce("Submitting reset-password form without a username to validate required input.");
        clickFirst(CONTINUE_BUTTON, CONTINUE_BY_NAME);
        return this;
    }

    public ResetPasswordPage requestReset(String username) {
        announce("Submitting a password reset request for the approved test user.");
        typeFirst(username, USERNAME, USERNAME_BY_NAME);
        clickFirst(CONTINUE_BUTTON, CONTINUE_BY_NAME);
        return this;
    }

    public String feedbackText() {
        return textIfVisible(ERROR, ALERT, CONFIRMATION);
    }
}
