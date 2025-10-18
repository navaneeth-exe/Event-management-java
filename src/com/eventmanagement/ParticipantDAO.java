package com.eventmanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ParticipantDAO {

    /**
     * Creates a new participant in the database
     * @param participant The Participant object to create
     * @return The generated participantId, or -1 if failed
     */
    public static int createParticipant(Participant participant) {
        int generatedId = -1;
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "INSERT INTO Participant (participantName, userId, phoneNumber) " +
                          "VALUES (?, ?, ?)";
            
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, participant.getParticipantName());
            stmt.setInt(2, participant.getUserId());
            stmt.setString(3, participant.getPhoneNumber());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                generatedId = generatedKeys.getInt(1);
                participant.setParticipantId(generatedId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return generatedId;
    }

    /**
     * Retrieves a participant by their userId
     * @param userId The userId from the User table
     * @return Participant object or null if not found
     */
    public static Participant getParticipantByUserId(int userId) {
        Participant participant = null;
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "SELECT p.*, u.username, u.email, u.userType " +
                          "FROM Participant p " +
                          "INNER JOIN User u ON p.userId = u.userId " +
                          "WHERE p.userId = ?";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                participant = mapResultSetToParticipant(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return participant;
    }

    /**
     * Retrieves a participant by their participantId
     * @param participantId The ID of the participant
     * @return Participant object or null if not found
     */
    public static Participant getParticipantById(int participantId) {
        Participant participant = null;
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "SELECT p.*, u.username, u.email, u.userType " +
                          "FROM Participant p " +
                          "INNER JOIN User u ON p.userId = u.userId " +
                          "WHERE p.participantId = ?";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, participantId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                participant = mapResultSetToParticipant(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return participant;
    }

    /**
     * Updates an existing participant's information
     * @param participant The Participant object with updated information
     * @return true if successful, false otherwise
     */
    public static boolean updateParticipant(Participant participant) {
        Connection conn = DatabaseConnection.getConnection();
        boolean success = false;

        try {
            String query = "UPDATE Participant SET participantName = ?, phoneNumber = ? " +
                          "WHERE participantId = ?";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, participant.getParticipantName());
            stmt.setString(2, participant.getPhoneNumber());
            stmt.setInt(3, participant.getParticipantId());
            
            int rowsAffected = stmt.executeUpdate();
            success = (rowsAffected > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return success;
    }

    /**
     * Retrieves all participants
     * @return List of all Participant objects
     */
    public static List<Participant> getAllParticipants() {
        List<Participant> participants = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "SELECT p.*, u.username, u.email, u.userType " +
                          "FROM Participant p " +
                          "INNER JOIN User u ON p.userId = u.userId " +
                          "ORDER BY p.participantName";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Participant participant = mapResultSetToParticipant(rs);
                participants.add(participant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return participants;
    }

    /**
     * Deletes a participant from the database
     * @param participantId The ID of the participant to delete
     * @return true if successful, false otherwise
     */
    public static boolean deleteParticipant(int participantId) {
        Connection conn = DatabaseConnection.getConnection();
        boolean success = false;

        try {
            String query = "DELETE FROM Participant WHERE participantId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, participantId);
            
            int rowsAffected = stmt.executeUpdate();
            success = (rowsAffected > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return success;
    }

    /**
     * Checks if a participant exists by userId
     * @param userId The userId to check
     * @return true if participant exists, false otherwise
     */
    public static boolean participantExists(int userId) {
        Connection conn = DatabaseConnection.getConnection();
        boolean exists = false;

        try {
            String query = "SELECT COUNT(*) as count FROM Participant WHERE userId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                exists = (rs.getInt("count") > 0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return exists;
    }

    /**
     * Gets the count of events a participant has registered for
     * @param participantId The ID of the participant
     * @return Count of registered events
     */
    public static int getRegisteredEventsCount(int participantId) {
        Connection conn = DatabaseConnection.getConnection();
        int count = 0;

        try {
            String query = "SELECT COUNT(*) as count FROM EventRegistration " +
                          "WHERE participantId = ? AND status = 'registered'";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, participantId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return count;
    }

    /**
     * Helper method to map ResultSet to Participant object
     * @param rs ResultSet from database query
     * @return Participant object
     * @throws SQLException if database access error occurs
     */
    private static Participant mapResultSetToParticipant(ResultSet rs) throws SQLException {
        Participant participant = new Participant();
        participant.setParticipantId(rs.getInt("participantId"));
        participant.setParticipantName(rs.getString("participantName"));
        participant.setUserId(rs.getInt("userId"));
        participant.setPhoneNumber(rs.getString("phoneNumber"));
        
        // Set User fields if available from JOIN
        try {
            participant.setUsername(rs.getString("username"));
            participant.setEmail(rs.getString("email"));
            participant.setUserType(rs.getString("userType"));
        } catch (SQLException e) {
            // These fields might not be present in all queries
        }
        
        return participant;
    }
}
