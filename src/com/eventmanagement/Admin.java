package com.eventmanagement;

public class Admin extends User {
    private int adminId;
    private String adminName;

    // Constructors
    public Admin() {
        // Default constructor
    }

    public Admin(String username, String password, String email) {
        super(username, password, email, "admin"); // Set userType as "admin"
    }

    public Admin(int adminId, String adminName, String username, String password, String email) {
        this(username, password, email);
        this.adminId = adminId;
        this.adminName = adminName;
    }

    // Getters and Setters
    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    // Other methods (if needed)
    @Override
    public String toString() {
        return "Admin{" +
               "adminId=" + adminId +
               ", adminName='" + adminName + '\'' +
               ", username='" + getUsername() + '\'' +
               ", email='" + getEmail() + '\'' +
               ", userType='" + getUserType() + '\'' +
               '}';
    }
}
