package com.eventmanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAuthentication {
    public static User authenticateUser(String username, String password) {
        User user = null;

        // Establish a database connection
        Connection conn = DatabaseConnection.getConnection();

        try {
            // Retrieve the user from the User table based on the provided username and password
            String query = "SELECT * FROM User WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String userType = rs.getString("userType");
                if (userType.equalsIgnoreCase("EVENT_MANAGER")) {
                    // Create an Event manager object
                    user = new EventManagerUser();
                    user.setUserId(rs.getInt("userId"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    user.setUserType(userType);

                    // Retrieve event manager-specific details
                    query = "SELECT * FROM EventManager WHERE userId = ?";
                    stmt = conn.prepareStatement(query);
                    stmt.setInt(1, user.getUserId());
                    ResultSet eventManagerRS = stmt.executeQuery();

                    if (eventManagerRS.next()) {
                        EventManagerUser eventManager = (EventManagerUser) user;
                        eventManager.setEventManagerId(eventManagerRS.getInt("eventManagerId"));
                        eventManager.setEventManagerName(eventManagerRS.getString("eventManagerName"));
                    }
                } else if (userType.equalsIgnoreCase("ADMIN")) {
                    // Create an Admin object
                    user = new Admin();
                    user.setUserId(rs.getInt("userId"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    user.setUserType(userType);

                    // Retrieve admin-specific details
                    query = "SELECT * FROM Admin WHERE userId = ?";
                    stmt = conn.prepareStatement(query);
                    stmt.setInt(1, user.getUserId());
                    ResultSet adminRS = stmt.executeQuery();

                    if (adminRS.next()) {
                        Admin admin = (Admin) user;
                        admin.setAdminId(adminRS.getInt("adminId"));
                        admin.setAdminName(adminRS.getString("adminName"));
                    }
                } else if (userType.equalsIgnoreCase("PARTICIPANT")) {
                    // Create a Participant object
                    user = new Participant();
                    user.setUserId(rs.getInt("userId"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    user.setUserType(userType);

                    // Retrieve participant-specific details
                    query = "SELECT * FROM Participant WHERE userId = ?";
                    stmt = conn.prepareStatement(query);
                    stmt.setInt(1, user.getUserId());
                    ResultSet participantRS = stmt.executeQuery();

                    if (participantRS.next()) {
                        Participant participant = (Participant) user;
                        participant.setParticipantId(participantRS.getInt("participantId"));
                        participant.setParticipantName(participantRS.getString("participantName"));
                        participant.setPhoneNumber(participantRS.getString("phoneNumber"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the database connection
            DatabaseConnection.closeConnection(conn);
        }

        return user;
    }
}