# Quick Setup Guide

Get started with the E-Commerce Test Automation Framework in under 5 minutes!

---

## Prerequisites

- **Java 17** or higher
- **Maven 3.9+**
- **Git**
- **(Optional)** Docker Desktop (for Selenium Grid)

---

## Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/yourorg/ecommerce-test-automation.git
cd ecommerce-test-automation
```

### 2. Set Up Environment Variables

```bash
# Copy the template
cp .env.template .env

# Edit .env with your credentials
# (For this demo app, the default values work)
```

**`.env` contents:**
```properties
STANDARD_USER=standard_user
LOCKED_USER=locked_out_user
PROBLEM_USER=problem_user
PERFORMANCE_USER=performance_glitch_user
TEST_PASSWORD=secret_sauce
```

### 3. Run Tests

```bash
# Install dependencies and run smoke tests
mvn clean test -Dgroups=smoke

# Run all tests
mvn clean test

# Run regression tests
mvn test -Dgroups=regression
```

---

## Verification

If setup is correct, you should see:

```
[INFO] Configuration loaded successfully
[INFO] Loaded 5 secrets from .env file
[INFO] SecretsManager initialized with 1 provider(s)
...
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
```

---

## Docker Setup (Optional)

### Run with Selenium Grid

```bash
# Start Selenium Grid
docker-compose up -d

# Run tests on Grid
mvn test -Pci

# View reports
mvn allure:serve
```

### Stop Grid

```bash
docker-compose down
```

---

## IDE Setup

### IntelliJ IDEA

1. **Import Project:**
   - File → Open → Select `pom.xml`
   - Import as Maven project

2. **Set Up Environment Variables:**
   - Run → Edit Configurations
   - Add environment variables from `.env`
   - Or install EnvFile plugin for automatic `.env` loading

3. **Run Tests:**
   - Right-click test class → Run
   - Or use Maven tool window

### VS Code

1. **Install Extensions:**
   - Extension Pack for Java
   - Test Runner for Java

2. **Configure:**
   - Create `.vscode/settings.json`:
   ```json
   {
     "java.configuration.updateBuildConfiguration": "automatic",
     "java.test.config": {
       "envFile": "${workspaceFolder}/.env"
     }
   }
   ```

3. **Run Tests:**
   - Click run button above test methods
   - Or use Testing sidebar

---

## Troubleshooting

### Issue: "Required secret 'STANDARD_USER' not found!"

**Solution:**
```bash
# Verify .env file exists
ls -la .env

# Check contents
cat .env

# Ensure .env is in project root, not in subdirectory
pwd
```

### Issue: Tests fail with "WebDriverException"

**Solution:**
```bash
# Update WebDriver
mvn clean test -U

# Or manually download ChromeDriver
```

### Issue: "Could not load config file"

**Solution:**
```bash
# Verify config.properties exists
ls -la src/test/resources/config/config.properties

# Check file permissions
chmod 644 src/test/resources/config/config.properties
```

### Issue: Port 4444 already in use (Docker)

**Solution:**
```bash
# Stop existing containers
docker-compose down

# Kill process using port 4444
lsof -ti:4444 | xargs kill -9

# Restart
docker-compose up -d
```

---

## Running Different Test Suites

### Smoke Tests (Fast - 5 min)
```bash
mvn test -Dgroups=smoke
```

### Regression Tests (Full - 15 min)
```bash
mvn test -Dgroups=regression
```

### E2E Tests
```bash
mvn test -Dgroups=e2e
```

### Specific Browser
```bash
mvn test -Dbrowser=firefox
mvn test -Dbrowser=edge
```

### Headless Mode
```bash
mvn test -Dheadless=true
```

### Parallel Execution
```bash
mvn test -Dthread.count=8
```

---

## Generating Reports

### Allure Reports
```bash
# Generate and open report
mvn allure:serve

# Generate report without opening
mvn allure:generate
```

### ExtentReports
```bash
# Reports are automatically generated at:
# target/extent-reports/index.html

# Open in browser
open target/extent-reports/index.html
```

---

## CI/CD Setup

### GitHub Actions

Secrets are already configured in the workflow. Just add them to your repository:

1. Go to: **Settings → Secrets and variables → Actions**
2. Add secrets:
   - `STANDARD_USER`
   - `LOCKED_USER`
   - `PROBLEM_USER`
   - `PERFORMANCE_USER`
   - `TEST_PASSWORD`

3. Push to `main` or `develop` branch to trigger tests

### Jenkins

```groovy
pipeline {
    environment {
        STANDARD_USER = credentials('standard-user')
        LOCKED_USER = credentials('locked-user')
        PROBLEM_USER = credentials('problem-user')
        PERFORMANCE_USER = credentials('performance-user')
        TEST_PASSWORD = credentials('test-password')
    }
    stages {
        stage('Test') {
            steps {
                sh 'mvn clean test'
            }
        }
    }
}
```

---

## Next Steps

- Read [SECURITY.md](SECURITY.md) for security best practices
- Check [README.md](../README.md) for detailed architecture
- Review test classes in `src/test/java/com/ecommerce/tests/`
- Explore Page Objects in `src/test/java/com/ecommerce/tests/pages/`

---

## Getting Help

- Check [Troubleshooting](#troubleshooting) section above
- Review logs in `logs/` directory
- Read Allure reports for test failure details
- Contact team: #qa-automation slack channel

---

**Setup Time:** < 5 minutes ⚡
**Ready to run tests!** 🚀
