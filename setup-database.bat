@echo off
echo ===============================================
echo    Electronic Voting System - Database Setup
echo ===============================================
echo.
echo Choose your database configuration:
echo.
echo 1. MySQL (Recommended - Production Ready)
echo 2. H2 Database (Quick Testing)  
echo 3. SQL Server Express (Original Setup)
echo 4. Test Current Configuration
echo 5. Exit
echo.

:choice
set /p choice="Enter your choice (1-5): "

if "%choice%"=="1" goto mysql
if "%choice%"=="2" goto h2
if "%choice%"=="3" goto mssql
if "%choice%"=="4" goto test
if "%choice%"=="5" goto exit
echo Invalid choice. Please try again.
goto choice

:mysql
echo Switching to MySQL configuration...
copy "src\main\resources\application-mysql.properties" "src\main\resources\application.properties"
echo Configuration updated for MySQL!
echo.
echo IMPORTANT: Make sure MySQL is installed and running
echo 1. Install MySQL or XAMPP
echo 2. Create database 'electronic_voting_db'
echo 3. Update password in application.properties
echo.
echo Starting application with MySQL...
mvn spring-boot:run
goto end

:h2
echo Switching to H2 Database configuration...
copy "src\main\resources\application-h2.properties" "src\main\resources\application.properties"
echo Configuration updated for H2 Database!
echo H2 console will be available at: http://localhost:8080/h2-console
echo.
echo Starting application with H2 Database...
mvn spring-boot:run
goto end

:mssql
echo Switching to SQL Server Express configuration...
copy "src\main\resources\application-mysql-backup.properties" "src\main\resources\application.properties"
echo Configuration updated for SQL Server Express!
echo.
echo IMPORTANT: Make sure SQL Server Express is configured properly
echo 1. Enable TCP/IP in SQL Server Configuration Manager
echo 2. Restart SQL Server service
echo 3. Create database 'ELECTRONIC_VS'
echo.
echo Starting application with SQL Server Express...
mvn spring-boot:run
goto end

:test
echo Testing current configuration...
echo.
echo Current configuration:
findstr "spring.datasource.url" "src\main\resources\application.properties"
echo.
echo Checking if application can compile...
mvn compile
if errorlevel 1 (
    echo.
    echo Compilation failed! Check your configuration.
) else (
    echo.
    echo Compilation successful! Ready to run.
)
goto end

:exit
echo Goodbye!
goto end

:end
echo.
echo ===============================================
echo Press any key to exit...
pause >nul