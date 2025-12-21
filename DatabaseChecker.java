import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 * Simple utility to check database tables and data
 * Run this from your project directory: javac DatabaseChecker.java && java DatabaseChecker
 */
public class DatabaseChecker {
    
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/electronic_voting_db";
        String username = "root";
        String password = "aduadi47";
        
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            System.out.println("✅ Database Connection: SUCCESS");
            System.out.println();
            
            // Check tables
            checkTables(conn);
            System.out.println();
            
            // Check users table data
            checkUsersData(conn);
            
        } catch (SQLException e) {
            System.out.println("ERROR: Database Connection FAILED");
            System.out.println("Error: " + e.getMessage());
            System.out.println();
            System.out.println("Troubleshooting:");
            System.out.println("1. Is MySQL running?");
            System.out.println("2. Are credentials correct? (root / aduadi47)");
            System.out.println("3. Does database 'electronic_voting_db' exist?");
        }
    }
    
    private static void checkTables(Connection conn) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet tables = metaData.getTables("electronic_voting_db", null, "%", new String[]{"TABLE"});

        System.out.println("Database Tables:");
        int count = 0;
        while (tables.next()) {
            String tableName = tables.getString("TABLE_NAME");
            System.out.println("  - " + tableName);
            count++;
        }
        System.out.println("Total tables: " + count);

        if (count == 0) {
            System.out.println("ERROR: No tables found - check database connection");
        }
    }
    
    private static void checkUsersData(Connection conn) throws SQLException {
        System.out.println("Users Table Data:");

        // Count users
        String countQuery = "SELECT COUNT(*) as total FROM users";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countQuery)) {
            if (rs.next()) {
                int total = rs.getInt("total");
                System.out.println("  Total users: " + total);
            }
        }

        // Show sample users
        String selectQuery = "SELECT id, email, first_name, last_name, role, is_active FROM users LIMIT 10";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectQuery)) {

            System.out.println("  User Records:");
            boolean foundAny = false;
            while (rs.next()) {
                foundAny = true;
                int id = rs.getInt("id");
                String email = rs.getString("email");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String role = rs.getString("role");
                boolean isActive = rs.getBoolean("is_active");

                System.out.println("    ID: " + id +
                                 " | Email: " + email +
                                 " | Name: " + (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "") +
                                 " | Role: " + role +
                                 " | Active: " + isActive);
            }

            if (!foundAny) {
                System.out.println("  INFO: No users found in database");
                System.out.println("  TIP: You can create a user by:");
                System.out.println("     - Using the application registration feature");
                System.out.println("     - Creating an admin via the API");
            }
        }

        // Check elections data
        checkElectionsData(conn);
    }

    private static void checkElectionsData(Connection conn) throws SQLException {
        System.out.println("\nElections Table Data:");

        // Count elections
        String countQuery = "SELECT COUNT(*) as total FROM elections";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countQuery)) {
            if (rs.next()) {
                int total = rs.getInt("total");
                System.out.println("  Total elections: " + total);
            }
        }

        // Show all elections with current time comparison
        String selectQuery = "SELECT id, title, status, start_date, end_date, NOW() as current_time FROM elections";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectQuery)) {

            System.out.println("  Election Records:");
            boolean foundAny = false;
            while (rs.next()) {
                foundAny = true;
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String status = rs.getString("status");
                Timestamp startDate = rs.getTimestamp("start_date");
                Timestamp endDate = rs.getTimestamp("end_date");
                Timestamp currentTime = rs.getTimestamp("current_time");

                boolean isActiveTime = startDate != null && endDate != null &&
                                      startDate.before(currentTime) && endDate.after(currentTime);
                boolean isActiveStatus = "ACTIVE".equals(status);

                System.out.println("    ID: " + id +
                                 " | Title: " + title +
                                 " | Status: " + status +
                                 " | Start: " + startDate +
                                 " | End: " + endDate +
                                 " | Current: " + currentTime +
                                 " | Time Active: " + isActiveTime +
                                 " | Should Show: " + (isActiveTime && isActiveStatus));
            }

            if (!foundAny) {
                System.out.println("  INFO: No elections found in database");
                System.out.println("  TIP: You can create elections via the admin panel");
            }
        }
    }
}