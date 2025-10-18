package com.eventmanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HallDAO {
    
    /**
     * Retrieves all halls from the database
     * @return List of all Hall objects
     */
    public static List<Hall> getAllHalls() {
        List<Hall> halls = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "SELECT * FROM Hall ORDER BY hallName";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Hall hall = new Hall();
                hall.setHallId(rs.getInt("hallId"));
                hall.setHallName(rs.getString("hallName"));
                hall.setCapacity(rs.getInt("capacity"));
                hall.setLocation(rs.getString("location"));
                hall.setAvailable(rs.getBoolean("isAvailable"));
                halls.add(hall);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return halls;
    }

    /**
     * Retrieves halls that are not booked on a specific date
     * @param eventDate The date to check for availability
     * @return List of available Hall objects
     */
    public static List<Hall> getAvailableHalls(Date eventDate) {
        List<Hall> availableHalls = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();

        try {
            // Query to get halls that don't have approved events on the given date
            String query = "SELECT h.* FROM Hall h " +
                          "WHERE h.isAvailable = true " +
                          "AND h.hallId NOT IN (" +
                          "    SELECT e.hallId FROM Event e " +
                          "    WHERE e.eventDate = ? " +
                          "    AND e.eventStatus = 'Approved'" +
                          ") " +
                          "ORDER BY h.hallName";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDate(1, new java.sql.Date(eventDate.getTime()));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Hall hall = new Hall();
                hall.setHallId(rs.getInt("hallId"));
                hall.setHallName(rs.getString("hallName"));
                hall.setCapacity(rs.getInt("capacity"));
                hall.setLocation(rs.getString("location"));
                hall.setAvailable(rs.getBoolean("isAvailable"));
                availableHalls.add(hall);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return availableHalls;
    }

    /**
     * Checks if a specific hall is available on a given date
     * @param hallId The ID of the hall to check
     * @param eventDate The date to check for availability
     * @return true if the hall is available, false otherwise
     */
    public static boolean isHallAvailable(int hallId, Date eventDate) {
        Connection conn = DatabaseConnection.getConnection();
        boolean available = false;

        try {
            // First check if the hall exists and is marked as available
            String hallQuery = "SELECT isAvailable FROM Hall WHERE hallId = ?";
            PreparedStatement hallStmt = conn.prepareStatement(hallQuery);
            hallStmt.setInt(1, hallId);
            ResultSet hallRs = hallStmt.executeQuery();

            if (hallRs.next()) {
                boolean hallIsAvailable = hallRs.getBoolean("isAvailable");
                
                if (!hallIsAvailable) {
                    return false; // Hall is marked as unavailable
                }

                // Check if there's an approved event on this date for this hall
                String eventQuery = "SELECT COUNT(*) as eventCount FROM Event " +
                                   "WHERE hallId = ? AND eventDate = ? AND eventStatus = 'Approved'";
                PreparedStatement eventStmt = conn.prepareStatement(eventQuery);
                eventStmt.setInt(1, hallId);
                eventStmt.setDate(2, new java.sql.Date(eventDate.getTime()));
                ResultSet eventRs = eventStmt.executeQuery();

                if (eventRs.next()) {
                    int eventCount = eventRs.getInt("eventCount");
                    available = (eventCount == 0); // Available if no approved events exist
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return available;
    }

    /**
     * Retrieves a specific hall by its ID
     * @param hallId The ID of the hall to retrieve
     * @return Hall object or null if not found
     */
    public static Hall getHallById(int hallId) {
        Hall hall = null;
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "SELECT * FROM Hall WHERE hallId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, hallId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                hall = new Hall();
                hall.setHallId(rs.getInt("hallId"));
                hall.setHallName(rs.getString("hallName"));
                hall.setCapacity(rs.getInt("capacity"));
                hall.setLocation(rs.getString("location"));
                hall.setAvailable(rs.getBoolean("isAvailable"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return hall;
    }

    /**
     * Retrieves the name of a hall by its ID
     * @param hallId The ID of the hall
     * @return Hall name or null if not found
     */
    public static String getHallName(int hallId) {
        String hallName = null;
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "SELECT hallName FROM Hall WHERE hallId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, hallId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                hallName = rs.getString("hallName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return hallName;
    }

    /**
     * Adds a new hall to the database
     * @param hall The Hall object to add
     * @return The generated hallId, or -1 if failed
     */
    public static int addHall(Hall hall) {
        int generatedId = -1;
        Connection conn = DatabaseConnection.getConnection();

        try {
            String query = "INSERT INTO Hall (hallName, capacity, location, isAvailable) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, hall.getHallName());
            stmt.setInt(2, hall.getCapacity());
            stmt.setString(3, hall.getLocation());
            stmt.setBoolean(4, hall.isAvailable());
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
     * Updates an existing hall in the database
     * @param hall The Hall object with updated information
     * @return true if update was successful, false otherwise
     */
    public static boolean updateHall(Hall hall) {
        Connection conn = DatabaseConnection.getConnection();
        boolean success = false;

        try {
            String query = "UPDATE Hall SET hallName = ?, capacity = ?, location = ?, isAvailable = ? WHERE hallId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, hall.getHallName());
            stmt.setInt(2, hall.getCapacity());
            stmt.setString(3, hall.getLocation());
            stmt.setBoolean(4, hall.isAvailable());
            stmt.setInt(5, hall.getHallId());
            
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
     * Deletes a hall from the database
     * @param hallId The ID of the hall to delete
     * @return true if deletion was successful, false otherwise
     */
    public static boolean deleteHall(int hallId) {
        Connection conn = DatabaseConnection.getConnection();
        boolean success = false;

        try {
            String query = "DELETE FROM Hall WHERE hallId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, hallId);
            
            int rowsAffected = stmt.executeUpdate();
            success = (rowsAffected > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }

        return success;
    }
}
