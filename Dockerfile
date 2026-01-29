# ============================================
# E-Commerce Test Automation - Docker Image
# ============================================
FROM maven:3.9.5-eclipse-temurin-17

LABEL maintainer="QA Team"
LABEL description="E-Commerce Test Automation Framework"

# Set working directory
WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src
COPY testng.xml .

# Create directories for reports
RUN mkdir -p /app/reports /app/screenshots /app/allure-results

# Set environment variables
ENV BROWSER=chrome
ENV HEADLESS=true
ENV SELENIUM_GRID_URL=http://selenium-hub:4444/wd/hub
ENV TEST_ENV=staging

# Default command - run all tests
ENTRYPOINT ["mvn", "clean", "test"]

# Allow passing additional Maven arguments
CMD ["-Dselenium.grid=true"]
