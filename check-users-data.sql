-- Check Users Table Data
-- Use this in your MySQL command line (if you have it set up)

USE electronic_voting_db;

-- Check if users table exists and show its structure
-- DESCRIBE users;

-- Count total users
SELECT COUNT(*) as total_users FROM users;

-- Show all users
SELECT 
    id,
    email,
    first_name,
    last_name,
    role,
    password_hash,
    is_active,
    is_verified,
    created_at
FROM users 
ORDER BY id;

-- Check if specific user exists
SELECT * FROM users WHERE email = 'admin@evoting.local';

-- Show table info
-- SHOW TABLE STATUS LIKE 'users';