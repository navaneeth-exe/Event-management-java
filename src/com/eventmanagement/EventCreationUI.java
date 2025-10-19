package com.eventmanagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Event Creation UI for Event Managers
 * Allows creating events with hall selection and availability checking
 */
public class EventCreationUI extends JFrame implements ActionListener {
    private EventManagerUser eventManager;
    private int eventManagerId;
    
    // Form fields
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JSpinner dateSpinner;
    private JComboBox<String> eventTypeCombo;
    private JComboBox<HallItem> hallCombo;
    private JButton submitButton, cancelButton, checkAvailabilityButton;
    private JLabel hallStatusLabel;
    private JSpinner maxParticipantsSpinner;
    private JCheckBox unlimitedCheckBox;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Date selectedDate;
    
    public EventCreationUI(EventManagerUser eventManager, int eventManagerId) {
        this.eventManager = eventManager;
        this.eventManagerId = eventManagerId;
        
        setTitle("Create New Event");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 720);
        setLocationRelativeTo(null);
        
        initializeUI();
    }
    
    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Create New Event");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Event Title
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Event Title:*"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        titleField = new JTextField(30);
        formPanel.add(titleField, gbc);
        
        row++;
        
        // Event Description
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Description:*"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descScrollPane, gbc);
        
        row++;
        
        // Event Type
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Event Type:*"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        String[] eventTypes = {"Technical", "Cultural", "Seminars", "Sports", "Talk Sessions", "Other"};
        eventTypeCombo = new JComboBox<>(eventTypes);
        formPanel.add(eventTypeCombo, gbc);
        
        row++;
        
        // Event Date
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Event Date:*"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        
        // Create date spinner with tomorrow as minimum date
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        Date minDate = tomorrow.getTime();
        
        Calendar maxCal = Calendar.getInstance();
        maxCal.add(Calendar.YEAR, 2);
        Date maxDate = maxCal.getTime();
        
        SpinnerDateModel dateModel = new SpinnerDateModel(minDate, minDate, maxDate, Calendar.DAY_OF_MONTH);
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.addChangeListener(e -> onDateChanged());
        
        formPanel.add(dateSpinner, gbc);
        
        row++;
        
        // Hall Selection
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Select Hall:*"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JPanel hallPanel = new JPanel(new BorderLayout(5, 0));
        hallCombo = new JComboBox<>();
        hallPanel.add(hallCombo, BorderLayout.CENTER);
        
        checkAvailabilityButton = new JButton("Check Availability");
        checkAvailabilityButton.setFont(new Font("Arial", Font.PLAIN, 11));
        checkAvailabilityButton.addActionListener(this);
        hallPanel.add(checkAvailabilityButton, BorderLayout.EAST);
        
        formPanel.add(hallPanel, gbc);
        
        row++;
        
        // Hall Status Label
        gbc.gridx = 1;
        gbc.gridy = row;
        hallStatusLabel = new JLabel(" ");
        hallStatusLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        formPanel.add(hallStatusLabel, gbc);
        
        row++;
        
        // Max Participants
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Max Participants:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        SpinnerNumberModel participantsModel = new SpinnerNumberModel(50, 10, 1000, 10);
        maxParticipantsSpinner = new JSpinner(participantsModel);
        formPanel.add(maxParticipantsSpinner, gbc);
        
        row++;
        
        // Unlimited Checkbox
        gbc.gridx = 1;
        gbc.gridy = row;
        unlimitedCheckBox = new JCheckBox("Unlimited Participants");
        unlimitedCheckBox.addItemListener(e -> {
            if (unlimitedCheckBox.isSelected()) {
                maxParticipantsSpinner.setEnabled(false);
            } else {
                maxParticipantsSpinner.setEnabled(true);
            }
        });
        formPanel.add(unlimitedCheckBox, gbc);
        
        row++;
        
        // Info Label
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("<html><i>* Required fields<br>Note: Event will be submitted for admin approval</i></html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        infoLabel.setForeground(Color.GRAY);
        formPanel.add(infoLabel, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        submitButton = new JButton("Create Event");
        submitButton.setBackground(new Color(40, 167, 69));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitButton.addActionListener(this);
        
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 14));
        cancelButton.addActionListener(this);
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(submitButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Load available halls for tomorrow (default date)
        loadAvailableHalls();
    }
    
    private void onDateChanged() {
        selectedDate = (Date) dateSpinner.getValue();
        loadAvailableHalls();
    }
    
    private void loadAvailableHalls() {
        selectedDate = (Date) dateSpinner.getValue();
        hallCombo.removeAllItems();
        hallStatusLabel.setText("Loading available halls...");
        hallStatusLabel.setForeground(Color.BLUE);
        
        try {
            List<Hall> availableHalls = HallDAO.getAvailableHalls(selectedDate);
            
            if (availableHalls.isEmpty()) {
                hallStatusLabel.setText("⚠ No halls available for this date");
                hallStatusLabel.setForeground(Color.RED);
                hallCombo.addItem(new HallItem(null, "No halls available"));
                hallCombo.setEnabled(false);
            } else {
                for (Hall hall : availableHalls) {
                    hallCombo.addItem(new HallItem(hall, 
                        hall.getHallName() + " (Capacity: " + hall.getCapacity() + ")"));
                }
                hallStatusLabel.setText("✓ " + availableHalls.size() + " hall(s) available");
                hallStatusLabel.setForeground(new Color(40, 167, 69));
                hallCombo.setEnabled(true);
            }
        } catch (Exception ex) {
            hallStatusLabel.setText("Error loading halls");
            hallStatusLabel.setForeground(Color.RED);
            ex.printStackTrace();
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            createEvent();
        } else if (e.getSource() == cancelButton) {
            closeWindow();
        } else if (e.getSource() == checkAvailabilityButton) {
            loadAvailableHalls();
        }
    }
    
    private void createEvent() {
        // Validate all fields
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String eventType = (String) eventTypeCombo.getSelectedItem();
        Date eventDate = (Date) dateSpinner.getValue();
        HallItem selectedHallItem = (HallItem) hallCombo.getSelectedItem();
        
        // Validation: Check required fields
        if (title.isEmpty()) {
            showError("Event title is required");
            titleField.requestFocus();
            return;
        }
        
        if (description.isEmpty()) {
            showError("Event description is required");
            descriptionArea.requestFocus();
            return;
        }
        
        if (selectedHallItem == null || selectedHallItem.getHall() == null) {
            showError("Please select a hall");
            return;
        }
        
        Hall selectedHall = selectedHallItem.getHall();
        
        // Validation: Check if date is in the future
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        
        Calendar eventCal = Calendar.getInstance();
        eventCal.setTime(eventDate);
        eventCal.set(Calendar.HOUR_OF_DAY, 0);
        eventCal.set(Calendar.MINUTE, 0);
        eventCal.set(Calendar.SECOND, 0);
        eventCal.set(Calendar.MILLISECOND, 0);
        
        if (!eventCal.after(today)) {
            showError("Event date must be in the future");
            return;
        }
        
        // Validation: Check if maxParticipants exceeds hall capacity
        if (!unlimitedCheckBox.isSelected()) {
            int maxParticipants = (Integer) maxParticipantsSpinner.getValue();
            int hallCapacity = selectedHall.getCapacity();
            
            if (maxParticipants > hallCapacity) {
                showError("Maximum participants (" + maxParticipants + ") cannot exceed hall capacity (" + hallCapacity + ")");
                maxParticipantsSpinner.requestFocus();
                return;
            }
        }
        
        // Double-check hall availability
        if (!HallDAO.isHallAvailable(selectedHall.getHallId(), eventDate)) {
            showError("This hall is already booked for the selected date.\nPlease select another hall or date.");
            loadAvailableHalls(); // Refresh the list
            return;
        }
        
        // Create event object
        Event newEvent = new Event();
        newEvent.setEventTitle(title);
        newEvent.setEventDescription(description);
        newEvent.setEventType(eventType);
        newEvent.setEventDate(eventDate);
        newEvent.setHallId(selectedHall.getHallId());
        newEvent.setManagerId(eventManagerId);
        newEvent.setApprovalStatus("PENDING");
        newEvent.setEventStatus("SCHEDULED");
        
        // Set max participants
        if (unlimitedCheckBox.isSelected()) {
            newEvent.setMaxParticipants(null); // Unlimited
        } else {
            newEvent.setMaxParticipants((Integer) maxParticipantsSpinner.getValue());
        }
        
        try {
            // Create event using EventDAO
            int eventId = EventDAO.createEvent(newEvent, eventManagerId);
            
            if (eventId > 0) {
                JOptionPane.showMessageDialog(this,
                    "Event created successfully!\n\n" +
                    "Event ID: " + eventId + "\n" +
                    "Title: " + title + "\n" +
                    "Date: " + dateFormat.format(eventDate) + "\n" +
                    "Hall: " + selectedHall.getHallName() + "\n\n" +
                    "Status: Waiting for admin approval",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Close this window and return to dashboard
                closeWindow();
            } else {
                showError("Failed to create event. Please try again.");
            }
        } catch (IllegalStateException ex) {
            // Hall not available (caught by EventDAO)
            showError(ex.getMessage());
            loadAvailableHalls(); // Refresh the list
        } catch (Exception ex) {
            showError("An error occurred: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Validation Error",
            JOptionPane.ERROR_MESSAGE);
    }
    
    private void closeWindow() {
        this.dispose();
    }
    
    /**
     * Helper class to store Hall object with display string in JComboBox
     */
    private static class HallItem {
        private Hall hall;
        private String displayText;
        
        public HallItem(Hall hall, String displayText) {
            this.hall = hall;
            this.displayText = displayText;
        }
        
        public Hall getHall() {
            return hall;
        }
        
        @Override
        public String toString() {
            return displayText;
        }
    }
    
    public static void main(String[] args) {
        // For testing purposes
        SwingUtilities.invokeLater(() -> {
            EventManagerUser testManager = new EventManagerUser();
            testManager.setEventManagerId(1);
            testManager.setEventManagerName("Test Manager");
            testManager.setUserId(2);
            
            EventCreationUI ui = new EventCreationUI(testManager, 1);
            ui.setVisible(true);
        });
    }
}
