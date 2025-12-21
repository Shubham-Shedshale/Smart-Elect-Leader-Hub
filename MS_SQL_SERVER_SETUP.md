# Microsoft SQL Server Setup Guide

## 🎯 Why MS SQL Server for Electronic Voting System

**Microsoft SQL Server** is an excellent choice for your Electronic Voting System because:

- ✅ **Enterprise-Grade**: Robust, scalable, and secure
- ✅ **High Performance**: Optimized for complex queries and large datasets
- ✅ **Advanced Security**: Built-in encryption, authentication, and authorization
- ✅ **ACID Compliance**: Ensures data integrity and consistency
- ✅ **Backup & Recovery**: Comprehensive backup and disaster recovery options
- ✅ **Monitoring**: Advanced monitoring and performance tuning tools
- ✅ **Free Option**: SQL Server Express is free for development and small deployments

---

## 🚀 Quick MS SQL Server Setup

### Step 1: Download and Install MS SQL Server Express

#### Option A: SQL Server Express (Recommended - Free)
1. Download **SQL Server Express**: https://www.microsoft.com/en-us/sql-server/sql-server-downloads
2. Choose "Download SQL Server Express"
3. Run the installer with default settings
4. Set **sa** password (remember this!)

#### Option B: SQL Server Developer Edition (Free for Development)
1. Download **SQL Server Developer**: https://www.microsoft.com/en-us/sql-server/sql-server-downloads
2. Choose "Download SQL Server Developer"
3. Install with "Developer" edition
4. Set **sa** password

### Step 2: Enable TCP/IP Protocol

1. **Open SQL Server Configuration Manager**
   - Search for "SQL Server Configuration Manager" in Start Menu

2. **Enable TCP/IP**
   - Expand "SQL Server Network Configuration"
   - Click "Protocols for SQLEXPRESS" (or your instance name)
   - Right-click "TCP/IP" → Properties
   - Set "Enabled" to "Yes"
   - Click "IP Addresses" tab
   - Scroll to bottom, find "IPAll"
   - Set "TCP Port" to **1433**
   - Click "OK"

3. **Restart SQL Server Service**
   - Go to "SQL Server Services"
   - Right-click "SQL Server (SQLEXPRESS)" → Restart

### Step 3: Create Database and Run Migration

#### Option A: Using SQL Server Management Studio (SSMS)
1. Download SSMS: https://learn.microsoft.com/sql/ssms/download-sql-server-management-studio-ssms
2. Connect to your SQL Server instance
3. Open and run `mssql-database-migration.sql` script
4. This creates all tables, constraints, indexes, and sample data

#### Option B: Using sqlcmd Command Line
```bash
# Connect and run migration script
sqlcmd -S localhost -U sa -P YourStrong@Passw0rd -i mssql-database-migration.sql
```

### Step 4: Add MS SQL JDBC Driver

#### Option A: Add to Maven (Recommended)
Add this dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <version>12.4.2.jre11</version>
    <scope>runtime</scope>
</dependency>
```

#### Option B: Download JDBC Driver
1. Download: https://learn.microsoft.com/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server
2. Extract and add `mssql-jdbc-*.jar` to your project's classpath

### Step 5: Update Application Configuration

Edit `src/main/resources/application-mssql.properties`:

```properties
# Update these lines with your SQL Server credentials:
spring.datasource.username=sa
spring.datasource.password=YourActualPassword
```

### Step 6: Switch to MS SQL Configuration

```bash
# Copy MS SQL configuration to main application.properties
copy "src\main\resources\application-mssql.properties" "src\main\resources\application.properties"
```

### Step 7: Test the Connection

```bash
# Test MS SQL connection
sqlcmd -S localhost -U sa -P YourActualPassword -Q "SELECT @@VERSION"

# Start the application
mvn spring-boot:run
```

---

## 🔧 MS SQL Server Configuration Details

### Application Properties Breakdown

```properties
# Database Connection
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=ElectronicVotingDB;encrypt=false;trustServerCertificate=true

# Security Settings
spring.datasource.username=sa
spring.datasource.password=YourStrong@Passw0rd
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# Connection Pool (Optimized for MS SQL)
spring.datasource.hikari.maximum-pool-size=15
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000

# JPA/Hibernate Configuration
spring.jpa.hibernate.dialect=org.hibernate.dialect.SQLServer2017Dialect
spring.jpa.hibernate.ddl-auto=update

# Performance Optimizations
spring.jpa.properties.hibernate.jdbc.batch_size=25
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
spring.jpa.properties.hibernate.jdbc.fetch_size=1000
```

### Database Schema Features

#### Tables Created:
- **users**: User management with roles (ADMIN, SUPER_ADMIN, VOTER, CANDIDATE)
- **elections**: Election management with status tracking
- **candidates**: Candidate information with election relationships
- **votes**: Secure voting records with audit trail
- **announcements**: System announcements with priority levels

#### Key Features:
- **Identity Columns**: Auto-incrementing primary keys (BIGINT IDENTITY)
- **Foreign Key Constraints**: Referential integrity between tables
- **Check Constraints**: Data validation for roles and statuses
- **Indexes**: Optimized for common query patterns
- **Stored Procedures**: Pre-built queries for common operations

#### Stored Procedures Included:
- `GetElectionResults`: Get election results with vote counts
- `HasUserVoted`: Check if user has voted in an election
- `GetUserVotingHistory`: Get user's voting history

---

## 🧪 Testing MS SQL Server Setup

### Test 1: Database Connection
```bash
sqlcmd -S localhost -U sa -P YourStrong@Passw0rd -Q "SELECT name FROM sys.databases WHERE name = 'ElectronicVotingDB'"
```

### Test 2: Table Verification
```bash
sqlcmd -S localhost -U sa -P YourStrong@Passw0rd -Q "USE ElectronicVotingDB; SELECT COUNT(*) as table_count FROM sys.tables"
```

### Test 3: Sample Data
```bash
sqlcmd -S localhost -U sa -P YourStrong@Passw0rd -Q "USE ElectronicVotingDB; SELECT * FROM users"
```

### Test 4: Application Startup
```bash
mvn spring-boot:run
```

Expected success indicators:
- `HikariPool-1 - Added connection` in logs
- No database connection errors
- Tables created automatically (if using `ddl-auto=update`)

---

## 🐛 Troubleshooting Common Issues

### Issue 1: "Connection Timeout"
**Solution**: Ensure TCP/IP is enabled and SQL Server is running
```bash
# Check SQL Server status
sc query MSSQL$SQLEXPRESS

# Start SQL Server if stopped
net start MSSQL$SQLEXPRESS
```

### Issue 2: "Login failed for user 'sa'"
**Solution**: Check username/password in application.properties
- Default username: `sa`
- Update password to match your SQL Server setup

### Issue 3: "Database 'ElectronicVotingDB' does not exist"
**Solution**: Run the migration script
```bash
sqlcmd -S localhost -U sa -P YourStrong@Passw0rd -i mssql-database-migration.sql
```

### Issue 4: "The driver is not configured for integrated security"
**Solution**: Use SQL Server authentication, not Windows authentication
- Set `integratedSecurity=false` in connection string
- Use SQL Server username/password

### Issue 5: "Port 1433 is already in use"
**Solution**: Check for existing SQL Server instances
```bash
# Check what's using port 1433
netstat -ano | findstr :1433

# Configure SQL Server to use different port if needed
```

---

## 🔄 Switching Between Database Configurations

### Use MS SQL Server (Current):
```bash
copy "src\main\resources\application-mssql.properties" "src\main\resources\application.properties"
mvn spring-boot:run
```

### Use MySQL:
```bash
copy "src\main\resources\application-mysql.properties" "src\main\resources\application.properties"
mvn spring-boot:run
```

### Use H2 (Testing):
```bash
copy "src\main\resources\application-h2.properties" "src\main\resources\application.properties"
mvn spring-boot:run
```

---

## 📊 Database Comparison

| Feature | MS SQL Server | MySQL | H2 |
|---------|---------------|-------|-----|
| **Setup Complexity** | ⭐⭐⭐ Medium | ⭐⭐ Easy | ⭐⭐⭐⭐⭐ Instant |
| **Performance** | ⭐⭐⭐⭐⭐ Excellent | ⭐⭐⭐⭐ Excellent | ⭐⭐ Fair |
| **Enterprise Features** | ✅ Extensive | ⚠️ Limited | ❌ None |
| **Security** | ✅ Advanced | ✅ Good | ⚠️ Basic |
| **Backup/Recovery** | ✅ Comprehensive | ✅ Good | ❌ None |
| **Monitoring** | ✅ Advanced | ✅ Good | ❌ None |
| **Cost** | 💰 Free (Express) | 💰 Free | 💰 Free |
| **Scalability** | ✅ Excellent | ✅ Very Good | ❌ Limited |

**Recommendation**: Use **MS SQL Server** for production deployments requiring enterprise features, advanced security, and comprehensive monitoring!

---

## 🎉 You're Ready!

Once MS SQL Server is configured:

1. **Start your application**: `mvn spring-boot:run`
2. **Access the app**: http://localhost:8888
3. **Verify database**: Check tables in SQL Server Management Studio
4. **Test features**: All Electronic Voting System features should work perfectly

**MS SQL Server provides enterprise-grade reliability and performance for your Electronic Voting System!** 🚀

---

## 📋 Quick Reference Commands

```bash
# Check SQL Server status
sc query MSSQL$SQLEXPRESS

# Start/Stop SQL Server
net start MSSQL$SQLEXPRESS
net stop MSSQL$SQLEXPRESS

# Test connection
sqlcmd -S localhost -U sa -P YourStrong@Passw0rd -Q "SELECT @@VERSION"

# Run migration script
sqlcmd -S localhost -U sa -P YourStrong@Passw0rd -i mssql-database-migration.sql

# Check database tables
sqlcmd -S localhost -U sa -P YourStrong@Passw0rd -Q "USE ElectronicVotingDB; SELECT name FROM sys.tables"

# View application logs
mvn spring-boot:run
