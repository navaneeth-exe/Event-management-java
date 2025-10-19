package com.eventmanagement;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

public class EventManagerDashboardUI extends JFrame implements ActionListener {
    private EventManagerUser eventManager;
    private int eventManagerId;
    private JTable eventTable;
    private JButton createEventButton, myEventsButton, viewApprovedButton, logoutButton, refreshButton, viewDetailsButton;
    private DefaultTableModel eventTableModel;
    private JLabel welcomeLabel, statusLabel;
    private boolean showingMyEvents = true;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public EventManagerDashboardUI(EventManagerUser eventManager, int eventManagerId) {
        this.eventManager = eventManager;
        this.eventManagerId = eventManagerId;
        setTitle("Event Manager Dashboard - " + eventManager.getEventManagerName());
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
        welcomeLabel = new JLabel("Welcome, " + eventManager.getEventManagerName() + " (Event Manager)");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Menu buttons
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        createEventButton = new JButton("Create New Event");
        createEventButton.setFont(new Font("Arial", Font.PLAIN, 14));
        createEventButton.setBackground(new Color(0, 123, 255));
        createEventButton.setForeground(Color.WHITE);
        createEventButton.addActionListener(this);
        
        myEventsButton = new JButton("My Events");
        myEventsButton.setFont(new Font("Arial", Font.PLAIN, 14));
        myEventsButton.addActionListener(this);
        
        viewApprovedButton = new JButton("View All Approved Events");
        viewApprovedButton.setFont(new Font("Arial", Font.PLAIN, 14));
        viewApprovedButton.addActionListener(this);
        
        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(this);
        
        menuPanel.add(createEventButton);
        menuPanel.add(myEventsButton);
        menuPanel.add(viewApprovedButton);
        menuPanel.add(logoutButton);
        topPanel.add(menuPanel, BorderLayout.CENTER);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center Panel - Event Table
        eventTable = new JTable();
        eventTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        eventTable.setModel(eventTableModel);
        eventTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventTable.setRowHeight(25);
        eventTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Add double-click listener for viewing event details
        eventTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = eventTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        viewEventDetails();
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(eventTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel - Action buttons and status
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 12));
        refreshButton.addActionListener(this);
        actionPanel.add(refreshButton);
        
        viewDetailsButton = new JButton("View Event Details");
        viewDetailsButton.setFont(new Font("Arial", Font.PLAIN, 12));
        viewDetailsButton.addActionListener(this);
        actionPanel.add(viewDetailsButton);
        
        bottomPanel.add(actionPanel, BorderLayout.WEST);
        
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        bottomPanel.add(statusLabel, BorderLayout.EAST);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        
        // Load my events by default
        loadMyEvents();
    }

    private void loadMyEvents() {
        showingMyEvents = true;
        eventTableModel.setRowCount(0);
        eventTableModel.setColumnCount(0);

        // Set up columns for my events view
        eventTableModel.addColumn("Event ID");
        eventTableModel.addColumn("Title");
        eventTableModel.addColumn("Date");
        eventTableModel.addColumn("Hall");
        eventTableModel.addColumn("Type");
        eventTableModel.addColumn("Status");

        List<Event> myEvents = EventDAO.getEventsByManagerId(eventManagerId);
        
        for (Event event : myEvents) {
            eventTableModel.addRow(new Object[]{
                event.getEventId(),
                event.getEventTitle(),
                dateFormat.format(event.getEventDate()),
                event.getHallName() != null ? event.getHallName() : "N/A",
                event.getEventType() != null ? event.getEventType() : "N/A",
                event.getApprovalStatus() != null ? event.getApprovalStatus() : "pending"
            });
        }
        
        // Apply color coding to status column
        applyStatusColorCoding(5);
        
        statusLabel.setText("Showing " + myEvents.size() + " of my event(s)");
    }

    private void loadApprovedEvents() {
        showingMyEvents = false;
        eventTableModel.setRowCount(0);
        eventTableModel.setColumnCount(0);

        // Set up columns for approved events view
        eventTableModel.addColumn("Event ID");
        eventTableModel.addColumn("Title");
        eventTableModel.addColumn("Date");
        eventTableModel.addColumn("Hall");
        eventTableModel.addColumn("Type");

        List<Event> approvedEvents = EventDAO.getAllApprovedEvents();
        
        for (Event event : approvedEvents) {
            eventTableModel.addRow(new Object[]{
                event.getEventId(),
                event.getEventTitle(),
                dateFormat.format(event.getEventDate()),
                event.getHallName() != null ? event.getHallName() : "N/A",
                event.getEventType() != null ? event.getEventType() : "N/A"
            });
        }
        
        statusLabel.setText("Showing " + approvedEvents.size() + " approved event(s)");
    }

    private void applyStatusColorCoding(int columnIndex) {
        eventTable.getColumnModel().getColumn(columnIndex).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String status = value.toString().toUpperCase();
                    if (status.equals("PENDING")) {
                        c.setBackground(new Color(255, 193, 7)); // Orange
                        c.setForeground(Color.BLACK);
                    } else if (status.equals("APPROVED")) {
                        c.setBackground(new Color(40, 167, 69)); // Green
                        c.setForeground(Color.WHITE);
                    } else if (status.equals("REJECTED")) {
                        c.setBackground(new Color(220, 53, 69)); // Red
                        c.setForeground(Color.WHITE);
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                }
                
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == createEventButton) {
            openEventCreationUI();
        } else if (e.getSource() == myEventsButton) {
            loadMyEvents();
        } else if (e.getSource() == viewApprovedButton) {
            loadApprovedEvents();
        } else if (e.getSource() == refreshButton) {
            if (showingMyEvents) {
                loadMyEvents();
            } else {
                loadApprovedEvents();
            }
        } else if (e.getSource() == viewDetailsButton) {
            viewEventDetails();
        } else if (e.getSource() == logoutButton) {
            logout();
        }
    }

    private void openEventCreationUI() {
        EventCreationUI eventCreationUI = new EventCreationUI(eventManager, eventManagerId);
        eventCreationUI.setVisible(true);
        
        // Add window listener to refresh when creation window closes
        eventCreationUI.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                // Refresh my events when creation window closes
                loadMyEvents();
            }
        });
    }

    private void viewEventDetails() {
        int selectedRow = eventTable.getSelectedRow();
        
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select an event to view details.",
                "No Event Selected",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get eventId from selected row (column 0)
        int eventId = (int) eventTable.getValueAt(selectedRow, 0);
        
        // Fetch event details from database
        Event event = EventDAO.getEventById(eventId);
        
        if (event != null) {
            // Create and show EventDetailsUI dialog
            EventDetailsUI detailsDialog = new EventDetailsUI(this, event);
            detailsDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to load event details. Event not found.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
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

    public static void main(String[] args) {
        // For testing purposes
        SwingUtilities.invokeLater(() -> {
            EventManagerUser testManager = new EventManagerUser();
            testManager.setEventManagerId(1);
            testManager.setEventManagerName("Test Manager");
            testManager.setUserId(2);
            testManager.setUsername("manager");
            testManager.setEmail("manager@test.com");
            
            EventManagerDashboardUI dashboard = new EventManagerDashboardUI(testManager, 1);
            dashboard.setVisible(true);
        });
    }
}
