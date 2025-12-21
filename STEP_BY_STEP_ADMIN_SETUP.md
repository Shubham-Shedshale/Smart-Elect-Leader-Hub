# Step-by-Step Admin Account Setup

## Prerequisites
Make sure your application is running on: `http://localhost:8888`

## Step 1: Create Admin Account

### Using curl (Command Line):
```bash
curl -X POST http://localhost:8888/api/auth/create-admin \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@evoting.local","password":"Admin123!@#"}'
```

### Using Postman or similar tool:
- **URL**: `POST http://localhost:8888/api/auth/create-admin`
- **Headers**: 
  - `Content-Type: application/json`
- **Body** (raw JSON):
```json
{
    "email": "admin@evoting.local",
    "password": "Admin123!@#"
}
```

### Expected Response:
```json
{
    "success": true,
    "message": "Admin account created successfully",
    "admin": {
        "id": 1,
        "email": "admin@evoting.local",
        "role": "ADMIN"
    }
}
```

## Step 2: Login as Admin

### Using curl (Command Line):
```bash
curl -X POST http://localhost:8888/api/auth/admin-login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@evoting.local","password":"Admin123!@#"}'
```

### Using Postman or similar tool:
- **URL**: `POST http://localhost:8888/api/auth/admin-login`
- **Headers**: 
  - `Content-Type: application/json`
- **Body** (raw JSON):
```json
{
    "email": "admin@evoting.local",
    "password": "Admin123!@#"
}
```

### Expected Response:
```json
{
    "success": true,
    "message": "Admin login successful",
    "user": {
        "id": 1,
        "email": "admin@evoting.local",
        "role": "ADMIN",
        "firstName": null,
        "lastName": null,
        "fullName": null,
        "isAdmin": true
    }
}
```

## Common Issues & Solutions

### Issue 1: "User with email already exists"
**Solution**: Skip to Step 2 (login) - admin account already exists

### Issue 2: 403 Forbidden on create-admin
**Solution**: Check if application is running on port 8888

### Issue 3: "Invalid credentials" on login
**Solution**: Verify you used the exact same email and password from Step 1

### Issue 4: 403 Forbidden on admin-login
**Solution**: Make sure you're using `/admin-login` NOT `/login`

## Alternative: Use the Automated Script

Instead of manual API calls, you can:
1. **Run**: `create-admin.bat` (double-click the file)
2. **Script will**: Create admin account automatically and provide login instructions

## Testing the Login

After successful login, you should see:
- `success: true`
- `message: "Admin login successful"`
- `user.role: "ADMIN"`
- `user.isAdmin: true`

## Next Steps

Once logged in successfully:
1. **Save the user information** from the response
2. **Use it for subsequent API calls** that require admin authentication
3. **Access admin functions** like creating elections, candidates, etc.

## Important Notes

- **Email**: Must be unique (no other user can have the same email)
- **Password**: Will be encrypted automatically
- **Role**: Automatically set to ADMIN
- **Verification**: Admin accounts are auto-verified (no OTP required)