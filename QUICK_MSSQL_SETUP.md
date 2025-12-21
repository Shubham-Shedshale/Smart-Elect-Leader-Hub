# Quick MSSQL Server Setup Summary

## What's Already Configured ✅

### 1. Application Properties
Your `application.properties` is now configured for:
- **Server**: `localhost\SQLEXPRESS`
- **Database**: `ELECTRONIC_VS`
- **Authentication**: Windows Authentication (Trusted_Connection)
- **JDBC URL**: `jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=ELECTRONIC_VS;encrypt=false;trustServerCertificate=true`

### 2. Maven Dependencies
✅ MSSQL JDBC driver is already included in `pom.xml`

### 3. Hibernate Configuration
✅ SQL Server dialect and JPA settings are configured

## What You Need to Do

### 1. Install SQL Server Express
- Download from: https://www.microsoft.com/en-us/sql-server/sql-server-downloads
- Choose "Express" edition (free)

### 2. Enable TCP/IP Protocol
1. Open **SQL Server Configuration Manager**
2. Navigate to: **SQL Server Network Configuration** > **Protocols for SQLEXPRESS**
3. Enable **TCP/IP** protocol
4. Restart **SQL Server (SQLEXPRESS)** service

### 3. Create Database
Open **SQL Server Management Studio (SSMS)** and run:
```sql
CREATE DATABASE ELECTRONIC_VS;
GO
```

### 4. Test Connection
The application will automatically:
- Connect using Windows Authentication
- Create tables via Hibernate
- Start on `http://localhost:8080`

## Connection String Details

Your current configuration uses:
```
jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=ELECTRONIC_VS;encrypt=false;trustServerCertificate=true
```

This means:
- **Server**: `localhost\SQLEXPRESS` (SQL Server Express instance)
- **Database**: `ELECTRONIC_VS`
- **Authentication**: Windows Authentication (no username/password needed)
- **Encryption**: Disabled for development
- **Certificate**: Trusted for development

## Common Issues & Solutions

### Issue: "Connection refused"
- **Solution**: Ensure SQL Server Express service is running
- **Check**: Windows Services > SQL Server (SQLEXPRESS)

### Issue: "Login failed"
- **Solution**: Use Windows Authentication (already configured)
- **Alternative**: Switch to SQL Server Authentication if needed

### Issue: "Database does not exist"
- **Solution**: Create database manually using SSMS
- **Command**: `CREATE DATABASE ELECTRONIC_VS;`

## Next Steps

1. **Install SQL Server Express** (if not already installed)
2. **Enable TCP/IP** protocol in SQL Server Configuration Manager
3. **Create database** `ELECTRONIC_VS` in SSMS
4. **Run application**: `mvn spring-boot:run`
5. **Access**: http://localhost:8080

The application should now start successfully and connect to your MSSQL database using Windows Authentication!