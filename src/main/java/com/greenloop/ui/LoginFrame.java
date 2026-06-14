package com.greenloop.ui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class LoginFrame extends JFrame {
    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    public LoginFrame() {
        setTitle("GreenLoop Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        UITheme.styleFrame(this, 480, 320);

        JLabel titleLabel = UITheme.createTitleLabel("GreenLoop");
        JLabel subtitleLabel = new JLabel("Management System", JLabel.CENTER);
        subtitleLabel.setForeground(UITheme.MUTED_TEXT);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(UITheme.BACKGROUND);
        titlePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(28, 24, 10, 24));
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        JPanel formPanel = UITheme.createCardPanel();
        formPanel.setLayout(new GridLayout(3, 2, 12, 12));

        JButton loginButton = UITheme.createPrimaryButton("Login");
        loginButton.addActionListener(e -> login());

        formPanel.add(new JLabel("Username"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel(""));
        formPanel.add(loginButton);
        UITheme.styleLabelsAndFields(formPanel);
   // this is the main panel of the card
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(UITheme.BACKGROUND);
        centerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 54, 34, 54));
        centerPanel.add(formPanel, BorderLayout.CENTER);

        add(titlePanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // A hard-coded login keeps this beginner project simple.
        if (username.equals("MDDDSS") && password.equals("MDDDSS123")) {
            new DashboardFrame().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.");
        }
    }
}
