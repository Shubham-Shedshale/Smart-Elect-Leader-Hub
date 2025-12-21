# VS Code Setup for Spring Boot

## 🚀 Quick Start (Easiest Method)

Since you already have VS Code, this is the fastest way to run your Spring Boot application:

### Step 1: Install Required Extensions
1. Open VS Code
2. Click the Extensions icon (or press `Ctrl+Shift+X`)
3. Install these extensions:
   - **Spring Boot Extension Pack** (by Microsoft)
   - **Language Support for Java** (by Red Hat)

### Step 2: Open Your Project
1. In VS Code: File → Open Folder
2. Navigate to your `ELECTRONIC VS` folder
3. Click "Select Folder"

### Step 3: Run the Application
1. Press `Ctrl+Shift+P` (or `F1`) to open Command Palette
2. Type: `Spring Boot: Run Application`
3. Select: `ElectronicVotingSystemApplication.java`
4. Wait for the application to start (check the terminal at the bottom)

### Step 4: Access Your Application
Once running, open your browser and go to:
- **Main Application**: http://localhost:8080
- **Health Check**: http://localhost:8080/health
- **H2 Database Console**: http://localhost:8080/h2-console

## 🎯 Alternative: Use the Easy Run Script

Double-click the `easy-run.bat` file I created for you and choose option 2 to automatically open VS Code.

## 📋 What to Expect

When the application starts successfully, you should see:
```
Tomcat started on port(s): 8080 (http) with context path ''
ElectronicVotingSystemApplication : Started ElectronicVotingSystemApplication in X.XXX seconds
```

## 🔧 Troubleshooting

**If you see errors:**
1. Make sure all extensions are installed
2. Wait a moment for VS Code to fully load the Java project
3. Check the terminal output for specific error messages
4. Try restarting VS Code

**If H2 Console doesn't work:**
- Go to http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)

## 🎉 You're Ready!

Your Spring Boot application is now running and ready for development!
