import java.sql.*;
import java.security.MessageDigest;

/**
 * Simple program to create an admin user in the Electronic Voting System database
 * Uses simple SHA-256 hashing for the password
 */
public class SimpleCreateAdmin {
    
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/electronic_voting_db";
        String username = "root";
        String password = "aduadi47";
        
        // Admin user details
        String adminEmail = "admin@evoting.local";
        String adminPassword = "Admin123!";
        String firstName = "System";
        String lastName = "Administrator";
        
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            System.out.println("✅ Database Connection: SUCCESS");
            
            // Check if admin already exists
            String checkQuery = "SELECT COUNT(*) FROM users WHERE email = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, adminEmail);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("ℹ️ Admin user already exists: " + adminEmail);
                    showUserInfo(conn, adminEmail);
                    return;
                }
            }
            
            // Create admin user
            System.out.println("🔧 Creating admin user...");
            
            // Hash the password using SHA-256 (simple approach)
            String hashedPassword = hashPassword(adminPassword);
            
            String insertQuery = "INSERT INTO users (email, password_hash, first_name, last_name, role, is_active, is_verified, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
            
            try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                stmt.setString(1, adminEmail);
                stmt.setString(2, hashedPassword);
                stmt.setString(3, firstName);
                stmt.setString(4, lastName);
                stmt.setString(5, "ADMIN");
                stmt.setBoolean(6, true);  // is_active
                stmt.setBoolean(7, true);  // is_verified
                
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    System.out.println("✅ Admin user created successfully!");
                    System.out.println("📧 Email: " + adminEmail);
                    System.out.println("🔑 Password: " + adminPassword);
                    System.out.println("👤 Name: " + firstName + " " + lastName);
                    System.out.println("🎭 Role: ADMIN");
                    
                    // Show created user info
                    System.out.println("\n📊 Created User Details:");
                    showUserInfo(conn, adminEmail);
                    
                    System.out.println("\n🌐 You can now login at: http://localhost:8888/login");
                    
                } else {
                    System.out.println("❌ Failed to create admin user");
                }
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Database Error: " + e.getMessage());
            System.out.println("\nTroubleshooting:");
            System.out.println("1. Is MySQL running?");
            System.out.println("2. Are credentials correct? (root / aduadi47)");
            System.out.println("3. Does database 'electronic_voting_db' exist?");
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    
    private static String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        
        return hexString.toString();
    }
    
    private static void showUserInfo(Connection conn, String email) throws SQLException {
        String query = "SELECT id, email, first_name, last_name, role, is_active, is_verified, created_at FROM users WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                System.out.println("  ID: " + rs.getInt("id"));
                System.out.println("  Email: " + rs.getString("email"));
                System.out.println("  Name: " + rs.getString("first_name") + " " + rs.getString("last_name"));
                System.out.println("  Role: " + rs.getString("role"));
                System.out.println("  Active: " + rs.getBoolean("is_active"));
                System.out.println("  Verified: " + rs.getBoolean("is_verified"));
                System.out.println("  Created: " + rs.getTimestamp("created_at"));
            }
        }
    }
}