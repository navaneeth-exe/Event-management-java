package com.eventmanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EventManagerUserManager {
	public static EventManagerUser getEventManagerUserById(int eventManagerId) {
        EventManagerUser eventManager = null;

        // Get the database connection
        Connection conn = DatabaseConnection.getConnection();

        try {
            // Retrieve the event manager from the EventManager table based on the eventManagerId
            String query = "SELECT * FROM EventManager WHERE eventManagerId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, eventManagerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                eventManager = new EventManagerUser();
                eventManager.setEventManagerId(rs.getInt("eventManagerId"));
                eventManager.setEventManagerName(rs.getString("eventManagerName"));
                eventManager.setUserId(rs.getInt("userId"));

                // Retrieve user details from the User table
                query = "SELECT * FROM User WHERE userId = ?";
                stmt = conn.prepareStatement(query);
                stmt.setInt(1, eventManager.getUserId());
                ResultSet userRS = stmt.executeQuery();

                if (userRS.next()) {
                    eventManager.setUsername(userRS.getString("username"));
                    eventManager.setPassword(userRS.getString("password"));
                    eventManager.setEmail(userRS.getString("email"));
                    eventManager.setUserType(userRS.getString("userType"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the database connection
            DatabaseConnection.closeConnection(conn);
        }

        return eventManager;
    }
}
