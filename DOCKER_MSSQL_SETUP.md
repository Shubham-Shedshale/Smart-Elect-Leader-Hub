# Docker SQL Server Setup (Quick Alternative)

## 🚀 **Super Quick Setup - 5 Minutes**

### **Prerequisite: Install Docker Desktop**
1. **Download**: https://www.docker.com/products/docker-desktop/
2. **Install** with default settings
3. **Start Docker Desktop**

### **Step 1: Start SQL Server Container**
```bash
# Run this command in Command Prompt or PowerShell:
docker run -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=aduadi47" -p 1433:1433 --name sqlserver -d mcr.microsoft.com/mssql/server:2022-latest
```

### **Step 2: Update Application Configuration**
Your current configuration will work perfectly with Docker:
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=ELECTRONIC_VS;encrypt=false;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=aduadi47
```

**Update your `application.properties`**:
```bash
# Update username to 'sa' for Docker container
# Update connection URL to exclude instance name
```

### **Step 3: Create Database**
```bash
# Connect to SQL Server and create database
docker exec -it sqlserver /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P aduadi47 -Q "CREATE DATABASE ELECTRONIC_VS;"
```

### **Step 4: Test Connection & Run Application**
```bash
# Start application
mvn spring-boot:run
```

## 🎯 **Why Docker SQL Server?**

✅ **Installation Time**: 5 minutes vs 30+ minutes  
✅ **No Windows Service Conflicts**: Runs in isolated container  
✅ **Easy Cleanup**: `docker stop sqlserver && docker rm sqlserver`  
✅ **Consistent Environment**: Same setup everywhere  
✅ **No TCP/IP Configuration**: Works out of the box  

## 📋 **Docker Commands Reference**

```bash
# Check if SQL Server is running
docker ps

# View logs
docker logs sqlserver

# Stop SQL Server
docker stop sqlserver

# Remove SQL Server (if needed)
docker rm -f sqlserver

# Restart SQL Server
docker start sqlserver

# Connect to SQL Server
docker exec -it sqlserver /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P aduadi47
```

## ✅ **Expected Success with Docker**

1. **Container starts successfully**:
   ```
   docker ps
   # Should show sqlserver running
   ```

2. **Application connects**:
   ```
   HikariPool-1 - Added connection
   ```

3. **Tables created automatically**:
   ```
   Hibernate: create table users ...
   ```

## 🆘 **If Docker Command Fails**

**Port already in use**:
```bash
# Check what's using port 1433
netstat -ano | findstr :1433

# Kill existing process or use different port
docker run -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=aduadi47" -p 1434:1433 --name sqlserver -d mcr.microsoft.com/mssql/server:2022-latest
# Then update application.properties to use port 1434
```

**Docker not running**:
- Start Docker Desktop application
- Wait for "Docker is running" status

## 🎉 **Both Options Work**

**Choose your preferred method**:
- **SQL Server Express**: Full local installation (30 minutes)
- **Docker SQL Server**: Containerized setup (5 minutes)

**Both will give you the same result**: A fully functional Electronic Voting System with MSSQL! 🚀