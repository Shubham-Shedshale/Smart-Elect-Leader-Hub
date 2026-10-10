# Simple MySQL Setup Guide

## 🎯 **You're Good to Go!**

Your application is now configured for **MySQL** with the credentials:
- **Username**: `USERNAME`
- **Password**: `PASSWORD`
- **Database**: `electronic_voting_db`

## 🚀 **Quick Setup (2 Options)**

### **Option A: XAMPP (Easiest - 5 minutes)**

1. **Download XAMPP**: https://www.apachefriends.org/download.html
2. **Install** with default settings
3. **Start services**: 
   - Open XAMPP Control Panel
   - Click "Start" next to **Apache** and **MySQL**
4. **Create database**:
   - Go to: http://localhost/phpmyadmin
   - Click "New" → Database name: `electronic_voting_db` → Create
5. **Run application**:
   ```bash
   mvn spring-boot:run
   ```

### **Option B: Standalone MySQL (10 minutes)**

1. **Download MySQL**: https://dev.mysql.com/downloads/installer/
2. **Install** with "Developer Default" configuration
3. **Set root password**: Use `PASSWORD` (or remember what you set!)
4. **Create database**:
   ```bash
   mysql -u root -p
   CREATE DATABASE electronic_voting_db;
   EXIT;
   ```
5. **Run application**:
   ```bash
   mvn spring-boot:run
   ```

## ⚡ **Test MySQL Connection**

Before running the app, test if MySQL is working:

```bash
# Test connection (if MySQL is in PATH)
mysql -u root -p electronic_voting_db

# Or use phpMyAdmin (XAMPP): http://localhost/phpmyadmin
```

## 🎉 **Expected Success**

When MySQL is running and you start the application:
- ✅ `HikariPool-1 - Added connection` in logs
- ✅ No "Connection refused" errors  
- ✅ Tables created automatically via Hibernate
- ✅ Application starts on **http://localhost:8080**

## 🆘 **Common MySQL Issues**

| Issue | Solution |
|-------|----------|
| "Connection refused" | MySQL service not running (start XAMPP MySQL) |
| "Access denied" | Wrong password - use `PASSWORD` |
| "Unknown database" | Create `electronic_voting_db` in phpMyAdmin |
| "Port 3306 in use" | Stop other MySQL instances or change port |

## 📋 **Quick Commands**

### **XAMPP Users**:
```cmd
# Start MySQL
# (Use XAMPP Control Panel instead)

# Create database
# (Use phpMyAdmin: http://localhost/phpmyadmin)
```

### **Standalone MySQL**:
```cmd
# Check MySQL status
sc query MySQL

# Start MySQL
net start MySQL

# Test connection
mysql -u root -p -e "SELECT VERSION();"
```

## ✅ **Why MySQL is Better for You**

- ✅ **No complex configuration** (unlike SQL Server Express)
- ✅ **Works immediately** with XAMPP
- ✅ **No TCP/IP setup** required
- ✅ **Automatic table creation** via Hibernate
- ✅ **Simple authentication** (root + password)

**Your Electronic Voting System will work perfectly with MySQL!** 🚀