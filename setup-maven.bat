@echo off
chcp 65001 >nul
echo 🔧 Maven Setup Assistant
echo =====================
echo.
echo Let's set up Maven so your Spring Boot application can run!
echo.
echo 📋 First, I need to know where you extracted Maven.
echo.
echo Please enter the path where you extracted Maven (e.g., C:\Program Files\Apache\maven)
echo.
set /p maven_path="Enter Maven path: "
echo.
echo 📝 Adding Maven to your PATH...
echo Maven path: %maven_path%
echo.
echo 💡 Instructions to add Maven to PATH:
echo ================================
echo 1. Press Windows Key + R, type "sysdm.cpl" and press Enter
echo 2. Click on the "Advanced" tab
echo 3. Click "Environment Variables" button
echo 4. Under "System Variables", scroll down and select "Path"
echo 5. Click "Edit"
echo 6. Click "New" and add this path:
echo    %maven_path%\bin
echo 7. Click "OK" on all windows
echo 8. Restart your command prompt or VS Code
echo.
echo 🧪 After setting up, test with: mvn -version
echo 🚀 Then run: mvn spring-boot:run
echo.
echo 💡 Or just double-click "easy-run.bat" and choose option 1
echo.
pause
