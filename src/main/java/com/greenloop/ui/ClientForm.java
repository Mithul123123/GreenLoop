package com.greenloop.ui;

import com.greenloop.db.DBConnection;
import com.greenloop.model.Client;

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

public class ClientForm extends JFrame {
    private final JTextField idField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField contactField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Contact", "Email"}, 0);

    public ClientForm() {
        setTitle("Client Management");
        setLayout(new BorderLayout());
        UITheme.styleFrame(this, 860, 540);

        JPanel formPanel = UITheme.createCardPanel();
        formPanel.setLayout(new GridLayout(4, 2, 10, 10));
        formPanel.add(new JLabel("Client ID"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Name"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Contact Number"));
        formPanel.add(contactField);
        formPanel.add(new JLabel("Email"));
        formPanel.add(emailField);
        UITheme.styleLabelsAndFields(formPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(UITheme.BACKGROUND);
        JButton addButton = UITheme.createPrimaryButton("Add Client");
        JButton updateButton = UITheme.createButton("Update");
        JButton deleteButton = UITheme.createButton("Delete");
        JButton detailsButton = UITheme.createButton("Display Details");
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(detailsButton);

        JTable table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> fillFieldsFromTable(table));

        addButton.addActionListener(e -> addClient());
        updateButton.addActionListener(e -> updateClient());
        deleteButton.addActionListener(e -> deleteClient());
        detailsButton.addActionListener(e -> showClientDetails());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.BACKGROUND);
        topPanel.add(UITheme.createHeader("Client Management", "Maintain retail business client records"), BorderLayout.NORTH);
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setBackground(UITheme.BACKGROUND);
        formWrapper.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 24, 14, 24));
        formWrapper.add(formPanel, BorderLayout.CENTER);
        topPanel.add(formWrapper, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(UITheme.createScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        loadClients();
    }

    private void addClient() {
        String sql = "INSERT INTO clients (name, contact_number, email) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, nameField.getText());
            pst.setString(2, contactField.getText());
            pst.setString(3, emailField.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Client added.");
            loadClients();
            clearFields();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void updateClient() {
        String sql = "UPDATE clients SET name=?, contact_number=?, email=? WHERE client_id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, nameField.getText());
            pst.setString(2, contactField.getText());
            pst.setString(3, emailField.getText());
            pst.setInt(4, Integer.parseInt(idField.getText()));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Client updated.");
            loadClients();
            clearFields();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void deleteClient() {
        String sql = "DELETE FROM clients WHERE client_id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, Integer.parseInt(idField.getText()));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Client deleted.");
            loadClients();
            clearFields();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadClients() {
        tableModel.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT * FROM clients");
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("client_id"),
                        rs.getString("name"),
                        rs.getString("contact_number"),
                        rs.getString("email")
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
            emailField.setText(tableModel.getValueAt(row, 3).toString());
        }
    }

    private void showClientDetails() {
        Client client = new Client(
                parseId(idField.getText()),
                nameField.getText(),
                contactField.getText(),
                emailField.getText()
        );
        JOptionPane.showMessageDialog(this, client.displayDetails());
    }

    private int parseId(String value) {
        if (value.isBlank()) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        contactField.setText("");
        emailField.setText("");
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage());
    }
}
