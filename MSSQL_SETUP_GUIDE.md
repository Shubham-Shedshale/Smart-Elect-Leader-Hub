# MSSQL Server Setup Guide for Electronic Voting System

## Prerequisites

### 1. Install SQL Server
You need to have Microsoft SQL Server installed. Choose one of these options:

#### Option A: SQL Server Express (Free)
- Download SQL Server Express from Microsoft's website
- Install with basic configuration
- Enable TCP/IP protocol in SQL Server Configuration Manager

#### Option B: SQL Server Developer Edition
- Free full-featured edition for development
- Download from Microsoft Developer website

#### Option C: Docker SQL Server
```bash
docker run -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=YourStrongPassword123!" -p 1433:1433 --name sqlserver -d mcr.microsoft.com/mssql/server:2022-latest
```

### 2. SQL Server Configuration Requirements

#### Enable TCP/IP Protocol
1. Open SQL Server Configuration Manager
2. Navigate to SQL Server Network Configuration > Protocols for SQLEXPRESS
3. Enable TCP/IP protocol
4. Set TCP Port to 1433 (if not already set)
5. Restart SQL Server service

#### Authentication Mode
1. Right-click on SQL Server instance in SSMS
2. Select Properties > Security
3. Enable "SQL Server and Windows Authentication mode"
4. Restart SQL Server service

### 3. Database Setup

#### Create Database
Run this SQL command in SQL Server Management Studio (SSMS):

```sql
CREATE DATABASE ElectronicVotingSystem;
GO

-- Create login (optional - will be created automatically if not exists)
-- IF NOT EXISTS (SELECT * FROM sys.server_principals WHERE name = 'sa')
-- BEGIN
--     CREATE LOGIN sa WITH PASSWORD = 'YourStrongPassword123!';
-- END
-- GO

USE ElectronicVotingSystem;
GO
```

#### Create User (if needed)
```sql
USE ElectronicVotingSystem;
GO

CREATE USER sa FOR LOGIN sa;
ALTER ROLE db_owner ADD MEMBER sa;
GO
```

## Application Configuration

### 1. application.properties Configuration

Update your `src/main/resources/application.properties` file:

```properties
# Microsoft SQL Server Configuration
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=ElectronicVotingSystem;encrypt=false;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YourStrongPassword123!
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# JPA/Hibernate Configuration
spring.jpa.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.use_sql_comments=true

# SMTP Configuration for Email
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true

# Application Configuration
app.base-url=http://localhost:8080
app.security.jwt-secret=mySecretKey
app.security.jwt-expiration=86400000

# Logging Configuration
logging.level.com.evoting=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Server Configuration
server.port=8080
server.servlet.context-path=/

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# H2 Console Configuration (Disabled for production)
# spring.h2.console.enabled=false

# Thymeleaf Configuration
spring.thymeleaf.cache=false
spring.thymeleaf.mode=HTML
spring.thymeleaf.encoding=UTF-8

# Management/Actuator Configuration
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always
management.endpoint.health.enabled=true
```

### 2. Maven Dependencies (Already configured)

Your `pom.xml` already includes the required MSSQL dependency:

```xml
<!-- Microsoft SQL Server -->
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <scope>runtime</scope>
</dependency>
```

## Connection Testing

### 1. Test Database Connection
You can test the connection using this Java code snippet:

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MSSQLConnectionTest {
    public static void main(String[] args) {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=ElectronicVotingSystem;encrypt=false;trustServerCertificate=true";
        String username = "sa";
        String password = "YourStrongPassword123!";
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Connected to Microsoft SQL Server successfully!");
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}
```

### 2. Common Connection Issues

#### Issue: "Connection refused"
- **Solution**: Ensure SQL Server is running and TCP/IP is enabled
- **Check**: Windows Services > SQL Server (SQLEXPRESS) is running

#### Issue: "Login failed"
- **Solution**: Verify username/password and authentication mode
- **Check**: SQL Server is in "SQL Server and Windows Authentication mode"

#### Issue: "Network-related error"
- **Solution**: Check firewall settings and TCP/IP configuration
- **Check**: Port 1433 is open and SQL Server is listening

#### Issue: "Database does not exist"
- **Solution**: Create the database manually or let Hibernate create it
- **Check**: Database name in connection string matches created database

## Security Considerations

### 1. Password Security
- Change default password `YourStrongPassword123!` to something more secure
- Use environment variables for sensitive data
- Consider using Docker secrets or Azure Key Vault in production

### 2. SSL Configuration
For production environments, enable SSL:
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=ElectronicVotingSystem;encrypt=true;trustServerCertificate=false
```

### 3. Firewall Rules
Ensure SQL Server port 1433 is open in Windows Firewall:
```bash
netsh advfirewall firewall add rule name="SQL Server" dir=in action=allow protocol=TCP localport=1433
```

## Performance Optimization

### 1. Connection Pooling
The application uses HikariCP by default. Configuration can be adjusted:

```properties
# HikariCP Configuration
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
```

### 2. Query Optimization
- Monitor slow queries with Spring Boot Actuator
- Add appropriate database indexes
- Use database connection monitoring tools

## Migration from H2 to MSSQL

To switch back from H2 to MSSQL:

1. **Update application.properties** with MSSQL configuration (as shown above)
2. **Ensure MSSQL Server is running** and accessible
3. **Create the database** manually if needed
4. **Run the application** - Hibernate will automatically create tables

## Troubleshooting Commands

### Check SQL Server Status
```cmd
sc query "MSSQL$SQLEXPRESS"
```

### Test Port Connectivity
```cmd
telnet localhost 1433
```

### Check SQL Server Logs
- Location: `C:\Program Files\Microsoft SQL Server\MSSQL15.SQLEXPRESS\MSSQL\Log\ERRORLOG`

## Next Steps

After MSSQL is configured:
1. Start the application: `mvn spring-boot:run`
2. Access the application at: http://localhost:8080
3. Create an admin user via the registration endpoint
4. Test the voting system functionality

## Additional Resources

- [Microsoft SQL Server Documentation](https://docs.microsoft.com/en-us/sql/)
- [Spring Boot Data JPA with SQL Server](https://docs.spring.io/spring-boot/docs/current/reference/html/data.html#data.sql.jpa)
- [MSSQL JDBC Driver Documentation](https://docs.microsoft.com/en-us/sql/connect/jdbc/)