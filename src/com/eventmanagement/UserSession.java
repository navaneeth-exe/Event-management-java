package com.eventmanagement;

/**
 * Singleton class to manage user session data throughout the application
 * Stores information about the currently logged-in user
 */
public class UserSession {
    private static UserSession instance;
    
    private User currentUser;
    private int roleSpecificId; // eventManagerId, adminId, or participantId
    private String roleName; // For display purposes
    
    // Private constructor to prevent instantiation
    private UserSession() {
    }
    
    /**
     * Gets the singleton instance of UserSession
     * @return The UserSession instance
     */
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }
    
    /**
     * Initializes the session with user data after successful login
     * @param user The authenticated User object
     * @param roleSpecificId The role-specific ID (eventManagerId, adminId, or participantId)
     */
    public void login(User user, int roleSpecificId) {
        this.currentUser = user;
        this.roleSpecificId = roleSpecificId;
        
        // Set role name for display
        if (user instanceof EventManagerUser) {
            this.roleName = "Event Manager";
        } else if (user instanceof Admin) {
            this.roleName = "Administrator";
        } else if (user instanceof Participant) {
            this.roleName = "Participant";
        }
        
        System.out.println("User session started: " + user.getUsername() + " (" + roleName + ")");
    }
    
    /**
     * Clears the session data (logout)
     */
    public void logout() {
        System.out.println("User session ended: " + (currentUser != null ? currentUser.getUsername() : "Unknown"));
        this.currentUser = null;
        this.roleSpecificId = 0;
        this.roleName = null;
    }
    
    /**
     * Checks if a user is currently logged in
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Gets the current logged-in user
     * @return User object or null if not logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Gets the role-specific ID (eventManagerId, adminId, or participantId)
     * @return The role-specific ID
     */
    public int getRoleSpecificId() {
        return roleSpecificId;
    }
    
    /**
     * Gets the user ID from the User table
     * @return userId or 0 if not logged in
     */
    public int getUserId() {
        return currentUser != null ? currentUser.getUserId() : 0;
    }
    
    /**
     * Gets the username
     * @return username or null if not logged in
     */
    public String getUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }
    
    /**
     * Gets the user's email
     * @return email or null if not logged in
     */
    public String getEmail() {
        return currentUser != null ? currentUser.getEmail() : null;
    }
    
    /**
     * Gets the user type (admin, event_manager, participant)
     * @return userType or null if not logged in
     */
    public String getUserType() {
        return currentUser != null ? currentUser.getUserType() : null;
    }
    
    /**
     * Gets the role name for display
     * @return Role name or null if not logged in
     */
    public String getRoleName() {
        return roleName;
    }
    
    /**
     * Checks if current user is an admin
     * @return true if admin, false otherwise
     */
    public boolean isAdmin() {
        return currentUser instanceof Admin;
    }
    
    /**
     * Checks if current user is an event manager
     * @return true if event manager, false otherwise
     */
    public boolean isEventManager() {
        return currentUser instanceof EventManagerUser;
    }
    
    /**
     * Checks if current user is a participant
     * @return true if participant, false otherwise
     */
    public boolean isParticipant() {
        return currentUser instanceof Participant;
    }
    
    /**
     * Gets the current user as EventManagerUser (with type checking)
     * @return EventManagerUser or null if not an event manager
     */
    public EventManagerUser getAsEventManager() {
        return currentUser instanceof EventManagerUser ? (EventManagerUser) currentUser : null;
    }
    
    /**
     * Gets the current user as Admin (with type checking)
     * @return Admin or null if not an admin
     */
    public Admin getAsAdmin() {
        return currentUser instanceof Admin ? (Admin) currentUser : null;
    }
    
    /**
     * Gets the current user as Participant (with type checking)
     * @return Participant or null if not a participant
     */
    public Participant getAsParticipant() {
        return currentUser instanceof Participant ? (Participant) currentUser : null;
    }
    
    @Override
    public String toString() {
        if (!isLoggedIn()) {
            return "UserSession{No user logged in}";
        }
        return "UserSession{" +
               "username='" + getUsername() + '\'' +
               ", userType='" + getUserType() + '\'' +
               ", roleName='" + roleName + '\'' +
               ", roleSpecificId=" + roleSpecificId +
               '}';
    }
}
