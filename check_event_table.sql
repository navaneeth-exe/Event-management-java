-- Check Event table structure
USE event_management_test;

-- Show the structure of Event table
DESCRIBE Event;

-- Show column names
SHOW COLUMNS FROM Event;

-- Try a test insert to see if it works
-- This will help identify the issue
SELECT 'Testing Event table structure...' AS Status;
