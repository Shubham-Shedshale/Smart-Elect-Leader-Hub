@echo off
echo ================================
echo Electronic Voting System
echo Admin Setup & Login Test
echo ================================
echo.

set BASE_URL=http://localhost:8888
set ADMIN_EMAIL=admin@evoting.local
set ADMIN_PASSWORD=Admin123!@#

echo Testing application connectivity...
curl -s %BASE_URL%/api/health > temp_health.json
if %errorlevel% neq 0 (
    echo ERROR: Application is not running on %BASE_URL%
    echo Please start with: mvn spring-boot:run
    pause
    exit /b 1
)

echo ✅ Application is running!
echo.

echo Step 1: Creating admin account...
echo Request: POST %BASE_URL%/api/auth/create-admin
echo.

curl -s -X POST %BASE_URL%/api/auth/create-admin ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"%ADMIN_EMAIL%\",\"password\":\"%ADMIN_PASSWORD%\"}" > temp_create_response.json

echo Response from create-admin:
type temp_create_response.json
echo.

echo Step 2: Testing admin login...
echo Request: POST %BASE_URL%/api/auth/admin-login
echo.

curl -s -X POST %BASE_URL%/api/auth/admin-login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"%ADMIN_EMAIL%\",\"password\":\"%ADMIN_PASSWORD%\"}" > temp_login_response.json

echo Response from admin-login:
type temp_login_response.json
echo.

echo ================================
echo SUMMARY
echo ================================
echo.
echo Admin Credentials:
echo Email: %ADMIN_EMAIL%
echo Password: %ADMIN_PASSWORD%
echo.
echo Endpoints tested:
echo - Create: POST %BASE_URL%/api/auth/create-admin
echo - Login:  POST %BASE_URL%/api/auth/admin-login
echo.
echo If you see success responses above, admin setup is complete!
echo.

REM Clean up
del temp_health.json temp_create_response.json temp_login_response.json 2>nul

pause