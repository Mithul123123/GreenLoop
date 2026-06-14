package com.greenloop.ui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

public class DashboardFrame extends JFrame {
    public DashboardFrame() {
        setTitle("GreenLoop Management System - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        UITheme.styleFrame(this, 920, 600);

        JLabel titleLabel = UITheme.createHeader(
                "GreenLoop Management System",
                "Eco-friendly packaging operations dashboard"
        );
        JPanel buttonPanel = new JPanel(new GridLayout(4, 2, 16, 16));
        buttonPanel.setBackground(UITheme.BACKGROUND);
        UITheme.addOuterPadding(buttonPanel);

        JButton productButton = createDashboardButton("Product Management");
        JButton clientButton = createDashboardButton("Client Management");
        JButton inventoryButton = createDashboardButton("Inventory Management");
        JButton agentButton = createDashboardButton("Delivery Agent Management");
        JButton orderButton = createDashboardButton("Order Management");
        JButton deliveryButton = createDashboardButton("Assign Deliveries");
        JButton reportButton = createDashboardButton("Reports");
        JButton exitButton = createDashboardButton("Exit");

        productButton.addActionListener(e -> new ProductForm().setVisible(true));
        clientButton.addActionListener(e -> new ClientForm().setVisible(true));
        inventoryButton.addActionListener(e -> new InventoryForm().setVisible(true));
        agentButton.addActionListener(e -> new DeliveryAgentForm().setVisible(true));
        orderButton.addActionListener(e -> new OrderForm().setVisible(true));
        deliveryButton.addActionListener(e -> new DeliveryForm().setVisible(true));
        reportButton.addActionListener(e -> new ReportForm().setVisible(true));
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(productButton);
        buttonPanel.add(clientButton);
        buttonPanel.add(inventoryButton);
        buttonPanel.add(agentButton);
        buttonPanel.add(orderButton);
        buttonPanel.add(deliveryButton);
        buttonPanel.add(reportButton);
        buttonPanel.add(exitButton);

        add(titleLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    private JButton createDashboardButton(String text) {
        JButton button = UITheme.createButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setBackground(UITheme.PANEL);
        return button;
    }
}
