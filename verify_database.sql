-- ============================================
-- Database Verification Script
-- Run this to check if database was set up correctly
-- ============================================

-- Check if database exists
SHOW DATABASES LIKE 'event_management_test';

-- Use the database
USE event_management_test;

-- Show all tables
SHOW TABLES;

-- Check User table data
SELECT * FROM User;

-- Check Admin table data
SELECT * FROM Admin;

-- Check EventManager table data
SELECT * FROM EventManager;

-- Check Participant table data
SELECT * FROM Participant;

-- Verify login credentials (check if users exist)
SELECT userId, username, password, email, userType 
FROM User 
WHERE username IN ('admin1', 'manager1', 'participant1');
