# 🚀 Maven Setup Complete Guide

## ✅ You've Installed Maven - Great Job!

Now you need to add Maven to your system's PATH so it can be used from any command prompt.

## 📋 Step-by-Step Setup

### 1. Find Your Maven Installation Path
When you extracted the Maven zip file, where did you put it?
Common locations:
- `C:\Program Files\Apache\maven`
- `C:\Users\YourName\maven`
- `C:\maven`

### 2. Add Maven to Windows PATH
Follow these steps carefully:

1. **Open System Properties:**
   - Press `Windows Key + R`
   - Type `sysdm.cpl`
   - Press Enter

2. **Environment Variables:**
   - Click the "Advanced" tab
   - Click "Environment Variables" button

3. **Edit PATH:**
   - Under "System Variables", scroll and find "Path"
   - Select "Path" and click "Edit"
   - Click "New"
   - Add your Maven bin path: `[Your Maven Path]\bin`
   - Example: `C:\Program Files\Apache\maven\bin`

4. **Save Changes:**
   - Click "OK"
   - Click "OK" again
   - Click "OK" to close System Properties

### 3. Restart and Test
1. **Close all command prompts and VS Code**
2. **Reopen a new command prompt**
3. **Test Maven:**
   ```bash
   mvn -version
   ```

## 🧪 Testing Maven

After setting up, test if Maven works:

```bash
cd "D:\ELECTRONIC VS"
mvn -version
```

**Expected output:**
```
Apache Maven 3.x.x
Maven home: C:\Program Files\Apache\maven
Java version: 17.0.14, vendor: Eclipse Adoptium
```

## 🚀 Run Your Spring Boot Application

Once Maven is working:

```bash
cd "D:\ELECTRONIC VS"
mvn spring-boot:run
```

**Expected output:**
```
Tomcat started on port(s): 8080 (http) with context path ''
Started ElectronicVotingSystemApplication in X.XXX seconds
```

## 🌐 Access Your Application

Visit these URLs in your browser:
- **Main App**: http://localhost:8080
- **Health Check**: http://localhost:8080/health
- **H2 Database**: http://localhost:8080/h2-console

## 💡 Alternative: Use the Easy Script

Instead of manual setup, you can:
1. **Double-click `setup-maven.bat`** - Follow the instructions
2. **Double-click `easy-run.bat`** - Choose option 1 to test Maven

## 🔧 Troubleshooting

**If `mvn -version` still doesn't work:**
1. Check that you added the correct path (include `\bin`)
2. Make sure you restarted your command prompt
3. Verify the Maven folder exists at your specified path
4. Try the `setup-maven.bat` script for guided setup

**If you see Java errors:**
- Java is working (we confirmed this earlier)
- Make sure you're in the correct project directory

## 🎉 You're Almost There!

Once Maven is configured, your Spring Boot application will run perfectly. The setup is straightforward - just follow the PATH instructions above!
