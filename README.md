# 🛒 E-Commerce Test Automation Framework

[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.oracle.com/java/)
[![Selenium](https://img.shields.io/badge/Selenium-4.15.0-green.svg)](https://www.selenium.dev/)
[![TestNG](https://img.shields.io/badge/TestNG-7.8.0-red.svg)](https://testng.org/)
[![Allure](https://img.shields.io/badge/Allure-2.24.0-orange.svg)](https://docs.qameta.io/allure/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![Security](https://img.shields.io/badge/Security-Hardened-success.svg)](docs/SECURITY.md)
[![OWASP](https://img.shields.io/badge/OWASP-Scanning-informational.svg)](docs/SECURITY.md#security-scanning)

A robust, enterprise-grade test automation framework built with **Page Object Model (POM)** design pattern, featuring **parallel execution** via Dockerized Selenium Grid, comprehensive **CI/CD integration**, and advanced **reporting capabilities**.

---

## 🔒 Security First

This framework implements **production-grade security** practices:

- ✅ **Zero hardcoded credentials** - All secrets loaded from environment variables or .env files
- ✅ **Automated security scanning** - OWASP Dependency Check (CVSS ≥ 7), TruffleHog v3 secret scanning, Trivy container scanning
- ✅ **Credential masking** - Automatic redaction in logs and reports
- ✅ **GitHub Security integration** - SARIF results uploaded via CodeQL v3 action
- ✅ **Future-proof architecture** - Abstraction layer supports Azure Key Vault, AWS Secrets Manager

**Quick Setup:**
1. Copy `.env.template` to `.env`
2. Run tests: `mvn clean test`

📖 **Documentation:**
- **[Security Guide](docs/SECURITY.md)** - Comprehensive security practices
- **[Setup Guide](docs/SETUP.md)** - Get started in < 5 minutes

---

## 📊 Framework Statistics

| Metric | Value |
|--------|-------|
| **Total Test Cases** | 60+ |
| **Test Categories** | Login, Cart, Checkout, Products, Navigation, E2E |
| **Parallel Threads** | 8 (configurable up to 16) |
| **Time Reduction** | ~60% vs sequential execution |
| **Suite Reliability** | 99%+ (with RetryAnalyzer) |

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    TEST AUTOMATION FRAMEWORK                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐      │
│  │   Test       │    │    Page      │    │   Utilities  │      │
│  │   Classes    │───▶│   Objects    │───▶│   & Config   │      │
│  │              │    │   (POM)      │    │              │      │
│  └──────────────┘    └──────────────┘    └──────────────┘      │
│         │                   │                   │               │
│         ▼                   ▼                   ▼               │
│  ┌─────────────────────────────────────────────────────┐       │
│  │              SELENIUM GRID (Docker)                  │       │
│  │  ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐       │       │
│  │  │Chrome 1│ │Chrome 2│ │Chrome 3│ │Chrome 4│       │       │
│  │  └────────┘ └────────┘ └────────┘ └────────┘       │       │
│  │  ┌────────┐ ┌────────┐                              │       │
│  │  │Firefox │ │ Edge   │                              │       │
│  │  └────────┘ └────────┘                              │       │
│  └─────────────────────────────────────────────────────┘       │
│         │                                                       │
│         ▼                                                       │
│  ┌──────────────────────────────────────────────────────┐      │
│  │              REPORTING & CI/CD                        │      │
│  │  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐ │      │
│  │  │ Allure  │  │ Extent  │  │ Jenkins │  │ GitHub  │ │      │
│  │  │ Report  │  │ Report  │  │   CI    │  │ Actions │ │      │
│  │  └─────────┘  └─────────┘  └─────────┘  └─────────┘ │      │
│  └──────────────────────────────────────────────────────┘      │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🚀 Quick Start

### Prerequisites

- Java JDK 17+
- Maven 3.9+
- Docker & Docker Compose (for Selenium Grid)
- Chrome/Firefox/Edge browser

### Installation

```bash
# Clone repository
git clone https://github.com/your-org/ecommerce-test-automation.git
cd ecommerce-test-automation

# Set up environment variables
cp .env.template .env
# Edit .env with your credentials (see docs/SETUP.md for details)

# Install dependencies
mvn clean install -DskipTests
```

**Important:** You must set up the `.env` file before running tests. See [docs/SETUP.md](docs/SETUP.md) for detailed instructions.

### Running Tests

```bash
# Run all tests locally
mvn clean test

# Run smoke tests only
mvn test -Dgroups=smoke

# Run with Selenium Grid
docker-compose up -d selenium-hub chrome-node-1 chrome-node-2 chrome-node-3 chrome-node-4
mvn test -Dselenium.grid=true

# Run in headless mode
mvn test -Dheadless=true

# Generate Allure report
mvn allure:serve
```

---

## 📁 Project Structure

```
ecommerce-test-automation/
├── src/
│   ├── main/java/com/ecommerce/
│   │   ├── config/
│   │   │   └── ConfigManager.java          # Configuration management
│   │   ├── security/
│   │   │   ├── SecretsProvider.java        # Secrets abstraction interface
│   │   │   ├── EnvironmentSecretsProvider.java  # Env vars + .env loader
│   │   │   ├── SecretsManager.java         # Secrets facade
│   │   │   └── CredentialMasker.java       # Credential masking utility
│   │   ├── exceptions/
│   │   │   ├── SecretAccessException.java  # Security exception
│   │   │   ├── TestExecutionException.java
│   │   │   └── ElementWaitTimeoutException.java
│   │   └── utils/
│   │       └── ScreenshotUtils.java        # Screenshot utilities
│   │
│   └── test/java/com/ecommerce/
│       ├── tests/
│       │   ├── base/
│       │   │   └── BaseTest.java           # Base test with Grid support
│       │   ├── ui/
│       │   │   ├── LoginTest.java          # 12 login tests
│       │   │   ├── CartTest.java           # 10 cart tests
│       │   │   ├── CheckoutTest.java       # 12 checkout tests
│       │   │   ├── ProductTest.java        # 10 product tests
│       │   │   ├── NavigationTest.java     # 8 navigation tests
│       │   │   └── RemoveFromCartTest.java # 8 remove-from-cart tests
│       │   └── e2e/
│       │       └── E2ETest.java            # 8 E2E tests
│       ├── pages/
│       │   ├── base/
│       │   │   └── BasePage.java           # Base page object
│       │   ├── LoginPage.java
│       │   ├── HomePage.java
│       │   ├── CartPage.java
│       │   └── CheckoutPage.java
│       ├── listeners/
│       │   ├── TestListener.java           # Test event listener
│       │   └── RetryAnalyzer.java          # Flaky test handler
│       └── utils/
│           └── TestDataProvider.java       # Data providers
│
├── src/test/resources/
│   ├── config/
│   │   └── config.properties               # Test configuration (no credentials!)
│   ├── allure/
│   │   ├── categories.json                 # Failure categories
│   │   └── environment.properties          # Environment info
│   ├── allure.properties
│   └── log4j2.xml                          # Logging configuration
│
├── docs/
│   ├── SECURITY.md                          # Security practices & setup
│   └── SETUP.md                             # Quick start guide
├── .env.template                            # Environment variables template
├── .env                                     # Local env vars (not committed!)
├── docker-compose.yml                       # Selenium Grid setup
├── Dockerfile                               # Test container
├── Jenkinsfile                              # Jenkins pipeline
├── .github/workflows/
│   ├── test-automation.yml                 # Test execution pipeline
│   └── security-scan.yml                   # Security scanning pipeline
├── testng.xml                               # Local test suite (3 suites, 2-4 threads)
├── testng-grid.xml                          # CI/Grid test suite (full parallelism, used by -Pci)
├── pom.xml                                  # Maven configuration
└── README.md
```

---

## 🧪 Test Categories

### 1. Login Tests (12 tests)
- Successful login with valid credentials
- Login validation with various invalid inputs
- Security tests (password masking)
- Data-driven tests with multiple user types

### 2. Cart Tests (10 tests)
- Add single/multiple products
- Cart persistence across navigation
- Cart total calculation
- Empty cart validation

### 3. Checkout Tests (12 tests)
- Complete checkout flow
- Form validation (empty fields, special characters)
- Cancel and navigation tests
- Data-driven checkout validation

### 4. Product Tests (10 tests)
- Product display verification
- Sorting functionality (A-Z, Z-A, Price)
- Product count validation
- Price range validation

### 5. Navigation Tests (8 tests)
- Menu navigation
- Logout functionality
- Browser back/forward behavior
- Session persistence

### 6. E2E Tests (8 tests)
- Complete purchase flow
- Multi-product purchase
- Cart modification during checkout
- Performance user flows

---

## 🐳 Docker & Selenium Grid

### Start Selenium Grid

```bash
# Start Grid with 4 Chrome nodes (16 parallel sessions)
docker-compose up -d selenium-hub chrome-node-1 chrome-node-2 chrome-node-3 chrome-node-4

# View Grid status
open http://localhost:4444

# Run tests on Grid
docker-compose up test-runner

# Stop Grid
docker-compose down
```

### Grid Architecture

```
                    ┌─────────────────┐
                    │  Selenium Hub   │
                    │  :4444          │
                    └────────┬────────┘
                             │
       ┌─────────────┬───────┼───────┬─────────────┐
       ▼             ▼       ▼       ▼             ▼
┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐
│ Chrome #1  │ │ Chrome #2  │ │ Chrome #3  │ │ Chrome #4  │
│ 4 sessions │ │ 4 sessions │ │ 4 sessions │ │ 4 sessions │
└────────────┘ └────────────┘ └────────────┘ └────────────┘

Total Parallel Capacity: 16 sessions
```

---

## 🔄 CI/CD Integration

### Jenkins Pipeline

```bash
# Jenkins will automatically:
# 1. Start Selenium Grid
# 2. Run tests in parallel
# 3. Generate Allure report
# 4. Calculate reliability metrics
# 5. Send notifications
```

### GitHub Actions

Two separate workflows run in CI:

**`test-automation.yml`** — Test execution pipeline
```bash
# Triggers:
# - Push to main/develop
# - Pull requests
# - Daily regression at 2 AM UTC
# - Manual workflow dispatch (smoke / regression / all)

# Pipeline steps:
# 1. Start Selenium Grid (Docker services)
# 2. Run tests in parallel (headless Chrome/Firefox)
# 3. Generate Allure report  →  mvn allure:report
# 4. Upload artifacts (report + surefire results + screenshots)
# 5. Deploy Allure report to GitHub Pages (main branch only)
```

**`security-scan.yml`** — Security scanning pipeline
```bash
# Triggers:
# - Push to main/develop
# - Pull requests
# - Weekly on Monday at 6 AM UTC

# Scans:
# 1. OWASP Dependency Check  (fails on CVSS >= 7)
# 2. Secret Scanning         (TruffleHog v3, verified secrets only)
# 3. Container Scanning      (Trivy 0.28.0, CRITICAL + HIGH only)
# 4. Results uploaded to GitHub Security tab (SARIF via CodeQL v3)
```

---

## 📊 Reporting

### Allure Report

```bash
# Generate and open report (local development)
mvn allure:serve

# Generate static report only (used in CI)
mvn allure:report
# Output: target/site/allure-maven-plugin/index.html
```

### Report Features

- ✅ Test execution timeline
- 📈 Trends and history
- 📸 Screenshots on failure
- 🏷️ Categorized failures
- 📋 Step-by-step execution

---

## 🔧 Configuration

### config.properties

```properties
# Browser
browser=chrome
headless=false

# Timeouts
implicit.wait=10
explicit.wait=20
page.load.timeout=30

# Selenium Grid
selenium.grid=false
selenium.grid.url=http://localhost:4444/wd/hub

# Parallel execution
thread.count=8

# Retry
retry.count=2
```

### Environment Variables

```bash
export BROWSER=chrome
export HEADLESS=true
export SELENIUM_GRID_URL=http://selenium-hub:4444/wd/hub
export TEST_ENV=staging
```

---

## 📈 Performance Metrics

| Execution Mode | Test Count | Duration | Improvement |
|----------------|------------|----------|-------------|
| Sequential     | 60 tests   | ~25 min  | Baseline    |
| Parallel (4)   | 60 tests   | ~12 min  | 52% faster  |
| Parallel (8)   | 60 tests   | ~8 min   | 68% faster  |
| Grid (16)      | 60 tests   | ~5 min   | 80% faster  |

---

## 🛡️ Reliability Features

### RetryAnalyzer

- Automatically retries flaky tests (2 attempts)
- Tracks flaky test metrics
- Reports retry statistics in Allure

### Suite Reliability Target: 99%

```
Reliability = (Passed Tests / Total Tests) × 100

With RetryAnalyzer:
- First run failures are retried
- Only persistent failures count
- Achieved: 99%+ reliability
```

---

## 🧑‍💻 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

---

## 📝 Commands Reference

| Command | Description |
|---------|-------------|
| `mvn clean test` | Run all tests |
| `mvn test -Dgroups=smoke` | Run smoke tests |
| `mvn test -Dgroups=regression` | Run regression tests |
| `mvn test -Dgroups=e2e` | Run E2E tests |
| `mvn test -Dbrowser=firefox` | Run on Firefox |
| `mvn test -Dheadless=true` | Run headless |
| `mvn test -Pci` | Run with CI profile |
| `mvn test -Pgrid` | Run on Selenium Grid |
| `mvn allure:report` | Generate static Allure report (`target/site/allure-maven-plugin`) |
| `mvn allure:serve` | Generate & view Allure report in browser |
| `docker-compose up -d` | Start Selenium Grid |
| `docker-compose down` | Stop Selenium Grid |

---

## 📜 License

This project is licensed under the MIT License.

---

## 📧 Contact

**QA Team** - qa-team@company.com

Project Link: [https://github.com/your-org/ecommerce-test-automation](https://github.com/your-org/ecommerce-test-automation)
