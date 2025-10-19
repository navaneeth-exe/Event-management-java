package com.eventmanagement;

import java.util.Date;

public class Event {
    private int eventId;
    private String eventTitle;
    private String eventDescription;
    private String eventType; // Technical, Cultural, Seminars, Sports, Talk Sessions, Other
    private Date eventDate;
    private String eventStatus; // e.g., "pending", "approved", "cancelled" (legacy field)
    private int managerId; // Event manager who created the event
    private int hallId; // Hall where the event will be held
    private String hallName; // Hall name for display (not stored in DB)
    private String approvalStatus; // pending, approved, rejected
    private Integer approvedBy; // userId of admin who approved/rejected
    private Date approvalDate; // Timestamp of approval/rejection
    private Integer maxParticipants; // Maximum participants allowed (NULL = unlimited) 

    // Constructors
    public Event() {
        // Default constructor
    }

    public Event(String eventTitle, String eventDescription, String eventType, Date eventDate, int managerId, int hallId) {
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.managerId = managerId;
        this.hallId = hallId;
        this.approvalStatus = "PENDING"; // Default status
        this.eventStatus = "SCHEDULED"; // Legacy field
    }

    // Getters and Setters
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

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

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Integer getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Integer approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    // Other methods (if needed)
    @Override
    public String toString() {
        return "Event{" +
               "eventId=" + eventId +
               ", eventTitle='" + eventTitle + '\'' +
               ", eventDescription='" + eventDescription + '\'' +
               ", eventType='" + eventType + '\'' +
               ", eventDate=" + eventDate +
               ", eventStatus='" + eventStatus + '\'' +
               ", managerId=" + managerId +
               ", hallId=" + hallId +
               ", hallName='" + hallName + '\'' +
               ", approvalStatus='" + approvalStatus + '\'' +
               ", approvedBy=" + approvedBy +
               ", approvalDate=" + approvalDate +
               ", maxParticipants=" + maxParticipants +
               '}';
    }
}