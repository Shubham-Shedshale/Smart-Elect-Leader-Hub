-- Create Admin User SQL Script
-- Run this in MySQL to create an admin user manually

USE electronic_voting_db;

-- Insert admin user directly into the database
INSERT INTO users (
    email, 
    password_hash, 
    first_name, 
    last_name, 
    role, 
    is_active, 
    is_verified, 
    created_at, 
    updated_at
) VALUES (
    'admin@evoting.local',
    '$2a$10$r9Z9Z9Z9Z9Z9Z9Z9Z9Z9ZOF6t5u7R4K8n4J6l2sY5d9hA2z6k4',  -- This is a BCrypt hash for "Admin123!"
    'System',
    'Administrator', 
    'ADMIN',
    1,  -- is_active
    1,  -- is_verified
    NOW(),
    NOW()
);

-- Verify the user was created
SELECT id, email, first_name, last_name, role, is_active, is_verified, created_at 
FROM users 
WHERE email = 'admin@evoting.local';

-- Show total user count
SELECT COUNT(*) as total_users FROM users;