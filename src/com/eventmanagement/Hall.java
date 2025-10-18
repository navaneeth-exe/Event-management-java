package com.eventmanagement;

public class Hall {
    private int hallId;
    private String hallName;
    private int capacity;
    private String location;
    private boolean isAvailable;

    // Constructors
    public Hall() {
        // Default constructor
    }

    public Hall(String hallName, int capacity, String location, boolean isAvailable) {
        this.hallName = hallName;
        this.capacity = capacity;
        this.location = location;
        this.isAvailable = isAvailable;
    }

    public Hall(int hallId, String hallName, int capacity, String location, boolean isAvailable) {
        this.hallId = hallId;
        this.hallName = hallName;
        this.capacity = capacity;
        this.location = location;
        this.isAvailable = isAvailable;
    }

    // Getters and Setters
    public int getHallId() {
        return hallId;
    }

    public void setHallId(int hallId) {
        this.hallId = hallId;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    // Other methods
    @Override
    public String toString() {
        return "Hall{" +
               "hallId=" + hallId +
               ", hallName='" + hallName + '\'' +
               ", capacity=" + capacity +
               ", location='" + location + '\'' +
               ", isAvailable=" + isAvailable +
               '}';
    }
}
