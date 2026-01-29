// ============================================
// E-Commerce Test Automation - Jenkins Pipeline
// ============================================

pipeline {
    agent any
    
    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }
    
    environment {
        // Test Configuration
        BROWSER = 'chrome'
        HEADLESS = 'true'
        TEST_ENV = 'staging'
        
        // Selenium Grid
        SELENIUM_GRID_URL = 'http://selenium-hub:4444/wd/hub'
        
        // Allure
        ALLURE_RESULTS = 'target/allure-results'
        
        // Notifications
        SLACK_CHANNEL = '#qa-automation'
        EMAIL_RECIPIENTS = 'qa-team@company.com'
    }
    
    parameters {
        choice(
            name: 'TEST_SUITE',
            choices: ['smoke', 'regression', 'all'],
            description: 'Select test suite to run'
        )
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox', 'edge'],
            description: 'Select browser'
        )
        choice(
            name: 'ENVIRONMENT',
            choices: ['staging', 'qa', 'production'],
            description: 'Select test environment'
        )
        booleanParam(
            name: 'USE_SELENIUM_GRID',
            defaultValue: true,
            description: 'Run tests on Selenium Grid'
        )
        string(
            name: 'THREAD_COUNT',
            defaultValue: '8',
            description: 'Number of parallel threads'
        )
    }
    
    options {
        timeout(time: 60, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        ansiColor('xterm')
    }
    
    stages {
        stage('🔧 Setup') {
            steps {
                script {
                    echo "╔════════════════════════════════════════════╗"
                    echo "║     E-COMMERCE TEST AUTOMATION             ║"
                    echo "╠════════════════════════════════════════════╣"
                    echo "║ Suite: ${params.TEST_SUITE}"
                    echo "║ Browser: ${params.BROWSER}"
                    echo "║ Environment: ${params.ENVIRONMENT}"
                    echo "║ Parallel Threads: ${params.THREAD_COUNT}"
                    echo "║ Selenium Grid: ${params.USE_SELENIUM_GRID}"
                    echo "╚════════════════════════════════════════════╝"
                }
                
                // Clean workspace
                cleanWs()
                
                // Checkout code
                checkout scm
            }
        }
        
        stage('📦 Dependencies') {
            steps {
                echo "Installing Maven dependencies..."
                sh 'mvn clean install -DskipTests -B'
            }
        }
        
        stage('🐳 Start Selenium Grid') {
            when {
                expression { params.USE_SELENIUM_GRID == true }
            }
            steps {
                echo "Starting Selenium Grid..."
                sh '''
                    docker-compose up -d selenium-hub chrome-node-1 chrome-node-2 chrome-node-3 chrome-node-4
                    
                    # Wait for Grid to be ready
                    echo "Waiting for Selenium Grid to be ready..."
                    for i in {1..30}; do
                        if curl -s http://localhost:4444/wd/hub/status | grep -q '"ready":true'; then
                            echo "Selenium Grid is ready!"
                            break
                        fi
                        echo "Waiting... ($i/30)"
                        sleep 5
                    done
                '''
            }
        }
        
        stage('🧪 Run Tests') {
            steps {
                script {
                    def mvnCommand = "mvn clean test"
                    
                    // Add test suite parameter
                    if (params.TEST_SUITE == 'smoke') {
                        mvnCommand += " -Dgroups=smoke"
                    } else if (params.TEST_SUITE == 'regression') {
                        mvnCommand += " -Dgroups=regression"
                    }
                    // 'all' runs everything without groups filter
                    
                    // Add other parameters
                    mvnCommand += " -Dbrowser=${params.BROWSER}"
                    mvnCommand += " -Denvironment=${params.ENVIRONMENT}"
                    mvnCommand += " -Dheadless=${HEADLESS}"
                    mvnCommand += " -Dthread.count=${params.THREAD_COUNT}"
                    
                    // Selenium Grid parameters
                    if (params.USE_SELENIUM_GRID) {
                        mvnCommand += " -Dselenium.grid=true"
                        mvnCommand += " -Dselenium.grid.url=${SELENIUM_GRID_URL}"
                    }
                    
                    mvnCommand += " -B"
                    
                    echo "Executing: ${mvnCommand}"
                    
                    // Run tests and capture exit code
                    def exitCode = sh(script: mvnCommand, returnStatus: true)
                    
                    if (exitCode != 0) {
                        unstable("Some tests failed")
                    }
                }
            }
            post {
                always {
                    // Archive test results
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('📊 Generate Reports') {
            steps {
                echo "Generating Allure Report..."
                
                allure([
                    includeProperties: true,
                    jdk: '',
                    properties: [],
                    reportBuildPolicy: 'ALWAYS',
                    results: [[path: 'target/allure-results']]
                ])
            }
        }
        
        stage('📈 Analyze Results') {
            steps {
                script {
                    // Parse test results
                    def testResults = readFile('target/surefire-reports/testng-results.xml')
                    
                    // Calculate metrics (simplified)
                    def passed = (testResults =~ /passed="(\d+)"/).findAll()
                    def failed = (testResults =~ /failed="(\d+)"/).findAll()
                    def skipped = (testResults =~ /skipped="(\d+)"/).findAll()
                    
                    def totalPassed = passed ? passed[0][1].toInteger() : 0
                    def totalFailed = failed ? failed[0][1].toInteger() : 0
                    def totalSkipped = skipped ? skipped[0][1].toInteger() : 0
                    def total = totalPassed + totalFailed + totalSkipped
                    
                    def reliability = total > 0 ? (totalPassed / total * 100).round(2) : 0
                    
                    echo "╔════════════════════════════════════════════╗"
                    echo "║     TEST RESULTS SUMMARY                   ║"
                    echo "╠════════════════════════════════════════════╣"
                    echo "║ Total Tests: ${total}"
                    echo "║ ✅ Passed: ${totalPassed}"
                    echo "║ ❌ Failed: ${totalFailed}"
                    echo "║ ⚠️  Skipped: ${totalSkipped}"
                    echo "║ 📊 Reliability: ${reliability}%"
                    echo "╚════════════════════════════════════════════╝"
                    
                    // Set build status based on reliability
                    if (reliability < 95) {
                        unstable("Reliability below 95%: ${reliability}%")
                    }
                    
                    // Save metrics for trending
                    writeFile file: 'metrics.json', text: """
                    {
                        "buildNumber": "${BUILD_NUMBER}",
                        "totalTests": ${total},
                        "passed": ${totalPassed},
                        "failed": ${totalFailed},
                        "skipped": ${totalSkipped},
                        "reliability": ${reliability},
                        "timestamp": "${new Date().format('yyyy-MM-dd HH:mm:ss')}"
                    }
                    """
                    archiveArtifacts artifacts: 'metrics.json', fingerprint: true
                }
            }
        }
        
        stage('🐳 Stop Selenium Grid') {
            when {
                expression { params.USE_SELENIUM_GRID == true }
            }
            steps {
                echo "Stopping Selenium Grid..."
                sh 'docker-compose down || true'
            }
        }
    }
    
    post {
        always {
            // Archive artifacts
            archiveArtifacts artifacts: '''
                target/surefire-reports/**/*,
                reports/**/*,
                screenshots/**/*,
                target/allure-results/**/*
            ''', allowEmptyArchive: true
            
            // Clean up
            cleanWs(cleanWhenNotBuilt: false,
                    deleteDirs: true,
                    disableDeferredWipeout: true,
                    notFailBuild: true)
        }
        
        success {
            script {
                echo "✅ BUILD SUCCESSFUL"
                
                // Slack notification (if configured)
                // slackSend(
                //     channel: SLACK_CHANNEL,
                //     color: 'good',
                //     message: "✅ E-Commerce Tests PASSED\nBuild: ${BUILD_NUMBER}\nSuite: ${params.TEST_SUITE}"
                // )
            }
        }
        
        failure {
            script {
                echo "❌ BUILD FAILED"
                
                // Email notification
                // emailext(
                //     subject: "❌ E-Commerce Tests FAILED - Build #${BUILD_NUMBER}",
                //     body: """
                //         Test execution failed.
                //         
                //         Build: ${BUILD_URL}
                //         Suite: ${params.TEST_SUITE}
                //         Browser: ${params.BROWSER}
                //         
                //         Please check the Allure report for details.
                //     """,
                //     recipientProviders: [[$class: 'CulpritsRecipientProvider']],
                //     to: EMAIL_RECIPIENTS
                // )
            }
        }
        
        unstable {
            echo "⚠️ BUILD UNSTABLE - Some tests failed"
        }
    }
}
