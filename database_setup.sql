-- ============================================
-- Event Management System Database Setup
-- Database: event_management_test
-- ============================================

-- Drop database if exists and create new one
DROP DATABASE IF EXISTS event_management_test;
CREATE DATABASE event_management_test;
USE event_management_test;

-- ============================================
-- Table: User
-- ============================================
CREATE TABLE User (
    userId INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    userType ENUM('ADMIN', 'EVENT_MANAGER', 'PARTICIPANT') NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- Table: Admin
-- ============================================
CREATE TABLE Admin (
    adminId INT AUTO_INCREMENT PRIMARY KEY,
    adminName VARCHAR(100) NOT NULL,
    userId INT NOT NULL UNIQUE,
    FOREIGN KEY (userId) REFERENCES User(userId) ON DELETE CASCADE
);

-- ============================================
-- Table: EventManager
-- ============================================
CREATE TABLE EventManager (
    eventManagerId INT AUTO_INCREMENT PRIMARY KEY,
    eventManagerName VARCHAR(100) NOT NULL,
    userId INT NOT NULL UNIQUE,
    FOREIGN KEY (userId) REFERENCES User(userId) ON DELETE CASCADE
);

-- ============================================
-- Table: Participant
-- ============================================
CREATE TABLE Participant (
    participantId INT AUTO_INCREMENT PRIMARY KEY,
    participantName VARCHAR(100) NOT NULL,
    userId INT NOT NULL UNIQUE,
    phoneNumber VARCHAR(15),
    FOREIGN KEY (userId) REFERENCES User(userId) ON DELETE CASCADE
);

-- ============================================
-- Table: Hall
-- ============================================
CREATE TABLE Hall (
    hallId INT AUTO_INCREMENT PRIMARY KEY,
    hallName VARCHAR(100) NOT NULL UNIQUE,
    capacity INT NOT NULL,
    location VARCHAR(255) NOT NULL,
    isAvailable BOOLEAN DEFAULT TRUE,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- Table: Event
-- ============================================
CREATE TABLE Event (
    eventId INT AUTO_INCREMENT PRIMARY KEY,
    eventTitle VARCHAR(200) NOT NULL,
    eventDescription TEXT,
    eventType VARCHAR(50) NOT NULL,
    eventDate DATETIME NOT NULL,
    eventStatus ENUM('SCHEDULED', 'ONGOING', 'COMPLETED', 'CANCELLED') DEFAULT 'SCHEDULED',
    managerId INT NOT NULL,
    hallId INT NOT NULL,
    approvalStatus ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    approvedBy INT NULL,
    approvalDate TIMESTAMP NULL,
    maxParticipants INT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (managerId) REFERENCES EventManager(eventManagerId) ON DELETE RESTRICT,
    FOREIGN KEY (hallId) REFERENCES Hall(hallId) ON DELETE RESTRICT,
    FOREIGN KEY (approvedBy) REFERENCES Admin(adminId) ON DELETE SET NULL
);

-- ============================================
-- Table: EventRegistration
-- ============================================
CREATE TABLE EventRegistration (
    registrationId INT AUTO_INCREMENT PRIMARY KEY,
    eventId INT NOT NULL,
    participantId INT NOT NULL,
    registrationDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('REGISTERED', 'ATTENDED', 'CANCELLED') DEFAULT 'REGISTERED',
    FOREIGN KEY (eventId) REFERENCES Event(eventId) ON DELETE CASCADE,
    FOREIGN KEY (participantId) REFERENCES Participant(participantId) ON DELETE CASCADE,
    UNIQUE KEY unique_registration (eventId, participantId)
);

-- ============================================
-- INSERT SAMPLE DATA
-- ============================================

-- Insert Users
INSERT INTO User (username, password, email, userType) VALUES
('admin1', 'admin123', 'admin1@eventmanagement.com', 'ADMIN'),
('manager1', 'manager123', 'manager1@eventmanagement.com', 'EVENT_MANAGER'),
('manager2', 'manager123', 'manager2@eventmanagement.com', 'EVENT_MANAGER'),
('participant1', 'part123', 'participant1@eventmanagement.com', 'PARTICIPANT'),
('participant2', 'part123', 'participant2@eventmanagement.com', 'PARTICIPANT'),
('participant3', 'part123', 'participant3@eventmanagement.com', 'PARTICIPANT');

-- Insert Admin
INSERT INTO Admin (adminName, userId) VALUES
('System Administrator', 1);

-- Insert Event Managers
INSERT INTO EventManager (eventManagerName, userId) VALUES
('John Smith', 2),
('Sarah Johnson', 3);

-- Insert Participants
INSERT INTO Participant (participantName, userId, phoneNumber) VALUES
('Alice Brown', 4, '555-0101'),
('Bob Wilson', 5, '555-0102'),
('Charlie Davis', 6, '555-0103');

-- Insert Halls
INSERT INTO Hall (hallName, capacity, location, isAvailable) VALUES
('Grand Ballroom', 500, 'Building A, Floor 1', TRUE),
('Conference Hall A', 100, 'Building B, Floor 2', TRUE),
('Seminar Room 1', 50, 'Building C, Floor 3', TRUE),
('Auditorium', 300, 'Building A, Floor 2', TRUE),
('Meeting Room 5', 25, 'Building D, Floor 1', TRUE);

-- Insert Events
INSERT INTO Event (eventTitle, eventDescription, eventType, eventDate, eventStatus, managerId, hallId, approvalStatus, approvedBy, approvalDate, maxParticipants) VALUES
('Annual Tech Conference 2024', 'A comprehensive technology conference featuring industry leaders', 'Conference', '2024-12-15 09:00:00', 'SCHEDULED', 1, 1, 'APPROVED', 1, CURRENT_TIMESTAMP, 450),
('Web Development Workshop', 'Hands-on workshop covering modern web development practices', 'Workshop', '2024-11-20 14:00:00', 'SCHEDULED', 2, 2, 'APPROVED', 1, CURRENT_TIMESTAMP, 80),
('AI & Machine Learning Seminar', 'Introduction to AI and ML concepts for beginners', 'Seminar', '2024-11-25 10:00:00', 'SCHEDULED', 1, 3, 'PENDING', NULL, NULL, NULL),
('Startup Networking Event', 'Connect with entrepreneurs and investors', 'Networking', '2024-12-01 18:00:00', 'SCHEDULED', 2, 4, 'APPROVED', 1, CURRENT_TIMESTAMP, NULL),
('Project Management Masterclass', 'Advanced project management techniques and tools', 'Training', '2024-12-10 09:00:00', 'SCHEDULED', 1, 2, 'PENDING', NULL, NULL, 95);

-- Insert Event Registrations
INSERT INTO EventRegistration (eventId, participantId, status) VALUES
(1, 1, 'REGISTERED'),
(1, 2, 'REGISTERED'),
(1, 3, 'REGISTERED'),
(2, 1, 'REGISTERED'),
(2, 2, 'REGISTERED'),
(3, 3, 'REGISTERED'),
(4, 1, 'REGISTERED'),
(4, 2, 'REGISTERED'),
(4, 3, 'REGISTERED');

-- ============================================
-- VERIFICATION QUERIES
-- ============================================

-- Display all tables
SHOW TABLES;

-- Display counts
SELECT 'Users' AS TableName, COUNT(*) AS RecordCount FROM User
UNION ALL
SELECT 'Admins', COUNT(*) FROM Admin
UNION ALL
SELECT 'Event Managers', COUNT(*) FROM EventManager
UNION ALL
SELECT 'Participants', COUNT(*) FROM Participant
UNION ALL
SELECT 'Halls', COUNT(*) FROM Hall
UNION ALL
SELECT 'Events', COUNT(*) FROM Event
UNION ALL
SELECT 'Registrations', COUNT(*) FROM EventRegistration;

-- ============================================
-- Database Setup Complete
-- ============================================
