package com.greenloop.ui;

import com.greenloop.db.DBConnection;
import com.greenloop.notification.EmailNotification;
import com.greenloop.notification.Notification;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DeliveryForm extends JFrame {
    private final JComboBox<String> orderBox = new JComboBox<>();
    private final JComboBox<String> agentBox = new JComboBox<>();
    private final JComboBox<String> statusBox = new JComboBox<>(new String[]{"Pending", "Assigned", "Delivered"});
    private final DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Order ID", "Client", "Agent", "Total", "Status"}, 0);

    public DeliveryForm() {
        setTitle("Assign Delivery Agents");
        setLayout(new BorderLayout());
        UITheme.styleFrame(this, 920, 560);

        JPanel formPanel = UITheme.createCardPanel();
        formPanel.setLayout(new GridLayout(3, 2, 10, 10));
        formPanel.add(new JLabel("Order"));
        formPanel.add(orderBox);
        formPanel.add(new JLabel("Delivery Agent"));
        formPanel.add(agentBox);
        formPanel.add(new JLabel("Delivery Status"));
        formPanel.add(statusBox);
        UITheme.styleLabelsAndFields(formPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(UITheme.BACKGROUND);
        JButton assignButton = UITheme.createPrimaryButton("Assign Agent");
        JButton refreshButton = UITheme.createButton("Refresh");
        buttonPanel.add(assignButton);
        buttonPanel.add(refreshButton);

        assignButton.addActionListener(e -> assignAgent());
        refreshButton.addActionListener(e -> loadData());

        JTable table = new JTable(tableModel);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.BACKGROUND);
        topPanel.add(UITheme.createHeader("Delivery Assignment", "Assign agents and update delivery status"), BorderLayout.NORTH);
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setBackground(UITheme.BACKGROUND);
        formWrapper.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 24, 14, 24));
        formWrapper.add(formPanel, BorderLayout.CENTER);
        topPanel.add(formWrapper, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(UITheme.createScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        loadData();
    }

    private void loadData() {
        loadOrders();
        loadAgents();
        loadDeliveryTable();
    }

    private void loadOrders() {
        orderBox.removeAllItems();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT order_id FROM orders ORDER BY order_id DESC");
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                orderBox.addItem(String.valueOf(rs.getInt("order_id")));
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadAgents() {
        agentBox.removeAllItems();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT agent_id, name FROM delivery_agents");
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                agentBox.addItem(rs.getInt("agent_id") + " - " + rs.getString("name"));
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadDeliveryTable() {
        tableModel.setRowCount(0);
        String sql = "SELECT o.order_id, c.name AS client_name, d.name AS agent_name, o.total_amount, o.delivery_status " +
                "FROM orders o JOIN clients c ON o.client_id = c.client_id " +
                "LEFT JOIN delivery_agents d ON o.agent_id = d.agent_id ORDER BY o.order_id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getString("client_name"),
                        rs.getString("agent_name"),
                        rs.getDouble("total_amount"),
                        rs.getString("delivery_status")
                });
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void assignAgent() {
        if (orderBox.getSelectedItem() == null || agentBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select an order and an agent.");
            return;
        }

        String sql = "UPDATE orders SET agent_id=?, delivery_status=? WHERE order_id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, getSelectedAgentId());
            pst.setString(2, statusBox.getSelectedItem().toString());
            pst.setInt(3, Integer.parseInt(orderBox.getSelectedItem().toString()));
            pst.executeUpdate();
            sendAssignmentEmails();
            JOptionPane.showMessageDialog(this, "Delivery updated.");
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void sendAssignmentEmails() {
        Notification notification = new EmailNotification();
        String orderId = orderBox.getSelectedItem().toString();
        String agentName = agentBox.getSelectedItem().toString();
        System.out.println("Email feature called for order " + orderId);
        notification.sendNotification("client@example.com", "Order Dispatched", "Your GreenLoop order " + orderId + " has been dispatched.");
        notification.sendNotification("agent@example.com", "Delivery Assigned", "You have been assigned to " + agentName + " for order " + orderId + ".");
    }

    private int getSelectedAgentId() {
        String selected = agentBox.getSelectedItem().toString();
        return Integer.parseInt(selected.split(" - ")[0]);
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage());
    }
}
