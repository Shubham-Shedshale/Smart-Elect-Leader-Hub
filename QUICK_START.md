# Electronic Voting System - Quick Start Guide

## 🚀 How to Run the Application

Since Maven is not available on your system, here are several ways to run the Electronic Voting System:

### **Method 1: Using IntelliJ IDEA (Recommended)**

1. **Install IntelliJ IDEA Community Edition** (free)
   - Download from: https://www.jetbrains.com/idea/download/

2. **Open the project**:
   - Launch IntelliJ IDEA
   - File → Open → Select the `electronic-voting-system` folder

3. **Run the application**:
   - Right-click on `ElectronicVotingSystemApplication.java`
   - Select "Run 'ElectronicVotingSystemApplication.main()'"
   - Or press the green play button

### **Method 2: Using Eclipse**

1. **Install Eclipse for Java Developers**
   - Download from: https://www.eclipse.org/downloads/

2. **Import the project**:
   - File → Import → Maven → Existing Maven Projects
   - Select the project folder

3. **Run the application**:
   - Right-click on the main class → Run As → Java Application

### **Method 3: Using VS Code**

1. **Install VS Code** and Java extensions:
   - Extension Pack for Java (Microsoft)

2. **Open the project folder**:
   - File → Open Folder → Select project folder

3. **Run the application**:
   - Open `ElectronicVotingSystemApplication.java`
   - Click "Run" in the code lens above the main method

### **Method 4: Using the Provided Script**

Double-click on `run-application.bat` in the project folder, which will:
- Check if Java is installed
- Try to download and install Maven automatically
- Provide alternative setup instructions

### **Method 5: Manual JAR Build (After Installing Maven)**

```bash
# After installing Maven from https://maven.apache.org/
mvn clean package -DskipTests
java -jar target/electronic-voting-system-1.0.0.jar
```

## ⚙️ Setup Requirements

### 1. **Java Installation**
- **Required**: Java 17 or higher
- **Download**: https://adoptium.net/ (recommended)
- **Verify installation**: Open command prompt and run `java -version`

### 2. **Database Setup (MS SQL Server)**
- Install Microsoft SQL Server (free Express version available)
- Create database: `ElectronicVotingSystem`
- Update `src/main/resources/application.properties` with your database credentials

### 3. **Email Configuration**
- Configure SMTP settings in `application.properties`
- For testing, you can use a Gmail account with app password

## 🌐 Application URLs

Once running, access these URLs in your browser:

- **Landing Page**: http://localhost:8080/
- **Login Page**: http://localhost:8080/login
- **Admin Dashboard**: http://localhost:8080/admin/dashboard
- **Voter Dashboard**: http://localhost:8080/voter/dashboard

## 📝 Default Admin Account

To get started quickly, you can create an admin account:

1. **Register via API** (using Postman, curl, or similar):
   ```json
   POST http://localhost:8080/api/auth/create-admin
   {
     "email": "admin@company.com",
     "password": "Admin123!"
   }
   ```

2. **Or manually insert in database**:
   ```sql
   INSERT INTO users (email, password_hash, role, is_active, is_verified)
   VALUES ('admin@company.com', '$2a$10$hash', 'ADMIN', true, true);
   ```

## 🔧 Troubleshooting

### Java Not Found
- Install Java 17+ from https://adoptium.net/
- Add Java to system PATH
- Restart command prompt after installation

### Port 8080 Already in Use
- Stop other applications using port 8080
- Or modify `server.port=8081` in `application.properties`

### Database Connection Issues
- Verify SQL Server is running
- Check database credentials in `application.properties`
- Ensure TCP/IP is enabled for SQL Server

### Maven Issues
- Use IDE instead of command line
- Or install Maven from https://maven.apache.org/

## 📚 Testing the System

1. **Start the application** using any method above
2. **Access the landing page** at http://localhost:8080/
3. **Click "Admin"** to go to admin login
4. **Create your first admin account** using the API
5. **Login and start creating elections!**

---

**Need Help?** Check the full README.md for detailed documentation.