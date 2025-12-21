# MySQL Database Setup Guide

## 🎯 Why MySQL is Better

**MySQL** is an excellent choice for your Electronic Voting System because:

- ✅ **Easy Installation**: Simple setup process
- ✅ **No TCP/IP Issues**: Works out of the box
- ✅ **Cross-Platform**: Works on Windows, macOS, Linux
- ✅ **Production Ready**: Industry-standard database
- ✅ **Free and Open Source**: No licensing costs
- ✅ **Excellent Documentation**: Extensive community support

---

## 🚀 Quick MySQL Setup

### Step 1: Download and Install MySQL

#### Option A: MySQL Installer (Windows - Recommended)
1. Download **MySQL Installer for Windows**: https://dev.mysql.com/downloads/installer/
2. Run the installer
3. Choose **"Developer Default"** setup type
4. During installation, set root password (remember this!)

#### Option B: MySQL Community Server
1. Download **MySQL Community Server**: https://dev.mysql.com/downloads/mysql/
2. Install with default settings
3. Set root password during setup

### Step 2: Verify MySQL Installation

Open **Command Prompt** or **MySQL Command Line Client** and run:

```sql
mysql --version
```

You should see the MySQL version information.

### Step 3: Create Database

Connect to MySQL and create the database:

```sql
# Connect to MySQL
mysql -u root -p

# Create database
CREATE DATABASE electronic_voting_db;

# Verify database creation
SHOW DATABASES;

# Exit MySQL
EXIT;
```

### Step 4: Update Application Configuration

Edit `src/main/resources/application.properties`:

```properties
# Update these lines with your MySQL credentials:
spring.datasource.username=root
spring.datasource.password=your_actual_password_here
```

**Important**: Replace `your_password_here` with the password you set during MySQL installation.

---

## 🛠️ Alternative: XAMPP (Easiest Option)

If you want the simplest setup, use **XAMPP** (includes MySQL + phpMyAdmin):

### Step 1: Install XAMPP
1. Download XAMPP: https://www.apachefriends.org/download.html
2. Install with default settings
3. Start **Apache** and **MySQL** services from XAMPP Control Panel

### Step 2: Access phpMyAdmin
1. Open browser: http://localhost/phpmyadmin
2. Default username: `root`
3. Default password: `(leave empty)`

### Step 3: Create Database in phpMyAdmin
1. Click **"New"** to create a new database
2. Database name: `electronic_voting_db`
3. Collation: `utf8mb4_general_ci`
4. Click **"Create"**

### Step 4: Update Application Configuration
```properties
spring.datasource.username=root
spring.datasource.password=
```

---

## 🔧 Advanced MySQL Configuration

### Connection Pool Settings (Already Configured)
```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
```

### Performance Optimizations
```sql
-- Create indexes for better performance (Hibernate will handle this automatically)
-- But you can also create them manually if needed:

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_elections_status ON elections(status);
CREATE INDEX idx_votes_election_id ON votes(election_id);
CREATE INDEX idx_votes_user_id ON votes(user_id);
```

---

## 🧪 Testing the Connection

### Test 1: Manual MySQL Connection
```bash
mysql -u root -p -h localhost electronic_voting_db
```

### Test 2: Application Connection
1. Update `application.properties` with correct credentials
2. Run the application: `mvn spring-boot:run`
3. Check logs for: `HikariPool-1 - Added connection`
4. Verify no database connection errors

### Test 3: Database Access
1. Application should create tables automatically
2. Check MySQL logs or phpMyAdmin to see created tables:
   - users
   - elections  
   - candidates
   - votes
   - announcements

---

## 🐛 Troubleshooting

### Issue: "Connection refused"
**Solution**: Ensure MySQL service is running
- Windows: Check MySQL service in Services
- XAMPP: Start MySQL in XAMPP Control Panel

### Issue: "Access denied for user 'root'"
**Solution**: Check username/password in `application.properties`
- Verify MySQL root password
- Try with empty password for XAMPP

### Issue: "Unknown database 'electronic_voting_db'"
**Solution**: Create the database first
```sql
CREATE DATABASE electronic_voting_db;
```

### Issue: "Table doesn't exist"
**Solution**: Check `spring.jpa.hibernate.ddl-auto=update`
- This should create tables automatically
- If not, check database user permissions

---

## 🔄 Switching Between Database Configurations

### Use MySQL (Current):
```bash
# Configuration already set in application.properties
mvn spring-boot:run
```

### Use H2 (For quick testing):
```bash
# Switch to H2 configuration
cp src/main/resources/application-h2.properties src/main/resources/application.properties
mvn spring-boot:run
```

### Use SQL Server Express:
```bash
# Switch to MSSQL configuration  
cp src/main/resources/application-mysql-backup.properties src/main/resources/application.properties
# Follow SQL Server setup guide
```

---

## 📊 Database Comparison

| Feature | MySQL | SQL Server Express | H2 |
|---------|-------|-------------------|-----|
| **Setup Complexity** | ⭐⭐ Easy | ⭐⭐⭐ Complex | ⭐⭐⭐⭐⭐ Instant |
| **TCP/IP Issues** | ✅ None | ❌ Common | ✅ None |
| **Production Ready** | ✅ Excellent | ✅ Good | ❌ No |
| **Cross-Platform** | ✅ Yes | ❌ Windows Only | ✅ Yes |
| **Performance** | ✅ Excellent | ✅ Good | ⭐⭐ Fair |
| **Community Support** | ✅ Extensive | ✅ Good | ✅ Good |

**Recommendation**: Use **MySQL** for development and production - it's the most reliable and easiest to set up!

---

## 🎉 You're Ready!

Once MySQL is installed and configured:

1. **Start your application**: `mvn spring-boot:run`
2. **Access the app**: http://localhost:8080
3. **Check database**: Use phpMyAdmin (http://localhost/phpmyadmin) or MySQL Workbench
4. **Verify tables**: All 5 tables should be created automatically

**MySQL is now your database solution - simple, reliable, and production-ready!** 🚀