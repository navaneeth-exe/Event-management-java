package com.eventmanagement;

import java.util.Date;

public class EventRegistration {
    private int registrationId;
    private int eventId;
    private int participantId;
    private Date registrationDate;
    private String status; // REGISTERED, CANCELLED, ATTENDED

    // For display purposes (not stored in DB)
    private String eventTitle;
    private String participantName;
    private String hallName;
    private Date eventDate;

    // Constructors
    public EventRegistration() {
        // Default constructor
    }

    public EventRegistration(int eventId, int participantId) {
        this.eventId = eventId;
        this.participantId = participantId;
        this.status = "REGISTERED";
        this.registrationDate = new Date();
    }

    public EventRegistration(int registrationId, int eventId, int participantId, Date registrationDate, String status) {
        this.registrationId = registrationId;
        this.eventId = eventId;
        this.participantId = participantId;
        this.registrationDate = registrationDate;
        this.status = status;
    }

    // Getters and Setters
    public int getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(int registrationId) {
        this.registrationId = registrationId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getParticipantId() {
        return participantId;
    }

    public void setParticipantId(int participantId) {
        this.participantId = participantId;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Display fields
    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    // Other methods
    @Override
    public String toString() {
        return "EventRegistration{" +
               "registrationId=" + registrationId +
               ", eventId=" + eventId +
               ", participantId=" + participantId +
               ", registrationDate=" + registrationDate +
               ", status='" + status + '\'' +
               ", eventTitle='" + eventTitle + '\'' +
               ", participantName='" + participantName + '\'' +
               '}';
    }
}
