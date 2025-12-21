@echo off
echo ========================================
echo Electronic Voting System - Setup & Run
echo ========================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17 or higher
    echo Download from: https://adoptium.net/
    pause
    exit /b 1
)

echo Java found! ✓
echo.

REM Check if Maven is available
mvn -version >nul 2>&1
if %errorlevel% equ 0 (
    echo Maven found! ✓
    echo.
    echo Starting Spring Boot application...
    mvn spring-boot:run
) else (
    echo Maven not found. Installing Maven wrapper...
    echo.
    
    REM Try to download Maven wrapper
    curl -s -o mvnw.cmd https://raw.githubusercontent.com/takari/maven-wrapper/master/mvnw.cmd
    curl -s -o .mvn/wrapper/maven-wrapper.jar https://repo1.maven.org/maven2/org/codehaus/plexus/plexus-utils/1.1/plexus-utils-1.1.jar
    
    if exist mvnw.cmd (
        echo Maven wrapper installed! Starting application...
        .\mvnw.cmd spring-boot:run
    ) else (
        echo.
        echo ========================================
        echo ALTERNATIVE: Manual Setup Required
        echo ========================================
        echo.
        echo Option 1 - Install Maven:
        echo 1. Download Maven from: https://maven.apache.org/download.cgi
        echo 2. Extract to C:\Program Files\Apache\maven
        echo 3. Add C:\Program Files\Apache\maven\bin to PATH
        echo 4. Restart command prompt and run this script again
        echo.
        echo Option 2 - Use IDE (Recommended):
        echo 1. Import project in IntelliJ IDEA, Eclipse, or VSCode
        right-click ElectronicVotingSystemApplication.java and select "Run"
        echo.
        echo Option 3 - Quick JAR Build:
        echo mvn clean package -DskipTests
        echo java -jar target/electronic-voting-system-1.0.0.jar
        echo.
        pause
    )
)
