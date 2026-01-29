# Security Practices

## Overview

This document describes the security practices for the E-Commerce Test Automation Framework, including secrets management, credential handling, and security scanning.

---

## Secrets Management Architecture

### Design Principles

The framework uses a **multi-tier secrets management approach** with the following priorities:

1. **Environment Variables** (highest priority) - CI/CD systems, cloud deployments
2. **.env Files** - Local development
3. **Future: Cloud Providers** - Azure Key Vault, AWS Secrets Manager (via abstraction layer)

### Architecture Diagram

```
SecretsManager (Facade)
    ├── SecretsProvider (Interface)
    │   ├── EnvironmentSecretsProvider (Current - env vars + .env)
    │   ├── AzureKeyVaultSecretsProvider (Future)
    │   └── AwsSecretsManagerProvider (Future)
    └── CredentialMasker (Utility)
```

---

## Local Development Setup

### First-Time Setup

1. **Copy the environment template:**
   ```bash
   cp .env.template .env
   ```

2. **Edit `.env` with your credentials:**
   ```properties
   STANDARD_USER=standard_user
   LOCKED_USER=locked_out_user
   PROBLEM_USER=problem_user
   PERFORMANCE_USER=performance_glitch_user
   TEST_PASSWORD=secret_sauce
   ```

3. **Verify setup:**
   ```bash
   mvn clean test -Dgroups=smoke
   ```

### Important Notes

- **NEVER commit `.env` to git** - It's in `.gitignore` for your protection
- `.env.template` is the only file that should be committed
- Use `.env.template` as reference for required variables

---

## CI/CD Configuration

### GitHub Actions Secrets

1. Navigate to: **Repository → Settings → Secrets and variables → Actions**

2. Add the following secrets:

   | Secret Name | Value | Description |
   |-------------|-------|-------------|
   | `STANDARD_USER` | `standard_user` | Standard test user |
   | `LOCKED_USER` | `locked_out_user` | Locked out test user |
   | `PROBLEM_USER` | `problem_user` | Problem test user |
   | `PERFORMANCE_USER` | `performance_glitch_user` | Performance test user |
   | `TEST_PASSWORD` | `secret_sauce` | Shared password |

3. Secrets are automatically injected into workflows via environment variables

### Verifying CI/CD Setup

- Check GitHub Actions logs - secrets should appear as `***` (masked)
- Tests should pass without hardcoded credentials
- No credentials should appear in build artifacts

---

## Security Features

### 1. Credential Masking

All credentials are automatically masked in logs:

```java
// Example: "secret_sauce" appears as "se***ce" in logs
log.info("User credentials loaded: {}", configManager.getCredentialMasked("test.password"));
```

### 2. Fail-Fast Security

If required secrets are missing, the framework fails immediately with helpful error messages:

```
Required secret 'STANDARD_USER' not found!

Please set up your environment:
  1. Copy .env.template to .env
  2. Fill in the required values
  OR
  Set environment variable: STANDARD_USER

For CI/CD, ensure GitHub Secrets are configured.
See docs/SECURITY.md for details.
```

### 3. Secure Error Handling

- Stack traces are sanitized to remove credentials
- File paths containing sensitive info are masked
- URL credentials are redacted

---

## Security Scanning

### OWASP Dependency Check

Scans all dependencies for known vulnerabilities (CVEs):

```bash
# Run manually
mvn dependency-check:check

# View report
open target/dependency-check-report.html
```

- Automatically runs in CI/CD pipeline
- Fails build on HIGH or CRITICAL vulnerabilities
- Suppression file: `dependency-check-suppression.xml`

### Secret Scanning

Uses TruffleHog to detect accidentally committed secrets:

```bash
# Run manually (requires TruffleHog installed)
trufflehog filesystem . --only-verified
```

- Automatically runs on every push/PR
- Scans commit history for leaked credentials
- Blocks commits containing secrets

### Container Security Scanning

Uses Trivy to scan Docker images:

```bash
# Run manually
docker build -t ecommerce-test:latest .
trivy image ecommerce-test:latest
```

- Scans base images and custom Dockerfile
- Detects vulnerable packages in containers
- Uploads results to GitHub Security tab

---

## Credential Rotation

### When to Rotate

- Regularly (every 90 days recommended)
- After team member departure
- If credentials are accidentally exposed
- Security incident or breach

### Rotation Procedure

1. **Update secrets in all environments:**
   - Local: Update `.env` file
   - CI/CD: Update GitHub Secrets
   - Cloud: Update Azure Key Vault / AWS Secrets Manager

2. **Communicate changes to team:**
   - Notify all developers to update local `.env`
   - Update documentation if needed

3. **Verify rotation:**
   ```bash
   # Clear any cached secrets
   rm -rf target/

   # Run tests to verify new credentials work
   mvn clean test -Dgroups=smoke
   ```

4. **Audit logs:**
   - Check application logs for failed authentication
   - Review security scan results

---

## Security Incident Response

### If Credentials Are Exposed

1. **Immediately revoke/change credentials:**
   - Update test application passwords
   - Rotate all affected secrets

2. **Assess impact:**
   - Check git history: `git log -S "secret_sauce" --all`
   - Review access logs
   - Identify who had access

3. **Clean git history (if needed):**
   ```bash
   # Use git-filter-repo to remove secrets from history
   git-filter-repo --path config.properties --invert-paths
   ```

4. **Update all environments:**
   - Follow credential rotation procedure
   - Force team to pull latest changes

5. **Post-incident:**
   - Document incident
   - Review security practices
   - Implement additional safeguards

---

## Best Practices

### DO ✅

- Use `.env` file for local development
- Keep `.env.template` updated with required variables
- Use GitHub Secrets for CI/CD
- Rotate credentials regularly
- Review security scan results
- Use `getCredentialMasked()` for logging
- Report security issues immediately

### DON'T ❌

- Commit `.env` file to git
- Hardcode credentials in code
- Share credentials via email/Slack
- Log credentials in plain text
- Disable security scanning
- Ignore security warnings
- Use production credentials in tests

---

## Future Enhancements

### Azure Key Vault Integration (Planned)

When ready to add Azure Key Vault:

1. Uncomment Azure dependencies in `pom.xml`
2. Create `AzureKeyVaultSecretsProvider` implementing `SecretsProvider`
3. Configure Azure credentials in environment
4. No changes to existing code required!

### AWS Secrets Manager Integration (Planned)

Similar process for AWS Secrets Manager - abstraction layer allows easy addition.

---

## Security Contacts

For security issues or questions:

- Email: security@yourcompany.com
- Slack: #security-team
- Emergency: [On-call rotation]

---

## Compliance

This secrets management approach helps meet:

- **SOC 2 Type II** - Access controls, audit logs
- **ISO 27001** - Information security management
- **GDPR** - Data protection (for PII in test data)
- **PCI DSS** - Secure credential storage

---

## Additional Resources

- [OWASP Secrets Management Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Secrets_Management_Cheat_Sheet.html)
- [GitHub Encrypted Secrets](https://docs.github.com/en/actions/security-guides/encrypted-secrets)
- [Azure Key Vault Best Practices](https://docs.microsoft.com/en-us/azure/key-vault/general/best-practices)

---

**Last Updated:** 2026-01-29
**Version:** 2.0
