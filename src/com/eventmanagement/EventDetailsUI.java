package com.eventmanagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Event Details UI - Dialog to show event details and registered participants
 */
public class EventDetailsUI extends JDialog implements ActionListener {
    private Event event;
    private JButton refreshButton, closeButton;
    private JTable participantsTable;
    private DefaultTableModel tableModel;
    private JLabel capacityLabel;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public EventDetailsUI(JFrame parent, Event event) {
        super(parent, "Event Details - " + event.getEventTitle(), true);
        this.event = event;
        
        setSize(900, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        initializeUI();
        loadEventDetails();
        loadParticipantsList();
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // TOP PANEL - Event Information
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Event Information"));

        JPanel infoPanel = new JPanel(new GridLayout(6, 2, 10, 8));
        
        // Event Title
        JLabel titleLabelHeader = new JLabel("Event Title:");
        titleLabelHeader.setFont(new Font("Arial", Font.BOLD, 12));
        infoPanel.add(titleLabelHeader);
        
        JLabel titleLabel = new JLabel(event.getEventTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        infoPanel.add(titleLabel);
        
        // Event Type
        JLabel typeLabelHeader = new JLabel("Event Type:");
        typeLabelHeader.setFont(new Font("Arial", Font.BOLD, 12));
        infoPanel.add(typeLabelHeader);
        
        JLabel typeLabel = new JLabel(event.getEventType() != null ? event.getEventType() : "N/A");
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoPanel.add(typeLabel);
        
        // Event Date
        JLabel dateLabelHeader = new JLabel("Event Date:");
        dateLabelHeader.setFont(new Font("Arial", Font.BOLD, 12));
        infoPanel.add(dateLabelHeader);
        
        JLabel dateLabel = new JLabel(dateFormat.format(event.getEventDate()));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoPanel.add(dateLabel);
        
        // Hall
        JLabel hallLabelHeader = new JLabel("Hall:");
        hallLabelHeader.setFont(new Font("Arial", Font.BOLD, 12));
        infoPanel.add(hallLabelHeader);
        
        JLabel hallLabel = new JLabel(event.getHallName() != null ? event.getHallName() : "N/A");
        hallLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoPanel.add(hallLabel);
        
        // Capacity
        JLabel capacityLabelHeader = new JLabel("Capacity:");
        capacityLabelHeader.setFont(new Font("Arial", Font.BOLD, 12));
        infoPanel.add(capacityLabelHeader);
        
        capacityLabel = new JLabel("Loading...");
        capacityLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoPanel.add(capacityLabel);
        
        // Description
        JLabel descLabelHeader = new JLabel("Description:");
        descLabelHeader.setFont(new Font("Arial", Font.BOLD, 12));
        infoPanel.add(descLabelHeader);
        
        JTextArea descriptionArea = new JTextArea(event.getEventDescription());
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 12));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(infoPanel.getBackground());
        descriptionArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setPreferredSize(new Dimension(0, 60));
        infoPanel.add(descScrollPane);
        
        topPanel.add(infoPanel, BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // CENTER PANEL - Participants Table
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Registered Participants"));

        // Create table
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        tableModel.addColumn("Participant ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Email");
        tableModel.addColumn("Phone");
        tableModel.addColumn("Registered Date");
        
        participantsTable = new JTable(tableModel);
        participantsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        participantsTable.setRowHeight(25);
        participantsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Hide Participant ID column
        participantsTable.getColumnModel().getColumn(0).setMinWidth(0);
        participantsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        participantsTable.getColumnModel().getColumn(0).setWidth(0);
        
        JScrollPane tableScrollPane = new JScrollPane(participantsTable);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // BOTTOM PANEL - Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 14));
        refreshButton.setBackground(new Color(0, 123, 255));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(this);
        
        closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        closeButton.setBackground(new Color(108, 117, 125));
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(this);
        
        bottomPanel.add(refreshButton);
        bottomPanel.add(closeButton);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadEventDetails() {
        // Update capacity information
        int currentCount = EventRegistrationDAO.getRegistrationCount(event.getEventId());
        String capacityText;
        
        if (event.getMaxParticipants() == null) {
            capacityText = currentCount + "/Unlimited";
        } else {
            capacityText = currentCount + "/" + event.getMaxParticipants();
        }
        
        capacityLabel.setText(capacityText);
    }

    private void loadParticipantsList() {
        // Clear existing rows
        tableModel.setRowCount(0);
        
        // Fetch registrations for this event
        List<EventRegistration> registrations = EventRegistrationDAO.getRegistrationsByEvent(event.getEventId());
        
        SimpleDateFormat regDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (EventRegistration registration : registrations) {
            // Only show active registrations
            if (!"registered".equalsIgnoreCase(registration.getStatus())) {
                continue;
            }
            
            // Get participant details
            Participant participant = ParticipantDAO.getParticipantById(registration.getParticipantId());
            
            if (participant != null) {
                String name = participant.getParticipantName() != null ? participant.getParticipantName() : "N/A";
                String email = participant.getEmail() != null ? participant.getEmail() : "N/A";
                String phone = participant.getPhoneNumber() != null ? participant.getPhoneNumber() : "N/A";
                String regDate = registration.getRegistrationDate() != null ? 
                    regDateFormat.format(registration.getRegistrationDate()) : "N/A";
                
                tableModel.addRow(new Object[]{
                    participant.getParticipantId(),
                    name,
                    email,
                    phone,
                    regDate
                });
            }
        }
        
        // Update dialog title with participant count
        int participantCount = tableModel.getRowCount();
        setTitle("Event Details - " + event.getEventTitle() + " (" + participantCount + " participants)");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == refreshButton) {
            loadEventDetails();
            loadParticipantsList();
            JOptionPane.showMessageDialog(this,
                "Participant list refreshed successfully.",
                "Refreshed",
                JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getSource() == closeButton) {
            dispose();
        }
    }

    // Test main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create a test event
            Event testEvent = new Event();
            testEvent.setEventId(1);
            testEvent.setEventTitle("Annual Tech Conference 2024");
            testEvent.setEventDescription("A comprehensive technology conference featuring industry leaders and innovative sessions.");
            testEvent.setEventType("Conference");
            testEvent.setEventDate(new java.util.Date());
            testEvent.setHallName("Grand Ballroom");
            testEvent.setMaxParticipants(100);
            
            JFrame testFrame = new JFrame("Test Frame");
            testFrame.setSize(400, 300);
            testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            testFrame.setLocationRelativeTo(null);
            testFrame.setVisible(true);
            
            EventDetailsUI dialog = new EventDetailsUI(testFrame, testEvent);
            dialog.setVisible(true);
        });
    }
}
