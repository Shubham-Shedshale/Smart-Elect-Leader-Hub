@echo off
chcp 65001 >nul
echo 🔧 Spring Boot Application Runner
echo ================================
echo.
echo Java Version:
java -version
echo.
echo Maven Status:
mvn -version 2>nul && echo "✅ Maven is available" || echo "❌ Maven not found - will use IDE method"
echo.
echo 📋 Available Options:
echo 1. Try to run with Maven (if available)
echo 2. Open in VS Code for IDE-based running
echo 3. Install Maven
echo 4. Exit
echo.
set /p choice="Choose an option (1-4): "

if "%choice%"=="1" goto maven
if "%choice%"=="2" goto vscode
if "%choice%"=="3" goto install
if "%choice%"=="4" goto exit

:maven
echo 🚀 Trying to run with Maven...
mvn spring-boot:run
if %ERRORLEVEL% EQU 0 goto success
echo ❌ Maven failed. Try option 2 or 3.
pause
goto end

:vscode
echo 📝 Opening in VS Code...
code .
echo.
echo 💡 Instructions for VS Code:
echo 1. Wait for VS Code to open
echo 2. Press Ctrl+Shift+P (or F1)
echo 3. Type "Spring Boot: Run Application"
echo 4. Select "ElectronicVotingSystemApplication.java"
echo 5. The application will start automatically
echo.
echo 🌐 After starting, access:
echo   - Main App: http://localhost:8080
echo   - Health Check: http://localhost:8080/health
echo   - H2 Console: http://localhost:8080/h2-console
pause
goto end

:install
echo 📦 Installing Maven...
echo.
echo Please follow these steps:
echo 1. Download Maven from: https://maven.apache.org/download.cgi
echo 2. Choose "Binary zip archive" (apache-maven-x.x.x-bin.zip)
echo 3. Extract to: C:\Program Files\Apache\maven
echo 4. Add C:\Program Files\Apache\maven\bin to your PATH
echo 5. Restart this script
echo.
echo 🔄 Press any key to open Maven download page...
pause
start https://maven.apache.org/download.cgi
goto end

:success
echo ✅ Application started successfully!
echo 🌐 Access your application at:
echo   - Main App: http://localhost:8080
echo   - Health Check: http://localhost:8080/health
echo   - H2 Console: http://localhost:8080/h2-console
echo.
echo 💡 Press Ctrl+C to stop the application
goto end

:exit
echo 👋 Goodbye!

:end
echo.
pause
