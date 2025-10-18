package com.eventmanagement;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Participant Dashboard UI
 * Allows participants to view and register for approved events
 */
public class ParticipantDashboardUI extends JFrame implements ActionListener {
    private Participant participant;
    private JTable eventTable;
    private JButton viewEventsButton, myRegistrationsButton, logoutButton, refreshButton;
    private DefaultTableModel eventTableModel;
    private JLabel welcomeLabel, statusLabel;
    private JTextField searchField;
    private boolean showingAvailableEvents = true;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public ParticipantDashboardUI(Participant participant) {
        this.participant = participant;
        setTitle("Participant Dashboard - " + participant.getParticipantName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        initializeUI();
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel - Welcome and Menu
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Welcome message
        welcomeLabel = new JLabel("Welcome, " + participant.getParticipantName() + " (Participant)");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Menu buttons
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        viewEventsButton = new JButton("View Available Events");
        viewEventsButton.setFont(new Font("Arial", Font.PLAIN, 14));
        viewEventsButton.setBackground(new Color(0, 123, 255));
        viewEventsButton.setForeground(Color.WHITE);
        viewEventsButton.addActionListener(this);
        
        myRegistrationsButton = new JButton("My Registrations");
        myRegistrationsButton.setFont(new Font("Arial", Font.PLAIN, 14));
        myRegistrationsButton.addActionListener(this);
        
        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(this);
        
        menuPanel.add(viewEventsButton);
        menuPanel.add(myRegistrationsButton);
        menuPanel.add(logoutButton);
        topPanel.add(menuPanel, BorderLayout.CENTER);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center Panel - Event Table
        eventTable = new JTable();
        eventTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only action column is "editable" (for button clicks)
                return column == getColumnCount() - 1;
            }
        };
        eventTable.setModel(eventTableModel);
        eventTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventTable.setRowHeight(35);
        eventTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Add mouse listener for button clicks
        eventTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = eventTable.rowAtPoint(e.getPoint());
                int column = eventTable.columnAtPoint(e.getPoint());
                
                if (row >= 0 && column == eventTable.getColumnCount() - 1) {
                    handleActionButtonClick(row);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(eventTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel - Search, Refresh and status
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        // Search field
        actionPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchField.addActionListener(e -> applySearch());
        actionPanel.add(searchField);
        
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> applySearch());
        actionPanel.add(searchButton);
        
        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 12));
        refreshButton.addActionListener(this);
        actionPanel.add(refreshButton);
        
        bottomPanel.add(actionPanel, BorderLayout.WEST);
        
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        bottomPanel.add(statusLabel, BorderLayout.EAST);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        
        // Load available events by default
        loadAvailableEvents();
    }

    private void loadAvailableEvents() {
        showingAvailableEvents = true;
        eventTableModel.setRowCount(0);
        eventTableModel.setColumnCount(0);

        // Set up columns for available events view
        eventTableModel.addColumn("Event ID");
        eventTableModel.addColumn("Title");
        eventTableModel.addColumn("Date");
        eventTableModel.addColumn("Hall");
        eventTableModel.addColumn("Type");
        eventTableModel.addColumn("Registered");
        eventTableModel.addColumn("Action");

        List<Event> approvedEvents = EventDAO.getAllApprovedEvents();
        
        for (Event event : approvedEvents) {
            int registrationCount = EventRegistrationDAO.getRegistrationCount(event.getEventId());
            boolean isRegistered = EventRegistrationDAO.isParticipantRegistered(
                event.getEventId(), participant.getParticipantId());
            
            String actionText = isRegistered ? "Registered ✓" : "Register";
            
            eventTableModel.addRow(new Object[]{
                event.getEventId(),
                event.getEventTitle(),
                dateFormat.format(event.getEventDate()),
                event.getHallName() != null ? event.getHallName() : "N/A",
                event.getEventType() != null ? event.getEventType() : "N/A",
                registrationCount,
                actionText
            });
        }
        
        // Set custom renderer for action column
        eventTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        
        // Hide Event ID column
        eventTable.getColumnModel().getColumn(0).setMinWidth(0);
        eventTable.getColumnModel().getColumn(0).setMaxWidth(0);
        eventTable.getColumnModel().getColumn(0).setWidth(0);
        
        statusLabel.setText("Showing " + approvedEvents.size() + " approved event(s)");
    }

    private void loadMyRegistrations() {
        showingAvailableEvents = false;
        eventTableModel.setRowCount(0);
        eventTableModel.setColumnCount(0);

        // Set up columns for my registrations view
        eventTableModel.addColumn("Registration ID");
        eventTableModel.addColumn("Event Title");
        eventTableModel.addColumn("Date");
        eventTableModel.addColumn("Hall");
        eventTableModel.addColumn("Type");
        eventTableModel.addColumn("Status");
        eventTableModel.addColumn("Action");

        List<EventRegistration> registrations = EventRegistrationDAO.getRegistrationsByParticipant(
            participant.getParticipantId());
        
        for (EventRegistration registration : registrations) {
            String actionText = registration.getStatus().equals("registered") ? "Cancel" : "Cancelled";
            
            eventTableModel.addRow(new Object[]{
                registration.getRegistrationId(),
                registration.getEventTitle(),
                registration.getEventDate() != null ? dateFormat.format(registration.getEventDate()) : "N/A",
                registration.getHallName() != null ? registration.getHallName() : "N/A",
                "Event", // Could add event type to EventRegistration model
                registration.getStatus(),
                actionText
            });
        }
        
        // Set custom renderer for action column
        eventTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        
        // Hide Registration ID column
        eventTable.getColumnModel().getColumn(0).setMinWidth(0);
        eventTable.getColumnModel().getColumn(0).setMaxWidth(0);
        eventTable.getColumnModel().getColumn(0).setWidth(0);
        
        statusLabel.setText("Showing " + registrations.size() + " registration(s)");
    }

    private void handleActionButtonClick(int row) {
        if (showingAvailableEvents) {
            // Handle Register button click
            int eventId = (int) eventTable.getValueAt(row, 0);
            String actionText = (String) eventTable.getValueAt(row, 6);
            
            if (actionText.equals("Register")) {
                registerForEvent(eventId, row);
            } else {
                JOptionPane.showMessageDialog(this,
                    "You are already registered for this event.",
                    "Already Registered",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            // Handle Cancel Registration button click
            int registrationId = (int) eventTable.getValueAt(row, 0);
            String actionText = (String) eventTable.getValueAt(row, 6);
            
            if (actionText.equals("Cancel")) {
                cancelRegistration(registrationId, row);
            } else {
                JOptionPane.showMessageDialog(this,
                    "This registration has already been cancelled.",
                    "Already Cancelled",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void registerForEvent(int eventId, int row) {
        String eventTitle = (String) eventTable.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Do you want to register for:\n\"" + eventTitle + "\"?",
            "Confirm Registration",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int registrationId = EventRegistrationDAO.registerForEvent(
                    eventId, participant.getParticipantId());
                
                if (registrationId > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Successfully registered for \"" + eventTitle + "\"!\n\n" +
                        "Registration ID: " + registrationId,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh the table
                    loadAvailableEvents();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to register. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "An error occurred: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void cancelRegistration(int registrationId, int row) {
        String eventTitle = (String) eventTable.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel your registration for:\n\"" + eventTitle + "\"?",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = EventRegistrationDAO.cancelRegistration(registrationId);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Registration cancelled successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh the table
                    loadMyRegistrations();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to cancel registration. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "An error occurred: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void applySearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        
        if (searchText.isEmpty()) {
            // Reload all data
            if (showingAvailableEvents) {
                loadAvailableEvents();
            } else {
                loadMyRegistrations();
            }
            return;
        }
        
        // Filter table rows
        DefaultTableModel model = (DefaultTableModel) eventTable.getModel();
        int rowCount = model.getRowCount();
        
        for (int i = rowCount - 1; i >= 0; i--) {
            boolean matchFound = false;
            
            // Search in title, date, hall, and type columns
            for (int j = 1; j < model.getColumnCount() - 1; j++) {
                Object value = model.getValueAt(i, j);
                if (value != null && value.toString().toLowerCase().contains(searchText)) {
                    matchFound = true;
                    break;
                }
            }
            
            if (!matchFound) {
                model.removeRow(i);
            }
        }
        
        statusLabel.setText("Search results: " + model.getRowCount() + " event(s)");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == viewEventsButton) {
            searchField.setText("");
            loadAvailableEvents();
        } else if (e.getSource() == myRegistrationsButton) {
            searchField.setText("");
            loadMyRegistrations();
        } else if (e.getSource() == refreshButton) {
            searchField.setText("");
            if (showingAvailableEvents) {
                loadAvailableEvents();
            } else {
                loadMyRegistrations();
            }
        } else if (e.getSource() == logoutButton) {
            logout();
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Clear session
            UserSession.getInstance().logout();
            
            // Close this window
            this.dispose();
            
            // Show welcome page
            new WelcomePageUI().setVisible(true);
        }
    }

    /**
     * Custom renderer to display buttons in table cells
     */
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            String text = (value == null) ? "" : value.toString();
            setText(text);
            
            if (text.contains("✓") || text.equals("Cancelled")) {
                setBackground(Color.LIGHT_GRAY);
                setForeground(Color.DARK_GRAY);
                setEnabled(false);
            } else if (text.equals("Register")) {
                setBackground(new Color(40, 167, 69));
                setForeground(Color.WHITE);
                setEnabled(true);
            } else if (text.equals("Cancel")) {
                setBackground(new Color(220, 53, 69));
                setForeground(Color.WHITE);
                setEnabled(true);
            } else {
                setBackground(UIManager.getColor("Button.background"));
                setForeground(Color.BLACK);
                setEnabled(true);
            }
            
            return this;
        }
    }

    public static void main(String[] args) {
        // For testing purposes
        SwingUtilities.invokeLater(() -> {
            Participant testParticipant = new Participant();
            testParticipant.setParticipantId(1);
            testParticipant.setParticipantName("Test Participant");
            testParticipant.setUserId(3);
            testParticipant.setEmail("test@example.com");
            testParticipant.setPhoneNumber("+1-555-0123");
            
            ParticipantDashboardUI dashboard = new ParticipantDashboardUI(testParticipant);
            dashboard.setVisible(true);
        });
    }
}
