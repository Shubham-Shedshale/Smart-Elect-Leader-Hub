@echo off
echo ===============================================
echo    Electronic Voting System - Database Setup
echo ===============================================
echo.
echo This script will help you set up the database for the application.
echo.

echo Available Database Options:
echo.
echo 1. MySQL Setup (Recommended)
echo 2. H2 Database Setup (Quick Test)
echo 3. MS SQL Server Setup (Updated)
echo 4. Check Current Status
echo 5. Exit
echo.

:choice
set /p choice="Enter your choice (1-5): "

if "%choice%"=="1" goto mysql_setup
if "%choice%"=="2" goto h2_setup
if "%choice%"=="3" goto mssql_setup
if "%choice%"=="4" goto check_status
if "%choice%"=="5" goto exit
echo Invalid choice. Please try again.
goto choice

:mysql_setup
echo.
echo ============= MySQL Setup =============
echo.
echo This will guide you through MySQL setup:
echo.
echo Step 1: Install MySQL (if not already installed)
echo - Download from: https://dev.mysql.com/downloads/installer/
echo - Or use XAMPP: https://www.apachefriends.org/download.html
echo.
echo Step 2: Create the database
echo Please run these commands in MySQL:
echo.
echo CREATE DATABASE electronic_voting_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
echo USE electronic_voting_db;
echo.
echo Step 3: Update credentials in application.properties
echo - Username: root (or your MySQL username)
echo - Password: your_mysql_password
echo.
echo Step 4: Test connection
echo mysql -u root -p -h localhost electronic_voting_db
echo.
echo Press any key to continue with application startup...
pause >nul
goto start_application

:h2_setup
echo.
echo ============= H2 Database Setup =============
echo.
echo Switching to H2 Database configuration...
copy "src\main\resources\application-h2.properties" "src\main\resources\application.properties"
if errorlevel 1 (
    echo ERROR: Could not copy configuration file!
    goto end
)
echo Configuration updated for H2 Database!
echo.
echo H2 Database Features:
echo - No installation required
echo - In-memory database
echo - Perfect for testing
echo - Console available at: http://localhost:8080/h2-console
echo.
goto start_application

:mssql_setup
echo.
echo ============= MS SQL Server Setup =============
echo.
echo Configuration for existing SQL Server Express instance:
echo.
echo Instance: SQLEXPRESS
echo Database: ELECTRONIC_VS
echo.
echo Step 1: Ensure SQL Server Express is running
echo - Open SQL Server Configuration Manager
echo - Verify "SQL Server (SQLEXPRESS)" service is running
echo.
echo Step 2: Enable TCP/IP Protocol (if not already done)
echo - Open SQL Server Configuration Manager
echo - Go to SQL Server Network Configuration
echo - Click "Protocols for SQLEXPRESS"
echo - Right-click "TCP/IP" → Properties
echo - Set "Enabled" to "Yes"
echo - Click "IP Addresses" tab
echo - Find "IPAll" section
echo - Set "TCP Port" to 1433
echo - Click "OK"
echo - Restart SQL Server service
echo.
echo Step 3: Run Migration Script for ELECTRONIC_VS Database
echo - Open SQL Server Management Studio (SSMS)
echo - Connect to: localhost\SQLEXPRESS
echo - Run the script: mssql-database-migration.sql
echo - This creates all tables, constraints, indexes, and sample data
echo.
echo Step 4: Update Application Configuration
echo - Edit src\main\resources\application-mssql.properties
echo - Update username and password for your SQL Server
echo - Default: username=sa, password=YourStrong@Passw0rd
echo.
echo Step 5: Switch to MS SQL Configuration
echo copy "src\main\resources\application-mssql.properties" "src\main\resources\application.properties"
echo.
echo Step 6: Add MS SQL JDBC Driver to Maven
echo - Ensure mssql-jdbc dependency is in pom.xml
echo - Or download from: https://learn.microsoft.com/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server
echo.
echo Step 7: Test Connection
echo sqlcmd -S localhost\SQLEXPRESS -U sa -P YourPassword -Q "SELECT name FROM sys.databases WHERE name = 'ELECTRONIC_VS'"
echo.
echo Press any key to continue with application startup...
pause >nul
goto start_application

:check_status
echo.
echo ============= Current Status Check =============
echo.
echo 1. Checking application.properties...
if exist "src\main\resources\application.properties" (
    echo ✓ application.properties exists
    echo Current database configuration:
    findstr "spring.datasource.url" "src\main\resources\application.properties"
    echo.
) else (
    echo ✗ application.properties not found!
)

echo 2. Checking database configuration files...
if exist "src\main\resources\application-mysql.properties" (
    echo ✓ application-mysql.properties exists
) else (
    echo ✗ application-mysql.properties not found!
)

if exist "src\main\resources\application-h2.properties" (
    echo ✓ application-h2.properties exists
) else (
    echo ✗ application-h2.properties not found!
)

if exist "src\main\resources\application-mysql-backup.properties" (
    echo ✓ application-mysql-backup.properties exists
) else (
    echo ✗ application-mysql-backup.properties not found!
)

echo.
echo 3. Checking Maven compilation...
echo Compiling project to check for errors...
mvn compile -q
if errorlevel 1 (
    echo ✗ Compilation failed! Check your configuration.
) else (
    echo ✓ Compilation successful!
)

echo.
goto end

:start_application
echo.
echo Starting application with current configuration...
echo.
echo If you get database connection errors:
echo 1. Check that your database server is running
echo 2. Verify credentials in application.properties
echo 3. Ensure the database exists
echo 4. Check network connectivity
echo.
mvn spring-boot:run
goto end

:exit
echo.
echo Goodbye!
goto end

:end
echo.
echo ===============================================
echo Press any key to exit...
pause >nul
