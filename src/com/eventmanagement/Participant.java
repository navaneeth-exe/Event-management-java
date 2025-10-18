package com.eventmanagement;

public class Participant extends User {
    private int participantId;
    private String participantName;
    private String phoneNumber;

    // Constructors
    public Participant() {
        // Default constructor
    }

    public Participant(String username, String password, String email) {
        super(username, password, email, "participant"); // Set userType as "participant"
    }

    public Participant(int participantId, String participantName, String phoneNumber, String username, String password, String email) {
        this(username, password, email);
        this.participantId = participantId;
        this.participantName = participantName;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    public int getParticipantId() {
        return participantId;
    }

    public void setParticipantId(int participantId) {
        this.participantId = participantId;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // Other methods (if needed)
    @Override
    public String toString() {
        return "Participant{" +
               "participantId=" + participantId +
               ", participantName='" + participantName + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               ", username='" + getUsername() + '\'' +
               ", email='" + getEmail() + '\'' +
               ", userType='" + getUserType() + '\'' +
               '}';
    }
}
