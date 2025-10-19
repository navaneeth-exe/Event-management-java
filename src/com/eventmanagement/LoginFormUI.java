package com.eventmanagement;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The login form UI for the Event Management System.
 */
public class LoginFormUI extends JFrame implements ActionListener {
    private static final String TITLE = "Login";
    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;
    private static final Color BG = new Color(237, 244, 237);
    private static final Color PRIMARY = new Color(25, 123, 189);
    private static final Color LABEL_COLOR = new Color(46, 71, 86);

    private JTextField userNameTextField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFormUI() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle(TITLE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BG);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints constraints = new GridBagConstraints();

        // Title
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(10, 10, 15, 10);
        JLabel titleLabel = new JLabel("Login to Your Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(LABEL_COLOR);
        mainPanel.add(titleLabel, constraints);

        // Reset gridwidth for form rows
        constraints.gridwidth = 1;

        // Borders for text fields
        Border line = BorderFactory.createLineBorder(new Color(171, 209, 181), 1);
        Border padding = new EmptyBorder(5, 10, 5, 10);
        Border textBorder = new CompoundBorder(line, padding);

        // Username
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.insets = new Insets(5, 10, 5, 10);
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(LABEL_COLOR);
        mainPanel.add(userLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        userNameTextField = new JTextField(20);
        userNameTextField.setBorder(textBorder);
        mainPanel.add(userNameTextField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(LABEL_COLOR);
        mainPanel.add(passLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        passwordField = new JPasswordField(20);
        passwordField.setBorder(textBorder);
        mainPanel.add(passwordField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        loginButton = new JButton("Login");
        loginButton.setBackground(PRIMARY);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginButton.setPreferredSize(new Dimension(200, 40));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(this);
        mainPanel.add(loginButton, constraints);

        getContentPane().setBackground(BG);
        add(mainPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String username = userNameTextField.getText();
            char[] passwordChars = passwordField.getPassword();

            // Validate user input
            if (username.isEmpty() || passwordChars.length == 0) {
                JOptionPane.showMessageDialog(this, "Please enter a username and password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Call the UserAuthentication.authenticateUser() method
            User user = UserAuthentication.authenticateUser(username, new String(passwordChars));

            if (user != null) {
                // Successful login - store in session
                handleSuccessfulLogin(user);

                // Clear the form after successful login
                userNameTextField.setText("");
                passwordField.setText("");
                
                // Close login window
                this.dispose();
            } else {
                // Failed login
                JOptionPane.showMessageDialog(this, "Invalid username or password. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleSuccessfulLogin(User user) {
        UserSession session = UserSession.getInstance();
        
        if (user instanceof EventManagerUser) {
            // Get Event Manager ID and store in session
            int eventManagerId = ((EventManagerUser) user).getEventManagerId();
            session.login(user, eventManagerId);
            
            // Open the event manager dashboard
            new EventManagerDashboardUI((EventManagerUser) user, eventManagerId).setVisible(true);
            
        } else if (user instanceof Admin) {
            // Get Admin ID and store in session
            int adminId = ((Admin) user).getAdminId();
            session.login(user, adminId);
            
            // Open the admin dashboard
            new AdminDashboardUI((Admin) user).setVisible(true);
            
        } else if (user instanceof Participant) {
            // Get Participant ID and store in session
            int participantId = ((Participant) user).getParticipantId();
            session.login(user, participantId);
            
            // Open the participant dashboard
            new ParticipantDashboardUI((Participant) user).setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFormUI loginForm = new LoginFormUI();
            loginForm.setVisible(true);
        });
    }
}