package com.eventmanagement;

public class EventManagerUser extends User {
    private int eventManagerId;
    private String eventManagerName;

    // Constructors
    public EventManagerUser() {
        // Default constructor
    }

    public EventManagerUser(String username, String password, String email) {
        super(username, password, email, "event_manager"); // Set userType as "event_manager"
    }

    public EventManagerUser(int eventManagerId, String eventManagerName, String username, String password, String email) {
        this(username, password, email);
        this.eventManagerId = eventManagerId;
        this.eventManagerName = eventManagerName;
    }

    // Getters and Setters
    public int getEventManagerId() {
        return eventManagerId;
    }

    public void setEventManagerId(int eventManagerId) {
        this.eventManagerId = eventManagerId;
    }

    public String getEventManagerName() {
        return eventManagerName;
    }

    public void setEventManagerName(String eventManagerName) {
        this.eventManagerName = eventManagerName;
    }

    // Other methods (if needed)
    @Override
    public String toString() {
        return "EventManagerUser{" +
               "eventManagerId=" + eventManagerId +
               ", eventManagerName='" + eventManagerName + '\'' +
               ", username='" + getUsername() + '\'' + // Access inherited attributes
               ", email='" + getEmail() + '\'' +
               ", userType='" + getUserType() + '\'' + 
               '}';
    }
}
