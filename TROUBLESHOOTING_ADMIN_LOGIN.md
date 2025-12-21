# Troubleshooting Admin Login 403 Error

## Your Configuration ✅
- **Application Port**: 8888 (correct!)
- **Base URL**: http://localhost:8888 (correct!)

## 403 Forbidden Error Causes & Solutions

### 1. Most Common: Admin Account Not Created Yet ❌
**Problem**: Trying to login before creating admin account
**Solution**: 
```bash
# First create the admin account
curl -X POST http://localhost:8888/api/auth/create-admin \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@evoting.local","password":"Admin123!@#"}'

# Then try to login
curl -X POST http://localhost:8888/api/auth/admin-login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@evoting.local","password":"Admin123!@#"}'
```

### 2. Wrong Request Format ⚠️
**Problem**: Incorrect JSON payload or headers
**Solution**: Ensure proper request format:
- **Content-Type**: `application/json`
- **Method**: `POST`
- **Body**: 
```json
{
    "email": "admin@evoting.local",
    "password": "Admin123!@#"
}
```

### 3. Application Not Running 🚫
**Problem**: Application is not accessible
**Solution**: 
1. Check if application is running: `http://localhost:8888/api/health`
2. If health endpoint returns error, restart application: `mvn spring-boot:run`

### 4. Wrong Endpoint URL 🔗
**Problem**: Using incorrect URL path
**Solution**: Use exact endpoint:
- **Create Admin**: `POST http://localhost:8888/api/auth/create-admin`
- **Admin Login**: `POST http://localhost:8888/api/auth/admin-login`

### 5. Security Configuration Issues 🔒
**Problem**: Spring Security blocking requests
**Solution**: Check application logs for detailed error messages

## Step-by-Step Solution

### Step 1: Verify Application Health
```bash
curl http://localhost:8888/api/health
```
Expected response: JSON with status information

### Step 2: Create Admin Account
```bash
curl -X POST http://localhost:8888/api/auth/create-admin \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@evoting.local","password":"Admin123!@#"}'
```
Expected response: 
```json
{
    "success": true,
    "message": "Admin account created successfully"
}
```

### Step 3: Login as Admin
```bash
curl -X POST http://localhost:8888/api/auth/admin-login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@evoting.local","password":"Admin123!@#"}'
```
Expected response:
```json
{
    "success": true,
    "message": "Admin login successful",
    "user": {
        "id": 1,
        "email": "admin@evoting.local",
        "role": "ADMIN"
    }
}
```

## Quick Test with Updated Script

Use the updated `create-admin.bat` script which now uses port 8888:

1. **Start application**: `mvn spring-boot:run`
2. **Run admin script**: Double-click `create-admin.bat`
3. **Script will**: 
   - Check if app is running on port 8888
   - Create admin account
   - Provide login instructions

## Alternative: Manual Database Check

If API calls continue to fail, check database directly:

1. **Access H2 Console**: http://localhost:8888/h2-console
2. **JDBC URL**: `jdbc:h2:mem:electronic_voting_db`
3. **Username**: `sa`
4. **Password**: (leave empty)
5. **Run SQL**:
```sql
SELECT * FROM [user] WHERE role IN ('ADMIN', 'SUPER_ADMIN');
```

## Common Error Messages & Fixes

### "User with email already exists"
- **Meaning**: Admin account already created
- **Action**: Try logging in with existing credentials

### "Access denied" 
- **Meaning**: User exists but doesn't have admin role
- **Action**: Check user's role in database

### "Invalid credentials"
- **Meaning**: Wrong email/password
- **Action**: Verify exact credentials used during creation

### 403 Forbidden
- **Meaning**: Request is malformed or blocked
- **Action**: Check request format and application logs

## Expected Workflow

1. ✅ Application starts on port 8888
2. ✅ Admin account created successfully  
3. ✅ Admin login returns success with user details
4. ✅ Use returned user info for authenticated requests

If you continue getting 403 errors, please check:
- Application startup logs for errors
- Network firewall settings
- Request payload format (use JSON validation tools)

## Postman Testing

If using Postman:
1. **Create Admin**:
   - URL: `POST http://localhost:8888/api/auth/create-admin`
   - Headers: `Content-Type: application/json`
   - Body: `{"email":"admin@evoting.local","password":"Admin123!@#"}`

2. **Admin Login**:
   - URL: `POST http://localhost:8888/api/auth/admin-login`
   - Same headers and body as above