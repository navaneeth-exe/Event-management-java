package com.eventmanagement;

import javax.swing.*;
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
        setSize(400, 300);
        setLocationRelativeTo(null);

        // Use GridBagLayout for more flexible layout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add padding

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        usernameField = new JTextField();
        panel.add(usernameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        emailField = new JTextField();
        panel.add(emailField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField();
        panel.add(passwordField, gbc);

        // Name
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        nameField = new JTextField();
        panel.add(nameField, gbc);

        // User Type
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("User Type:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel userTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Phone Number:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        phoneField = new JTextField();
        panel.add(phoneField, gbc);

        // Register Button
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.EAST; // Align button to the right
        registerButton = new JButton("Register");
        registerButton.addActionListener(this);
        panel.add(registerButton, gbc);

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
                userType = "event_manager";
            } else if (adminRadioButton.isSelected()) {
                userType = "admin";
            } else {
                userType = "participant";
            }

            // Input Validation
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate phone number for participants
            if (userType.equals("participant") && phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Phone number is required for participants.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create a User object
            User user;
            if (userType.equals("event_manager")) {
                user = new EventManagerUser();
                ((EventManagerUser) user).setEventManagerName(name);
            } else if (userType.equals("admin")) {
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