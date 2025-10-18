🎯 Event Management System (Java + MySQL)

A desktop-based event management system built using Java Swing and MySQL, featuring role-based access, admin approval workflows, and smart hall booking.

📘 Overview

This system helps institutions plan, approve, and manage events efficiently — from event creation to participant registration — all through a clean, intuitive desktop app.

Key Highlights:

🎭 Three User Roles: Admin, Event Manager, Participant
✅ Event Approval Workflow (Admin-controlled)
🏛️ Smart Hall Booking: Prevents double bookings
🔐 User Authentication with session management
💡 Color-coded Status: Pending 🟠 | Approved 🟢 | Rejected 🔴

⚙️ Features
👨‍💼 Administrator
Approve / Reject Events
View Pending & All Events
Track who approved or rejected events
Real-time dashboard refresh

🎪 Event Manager
Create Events (Title, Type, Description, Date, Hall)
View own events with approval status
View all approved events

👤 Participant
Browse and register for approved events
View and cancel registrations
Prevent duplicate registrations
Search by title, date, hall, or type

🧱 System Architecture
Three-Tier Architecture:
Presentation Layer – Java Swing UIs (Dashboards, Forms, Dialogs)
Business Logic Layer – Managers, DAO classes, authentication
Data Layer – MySQL database with JDBC connectivity

🗄️ Database Schema (Simplified)
Core Tables:
User, EventManager, Admin, Participant, Hall, Event, EventRegistration

💡 Relationships:
One EventManager → Many Events
One Event → Many EventRegistrations
Admin approves or rejects events

🧰 Technologies Used
Category	Tech
Language	Java SE 11+
GUI	Swing / AWT
Database	MySQL 8.0+
Connectivity	JDBC (MySQL Connector/J)
Patterns	DAO, Singleton, MVC (partial), Factory
IDEs	Eclipse / IntelliJ IDEA / NetBeans
🚀 Installation & Setup

1️⃣ Prerequisites
JDK 11+
MySQL 8.0+
MySQL Connector/J
Any Java IDE

2️⃣ Database Setup
CREATE DATABASE event_management_system;
-- Run table creation SQL scripts (provided in /database/ or documentation)

3️⃣ Configure Database Connection
Edit DatabaseConnection.java:
private static final String URL = "jdbc:mysql://localhost:3306/event_management_system";
private static final String USERNAME = "your_username";
private static final String PASSWORD = "your_password";

4️⃣ Run the App

Open and run:
src/com/eventmanagement/ui/WelcomePageUI.java

🧭 Workflow Example

1. Event Manager: Creates an event → waits for admin approval
2. Admin: Approves/rejects event → visible to participants
3. Participant: Registers → views/cancels registration

📂 Project Structure
Event_Management_System/
├── src/com/eventmanagement/
│   ├── model/ (User, Event, Hall, Participant…)
│   ├── dao/ (EventDAO, HallDAO, RegistrationDAO…)
│   ├── ui/ (Dashboards, Login, Registration, Event Creation)
│   ├── manager/ (Business logic)
│   ├── auth/ (Login & Session)
│   └── utils/ (DatabaseConnection)
├── lib/mysql-connector-java.jar
└── README.md

🔒 Security Notes

⚠️ Current Issues:
Passwords stored in plain text
Hardcoded DB credentials
No session timeout

🧩 Recommended Fixes:
Implement BCrypt password hashing
Move DB credentials to environment variables
Add input validation & logging

🐞 Known Limitations
❌ No event editing or deletion
⚠️ No email notifications
🚫 Capacity not enforced
🕒 No transaction management
🔁 Some code duplication

💡 Future Enhancements
 Password hashing with BCrypt
 Event editing & deletion
 Email notifications for approvals
 Capacity enforcement
 Transaction & connection pooling
 Event calendar + search filters
 Spring Boot migration (optional)

🧩 Troubleshooting
Issue	Quick Fix
❌ com.mysql.cj.jdbc.Driver not found	Add MySQL connector JAR to build path
🚫 Access denied for user	Check MySQL username/password
⚙️ Tables not found	Run all SQL scripts in MySQL
🏛️ No halls available	Ensure hall data exists & is marked available

📄 License
This project is developed for educational purposes. Feel free to use, modify, and distribute as needed.
