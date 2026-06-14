package com.greenloop.ui;

import com.greenloop.db.DBConnection;
import com.greenloop.model.DeliveryAgent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DeliveryAgentForm extends JFrame {
    private final JTextField idField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField contactField = new JTextField();
    private final JTextField vehicleField = new JTextField();
    private final DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Contact", "Vehicle"}, 0);

    public DeliveryAgentForm() {
        setTitle("Delivery Agent Management");
        setLayout(new BorderLayout());
        UITheme.styleFrame(this, 860, 540);

        JPanel formPanel = UITheme.createCardPanel();
        formPanel.setLayout(new GridLayout(4, 2, 10, 10));
        formPanel.add(new JLabel("Agent ID"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Name"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Contact Number"));
        formPanel.add(contactField);
        formPanel.add(new JLabel("Vehicle Number"));
        formPanel.add(vehicleField);
        UITheme.styleLabelsAndFields(formPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(UITheme.BACKGROUND);
        JButton addButton = UITheme.createPrimaryButton("Add Agent");
        JButton updateButton = UITheme.createButton("Update");
        JButton deleteButton = UITheme.createButton("Delete");
        JButton detailsButton = UITheme.createButton("Display Details");
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(detailsButton);

        JTable table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> fillFieldsFromTable(table));

        addButton.addActionListener(e -> addAgent());
        updateButton.addActionListener(e -> updateAgent());
        deleteButton.addActionListener(e -> deleteAgent());
        detailsButton.addActionListener(e -> showAgentDetails());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.BACKGROUND);
        topPanel.add(UITheme.createHeader("Delivery Agent Management", "Manage drivers and vehicle details"), BorderLayout.NORTH);
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setBackground(UITheme.BACKGROUND);
        formWrapper.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 24, 14, 24));
        formWrapper.add(formPanel, BorderLayout.CENTER);
        topPanel.add(formWrapper, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(UITheme.createScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        loadAgents();
    }

    private void addAgent() {
        String sql = "INSERT INTO delivery_agents (name, contact_number, vehicle_number) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, nameField.getText());
            pst.setString(2, contactField.getText());
            pst.setString(3, vehicleField.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Delivery agent added.");
            loadAgents();
            clearFields();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void updateAgent() {
        String sql = "UPDATE delivery_agents SET name=?, contact_number=?, vehicle_number=? WHERE agent_id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, nameField.getText());
            pst.setString(2, contactField.getText());
            pst.setString(3, vehicleField.getText());
            pst.setInt(4, Integer.parseInt(idField.getText()));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Delivery agent updated.");
            loadAgents();
            clearFields();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void deleteAgent() {
        String sql = "DELETE FROM delivery_agents WHERE agent_id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, Integer.parseInt(idField.getText()));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Delivery agent deleted.");
            loadAgents();
            clearFields();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadAgents() {
        tableModel.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT * FROM delivery_agents");
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("agent_id"),
                        rs.getString("name"),
                        rs.getString("contact_number"),
                        rs.getString("vehicle_number")
                });
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void fillFieldsFromTable(JTable table) {
        int row = table.getSelectedRow();
        if (row >= 0) {
            idField.setText(tableModel.getValueAt(row, 0).toString());
            nameField.setText(tableModel.getValueAt(row, 1).toString());
            contactField.setText(tableModel.getValueAt(row, 2).toString());
            vehicleField.setText(tableModel.getValueAt(row, 3).toString());
        }
    }

    private void showAgentDetails() {
        int agentId = idField.getText().isBlank() ? 0 : Integer.parseInt(idField.getText());
        DeliveryAgent agent = new DeliveryAgent(agentId, nameField.getText(), contactField.getText(), vehicleField.getText());
        JOptionPane.showMessageDialog(this, agent.displayDetails());
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        contactField.setText("");
        vehicleField.setText("");
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage());
    }
}
