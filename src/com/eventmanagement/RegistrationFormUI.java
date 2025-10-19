package com.eventmanagement;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistrationFormUI extends JFrame implements ActionListener {
    private JTextField usernameField, emailField, nameField, phoneField;
    private JPasswordField passwordField;
    private JRadioButton eventManagerRadioButton, adminRadioButton, participantRadioButton;
    private JButton registerButton;

    public RegistrationFormUI() {
        setTitle("Registration");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(null);

        Color BG = new Color(237, 244, 237);
        Color LABEL_COLOR = new Color(46, 71, 86);
        Color PRIMARY = new Color(25, 123, 189);

        // Use GridBagLayout for more flexible layout
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add padding

        // Borders for fields
        Border line = BorderFactory.createLineBorder(new Color(171, 209, 181), 1);
        Border padding = new EmptyBorder(5, 10, 5, 10);
        Border fieldBorder = new CompoundBorder(line, padding);

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 5, 15, 5);
        JLabel title = new JLabel("Create New Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(LABEL_COLOR);
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(LABEL_COLOR);
        panel.add(userLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        usernameField = new JTextField();
        usernameField.setBorder(fieldBorder);
        panel.add(usernameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(LABEL_COLOR);
        panel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        emailField = new JTextField();
        emailField.setBorder(fieldBorder);
        panel.add(emailField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(LABEL_COLOR);
        panel.add(passLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField();
        passwordField.setBorder(fieldBorder);
        panel.add(passwordField, gbc);

        // Name
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(LABEL_COLOR);
        panel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        nameField = new JTextField();
        nameField.setBorder(fieldBorder);
        panel.add(nameField, gbc);

        // User Type
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel typeLabel = new JLabel("User Type:");
        typeLabel.setForeground(LABEL_COLOR);
        panel.add(typeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel userTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userTypePanel.setBackground(BG);
        eventManagerRadioButton = new JRadioButton("Event Manager");
        adminRadioButton = new JRadioButton("Admin");
        participantRadioButton = new JRadioButton("Participant");
        participantRadioButton.setSelected(true); // Set Participant as default
        ButtonGroup userTypeGroup = new ButtonGroup();
        userTypeGroup.add(eventManagerRadioButton);
        userTypeGroup.add(adminRadioButton);
        userTypeGroup.add(participantRadioButton);
        userTypePanel.add(eventManagerRadioButton);
        userTypePanel.add(adminRadioButton);
        userTypePanel.add(participantRadioButton);
        panel.add(userTypePanel, gbc);

        // Phone Number (for Participant)
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setForeground(LABEL_COLOR);
        panel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        phoneField = new JTextField();
        phoneField.setBorder(fieldBorder);
        panel.add(phoneField, gbc);

        // Register Button
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.EAST; // Align button to the right
        registerButton = new JButton("Register");
        registerButton.setBackground(PRIMARY);
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        registerButton.setPreferredSize(new Dimension(200, 40));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(this);
        panel.add(registerButton, gbc);

        getContentPane().setBackground(BG);
        add(panel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String name = nameField.getText();
            String phone = phoneField.getText();
            String userType;
            
            if (eventManagerRadioButton.isSelected()) {
                userType = "EVENT_MANAGER";
            } else if (adminRadioButton.isSelected()) {
                userType = "ADMIN";
            } else {
                userType = "PARTICIPANT";
            }

            // Input Validation
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate phone number for participants
            if (userType.equals("PARTICIPANT") && phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Phone number is required for participants.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create a User object
            User user;
            if (userType.equals("EVENT_MANAGER")) {
                user = new EventManagerUser();
                ((EventManagerUser) user).setEventManagerName(name);
            } else if (userType.equals("ADMIN")) {
                user = new Admin();
                ((Admin) user).setAdminName(name);
            } else {
                user = new Participant();
                ((Participant) user).setParticipantName(name);
                ((Participant) user).setPhoneNumber(phone);
            }
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            user.setUserType(userType);

            // Call the UserRegistration.registerUser() method
            int generatedId = UserRegistration.registerUser(user);

            if (generatedId != -1) {
                // Registration successful
                String message = "Registration successful! ";
                if (user instanceof EventManagerUser) {
                    message += "Your Event Manager ID is: " + generatedId;
                } else if (user instanceof Admin) {
                    message += "Your Admin ID is: " + generatedId;
                } else if (user instanceof Participant) {
                    message += "Your Participant ID is: " + generatedId;
                }
                JOptionPane.showMessageDialog(this, message);

                // Clear the form 
                usernameField.setText("");
                emailField.setText("");
                passwordField.setText("");
                nameField.setText("");
                phoneField.setText("");
                participantRadioButton.setSelected(true); 
            } else {
                // Handle registration error
                JOptionPane.showMessageDialog(this, "Registration failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}