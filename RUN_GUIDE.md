# How to Run Your Spring Boot Application

## Problem Identified
You don't have Maven installed, which is needed to build and run Spring Boot applications.

## Solution Options

### Option 1: Install Maven (Recommended)
1. Download Maven from: https://maven.apache.org/download.cgi
2. Choose the "Binary zip archive" 
3. Extract to: `C:\Program Files\Apache\maven`
4. Add Maven to PATH:
   - Search for "Environment Variables" in Windows
   - Click "Environment Variables"
   - Under "System Variables", find "Path" and click "Edit"
   - Add: `C:\Program Files\Apache\maven\bin`
5. Restart your command prompt
6. Test with: `mvn -version`

### Option 2: Use Your IDE (Easiest)
Since you have Visual Studio Code, you can use the Spring Boot extension:

1. **Install Extensions in VS Code:**
   - "Spring Boot Extension Pack" by Microsoft
   - "Language Support for Java" by Red Hat

2. **Run the Application:**
   - Open your project in VS Code
   - Press `Ctrl+Shift+P` (or `F1`)
   - Type "Spring Boot: Run Application"
   - Select your `ElectronicVotingSystemApplication.java` file

### Option 3: Manual Build and Run (If you want to try without Maven)

<execute_command>
<command>echo "Creating manual build script..."</command>
<requires_approval>false</requires_approval>
<task_progress>
- [x] Check Java installation ✓
- [x] Check Maven installation ✓ (Not found)
- [ ] Provide running instructions
- [ ] Create simplified run script
- [ ] Test application startup
</task_progress>
</write_to_file>
