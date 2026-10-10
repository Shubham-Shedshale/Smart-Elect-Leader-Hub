# How to Check Your Database Tables

## 🔍 **Multiple Ways to Verify Table Creation**

Your Electronic Voting System should have created these 5 tables automatically:
- `users` - User accounts and authentication
- `elections` - Election management  
- `candidates` - Candidate information
- `votes` - Voting records
- `announcements` - System announcements

---

## **Method 1: MySQL Command Line** (Recommended)

If you have MySQL command line installed:

```bash
# Connect to MySQL
mysql -u root -p

# Select your database
USE electronic_voting_db;

# Show all tables
SHOW TABLES;

# Show structure of each table
DESCRIBE users;
DESCRIBE elections;
DESCRIBE candidates;
DESCRIBE votes;
DESCRIBE announcements;

# Show table creation details
SHOW CREATE TABLE users;
```

**Expected Result:**
```
+--------------------------+
| Tables_in_electronic_voting_db |
+--------------------------+
| announcements            |
| candidates               |
| elections                |
| users                    |
| votes                    |
+--------------------------+
```

---

## **Method 2: phpMyAdmin** (Visual Interface)

If you're using XAMPP:

1. **Open**: http://localhost/phpmyadmin
2. **Login**: username: `USERNAME`, password: `PASSWORD`
3. **Select Database**: Click on `electronic_voting_db`
4. **View Tables**: You'll see all 5 tables listed
5. **Check Structure**: Click on any table name to see its columns

---

## **Method 3: Application Logs** (What You Already Saw)

When your application started, you should have seen logs like:

```
Hibernate: create table users (
    id bigint not null auto_increment,
    email varchar(255) not null,
    first_name varchar(255),
    ...
    primary key (id)
) engine=InnoDB

Hibernate: create table elections (
    id bigint not null auto_increment,
    title varchar(255) not null,
    ...
    primary key (id)
) engine=InnoDB
```

**This confirms tables were created successfully!**

---

## **Method 4: Direct Database Connection Test**

Create a simple test file to verify connection:

```java
// TestDatabaseConnection.java
import java.sql.*;

public class TestDatabaseConnection {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/electronic_voting_db";
        String username = "USERNAME";
        String password = "PASSWORD";
        
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables("electronic_voting_db", null, "%", new String[]{"TABLE"});
            
            System.out.println("Database Tables:");
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("- " + tableName);
            }
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}
```

---

## **Method 5: Check via Application API**

Your application has health endpoints that might show database status:

```bash
# Check application health (if running)
curl http://localhost:8888/actuator/health

# Check database connection status
curl http://localhost:8888/actuator/health/db
```

---

## **Expected Table Details**

### **Users Table** (`users`):
- `id` (Primary Key, Auto-increment)
- `email` (Unique)
- `password_hash`
- `first_name`, `last_name`
- `role` (ADMIN, SUPER_ADMIN, VOTER, CANDIDATE)
- `is_active`, `is_verified`
- Timestamps: `created_at`, `updated_at`, `last_login`

### **Elections Table** (`elections`):
- `id` (Primary Key, Auto-increment)
- `title`, `description`
- `start_date`, `end_date`
- `status` (DRAFT, SCHEDULED, ACTIVE, COMPLETED, CANCELLED)
- `allow_multiple_candidates`, `max_votes_per_user`
- Timestamps and audit fields

### **Candidates Table** (`candidates`):
- `id` (Primary Key, Auto-increment)
- `full_name`, `party_name`, `position`
- `bio`, `manifesto`
- `email_address`, `contact_number`
- `status` (ACTIVE, WITHDRAWN, DISQUALIFIED, PENDING)
- `votes_count`
- Foreign key to `elections`

### **Votes Table** (`votes`):
- `id` (Primary Key, Auto-increment)
- `user_id` (Foreign key to users)
- `election_id` (Foreign key to elections)
- `candidate_id` (Foreign key to candidates)
- `voted_at` (timestamp)
- Audit fields (ip_address, session_id, user_agent)

### **Announcements Table** (`announcements`):
- `id` (Primary Key, Auto-increment)
- `title`, `content`
- `type` (ELECTION_SCHEDULE, CANDIDATE_ANNOUNCEMENT, etc.)
- `priority` (0-3 scale)
- `is_published`, `published_at`
- Foreign key to `elections`

---

## **Troubleshooting**

### **If No Tables Appear:**
1. **Check MySQL is running**: Ensure MySQL service is active
2. **Check credentials**: Verify username/password are correct
3. **Check database name**: Should be `electronic_voting_db`
4. **Check application logs**: Look for table creation messages
5. **Restart application**: Sometimes tables need full restart to create

### **If Connection Fails:**
1. **Verify MySQL service**: Check if MySQL is running
2. **Check port**: Default is 3306
3. **Check firewall**: Ensure MySQL port is not blocked
4. **Check credentials**: username: `USERNAME`, password: `PASSWORD`

---

## **Quick Verification Commands**

```bash
# One-liner to check tables (if mysql in PATH)
mysql -u root -paduadi47 -e "USE electronic_voting_db; SHOW TABLES;"

# Count records in each table
mysql -u root -paduadi47 -e "USE electronic_voting_db; SELECT 'users' as table_name, COUNT(*) as record_count FROM users UNION ALL SELECT 'elections', COUNT(*) FROM elections UNION ALL SELECT 'candidates', COUNT(*) FROM candidates UNION ALL SELECT 'votes', COUNT(*) FROM votes UNION ALL SELECT 'announcements', COUNT(*) FROM announcements;"
```

**Your tables should all be there and ready for use!** 🚀