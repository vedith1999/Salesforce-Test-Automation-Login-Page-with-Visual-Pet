# Salesforce Test Automation Login Page with Visual Pet

Selenium Java + Maven + TestNG framework for Salesforce login and forgot-password user journeys with an animated visual agent pet for interactive demo mode.

The suite uses the Page Object Model, safe credential handling, TestNG grouping, configurable browsers, screenshots on failure, visual pet animation, and CI-ready defaults.

## Tech Stack

- Java 17 compatible source level
- Maven
- Selenium WebDriver 4
- TestNG
- Selenium Manager for local browser driver resolution

## Covered Test Cases

- Salesforce login page loads over HTTPS and exposes expected controls.
- Invalid login attempt does not authenticate and shows an error message.
- Real credential login is supported only when credentials are supplied securely via environment variables or JVM properties.
- Forgot-password link navigates to the reset-password page.
- Forgot-password form enforces username input validation.
- Real password-reset submission is opt-in only to avoid accidentally sending reset emails from automation.
- Visual agent pet animation tracks test progress during demo mode.

## Run

Execute the test suite with Maven:

```bash
mvn clean test
```

### Runtime Options

Configure test execution with system properties:

```bash
# Browser selection (default: chrome)
mvn clean test -Dbrowser=chrome -Dheadless=true
mvn clean test -Dbrowser=firefox
mvn clean test -Dbrowser=safari

# Salesforce environment (default: https://login.salesforce.com)
mvn clean test -DbaseUrl=https://test.salesforce.com
mvn clean test -DbaseUrl=https://your-domain.my.salesforce.com
```

### Visual Agent Pet Demo Mode

Run tests with an animated agent pet that tracks progress:

```bash
mvn clean test -Dheadless=false -DvisualAgent=true -DvisualStepDelayMillis=1200
```

This mode is useful for interactive demonstrations and visual validation of test flows.

### Supported Browsers

- `chrome` (default)
- `firefox`
- `safari`

### Remote Selenium Grid

Execute tests against a remote Selenium Grid instance:

```bash
mvn clean test -DremoteUrl=http://localhost:4444/wd/hub
```

## Secure Test Data

**Important:** Never commit credentials to git. Use environment variables instead.

### Quick Start (Secure)

```bash
# 1. Copy the template
cp .env.example .env

# 2. Edit with your credentials (this file is git-ignored)
nano .env

# 3. Run tests
./setup-env.sh test
```

### Manual Setup

Provide credentials via environment variables:

```bash
export SF_USERNAME="automation-user@sandbox.salesforce.com"
export SF_PASSWORD="your-secure-password"
export SF_RESET_USERNAME="automation-user@sandbox.salesforce.com"

# Run tests
mvn clean test -Dsf.reset.submit.enabled=true
```

### For CI/CD (GitHub Actions)

Store credentials as GitHub Secrets (Settings → Secrets) - never in code.
The workflow uses them automatically: `.github/workflows/secure-test-automation.yml`

**For detailed security setup, see [SECURE_CREDENTIALS.md](SECURE_CREDENTIALS.md)**

## CI

The GitHub Actions workflow in `.github/workflows/selenium-tests.yml` runs the TestNG suite on push, pull request, and manual dispatch. Store real credentials as repository secrets if you decide to enable credential-based tests in CI.

## Development & Git Workflow

### Before Pushing Changes

Verify code quality and tests pass:

```bash
mvn clean test
git status
```

### Commit and Push

```bash
git add .
git commit -m "Descriptive commit message"
git push origin codex/build-salesforce-login-automation
```

### Create a Pull Request

Push your feature branch and open a pull request against `main` for code review before merging.

## Security Notes

- No credentials are hardcoded.
- Production password reset submission is opt-in and should use a dedicated automation user.
- Prefer `https://test.salesforce.com` or a Salesforce sandbox/custom domain for credential tests.
- Screenshots are written under `target/screenshots` and ignored by Git.
