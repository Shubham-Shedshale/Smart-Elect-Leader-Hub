import java.sql.*;

/**
 * Simple utility to check database tables and data
 */
public class SimpleDatabaseChecker {
    
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/electronic_voting_db";
        String username = "root";
        String password = "aduadi47";
        
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            System.out.println("Database Connection: SUCCESS");
            System.out.println();
            
            // Check users table data
            checkUsersData(conn);
            
        } catch (SQLException e) {
            System.out.println("Database Connection: FAILED");
            System.out.println("Error: " + e.getMessage());
            System.out.println();
            System.out.println("Troubleshooting:");
            System.out.println("1. Is MySQL running?");
            System.out.println("2. Are credentials correct? (root / aduadi47)");
            System.out.println("3. Does database 'electronic_voting_db' exist?");
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
                System.out.println("  No users found in database");
                System.out.println("  You can create a user by:");
                System.out.println("     - Using the application registration feature");
                System.out.println("     - Creating an admin via the API");
            }
        }
    }
}