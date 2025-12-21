# VS Code Setup Guide - Electronic Voting System

## 🚀 Running in Visual Studio Code

Yes! You can easily run the Electronic Voting System in VS Code. Here's how:

## 📋 Prerequisites

### 1. Install VS Code
- Download from: https://code.visualstudio.com/

### 2. Install Java Extensions
Install these extensions in VS Code:
- **Extension Pack for Java** (Microsoft) - `vscjava.vscode-java-pack`
- **Spring Boot Extension Pack** (VMware) - `pivotal.vscode-spring-boot`
- **Spring Boot Dashboard** (VMware) - `vscjava.vscode-spring-boot-dashboard`

### 3. Install Java 17+
- Download from: https://adoptium.net/
- Verify installation: Open VS Code terminal (Ctrl+`) and run `java -version`

## 🔧 Setup Steps

### Step 1: Open Project
1. Launch **VS Code**
2. **File** → **Open Folder**
3. Select the **Electronic Voting System** folder
4. VS Code will recognize it as a Maven project automatically

### Step 2: Install Dependencies
VS Code will prompt you to install dependencies. If not:
1. Open **Terminal** (Ctrl + `)
2. Run: `mvn compile`
3. If Maven is not installed, see **Alternative Methods** below

### Step 3: Run the Application

#### Method A: Using Code Lens (Easiest)
1. Open `ElectronicVotingSystemApplication.java` in the editor
2. Look for **"Run"** above the `main()` method
3. Click **"Run"** → Spring Boot will start automatically
4. Access the app at http://localhost:8080

#### Method B: Using Spring Boot Dashboard
1. Press `Ctrl + Shift + P` (Command Palette)
2. Type: **"Spring Boot: Show Dashboard"**
3. Click **"Start App"** next to your project
4. Watch the logs in the terminal

#### Method C: Using Debug
1. Open `ElectronicVotingSystemApplication.java`
2. Click the **debug icon** (bug) next to the "Run" button
3. Select **"Java Application"**
4. Set breakpoints and debug your code!

### Step 4: Access the Application
Once running, open your browser and go to:
- **Homepage**: http://localhost:8080/
- **Login**: http://localhost:8080/login
- **Admin Dashboard**: http://localhost:8080/admin/dashboard

## 🔧 VS Code Features You'll Love

### **IntelliSense & Auto-completion**
- Full Java code completion
- Method signatures and parameter hints
- Import statements auto-completion

### **Code Navigation**
- **Go to Definition**: `F12` on any method/variable
- **Find References**: `Shift + F12`
- **Go to File**: `Ctrl + P`
- **Command Palette**: `Ctrl + Shift + P`

### **Spring Boot Tools**
- **Spring Boot Dashboard**: View and manage running apps
- **Application Properties**: Auto-completion for Spring properties
- **Bean Navigation**: Navigate between Spring beans

### **Database Integration**
- **SQL Server Extension**: Install `ms-mssql.mssql`
- View and query your database directly in VS Code

### **Git Integration**
- Built-in Git support
- View changes in the source control panel (Ctrl + Shift + G)

## 🛠 Alternative: If Maven is Not Available

### Option 1: Install Maven Extension
1. Install **Maven for Java** extension by Microsoft
2. VS Code will automatically handle Maven commands

### Option 2: Use Spring Boot Maven Wrapper
The project includes Maven wrapper:
1. In terminal: `.\mvnw.cmd spring-boot:run` (Windows)
2. Or: `./mvnw spring-boot:run` (Mac/Linux)

### Option 3: Use Gradle (Alternative Build Tool)
If you prefer Gradle over Maven:
1. VS Code will auto-detect the project type
2. Use **Gradle Tasks** panel to run build tasks

## 📱 Testing the Application

### 1. Create Your First Admin
Use VS Code's **REST Client** extension or **Thunder Client**:

```http
POST http://localhost:8080/api/auth/create-admin
Content-Type: application/json

{
  "email": "admin@company.com",
  "password": "Admin123!"
}
```

### 2. Test the Web Interface
1. Go to http://localhost:8080/
2. Click **"Admin"** tab
3. Login with the credentials you created
4. Start creating your first election!

## 🔍 Useful VS Code Shortcuts

| Action | Shortcut |
|--------|----------|
| **Run Application** | `F5` or click "Run" |
| **Debug Application** | `Ctrl + F5` |
| **Show Problems** | `Ctrl + Shift + M` |
| **Toggle Terminal** | `Ctrl + ` |
| **Command Palette** | `Ctrl + Shift + P` |
| **Go to File** | `Ctrl + P` |
| **Quick Fix** | `Ctrl + .` |
| **Format Document** | `Shift + Alt + F` |

## 🌟 Pro Tips for VS Code

### **1. Configure Java Settings**
Create `.vscode/settings.json`:
```json
{
    "java.home": "C:\\Program Files\\Java\\jdk-17",
    "java.format.settings.url": "eclipse-formatter.xml",
    "spring-boot.ls.java.home": "C:\\Program Files\\Java\\jdk-17"
}
```

### **2. Enable Auto-save**
1. **File** → **Auto Save**
2. Never lose your work!

### **3. Use Multiple Tabs**
1. Open different parts of the project
2. Use `Ctrl + Tab` to switch between tabs
3. Great for editing models and controllers together

### **4. Terminal Integration**
1. Built-in terminal with **PowerShell/Command Prompt**
2. Perfect for running commands and checking logs
3. Use `Ctrl + ` to toggle

## 🐛 Troubleshooting in VS Code

### **Java Not Found**
- Install Java 17+ from https://adoptium.net/
- Set `JAVA_HOME` environment variable
- Restart VS Code

### **Extension Issues**
- **Reload VS Code**: `Ctrl + Shift + P` → "Developer: Reload Window"
- **Reinstall extensions** if problems persist

### **Port 8080 in Use**
- Check what's using port 8080
- Change port in `application.properties`: `server.port=8081`

### **Database Connection**
- Ensure SQL Server is running
- Check connection settings in `application.properties`
- Use VS Code SQL extension to test connection

## 🎉 Success Indicators

You'll know everything is working when you see:
- ✅ VS Code shows "Spring Boot" indicator in bottom status bar
- ✅ Terminal shows "Tomcat started on port(s): 8080"
- ✅ Browser loads http://localhost:8080 successfully
- ✅ No red error markers in Problems panel (Ctrl + Shift + M)

## 🚀 Next Steps

Once the app is running in VS Code:
1. **Explore the code** - use F12 to navigate
2. **Create your first election** in the admin dashboard
3. **Test voting functionality** as a voter
4. **Add some candidates** and see real-time results!
5. **Use debug mode** to understand how the app works

---

**Happy coding in VS Code!** 🎯