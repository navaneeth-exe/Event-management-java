package com.eventmanagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

public class AdminDashboardUI extends JFrame implements ActionListener {
    private Admin admin;
    private JTable eventTable;
    private JButton viewPendingButton, viewAllButton, logoutButton;
    private JButton approveButton, rejectButton, refreshButton, viewDetailsButton;
    private DefaultTableModel eventTableModel;
    private JLabel welcomeLabel, statusLabel;
    private boolean showingPendingOnly = false;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public AdminDashboardUI(Admin admin) {
        this.admin = admin;
        setTitle("Admin Dashboard - " + admin.getAdminName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        initializeUI();
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(237, 244, 237));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top Panel - Welcome and Menu
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(237, 244, 237));
        topPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(171, 209, 181), 1),
            "Dashboard",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(46, 71, 86)
        ));

        // Welcome message
        welcomeLabel = new JLabel("Welcome, " + admin.getAdminName() + " (Administrator)");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(new Color(34, 56, 88));
        topPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Menu buttons
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        menuPanel.setBackground(new Color(237, 244, 237));
        viewPendingButton = new JButton("View Pending Events");
        viewPendingButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        viewPendingButton.setBackground(new Color(107, 127, 215));
        viewPendingButton.setForeground(Color.WHITE);
        viewPendingButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewPendingButton.addActionListener(this);

        viewAllButton = new JButton("View All Events");
        viewAllButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        viewAllButton.setBackground(new Color(107, 127, 215));
        viewAllButton.setForeground(Color.WHITE);
        viewAllButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewAllButton.addActionListener(this);

        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutButton.setBackground(new Color(193, 18, 31));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(this);

        menuPanel.add(viewPendingButton);
        menuPanel.add(viewAllButton);
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
        eventTable.setRowHeight(30);
        eventTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        eventTable.getTableHeader().setBackground(new Color(34, 56, 88));
        eventTable.getTableHeader().setForeground(Color.WHITE);
        eventTable.setGridColor(new Color(224, 224, 224));
        eventTable.setShowVerticalLines(true);
        eventTable.setSelectionBackground(new Color(25, 123, 189));
        eventTable.setSelectionForeground(Color.WHITE);

        // Set alternating row colors
        eventTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(245, 249, 245));
                    }
                }
                return c;
            }
        });

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
        bottomPanel.setBackground(new Color(237, 244, 237));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        actionPanel.setBackground(new Color(237, 244, 237));
        approveButton = new JButton("Approve Selected");
        approveButton.setBackground(new Color(82, 183, 136));
        approveButton.setForeground(Color.WHITE);
        approveButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        approveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        approveButton.addActionListener(this);
        approveButton.setEnabled(false);

        rejectButton = new JButton("Reject Selected");
        rejectButton.setBackground(new Color(217, 4, 41));
        rejectButton.setForeground(Color.WHITE);
        rejectButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rejectButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rejectButton.addActionListener(this);
        rejectButton.setEnabled(false);

        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(this);

        viewDetailsButton = new JButton("View Event Details");
        viewDetailsButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        viewDetailsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewDetailsButton.addActionListener(this);

        actionPanel.add(approveButton);
        actionPanel.add(rejectButton);
        actionPanel.add(refreshButton);
        actionPanel.add(viewDetailsButton);
        bottomPanel.add(actionPanel, BorderLayout.WEST);

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        statusLabel.setForeground(new Color(34, 56, 88));
        bottomPanel.add(statusLabel, BorderLayout.EAST);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Load pending events by default
        loadPendingEvents();
    }

    private void loadPendingEvents() {
        showingPendingOnly = true;
        eventTableModel.setRowCount(0);
        eventTableModel.setColumnCount(0);

        // Set up columns for pending events view
        eventTableModel.addColumn("Event ID");
        eventTableModel.addColumn("Event Title");
        eventTableModel.addColumn("Event Manager");
        eventTableModel.addColumn("Date");
        eventTableModel.addColumn("Hall");
        eventTableModel.addColumn("Event Type");

        List<Event> pendingEvents = EventDAO.getAllPendingEvents();

        for (Event event : pendingEvents) {
            // Get manager name
            EventManagerUser manager = EventManagerUserManager.getEventManagerUserById(event.getManagerId());
            String managerName = (manager != null) ? manager.getEventManagerName() : "Unknown";

            eventTableModel.addRow(new Object[]{
                event.getEventId(),
                event.getEventTitle(),
                managerName,
                dateFormat.format(event.getEventDate()),
                event.getHallName() != null ? event.getHallName() : "N/A",
                event.getEventType() != null ? event.getEventType() : "N/A"
            });
        }

        // Enable approve/reject buttons for pending events
        approveButton.setEnabled(true);
        rejectButton.setEnabled(true);

        statusLabel.setText("Showing " + pendingEvents.size() + " pending event(s)");
    }

    private void loadAllEvents() {
        showingPendingOnly = false;
        eventTableModel.setRowCount(0);
        eventTableModel.setColumnCount(0);

        // Set up columns for all events view
        eventTableModel.addColumn("Event ID");
        eventTableModel.addColumn("Title");
        eventTableModel.addColumn("Manager");
        eventTableModel.addColumn("Date");
        eventTableModel.addColumn("Hall");
        eventTableModel.addColumn("Type");
        eventTableModel.addColumn("Status");

        List<Event> allEvents = EventDAO.getAllEvents();

        for (Event event : allEvents) {
            // Get manager name
            EventManagerUser manager = EventManagerUserManager.getEventManagerUserById(event.getManagerId());
            String managerName = (manager != null) ? manager.getEventManagerName() : "Unknown";

            eventTableModel.addRow(new Object[]{
                event.getEventId(),
                event.getEventTitle(),
                managerName,
                dateFormat.format(event.getEventDate()),
                event.getHallName() != null ? event.getHallName() : "N/A",
                event.getEventType() != null ? event.getEventType() : "N/A",
                event.getApprovalStatus() != null ? event.getApprovalStatus() : "pending"
            });
        }

        // Apply color coding to status column
        applyStatusColorCoding();

        // Disable approve/reject buttons for all events view
        approveButton.setEnabled(false);
        rejectButton.setEnabled(false);

        statusLabel.setText("Showing " + allEvents.size() + " total event(s)");
    }

    private void applyStatusColorCoding() {
        eventTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    String status = value.toString().toUpperCase();
                    if (status.equals("PENDING")) {
                        c.setBackground(new Color(255, 155, 113));
                        c.setForeground(Color.BLACK);
                    } else if (status.equals("APPROVED")) {
                        c.setBackground(new Color(82, 183, 136));
                        c.setForeground(Color.WHITE);
                    } else if (status.equals("REJECTED")) {
                        c.setBackground(new Color(217, 4, 41));
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
        if (e.getSource() == viewPendingButton) {
            loadPendingEvents();
        } else if (e.getSource() == viewAllButton) {
            loadAllEvents();
        } else if (e.getSource() == approveButton) {
            approveSelectedEvent();
        } else if (e.getSource() == rejectButton) {
            rejectSelectedEvent();
        } else if (e.getSource() == refreshButton) {
            if (showingPendingOnly) {
                loadPendingEvents();
            } else {
                loadAllEvents();
            }
        } else if (e.getSource() == viewDetailsButton) {
            viewEventDetails();
        } else if (e.getSource() == logoutButton) {
            logout();
        }
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

    private void approveSelectedEvent() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an event to approve.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int eventId = (int) eventTable.getValueAt(selectedRow, 0);
        String eventTitle = (String) eventTable.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to approve the event:\n\"" + eventTitle + "\"?",
            "Confirm Approval",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Get current admin's userId from session
                UserSession session = UserSession.getInstance();
                int adminUserId = session.getUserId();
                
                // Approve the event
                boolean success = EventDAO.approveEvent(eventId, adminUserId);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Event \"" + eventTitle + "\" has been approved successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh the table
                    loadPendingEvents();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to approve the event. Please try again.",
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

    private void rejectSelectedEvent() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an event to reject.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int eventId = (int) eventTable.getValueAt(selectedRow, 0);
        String eventTitle = (String) eventTable.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to reject the event:\n\"" + eventTitle + "\"?",
            "Confirm Rejection",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Get current admin's userId from session
                UserSession session = UserSession.getInstance();
                int adminUserId = session.getUserId();
                
                // Reject the event
                boolean success = EventDAO.rejectEvent(eventId, adminUserId);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Event \"" + eventTitle + "\" has been rejected.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh the table
                    loadPendingEvents();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to reject the event. Please try again.",
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
            Admin testAdmin = new Admin();
            testAdmin.setAdminId(1);
            testAdmin.setAdminName("Test Admin");
            testAdmin.setUserId(1);
            testAdmin.setUsername("admin");
            testAdmin.setEmail("admin@test.com");
            
            AdminDashboardUI dashboard = new AdminDashboardUI(testAdmin);
            dashboard.setVisible(true);
        });
    }
}