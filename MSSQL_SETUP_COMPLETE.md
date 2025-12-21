# Complete SQL Server Express Setup Guide

## 🎯 **Current Status**
✅ Application configuration is correct
✅ Database credentials: db_admin / aduadi47  
❌ SQL Server Express is not running

## 🚀 **Quick Setup Steps**

### **Step 1: Install SQL Server Express**
1. **Download SQL Server Express**: https://www.microsoft.com/en-us/sql-server/sql-server-downloads
2. **Choose**: "Download SQL Server Express" 
3. **Install with default settings**
4. **Set SA password**: Use `aduadi47` (or remember your choice)

### **Step 2: Enable TCP/IP Protocol**
1. **Open SQL Server Configuration Manager**
   - Search "SQL Server Configuration Manager" in Start Menu
2. **Navigate to**: SQL Server Network Configuration > Protocols for SQLEXPRESS
3. **Enable TCP/IP**:
   - Right-click "TCP/IP" → Properties
   - Set "Enabled" to "Yes"
   - Click "IP Addresses" tab
   - Scroll to bottom, find "IPAll"
   - Set "TCP Port" to **1433**
   - Click "OK"

### **Step 3: Restart SQL Server Service**
1. **Go to**: SQL Server Services
2. **Right-click**: "SQL Server (SQLEXPRESS)" → Restart
3. **Verify it's running** (Status should be "Running")

### **Step 4: Create Database (Optional)**
Open **SQL Server Management Studio (SSMS)** and run:
```sql
CREATE DATABASE ELECTRONIC_VS;
GO
```
*Note: Hibernate can also create this automatically if it doesn't exist.*

### **Step 5: Test Connection**
```bash
# Test SQL Server connection
sqlcmd -S localhost -U db_admin -P aduadi47 -Q "SELECT @@VERSION"

# Start the application
mvn spring-boot:run
```

## 🔧 **Current Application Configuration**

Your `application.properties` is configured correctly:
```properties
# Database Connection (✅ Correct)
spring.datasource.url=jdbc:sqlserver://localhost\\SQLEXPRESS:1433;databaseName=ELECTRONIC_VS;encrypt=false;trustServerCertificate=true
spring.datasource.username=db_admin
spring.datasource.password=aduadi47

# Hibernate Configuration (✅ Correct)
spring.jpa.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
spring.jpa.hibernate.ddl-auto=update

# Server Configuration
server.port=8888
```

## ⚡ **Quick Commands**

### **Check SQL Server Status**
```cmd
sc query MSSQL$SQLEXPRESS
```

### **Start SQL Server (if stopped)**
```cmd
net start MSSQL$SQLEXPRESS
```

### **Test Port 1433**
```cmd
telnet localhost 1433
```

### **Run Application**
```cmd
mvn spring-boot:run
```

## 🎉 **Expected Success Indicators**

When SQL Server is running and you start the application:
- ✅ `HikariPool-1 - Added connection` in logs
- ✅ No "Connection refused" errors
- ✅ Tables created automatically via Hibernate
- ✅ Application starts on http://localhost:8888

## 🆘 **If You Get "Login Failed" Error**

If you see: "Login failed for user 'db_admin'"

**Solution**: Create the user in SQL Server:
```sql
USE master;
GO
CREATE LOGIN db_admin WITH PASSWORD = 'aduadi47';
GO
CREATE USER db_admin FOR LOGIN db_admin;
ALTER ROLE db_owner ADD MEMBER db_admin;
GO
```

## 🚨 **Common Issues & Solutions**

| Issue | Solution |
|-------|----------|
| "Connection refused" | SQL Server not running - start service |
| "Login failed" | Wrong username/password or user doesn't exist |
| "Port 1433 not responding" | TCP/IP not enabled in SQL Server Configuration Manager |
| "Database doesn't exist" | Create manually or let Hibernate create it |

## 📋 **Next Steps**

1. **Install SQL Server Express** (if needed)
2. **Enable TCP/IP protocol** in Configuration Manager
3. **Restart SQL Server service**
4. **Create user 'db_admin'** (if needed)
5. **Run**: `mvn spring-boot:run`
6. **Access**: http://localhost:8888

**Your Electronic Voting System will work perfectly once SQL Server Express is running!** 🚀