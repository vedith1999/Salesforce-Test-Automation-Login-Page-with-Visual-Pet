package com.acme.salesforce.tests;

import com.acme.salesforce.config.TestConfig;
import com.acme.salesforce.pages.SalesforceLoginPage;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SalesforceLoginTests extends BaseTest {
    @Test(groups = {"smoke", "login", "security"})
    public void loginPageShouldExposeExpectedSecurityControls() {
        SalesforceLoginPage loginPage = new SalesforceLoginPage(driver()).open();

        Assert.assertTrue(driver().getCurrentUrl().startsWith("https://"),
                "Salesforce login should be served over HTTPS.");
        Assert.assertTrue(loginPage.isLoaded(), "Username, password, and login controls should be visible.");
        Assert.assertTrue(loginPage.forgotPasswordLinkIsVisible(),
                "Forgot password link should be available from the login page.");
    }

    @DataProvider
    public Object[][] invalidLoginData() {
        return new Object[][]{
                {"security.invalid.user@example.invalid", "DefinitelyWrongPassword!234"}
        };
    }

    @Test(dataProvider = "invalidLoginData", groups = {"login", "negative", "security"})
    public void invalidLoginShouldNotAuthenticate(String username, String password) {
        SalesforceLoginPage loginPage = new SalesforceLoginPage(driver()).open();

        loginPage.login(username, password);

        Assert.assertTrue(loginPage.hasLoginError(), "Invalid credentials should produce a login error.");
        Assert.assertFalse(loginPage.loginErrorText().isBlank(), "Login error should include user-facing guidance.");
    }

    @Test(groups = {"login", "positive", "optional"})
    public void validLoginShouldAuthenticateWhenCredentialsAreProvided() {
        if (!TestConfig.credentialsAvailable()) {
            throw new SkipException("Set SF_USERNAME and SF_PASSWORD, or -Dsf.username/-Dsf.password, to run valid login.");
        }

        SalesforceLoginPage loginPage = new SalesforceLoginPage(driver()).open();
        loginPage.login(TestConfig.salesforceUsername(), TestConfig.salesforcePassword());

        waitForLoginResult(loginPage);
        Assert.assertFalse(loginPage.hasLoginError(), "Valid Salesforce credentials should not produce a login error.");
        Assert.assertTrue(driver().getCurrentUrl().startsWith("https://"),
                "Post-login or verification challenge should remain on HTTPS.");
    }

    private void waitForLoginResult(SalesforceLoginPage loginPage) {
        long deadline = System.currentTimeMillis() + TestConfig.timeout().toMillis();
        while (System.currentTimeMillis() < deadline) {
            if (loginPage.hasLoginError() || !driver().getCurrentUrl().contains("/login")) {
                return;
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
