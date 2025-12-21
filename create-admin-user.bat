@echo off
echo ========================================
echo Create Admin User for Electronic Voting System
echo ========================================
echo.

set ADMIN_EMAIL=admin@evoting.local
set ADMIN_PASSWORD=Admin123!
set ADMIN_FIRST_NAME=System
set ADMIN_LAST_NAME=Administrator

echo Creating admin user with the following details:
echo Email: %ADMIN_EMAIL%
echo Password: %ADMIN_PASSWORD%
echo Name: %ADMIN_FIRST_NAME% %ADMIN_LAST_NAME%
echo.

echo Press any key to create admin user...
pause

echo.
echo Making API call to create admin user...
echo.

curl -X POST http://localhost:8888/api/auth/create-admin ^
  -H "Content-Type: application/json" ^
  -d "{
    \"email\": \"%ADMIN_EMAIL%\",
    \"password\": \"%ADMIN_PASSWORD%\",
    \"firstName\": \"%ADMIN_FIRST_NAME%\",
    \"lastName\": \"%ADMIN_LAST_NAME%\"
  }"

echo.
echo.
echo Admin user creation attempt completed.
echo.
echo Now you can login with:
echo Email: %ADMIN_EMAIL%
echo Password: %ADMIN_PASSWORD%
echo URL: http://localhost:8888/login
echo.
echo Press any key to exit...
pause