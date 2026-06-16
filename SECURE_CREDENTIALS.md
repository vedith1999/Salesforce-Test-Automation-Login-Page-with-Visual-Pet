# Secure Credential Management Guide

## Overview

This guide explains how to securely run tests with Salesforce credentials without exposing them in code, git history, or logs.

## For Local Development

### 1. Create Your Local `.env` File

```bash
# Copy the template
cp .env.example .env

# Edit with your credentials
nano .env
```

Content:
```env
SF_USERNAME=your-test-user@sandbox.salesforce.com
SF_PASSWORD=YourSecurePassword123!
SF_RESET_USERNAME=your-test-user@sandbox.salesforce.com
```

**Important:** `.env` is already in `.gitignore` - it will NEVER be committed.

### 2. Run Tests Securely

**Option A: Using setup script (Recommended)**
```bash
chmod +x setup-env.sh
./setup-env.sh test
```

**Option B: Manual load**
```bash
set -a && source .env && set +a
mvn clean test -Dheadless=false -DvisualAgent=true -Dsf.reset.submit.enabled=true
```

**Option C: One-liner**
```bash
export $(cat .env | grep -v '^#' | xargs) && mvn clean test -Dsf.reset.submit.enabled=true
```

### 3. Shell History Safety

Your credentials might be in shell history. Clear sensitive commands:
```bash
# Remove last command from history
history -d $(history 1 | awk '{print $1}')

# Or clear all history (careful!)
history -c
```

## For CI/CD (GitHub Actions)

### 1. Add Secrets to GitHub

Go to: **Settings → Secrets and variables → Actions**

Add these secrets:
- `SF_USERNAME` - your test user email
- `SF_PASSWORD` - your test user password  
- `SF_RESET_USERNAME` - your test user email

### 2. Use in Workflows

The workflow `.github/workflows/secure-test-automation.yml` automatically uses these secrets:

```yaml
env:
  SF_USERNAME: ${{ secrets.SF_USERNAME }}
  SF_PASSWORD: ${{ secrets.SF_PASSWORD }}
  SF_RESET_USERNAME: ${{ secrets.SF_RESET_USERNAME }}
run: mvn clean test
```

**Benefits:**
- ✓ Secrets never logged in console
- ✓ Masked in GitHub UI (appears as `***`)
- ✓ Only available to authorized workflows
- ✓ Separate from code repository

## Security Best Practices

### ✓ DO:
- Use environment variables (as shown above)
- Store credentials in `.env` (local, not committed)
- Use GitHub Secrets in CI/CD
- Use different credentials per environment (dev/staging/prod)
- Rotate credentials regularly
- Store passwords in a password manager (1Password, LastPass, etc.)

### ✗ DON'T:
- Commit credentials to git
- Hardcode passwords in code
- Share credentials in Slack/email
- Use same credentials across environments
- Store plain text passwords on your machine
- Log credentials in test output

## Prevent Accidental Commits

### Install Git Hook (Recommended)

```bash
# Move the pre-commit hook into git
mv .githooks-pre-commit .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit
```

This hook:
- ✓ Prevents committing `.env` file
- ✓ Scans for hardcoded credentials
- ✓ Blocks suspicious patterns (passwords, API keys)

Test it:
```bash
# This will be blocked:
echo "SF_PASSWORD=secret" >> test.java
git add test.java
git commit -m "test"  # ❌ Blocked by hook
```

## Salesforce Sandbox Best Practices

### Use a Dedicated Test User

1. Create a separate user in Salesforce sandbox
2. Give it minimal required permissions
3. Set up login IP restrictions
4. Enable 2FA if possible
5. Rotate password every 90 days

### Test User Credentials Example

```
Email: salesforce-automation@yourdomain.sandbox.com
Password: XYZ123!@#Secure
Access: Salesforce sandbox only
Permissions: Read/Write on login pages only
```

## Troubleshooting

### Problem: Tests say credentials not found
```bash
# Verify .env exists and is loaded
source .env
echo $SF_USERNAME  # Should print your username

# Verify file is readable
cat .env  # Should show your credentials
```

### Problem: `.env` was accidentally committed
```bash
# Remove from git history (DANGEROUS - use with caution)
git filter-branch --tree-filter 'rm -f .env' HEAD

# Never push the credentials you removed - change them!
```

### Problem: Credentials in shell history
```bash
# Clear bash history
cat /dev/null > ~/.bash_history

# Clear zsh history (used by default in recent macOS)
cat /dev/null > ~/.zsh_history
```

## Security Audit Checklist

Before pushing to GitHub:
- [ ] `.env` file is in `.gitignore`
- [ ] No credentials in code comments
- [ ] No credentials in test data files
- [ ] No credentials in README/documentation
- [ ] Pre-commit hook is installed
- [ ] GitHub workflow uses Secrets, not hardcoded values
- [ ] Test user has minimal required permissions
- [ ] CI/CD runs with headless=true (no visible UI)

## Additional Resources

- [GitHub Secrets Documentation](https://docs.github.com/en/actions/security-guides/encrypted-secrets)
- [OWASP: Secrets Management](https://cheatsheetseries.owasp.org/cheatsheets/Secrets_Management_Cheat_Sheet.html)
- [12 Factor App: Config](https://12factor.net/config)
- [Salesforce Test User Setup](https://help.salesforce.com/s/articleView?id=sf.users_create.htm)
