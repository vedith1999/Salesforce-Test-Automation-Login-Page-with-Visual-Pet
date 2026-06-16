package com.acme.salesforce.tests;

import com.acme.salesforce.config.TestConfig;
import com.acme.salesforce.pages.ResetPasswordPage;
import com.acme.salesforce.pages.SalesforceLoginPage;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

public class SalesforceForgotPasswordTests extends BaseTest {
    @Test(groups = {"smoke", "forgot-password", "security"})
    public void forgotPasswordLinkShouldNavigateToResetPasswordPage() {
        ResetPasswordPage resetPasswordPage = new SalesforceLoginPage(driver())
                .open()
                .openForgotPassword();

        Assert.assertTrue(driver().getCurrentUrl().startsWith("https://"),
                "Forgot-password page should be served over HTTPS.");
        Assert.assertTrue(resetPasswordPage.isLoaded(), "Reset-password form should be visible.");
    }

    @Test(groups = {"forgot-password", "negative", "security"})
    public void resetPasswordShouldRequireUsername() {
        ResetPasswordPage resetPasswordPage = new SalesforceLoginPage(driver())
                .open()
                .openForgotPassword()
                .submitBlankUsername();

        Assert.assertTrue(resetPasswordPage.isLoaded(),
                "Reset-password page should keep the user on the form when username is blank.");
    }

    @Test(groups = {"forgot-password", "positive", "optional"})
    public void resetPasswordSubmissionShouldBeOptInForApprovedTestUsers() {
        if (!TestConfig.resetSubmissionEnabled() || TestConfig.resetUsername() == null) {
            throw new SkipException("Set SF_RESET_USERNAME and -Dsf.reset.submit.enabled=true to submit a reset request.");
        }

        ResetPasswordPage resetPasswordPage = new SalesforceLoginPage(driver())
                .open()
                .openForgotPassword()
                .requestReset(TestConfig.resetUsername());

        Assert.assertTrue(driver().getCurrentUrl().startsWith("https://"),
                "Reset-password submission should remain on HTTPS.");
        Assert.assertFalse(resetPasswordPage.feedbackText().isBlank(),
                "Reset-password submission should return confirmation or user-facing guidance.");
    }
}
