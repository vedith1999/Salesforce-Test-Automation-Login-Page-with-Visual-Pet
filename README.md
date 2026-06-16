# Salesforce Security Test Automation

Selenium Java + Maven + TestNG framework for Salesforce login and forgot-password user journeys.

I interpreted "Melvin" as **Maven**. The suite uses the Page Object Model, safe credential handling, TestNG grouping, configurable browsers, screenshots on failure, and CI-ready defaults.

## Tech Stack

- Java 17 compatible source level
- Maven
- Selenium WebDriver 4
- TestNG
- Selenium Manager for local browser driver resolution

## Covered User Cases

- Salesforce login page loads over HTTPS and exposes expected controls.
- Invalid login attempt does not authenticate and shows an error.
- Real credential login is supported only when credentials are supplied securely.
- Forgot-password link navigates to the reset-password page.
- Forgot-password form enforces username input.
- Real password-reset submission is opt-in only to avoid accidentally sending reset emails from automation.

## Run

```bash
mvn clean test
```

Useful runtime options:

```bash
mvn clean test -Dbrowser=chrome -Dheadless=true
mvn clean test -DbaseUrl=https://test.salesforce.com
mvn clean test -DbaseUrl=https://your-domain.my.salesforce.com
```

Visible demo mode with the animated agent pet:

```bash
mvn test -Dheadless=false -DvisualAgent=true -DvisualStepDelayMillis=1200
```

Supported local browsers:

- `chrome`
- `firefox`
- `safari`

Remote Selenium Grid:

```bash
mvn clean test -DremoteUrl=http://localhost:4444/wd/hub
```

## Secure Test Data

Do not commit Salesforce credentials. Use environment variables or JVM properties.

Valid login test:

```bash
export SF_USERNAME="automation-user@example.com"
export SF_PASSWORD="your-password"
mvn clean test
```

Equivalent JVM properties:

```bash
mvn clean test -Dsf.username="automation-user@example.com" -Dsf.password="your-password"
```

Forgot-password real submission is intentionally disabled by default. Enable it only for an approved test user:

```bash
export SF_RESET_USERNAME="automation-user@example.com"
mvn clean test -Dsf.reset.submit.enabled=true
```

## CI

The GitHub Actions workflow in `.github/workflows/selenium-tests.yml` runs the TestNG suite on push, pull request, and manual dispatch. Store real credentials as repository secrets if you decide to enable credential-based tests in CI.

## Git Push Checklist

```bash
mvn clean test
git status
git add .
git commit -m "Add Salesforce Selenium TestNG automation framework"
git remote add origin <your-repository-url>
git push -u origin master
```

## Security Notes

- No credentials are hardcoded.
- Production password reset submission is opt-in and should use a dedicated automation user.
- Prefer `https://test.salesforce.com` or a Salesforce sandbox/custom domain for credential tests.
- Screenshots are written under `target/screenshots` and ignored by Git.
