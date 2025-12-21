@echo off
echo ========================================
echo Electronic Voting System - Test Script
echo ========================================
echo.

echo Checking if application is running...
echo.

echo Testing application health endpoint...
curl -s http://localhost:8888/actuator/health

echo.
echo.
echo If you see application health status above, your database is working!
echo.
echo Access your application:
echo - Main page: http://localhost:8888
echo - Login page: http://localhost:8888/login
echo.

echo Press any key to start the application...
pause

echo.
echo Starting Electronic Voting System...
echo.

mvn spring-boot:run

echo.
echo Application stopped.
pause