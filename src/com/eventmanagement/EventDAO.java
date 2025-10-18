package com.eventmanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventDAO {

    /**
     * Creates a new event after checking hall availability
     * @param event The Event object to create
     * @param managerId The ID of the event manager creating the event
     * @return The generated eventId, or -1 if failed
     * @throws IllegalStateException if hall is not available
     */
    public static int createEvent(Event event, int managerId) throws IllegalStateException {
        int generatedId = -1;
        
        // First, check if the hall is available on the requested date
        if (!HallDAO.isHallAvailable(event.getHallId(), event.getEventDate())) {
            throw new IllegalStateException("Hall is not available on the selected date. Please choose another hall or date.");
        }
        
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "INSERT INTO Event (eventTitle, eventDescription, eventType, eventDate, " +
                          "eventStatus, managerId, hallId, approvalStatus, maxParticipants) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, event.getEventTitle());
            stmt.setString(2, event.getEventDescription());
            stmt.setString(3, event.getEventType());
            stmt.setDate(4, new java.sql.Date(event.getEventDate().getTime()));
            stmt.setString(5, "SCHEDULED"); // Default event status
            stmt.setInt(6, managerId);
            stmt.setInt(7, event.getHallId());
            stmt.setString(8, "PENDING"); // Default approval status
            
            // Handle maxParticipants (can be null for unlimited)
            if (event.getMaxParticipants() != null) {
                stmt.setInt(9, event.getMaxParticipants());
            } else {
                stmt.setNull(9, java.sql.Types.INTEGER);
            }
            
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
     * Retrieves all events created by a specific manager
     * @param managerId The ID of the event manager
     * @return List of Event objects
     */
    public static List<Event> getEventsByManagerId(int managerId) {
        List<Event> events = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "SELECT e.*, h.hallName " +
                          "FROM Event e " +
                          "LEFT JOIN Hall h ON e.hallId = h.hallId " +
                          "WHERE e.managerId = ? " +
                          "ORDER BY e.eventDate DESC";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, managerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Event event = mapResultSetToEvent(rs);
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return events;
    }

    /**
     * Retrieves all events with JOIN to Hall table
     * @return List of all Event objects with hall names populated
     */
    public static List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "SELECT e.*, h.hallName " +
                          "FROM Event e " +
                          "LEFT JOIN Hall h ON e.hallId = h.hallId " +
                          "ORDER BY e.eventDate DESC";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Event event = mapResultSetToEvent(rs);
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return events;
    }

    /**
     * Retrieves all events with pending approval status
     * @return List of pending Event objects
     */
    public static List<Event> getAllPendingEvents() {
        List<Event> events = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "SELECT e.*, h.hallName " +
                          "FROM Event e " +
                          "LEFT JOIN Hall h ON e.hallId = h.hallId " +
                          "WHERE e.approvalStatus = 'PENDING' " +
                          "ORDER BY e.eventDate ASC";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Event event = mapResultSetToEvent(rs);
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return events;
    }

    /**
     * Retrieves all events with approved status
     * @return List of approved Event objects
     */
    public static List<Event> getAllApprovedEvents() {
        List<Event> events = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "SELECT e.*, h.hallName " +
                          "FROM Event e " +
                          "LEFT JOIN Hall h ON e.hallId = h.hallId " +
                          "WHERE e.approvalStatus = 'APPROVED' " +
                          "ORDER BY e.eventDate ASC";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Event event = mapResultSetToEvent(rs);
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return events;
    }

    /**
     * Retrieves all approved events on a specific date
     * @param date The date to search for
     * @return List of Event objects on that date
     */
    public static List<Event> getEventsByDate(Date date) {
        List<Event> events = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "SELECT e.*, h.hallName " +
                          "FROM Event e " +
                          "LEFT JOIN Hall h ON e.hallId = h.hallId " +
                          "WHERE e.eventDate = ? AND e.approvalStatus = 'APPROVED' " +
                          "ORDER BY e.eventTitle";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDate(1, new java.sql.Date(date.getTime()));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Event event = mapResultSetToEvent(rs);
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return events;
    }

    /**
     * Approves an event
     * @param eventId The ID of the event to approve
     * @param adminUserId The userId of the admin approving the event
     * @return true if successful, false otherwise
     */
    public static boolean approveEvent(int eventId, int adminUserId) {
        Connection conn = DatabaseConnection.getConnection();
        boolean success = false;

        try {
            String query = "UPDATE Event SET approvalStatus = 'APPROVED', eventStatus = 'SCHEDULED', " +
                          "approvedBy = ?, approvalDate = NOW() WHERE eventId = ?";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, adminUserId);
            stmt.setInt(2, eventId);
            
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
     * Rejects an event
     * @param eventId The ID of the event to reject
     * @param adminUserId The userId of the admin rejecting the event
     * @return true if successful, false otherwise
     */
    public static boolean rejectEvent(int eventId, int adminUserId) {
        Connection conn = DatabaseConnection.getConnection();
        boolean success = false;

        try {
            String query = "UPDATE Event SET approvalStatus = 'REJECTED', eventStatus = 'CANCELLED', " +
                          "approvedBy = ?, approvalDate = NOW() WHERE eventId = ?";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, adminUserId);
            stmt.setInt(2, eventId);
            
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
     * Updates an existing event
     * @param event The Event object with updated information
     * @return true if successful, false otherwise
     */
    public static boolean updateEvent(Event event) {
        Connection conn = DatabaseConnection.getConnection();
        boolean success = false;

        try {
            String query = "UPDATE Event SET eventTitle = ?, eventDescription = ?, eventType = ?, " +
                          "eventDate = ?, eventStatus = ?, hallId = ? WHERE eventId = ?";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, event.getEventTitle());
            stmt.setString(2, event.getEventDescription());
            stmt.setString(3, event.getEventType());
            stmt.setDate(4, new java.sql.Date(event.getEventDate().getTime()));
            stmt.setString(5, event.getEventStatus());
            stmt.setInt(6, event.getHallId());
            stmt.setInt(7, event.getEventId());
            
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
     * Deletes an event
     * @param eventId The ID of the event to delete
     * @return true if successful, false otherwise
     */
    public static boolean deleteEvent(int eventId) {
        Connection conn = DatabaseConnection.getConnection();
        boolean success = false;

        try {
            String query = "DELETE FROM Event WHERE eventId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, eventId);
            
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
     * Retrieves a specific event by ID
     * @param eventId The ID of the event
     * @return Event object or null if not found
     */
    public static Event getEventById(int eventId) {
        Event event = null;
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "SELECT e.*, h.hallName " +
                          "FROM Event e " +
                          "LEFT JOIN Hall h ON e.hallId = h.hallId " +
                          "WHERE e.eventId = ?";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                event = mapResultSetToEvent(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return event;
    }

    /**
     * Helper method to map ResultSet to Event object
     * @param rs ResultSet from database query
     * @return Event object
     * @throws SQLException if database access error occurs
     */
    private static Event mapResultSetToEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setEventId(rs.getInt("eventId"));
        event.setEventTitle(rs.getString("eventTitle"));
        event.setEventDescription(rs.getString("eventDescription"));
        event.setEventType(rs.getString("eventType"));
        event.setEventDate(rs.getDate("eventDate"));
        event.setEventStatus(rs.getString("eventStatus"));
        event.setManagerId(rs.getInt("managerId"));
        event.setHallId(rs.getInt("hallId"));
        event.setHallName(rs.getString("hallName")); // From JOIN
        event.setApprovalStatus(rs.getString("approvalStatus"));
        
        // Handle nullable fields
        Integer approvedBy = (Integer) rs.getObject("approvedBy");
        event.setApprovedBy(approvedBy);
        
        java.sql.Timestamp approvalTimestamp = rs.getTimestamp("approvalDate");
        if (approvalTimestamp != null) {
            event.setApprovalDate(new Date(approvalTimestamp.getTime()));
        }
        
        // Handle maxParticipants (can be null for unlimited)
        Integer maxParticipants = (Integer) rs.getObject("maxParticipants");
        event.setMaxParticipants(maxParticipants);
        
        return event;
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
            String query = "SELECT e.maxParticipants, COUNT(er.registrationId) as currentRegistrations " +
                          "FROM Event e " +
                          "LEFT JOIN EventRegistration er ON e.eventId = er.eventId " +
                          "AND er.status != 'CANCELLED' " +
                          "WHERE e.eventId = ? " +
                          "GROUP BY e.eventId, e.maxParticipants";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Integer maxParticipants = (Integer) rs.getObject("maxParticipants");
                
                // If maxParticipants is null, return -1 (unlimited)
                if (maxParticipants == null) {
                    remainingSlots = -1;
                } else {
                    int currentRegistrations = rs.getInt("currentRegistrations");
                    remainingSlots = maxParticipants - currentRegistrations;
                    // Ensure we don't return negative values
                    if (remainingSlots < 0) {
                        remainingSlots = 0;
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

    /**
     * Checks if an event is full (at maximum capacity)
     * @param eventId The ID of the event
     * @return true if event is full, false if slots available or unlimited
     */
    public static boolean isEventFull(int eventId) {
        int remainingSlots = getRemainingSlots(eventId);
        
        // -1 means unlimited capacity, so event is never full
        if (remainingSlots == -1) {
            return false;
        }
        
        // Event is full if remaining slots is 0
        return remainingSlots == 0;
    }
}
