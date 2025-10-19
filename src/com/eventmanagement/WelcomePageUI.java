package com.eventmanagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomePageUI extends JFrame implements ActionListener {
    private static final String TITLE = "Event Management System";
    private static final int WIDTH = 500;  // Increased width
    private static final int HEIGHT = 300;
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Color BACKGROUND_COLOR = new Color(237, 244, 237);
    private static final Color PRIMARY_COLOR = new Color(25, 123, 189);
    private static final Color TEXT_COLOR = new Color(46, 71, 86);

    private JButton loginButton;
    private JButton registerButton;

    public WelcomePageUI() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle(TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Use a GridBagLayout for more control over component placement
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add spacing

        // Welcome Message
        JLabel welcomeLabel = new JLabel("Welcome to the Event Management System!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span two columns
        gbc.anchor = GridBagConstraints.CENTER; // Center the label
        mainPanel.add(welcomeLabel, gbc);

        // Login Button
        loginButton = new JButton("Login");
        loginButton.setFont(BUTTON_FONT);
        loginButton.setBackground(PRIMARY_COLOR);
        loginButton.setForeground(Color.WHITE);
        Dimension btnSize = new Dimension(300, 45);
        loginButton.setPreferredSize(btnSize);
        loginButton.setMinimumSize(btnSize);
        loginButton.setMaximumSize(btnSize);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE; // Keep fixed button size
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(loginButton, gbc);

        // Register Button
        registerButton = new JButton("Register");
        registerButton.setFont(BUTTON_FONT);
        registerButton.setBackground(Color.WHITE);
        registerButton.setForeground(PRIMARY_COLOR);
        registerButton.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2));
        registerButton.setPreferredSize(btnSize);
        registerButton.setMinimumSize(btnSize);
        registerButton.setMaximumSize(btnSize);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(this);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE; // Keep fixed button size
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(registerButton, gbc);

        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            openLoginForm();
        } else if (e.getSource() == registerButton) {
            openRegistrationForm();
        }
    }

    private void openLoginForm() {
        LoginFormUI loginForm = new LoginFormUI();
        loginForm.setVisible(true);
    }

    private void openRegistrationForm() {
        RegistrationFormUI registrationForm = new RegistrationFormUI();
        registrationForm.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WelcomePageUI mainFrame = new WelcomePageUI();
            mainFrame.setVisible(true);
        });
    }
}