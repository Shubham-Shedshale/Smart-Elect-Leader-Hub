-- Microsoft SQL Server Database Migration Script for Electronic Voting System
-- This script creates all necessary tables, constraints, and indexes for MS SQL Server

USE master;
GO

-- Use existing ELECTRONIC_VS database
USE ELECTRONIC_VS;
GO

-- Enable ANSI NULLS for proper SQL Server compatibility
SET ANSI_NULLS ON;
GO

-- Enable Quoted Identifiers
SET QUOTED_IDENTIFIER ON;
GO

-- Drop existing tables if they exist (in proper order due to foreign keys)
IF OBJECT_ID('votes', 'U') IS NOT NULL DROP TABLE votes;
IF OBJECT_ID('announcements', 'U') IS NOT NULL DROP TABLE announcements;
IF OBJECT_ID('candidates', 'U') IS NOT NULL DROP TABLE candidates;
IF OBJECT_ID('elections', 'U') IS NOT NULL DROP TABLE elections;
IF OBJECT_ID('users', 'U') IS NOT NULL DROP TABLE users;
GO

-- Create Users Table
CREATE TABLE users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    email NVARCHAR(255) NOT NULL UNIQUE,
    first_name NVARCHAR(255) NOT NULL,
    last_name NVARCHAR(255) NOT NULL,
    password_hash NVARCHAR(255) NOT NULL,
    role NVARCHAR(255) NOT NULL CHECK (role IN ('ADMIN', 'SUPER_ADMIN', 'VOTER', 'CANDIDATE')),
    is_active BIT NOT NULL DEFAULT 1,
    is_verified BIT NOT NULL DEFAULT 0,
    otp_code NVARCHAR(255) NULL,
    otp_expires_at DATETIME2(6) NULL,
    last_login DATETIME2(6) NULL,
    created_at DATETIME2(6) NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2(6) NOT NULL DEFAULT GETDATE(),
    created_by BIGINT NULL
);
GO

-- Create Elections Table
CREATE TABLE elections (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX) NULL,
    start_date DATETIME2(6) NOT NULL,
    end_date DATETIME2(6) NOT NULL,
    status NVARCHAR(255) NOT NULL CHECK (status IN ('DRAFT', 'SCHEDULED', 'ACTIVE', 'COMPLETED', 'CANCELLED')),
    allow_multiple_candidates BIT NOT NULL DEFAULT 0,
    is_anonymous BIT NOT NULL DEFAULT 0,
    max_votes_per_user INT NOT NULL DEFAULT 1,
    created_at DATETIME2(6) NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2(6) NOT NULL DEFAULT GETDATE(),
    created_by BIGINT NULL
);
GO

-- Create Candidates Table
CREATE TABLE candidates (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    full_name NVARCHAR(255) NOT NULL,
    email_address NVARCHAR(255) NOT NULL,
    contact_number NVARCHAR(255) NULL,
    party_name NVARCHAR(255) NULL,
    position NVARCHAR(255) NULL,
    bio NVARCHAR(MAX) NULL,
    manifesto NVARCHAR(MAX) NULL,
    profile_image NVARCHAR(255) NULL,
    website_url NVARCHAR(255) NULL,
    status NVARCHAR(255) NOT NULL CHECK (status IN ('ACTIVE', 'WITHDRAWN', 'DISQUALIFIED', 'PENDING')),
    votes_count BIGINT NOT NULL DEFAULT 0,
    election_id BIGINT NOT NULL,
    created_at DATETIME2(6) NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2(6) NOT NULL DEFAULT GETDATE(),
    created_by BIGINT NULL
);
GO

-- Create Votes Table
CREATE TABLE votes (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    candidate_id BIGINT NOT NULL,
    election_id BIGINT NOT NULL,
    voted_at DATETIME2(6) NOT NULL DEFAULT GETDATE(),
    ip_address NVARCHAR(255) NULL,
    session_id NVARCHAR(255) NULL,
    user_agent NVARCHAR(255) NULL
);
GO

-- Create Announcements Table
CREATE TABLE announcements (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    title NVARCHAR(255) NOT NULL,
    content NVARCHAR(MAX) NOT NULL,
    type NVARCHAR(255) NOT NULL CHECK (type IN ('ELECTION_SCHEDULE', 'CANDIDATE_ANNOUNCEMENT', 'VOTING_PROCEDURE', 'RESULTS_ANNOUNCEMENT', 'GENERAL', 'URGENT')),
    priority TINYINT NOT NULL CHECK (priority >= 0 AND priority <= 3),
    target_audience NVARCHAR(255) NULL,
    attachment_url NVARCHAR(255) NULL,
    is_published BIT NOT NULL DEFAULT 0,
    published_at DATETIME2(6) NULL,
    expires_at DATETIME2(6) NULL,
    created_at DATETIME2(6) NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2(6) NOT NULL DEFAULT GETDATE(),
    created_by BIGINT NULL,
    election_id BIGINT NOT NULL
);
GO

-- Add Foreign Key Constraints
ALTER TABLE candidates
ADD CONSTRAINT FK_candidates_elections 
FOREIGN KEY (election_id) REFERENCES elections(id);
GO

ALTER TABLE votes
ADD CONSTRAINT FK_votes_users 
FOREIGN KEY (user_id) REFERENCES users(id);
GO

ALTER TABLE votes
ADD CONSTRAINT FK_votes_candidates 
FOREIGN KEY (candidate_id) REFERENCES candidates(id);
GO

ALTER TABLE votes
ADD CONSTRAINT FK_votes_elections 
FOREIGN KEY (election_id) REFERENCES elections(id);
GO

ALTER TABLE announcements
ADD CONSTRAINT FK_announcements_elections 
FOREIGN KEY (election_id) REFERENCES elections(id);
GO

-- Create Indexes for Performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_otp ON users(otp_code);

CREATE INDEX idx_elections_status ON elections(status);
CREATE INDEX idx_elections_dates ON elections(start_date, end_date);

CREATE INDEX idx_candidates_election ON candidates(election_id);
CREATE INDEX idx_candidates_status ON candidates(status);

CREATE INDEX idx_votes_user ON votes(user_id);
CREATE INDEX idx_votes_election ON votes(election_id);
CREATE INDEX idx_votes_candidate ON votes(candidate_id);
CREATE INDEX idx_votes_timestamp ON votes(voted_at);

CREATE INDEX idx_announcements_election ON announcements(election_id);
CREATE INDEX idx_announcements_date ON announcements(published_at);
CREATE INDEX idx_announcements_type ON announcements(type);

-- Create Stored Procedures for Common Operations
GO

-- Procedure to get election results
CREATE PROCEDURE GetElectionResults
    @ElectionId BIGINT
AS
BEGIN
    SELECT 
        c.full_name AS CandidateName,
        c.party_name AS PartyName,
        c.votes_count AS VoteCount,
        e.title AS ElectionTitle,
        COUNT(v.id) AS TotalVotes
    FROM candidates c
    LEFT JOIN votes v ON c.id = v.candidate_id
    INNER JOIN elections e ON c.election_id = e.id
    WHERE c.election_id = @ElectionId AND c.status = 'ACTIVE'
    GROUP BY c.id, c.full_name, c.party_name, c.votes_count, e.title
    ORDER BY VoteCount DESC;
END
GO

-- Procedure to check if user has voted in election
CREATE PROCEDURE HasUserVoted
    @UserId BIGINT,
    @ElectionId BIGINT
AS
BEGIN
    SELECT COUNT(*) AS VoteCount
    FROM votes v
    WHERE v.user_id = @UserId AND v.election_id = @ElectionId;
END
GO

-- Procedure to get user's voting history
CREATE PROCEDURE GetUserVotingHistory
    @UserId BIGINT
AS
BEGIN
    SELECT 
        e.title AS ElectionTitle,
        e.start_date AS ElectionStartDate,
        e.end_date AS ElectionEndDate,
        c.full_name AS CandidateVotedFor,
        v.voted_at AS VoteDate
    FROM votes v
    INNER JOIN elections e ON v.election_id = e.id
    INNER JOIN candidates c ON v.candidate_id = c.id
    WHERE v.user_id = @UserId
    ORDER BY v.voted_at DESC;
END
GO

-- Insert sample data for testing
PRINT 'Creating sample data...';

-- Insert sample users
IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@evoting.com')
BEGIN
    INSERT INTO users (email, first_name, last_name, password_hash, role, is_verified, created_by)
    VALUES 
    ('admin@evoting.com', 'Admin', 'User', '$2a$10$DiyQfFzqBWqM3b6y8YqW.eU4q7TqyM4b5YqW.eU4q7TqyM4b5YqW.e', 'SUPER_ADMIN', 1, NULL),
    ('voter1@evoting.com', 'John', 'Doe', '$2a$10$DiyQfFzqBWqM3b6y8YqW.eU4q7TqyM4b5YqW.eU4q7TqyM4b5YqW.e', 'VOTER', 1, NULL),
    ('candidate1@evoting.com', 'Jane', 'Smith', '$2a$10$DiyQfFzqBWqM3b6y8YqW.eU4q7TqyM4b5YqW.eU4q7TqyM4b5YqW.e', 'CANDIDATE', 1, NULL);
END

-- Insert sample election
IF NOT EXISTS (SELECT 1 FROM elections WHERE title = 'Sample Presidential Election')
BEGIN
    INSERT INTO elections (title, description, start_date, end_date, status, allow_multiple_candidates, is_anonymous, max_votes_per_user, created_by)
    VALUES (
        'Sample Presidential Election',
        'This is a sample election for testing purposes',
        DATEADD(hour, -1, GETDATE()), -- Start 1 hour ago
        DATEADD(day, 7, GETDATE()),   -- End in 7 days
        'ACTIVE',
        0,
        0,
        1,
        NULL
    );
END

-- Insert sample candidates
IF NOT EXISTS (SELECT 1 FROM candidates WHERE election_id = (SELECT id FROM elections WHERE title = 'Sample Presidential Election'))
BEGIN
    DECLARE @ElectionId_Candidates BIGINT = (SELECT id FROM elections WHERE title = 'Sample Presidential Election');
    
    INSERT INTO candidates (full_name, email_address, contact_number, party_name, position, bio, status, election_id, created_by)
    VALUES 
    ('Jane Smith', 'jane.smith@example.com', '+1234567890', 'Democratic Party', 'President', 'Experienced leader with a vision for the future', 'ACTIVE', @ElectionId_Candidates, NULL),
    ('John Johnson', 'john.johnson@example.com', '+1234567891', 'Republican Party', 'President', 'Business leader focused on economic growth', 'ACTIVE', @ElectionId_Candidates, NULL),
    ('Alice Brown', 'alice.brown@example.com', '+1234567892', 'Independent', 'President', 'Independent candidate for change', 'ACTIVE', @ElectionId_Candidates, NULL);
END

-- Insert sample announcements
IF NOT EXISTS (SELECT 1 FROM announcements WHERE title LIKE '%Sample%')
BEGIN
    DECLARE @ElectionId_Announcements BIGINT = (SELECT id FROM elections WHERE title = 'Sample Presidential Election');
    
    INSERT INTO announcements (title, content, type, priority, is_published, published_at, election_id, created_by)
    VALUES 
    ('Election Started', 'The sample presidential election has officially begun! You can now cast your vote.', 'ELECTION_SCHEDULE', 1, 1, GETDATE(), @ElectionId_Announcements, NULL),
    ('Voting Instructions', 'To vote, please login to your account and select your preferred candidate. You have one vote per election.', 'VOTING_PROCEDURE', 2, 1, GETDATE(), @ElectionId_Announcements, NULL),
    ('Important Notice', 'This is a sample election for testing the electronic voting system.', 'GENERAL', 0, 1, GETDATE(), @ElectionId_Announcements, NULL);
END

PRINT 'Sample data created successfully!';

-- Display success message
SELECT 'Microsoft SQL Server Database Migration Completed Successfully!' as Status;
SELECT 'Database: ElectronicVotingDB' as Database_Info;
SELECT 'Tables Created: users, elections, candidates, votes, announcements' as Tables_Info;
SELECT 'Stored Procedures: GetElectionResults, HasUserVoted, GetUserVotingHistory' as Procedures_Info;

-- Show table counts
SELECT 'Users' as Table_Name, COUNT(*) as Record_Count FROM users
UNION ALL
SELECT 'Elections' as Table_Name, COUNT(*) as Record_Count FROM elections
UNION ALL
SELECT 'Candidates' as Table_Name, COUNT(*) as Record_Count FROM candidates
UNION ALL
SELECT 'Votes' as Table_Name, COUNT(*) as Record_Count FROM votes
UNION ALL
SELECT 'Announcements' as Table_Name, COUNT(*) as Record_Count FROM announcements;
