package com.eventmanagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Event Details UI - Dialog to show event details and registered participants
 */
public class EventDetailsUI extends JDialog implements ActionListener {
    private Event event;
    private JButton refreshButton, closeButton;
    private JTable participantsTable;
    private DefaultTableModel tableModel;
    private JLabel capacityLabel;
    private JScrollPane tableScrollPane;
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
        participantsTable.setRowHeight(30);  // Increased from 25 to 30 for better visibility
        participantsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        participantsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        participantsTable.setFillsViewportHeight(true);  // Ensure table fills the viewport
        
        // Hide Participant ID column
        participantsTable.getColumnModel().getColumn(0).setMinWidth(0);
        participantsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        participantsTable.getColumnModel().getColumn(0).setWidth(0);
        
        tableScrollPane = new JScrollPane(participantsTable);
        tableScrollPane.setPreferredSize(new Dimension(800, 300));  // Set explicit size
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
        
        // Create a completely new table model to avoid any caching issues
        DefaultTableModel newModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Add columns
        newModel.addColumn("Participant ID");
        newModel.addColumn("Name");
        newModel.addColumn("Email");
        newModel.addColumn("Phone");
        newModel.addColumn("Registered Date");
        
        // Fetch registrations directly from database with a simpler query
        try {
            Connection conn = DatabaseConnection.getConnection();
            String query = "SELECT er.registrationId, er.participantId, er.registrationDate, er.status, " +
                          "p.participantName, p.phoneNumber, u.email " +
                          "FROM EventRegistration er " +
                          "JOIN Participant p ON er.participantId = p.participantId " +
                          "JOIN User u ON p.userId = u.userId " +
                          "WHERE er.eventId = ? AND er.status = 'REGISTERED' " +
                          "ORDER BY er.registrationDate ASC";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, event.getEventId());
            ResultSet rs = stmt.executeQuery();
            
            SimpleDateFormat regDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            int rowCount = 0;
            
            while (rs.next()) {
                String name = rs.getString("participantName") != null ? rs.getString("participantName") : "N/A";
                String email = rs.getString("email") != null ? rs.getString("email") : "N/A";
                String phone = rs.getString("phoneNumber") != null ? rs.getString("phoneNumber") : "N/A";
                String regDate = rs.getTimestamp("registrationDate") != null ? 
                    regDateFormat.format(rs.getTimestamp("registrationDate")) : "N/A";
                
                newModel.addRow(new Object[]{
                    rs.getInt("participantId"),
                    name,
                    email,
                    phone,
                    regDate
                });
                
                rowCount++;
            }
            
            DatabaseConnection.closeConnection(conn);
            
            // Replace the table model completely
            participantsTable.setModel(newModel);
            this.tableModel = newModel;
            
            // Set preferred column widths for better display
            if (participantsTable.getColumnCount() > 0) {
                // Hide Participant ID column
                participantsTable.getColumnModel().getColumn(0).setMinWidth(0);
                participantsTable.getColumnModel().getColumn(0).setMaxWidth(0);
                participantsTable.getColumnModel().getColumn(0).setWidth(0);
                participantsTable.getColumnModel().getColumn(0).setPreferredWidth(0);
                
                // Set widths for visible columns
                participantsTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
                participantsTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Email
                participantsTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Phone
                participantsTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Date
            }
            
            // Refresh table header
            if (participantsTable.getTableHeader() != null) {
                participantsTable.getTableHeader().revalidate();
                participantsTable.getTableHeader().repaint();
            }
            
            // Force complete UI refresh - multiple approaches to ensure it works
            SwingUtilities.invokeLater(() -> {
                // Refresh table
                participantsTable.revalidate();
                participantsTable.repaint();
                
                // Refresh the scroll pane explicitly
                if (tableScrollPane != null) {
                    tableScrollPane.revalidate();
                    tableScrollPane.repaint();
                    tableScrollPane.getViewport().revalidate();
                    tableScrollPane.getViewport().repaint();
                }
                
                // Refresh parent containers
                if (participantsTable.getParent() != null && participantsTable.getParent().getParent() != null) {
                    participantsTable.getParent().getParent().revalidate();
                    participantsTable.getParent().getParent().repaint();
                }
                
                // Refresh the entire dialog
                EventDetailsUI.this.revalidate();
                EventDetailsUI.this.repaint();
            });
            
            // Update dialog title
            setTitle("Event Details - " + event.getEventTitle() + " (" + rowCount + " participants)");
            
        } catch (Exception e) {
            System.out.println("ERROR: Failed to load participants: " + e.getMessage());
            e.printStackTrace();
            
            // Show error message to user
            JOptionPane.showMessageDialog(this,
                "Failed to load participant list: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
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
}
