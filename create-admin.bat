@echo off
echo ================================
echo Electronic Voting System
echo Admin Account Creation Script
echo ================================
echo.

REM Check if application is running
echo Checking if application is running on localhost:8080...
curl -s http://localhost:8888/api/health >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Application is not running!
    echo Please start the application first with: mvn spring-boot:run
    echo The application should be accessible at http://localhost:8888
    echo Then run this script again.
    pause
    exit /b 1
)

echo Application is running! Proceeding with admin creation...
echo.

REM Default admin credentials
set ADMIN_EMAIL=admin@evoting.local
set ADMIN_PASSWORD=Admin123!@#

echo Creating admin account with credentials:
echo Email: %ADMIN_EMAIL%
echo Password: %ADMIN_PASSWORD%
echo.

REM Create admin account
echo Creating admin account...
curl -s -X POST http://localhost:8888/api/auth/create-admin ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"%ADMIN_EMAIL%\",\"password\":\"%ADMIN_PASSWORD%\"}" > temp_admin_response.json

REM Check if admin was created successfully
findstr /C:"success" temp_admin_response.json >nul 2>&1
if %errorlevel% equ 0 (
    echo.
    echo ✅ ADMIN ACCOUNT CREATED SUCCESSFULLY!
    echo.
    echo Login Credentials:
    echo =================
    echo Email: %ADMIN_EMAIL%
    echo Password: %ADMIN_PASSWORD%
    echo.
    echo Login Endpoints:
    echo - Admin Login: POST http://localhost:8888/api/auth/admin-login
    echo - Admin Panel: http://localhost:8888/admin/login (if web interface exists)
    echo.
    echo To test login, you can use this curl command:
    echo curl -X POST http://localhost:8888/api/auth/admin-login -H "Content-Type: application/json" -d "{\"email\":\"%ADMIN_EMAIL%\",\"password\":\"%ADMIN_PASSWORD%\"}"
) else (
    echo.
    echo ❌ Failed to create admin account!
    echo Response:
    type temp_admin_response.json
    echo.
    echo This might happen if:
    echo 1. The admin account already exists
    echo 2. The application is not properly configured
    echo 3. Database connection issues
)

REM Clean up
del temp_admin_response.json 2>nul

echo.
pause