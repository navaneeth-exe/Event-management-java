package com.eventmanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventRegistrationDAO {

    /**
     * Registers a participant for an event
     * @param eventId The ID of the event
     * @param participantId The ID of the participant
     * @return The generated registrationId, or -1 if failed
     * @throws IllegalStateException if participant is already registered or event is full
     */
    public static int registerForEvent(int eventId, int participantId) throws IllegalStateException {
        // First check if participant is already registered
        if (isParticipantRegistered(eventId, participantId)) {
            throw new IllegalStateException("Participant is already registered for this event.");
        }

        // Check if event is full
        Connection conn = DatabaseConnection.getConnection();
        try {
            // Get event's maxParticipants
            String checkQuery = "SELECT maxParticipants FROM Event WHERE eventId = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, eventId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                Integer maxParticipants = (Integer) rs.getObject("maxParticipants");
                
                // Only check capacity if maxParticipants is not NULL (unlimited)
                if (maxParticipants != null) {
                    // Count current registrations with status='registered'
                    String countQuery = "SELECT COUNT(*) as count FROM EventRegistration " +
                                       "WHERE eventId = ? AND status = 'registered'";
                    PreparedStatement countStmt = conn.prepareStatement(countQuery);
                    countStmt.setInt(1, eventId);
                    ResultSet countRs = countStmt.executeQuery();
                    
                    if (countRs.next()) {
                        int currentCount = countRs.getInt("count");
                        if (currentCount >= maxParticipants) {
                            DatabaseConnection.closeConnection(conn);
                            throw new IllegalStateException("Event is full. Maximum participants reached.");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            DatabaseConnection.closeConnection(conn);
            e.printStackTrace();
            return -1;
        }

        int generatedId = -1;

        try {
            String query = "INSERT INTO EventRegistration (eventId, participantId, registrationDate, status) " +
                          "VALUES (?, ?, NOW(), 'registered')";
            
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, eventId);
            stmt.setInt(2, participantId);
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                generatedId = generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return generatedId;
    }

    /**
     * Cancels a registration by updating status to 'cancelled'
     * @param registrationId The ID of the registration to cancel
     * @return true if successful, false otherwise
     */
    public static boolean cancelRegistration(int registrationId) {
        Connection conn = DatabaseConnection.getConnection();
        boolean success = false;

        try {
            String query = "UPDATE EventRegistration SET status = 'cancelled' WHERE registrationId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, registrationId);
            
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
     * Retrieves all registrations for a specific participant
     * @param participantId The ID of the participant
     * @return List of EventRegistration objects with event details
     */
    public static List<EventRegistration> getRegistrationsByParticipant(int participantId) {
        List<EventRegistration> registrations = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "SELECT er.*, e.eventTitle, e.eventDate, h.hallName " +
                          "FROM EventRegistration er " +
                          "INNER JOIN Event e ON er.eventId = e.eventId " +
                          "LEFT JOIN Hall h ON e.hallId = h.hallId " +
                          "WHERE er.participantId = ? " +
                          "ORDER BY er.registrationDate DESC";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, participantId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                EventRegistration registration = mapResultSetToRegistration(rs);
                registrations.add(registration);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return registrations;
    }

    /**
     * Retrieves all registrations for a specific event
     * @param eventId The ID of the event
     * @return List of EventRegistration objects with participant details
     */
    public static List<EventRegistration> getRegistrationsByEvent(int eventId) {
        List<EventRegistration> registrations = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "SELECT er.*, p.participantName, e.eventTitle, e.eventDate, h.hallName " +
                          "FROM EventRegistration er " +
                          "INNER JOIN Participant p ON er.participantId = p.participantId " +
                          "INNER JOIN Event e ON er.eventId = e.eventId " +
                          "LEFT JOIN Hall h ON e.hallId = h.hallId " +
                          "WHERE er.eventId = ? " +
                          "ORDER BY er.registrationDate ASC";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                EventRegistration registration = mapResultSetToRegistration(rs);
                registrations.add(registration);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return registrations;
    }

    /**
     * Checks if a participant is already registered for an event
     * @param eventId The ID of the event
     * @param participantId The ID of the participant
     * @return true if already registered (with status 'registered'), false otherwise
     */
    public static boolean isParticipantRegistered(int eventId, int participantId) {
        Connection conn = DatabaseConnection.getConnection();
        boolean isRegistered = false;

        try {
            String query = "SELECT COUNT(*) as count FROM EventRegistration " +
                          "WHERE eventId = ? AND participantId = ? AND status = 'registered'";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, eventId);
            stmt.setInt(2, participantId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                isRegistered = (rs.getInt("count") > 0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return isRegistered;
    }

    /**
     * Gets the count of active registrations for an event
     * @param eventId The ID of the event
     * @return Count of registrations with status 'registered'
     */
    public static int getRegistrationCount(int eventId) {
        Connection conn = DatabaseConnection.getConnection();
        int count = 0;

        try {
            String query = "SELECT COUNT(*) as count FROM EventRegistration " +
                          "WHERE eventId = ? AND status = 'registered'";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, eventId);
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
     * Retrieves a specific registration by ID
     * @param registrationId The ID of the registration
     * @return EventRegistration object or null if not found
     */
    public static EventRegistration getRegistrationById(int registrationId) {
        EventRegistration registration = null;
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "SELECT er.*, p.participantName, e.eventTitle, e.eventDate, h.hallName " +
                          "FROM EventRegistration er " +
                          "INNER JOIN Participant p ON er.participantId = p.participantId " +
                          "INNER JOIN Event e ON er.eventId = e.eventId " +
                          "LEFT JOIN Hall h ON e.hallId = h.hallId " +
                          "WHERE er.registrationId = ?";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, registrationId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                registration = mapResultSetToRegistration(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return registration;
    }

    /**
     * Gets all active registrations (status = 'registered')
     * @return List of all active EventRegistration objects
     */
    public static List<EventRegistration> getAllActiveRegistrations() {
        List<EventRegistration> registrations = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "SELECT er.*, p.participantName, e.eventTitle, e.eventDate, h.hallName " +
                          "FROM EventRegistration er " +
                          "INNER JOIN Participant p ON er.participantId = p.participantId " +
                          "INNER JOIN Event e ON er.eventId = e.eventId " +
                          "LEFT JOIN Hall h ON e.hallId = h.hallId " +
                          "WHERE er.status = 'registered' " +
                          "ORDER BY er.registrationDate DESC";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                EventRegistration registration = mapResultSetToRegistration(rs);
                registrations.add(registration);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return registrations;
    }

    /**
     * Helper method to map ResultSet to EventRegistration object
     * @param rs ResultSet from database query
     * @return EventRegistration object
     * @throws SQLException if database access error occurs
     */
    private static EventRegistration mapResultSetToRegistration(ResultSet rs) throws SQLException {
        EventRegistration registration = new EventRegistration();
        registration.setRegistrationId(rs.getInt("registrationId"));
        registration.setEventId(rs.getInt("eventId"));
        registration.setParticipantId(rs.getInt("participantId"));
        registration.setRegistrationDate(rs.getTimestamp("registrationDate"));
        registration.setStatus(rs.getString("status"));
        
        // Set display fields from JOIN
        try {
            registration.setEventTitle(rs.getString("eventTitle"));
            registration.setEventDate(rs.getDate("eventDate"));
            registration.setHallName(rs.getString("hallName"));
        } catch (SQLException e) {
            // These fields might not be present in all queries
        }
        
        try {
            registration.setParticipantName(rs.getString("participantName"));
        } catch (SQLException e) {
            // This field might not be present in all queries
        }
        
        return registration;
    }

    /**
     * Gets the remaining slots for an event
     * @param eventId The ID of the event
     * @return Number of remaining slots, or -1 if unlimited capacity
     */
    public static int getRemainingSlots(int eventId) {
        Connection conn = DatabaseConnection.getConnection();
        int remainingSlots = -1;

        try {
            // Get event's maxParticipants
            String eventQuery = "SELECT maxParticipants FROM Event WHERE eventId = ?";
            PreparedStatement eventStmt = conn.prepareStatement(eventQuery);
            eventStmt.setInt(1, eventId);
            ResultSet eventRs = eventStmt.executeQuery();
            
            if (eventRs.next()) {
                Integer maxParticipants = (Integer) eventRs.getObject("maxParticipants");
                
                // If maxParticipants is NULL, return -1 (unlimited)
                if (maxParticipants == null) {
                    remainingSlots = -1;
                } else {
                    // Count current registrations with status='registered'
                    String countQuery = "SELECT COUNT(*) as count FROM EventRegistration " +
                                       "WHERE eventId = ? AND status = 'registered'";
                    PreparedStatement countStmt = conn.prepareStatement(countQuery);
                    countStmt.setInt(1, eventId);
                    ResultSet countRs = countStmt.executeQuery();
                    
                    if (countRs.next()) {
                        int currentCount = countRs.getInt("count");
                        remainingSlots = maxParticipants - currentCount;
                        // Ensure we don't return negative values
                        if (remainingSlots < 0) {
                            remainingSlots = 0;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return remainingSlots;
    }
}
