@echo off
echo Stopping current application...
taskkill /f /im java.exe >nul 2>&1
timeout /t 3 /nobreak >nul
echo Starting application...
mvn spring-boot:run
pause