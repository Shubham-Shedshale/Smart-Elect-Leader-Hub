# Database Connection Fix Guide

## 🔧 Problem Analysis

Your Spring Boot application is failing to start because it cannot connect to the MySQL database `electronic_voting_db`. The error shows:

```
java.sql.SQLSyntaxErrorException: Unknown database 'electronic_voting_db'
```

This means the database doesn't exist yet, or there are connection issues.

## 🚀 Quick Fix Solutions

### Option 1: Use H2 Database (Fastest Solution - 2 minutes)

If you want to get the application running immediately for testing:

1. **Switch to H2 Database:**
   ```bash
   copy "src\main\resources\application-h2.properties" "src\main\resources\application.properties"
   ```

2. **Start the application:**
   ```bash
   mvn spring-boot:run
   ```

3. **Access H2 Console:** http://localhost:8080/h2-console
   - JDBC URL: `jdbc:h2:mem:testdb`
   - Username: `sa`
   - Password: (leave empty)

### Option 2: Set up MySQL (Recommended for Production)

#### Step 1: Install MySQL

**Option A: XAMPP (Easiest)**
1. Download XAMPP: https://www.apachefriends.org/download.html
2. Install and start Apache + MySQL services
3. Access phpMyAdmin: http://localhost/phpmyadmin

**Option B: Standalone MySQL**
1. Download MySQL Installer: https://dev.mysql.com/downloads/installer/
2. Install with "Developer Default" configuration
3. Remember your root password

#### Step 2: Create Database

**Using phpMyAdmin (XAMPP):**
1. Go to http://localhost/phpmyadmin
2. Click "New" → Database name: `electronic_voting_db` → Create

**Using MySQL Command Line:**
```bash
mysql -u root -p
```

```sql
CREATE DATABASE electronic_voting_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE electronic_voting_db;
EXIT;
```

#### Step 3: Update Configuration

Edit `src/main/resources/application.properties`:

```properties
# For XAMPP (default settings):
spring.datasource.username=root
spring.datasource.password=

# For standalone MySQL:
spring.datasource.username=root
spring.datasource.password=your_mysql_password_here
```

#### Step 4: Test Connection

```bash
# Test MySQL connection
mysql -u root -p -h localhost electronic_voting_db

# Start application
mvn spring-boot:run
```

### Option 3: Use the Setup Script

Run the new database setup script:

```bash
database-setup.bat
```

This will guide you through the setup process step by step.

## 🔍 Detailed Troubleshooting

### Common Issues and Solutions

#### Issue 1: "Unknown database 'electronic_voting_db'"
**Solution:** Create the database first
```sql
CREATE DATABASE electronic_voting_db;
```

#### Issue 2: "Access denied for user 'root'"
**Solutions:**
- For XAMPP: Use empty password
- For MySQL: Use correct root password
- Check `application.properties` credentials

#### Issue 3: "Connection refused" or "Host not found"
**Solutions:**
- Ensure MySQL service is running
- Check if port 3306 is available
- Verify MySQL is configured for TCP/IP connections

#### Issue 4: "Table doesn't exist" after database creation
**Solution:** Check `spring.jpa.hibernate.ddl-auto=update` in `application.properties`

### MySQL Service Status Checks

**Windows:**
```cmd
# Check if MySQL service is running
sc query mysql

# Start MySQL service
net start mysql
```

**XAMPP:**
1. Open XAMPP Control Panel
2. Ensure MySQL status shows "Running"
3. If not, click "Start" button

### Network and Port Checks

```bash
# Check if MySQL port is listening
netstat -an | findstr :3306

# Test connection to MySQL
telnet localhost 3306
```

## 📋 Pre-flight Checklist

Before starting the application, ensure:

- [ ] MySQL/XAMPP is installed and running
- [ ] Database `electronic_voting_db` exists
- [ ] Username and password are correct in `application.properties`
- [ ] Port 3306 is available and not blocked by firewall
- [ ] `spring.jpa.hibernate.ddl-auto=update` is set

## 🧪 Verification Steps

1. **Test Database Connection:**
   ```bash
   mysql -u root -p electronic_voting_db
   ```

2. **Check Application Compilation:**
   ```bash
   mvn compile
   ```

3. **Start Application:**
   ```bash
   mvn spring-boot:run
   ```

4. **Verify Success Indicators:**
   - Look for: `HikariPool-1 - Added connection`
   - No database connection errors
   - Tables created automatically
   - Application starts successfully

## 🔄 Switching Between Databases

### MySQL Configuration:
```bash
copy "src\main\resources\application-mysql.properties" "src\main\resources\application.properties"
```

### H2 Configuration:
```bash
copy "src\main\resources\application-h2.properties" "src\main\resources\application.properties"
```

### SQL Server Configuration:
```bash
copy "src\main\resources\application-mysql-backup.properties" "src\main\resources\application.properties"
```

## 📞 Getting Help

If you're still having issues:

1. **Check the logs** for specific error messages
2. **Verify MySQL installation** with `mysql --version`
3. **Test basic connectivity** with MySQL client tools
4. **Use H2 database** as a fallback for immediate testing

## 🎯 Next Steps

Once the database is working:

1. **Create test data** through the application interface
2. **Test all features** (voting, admin functions, etc.)
3. **Consider production deployment** with proper security
4. **Set up monitoring** and backup procedures

---

