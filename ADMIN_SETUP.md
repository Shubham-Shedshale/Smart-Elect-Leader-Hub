# Admin Account Setup Guide

## No Default Admin Credentials ❌

The Electronic Voting System does **NOT** have any hardcoded default admin credentials for security reasons.

## How to Create Admin Account

### Method 1: API Endpoint (Recommended)

#### Step 1: Start the Application
```bash
mvn spring-boot:run
```

#### Step 2: Create Admin via API
Use a tool like **Postman**, **curl**, or **HTTPie** to call:

**POST** `http://localhost:8080/api/auth/create-admin`

**Request Body (JSON)**:
```json
{
    "email": "admin@example.com",
    "password": "SecureAdmin123!"
}
```

**Example using curl**:
```bash
curl -X POST http://localhost:8080/api/auth/create-admin \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "SecureAdmin123!"
  }'
```

#### Step 3: Login as Admin
**POST** `http://localhost:8080/api/auth/admin-login`

**Request Body (JSON)**:
```json
{
    "email": "admin@example.com",
    "password": "SecureAdmin123!"
}
```

### Method 2: SQL Database Direct Insert

If you have direct database access, you can insert an admin user directly:

#### For SQL Server:
```sql
USE ELECTRONIC_VS;
GO

INSERT INTO [user] (email, password_hash, role, is_active, is_verified, created_at, updated_at)
VALUES (
    'admin@example.com',
    '$2a$10$rG5J8X8X8X8X8X8X8X8XO9V7g4V6V6V6V6V6V6V6V6V6V6V6V6',  -- BCrypt hash of 'SecureAdmin123!'
    'ADMIN',
    1,
    1,
    GETDATE(),
    GETDATE()
);
GO
```

**Note**: You'll need to generate the actual BCrypt hash. You can use an online BCrypt generator or create one programmatically.

### Method 3: Quick Java Script

Create a simple Java class to generate admin:

```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateAdmin {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "SecureAdmin123!";
        String hashedPassword = encoder.encode(password);
        System.out.println("Email: admin@example.com");
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hashedPassword);
    }
}
```

## Login Endpoints

### Regular Login:
- **URL**: `POST /api/auth/login`
- **Use for**: Voter accounts

### Admin Login:
- **URL**: `POST /api/auth/admin-login`
- **Use for**: Admin accounts (ADMIN or SUPER_ADMIN role)

## Security Features

### Password Requirements:
- No specific requirements enforced
- Use strong passwords in production
- Recommended: Minimum 8 characters with mix of letters, numbers, symbols

### Account Verification:
- **Voters**: Require OTP verification via email
- **Admins**: Auto-verified (for development convenience)

### Role Types:
- `VOTER` - Regular voting users
- `ADMIN` - System administrators
- `SUPER_ADMIN` - Full system access

## Example Admin Account

**Recommended credentials for testing**:
- **Email**: `admin@evoting.local`
- **Password**: `Admin123!@#`

**Steps to create**:
1. Start the application
2. Call create-admin endpoint with above credentials
3. Login using admin-login endpoint

## API Response Examples

### Successful Admin Creation:
```json
{
    "success": true,
    "message": "Admin account created successfully",
    "admin": {
        "id": 1,
        "email": "admin@example.com",
        "role": "ADMIN"
    }
}
```

### Successful Admin Login:
```json
{
    "success": true,
    "message": "Admin login successful",
    "user": {
        "id": 1,
        "email": "admin@example.com",
        "role": "ADMIN",
        "firstName": null,
        "lastName": null,
        "fullName": null,
        "isAdmin": true
    }
}
```

## Troubleshooting

### "User with email already exists":
- Use a different email address
- Or login with existing admin account

### "Access denied" on admin-login:
- Ensure user has ADMIN or SUPER_ADMIN role
- Check if user is active (`is_active = 1`)

### "Invalid credentials":
- Verify email and password are correct
- Check if user exists in database
- Ensure password matches the stored hash

## Next Steps

1. **Create admin account** using one of the methods above
2. **Login via admin panel** at `http://localhost:8080/admin/login`
3. **Set up elections and candidates** through the admin dashboard
4. **Create voter accounts** for testing voting functionality

## Security Recommendations

### For Production:
- Use environment variables for admin credentials
- Implement password complexity requirements
- Enable HTTPS for all communications
- Set up proper email service configuration
- Consider using OAuth2 or LDAP integration
- Implement account lockout after failed attempts
- Add audit logging for admin actions