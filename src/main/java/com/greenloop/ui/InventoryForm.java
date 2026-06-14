package com.greenloop.ui;

import com.greenloop.db.DBConnection;

import javax.swing.JButton;
import javax.swing.JComboBox;
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

public class InventoryForm extends JFrame {
    private final JTextField idField = new JTextField();
    private final JComboBox<String> productBox = new JComboBox<>();
    private final JTextField quantityField = new JTextField();
    private final JTextField reorderField = new JTextField();
    private final DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "Product", "Quantity", "Reorder Level"}, 0);

    public InventoryForm() {
        setTitle("Inventory Management");
        setLayout(new BorderLayout());
        UITheme.styleFrame(this, 900, 540);

        JPanel formPanel = UITheme.createCardPanel();
        formPanel.setLayout(new GridLayout(4, 2, 10, 10));
        formPanel.add(new JLabel("Inventory ID"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Product"));
        formPanel.add(productBox);
        formPanel.add(new JLabel("Quantity"));
        formPanel.add(quantityField);
        formPanel.add(new JLabel("Reorder Level"));
        formPanel.add(reorderField);
        UITheme.styleLabelsAndFields(formPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(UITheme.BACKGROUND);
        JButton addButton = UITheme.createPrimaryButton("Add Stock");
        JButton updateButton = UITheme.createButton("Update Quantity");
        JButton refreshButton = UITheme.createButton("View Available Stock");
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(refreshButton);

        JTable table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> fillFieldsFromTable(table));

        addButton.addActionListener(e -> addStock());
        updateButton.addActionListener(e -> updateStock());
        refreshButton.addActionListener(e -> loadInventory());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.BACKGROUND);
        topPanel.add(UITheme.createHeader("Inventory Management", "Track available stock and reorder levels"), BorderLayout.NORTH);
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setBackground(UITheme.BACKGROUND);
        formWrapper.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 24, 14, 24));
        formWrapper.add(formPanel, BorderLayout.CENTER);
        topPanel.add(formWrapper, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(UITheme.createScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        loadProducts();
        loadInventory();
    }

    private void loadProducts() {
        productBox.removeAllItems();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT product_id, product_name FROM products");
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                productBox.addItem(rs.getInt("product_id") + " - " + rs.getString("product_name"));
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void addStock() {
        String sql = "INSERT INTO inventory (product_id, quantity, reorder_level) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, getSelectedProductId());
            pst.setInt(2, Integer.parseInt(quantityField.getText()));
            pst.setInt(3, Integer.parseInt(reorderField.getText()));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Stock added.");
            loadInventory();
            clearFields();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void updateStock() {
        String sql = "UPDATE inventory SET product_id=?, quantity=?, reorder_level=? WHERE inventory_id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, getSelectedProductId());
            pst.setInt(2, Integer.parseInt(quantityField.getText()));
            pst.setInt(3, Integer.parseInt(reorderField.getText()));
            pst.setInt(4, Integer.parseInt(idField.getText()));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Stock updated.");
            loadInventory();
            clearFields();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadInventory() {
        tableModel.setRowCount(0);
        String sql = "SELECT i.inventory_id, p.product_name, i.quantity, i.reorder_level " +
                "FROM inventory i JOIN products p ON i.product_id = p.product_id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("inventory_id"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getInt("reorder_level")
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
            quantityField.setText(tableModel.getValueAt(row, 2).toString());
            reorderField.setText(tableModel.getValueAt(row, 3).toString());
        }
    }

    private int getSelectedProductId() {
        String selected = productBox.getSelectedItem().toString();
        return Integer.parseInt(selected.split(" - ")[0]);
    }

    private void clearFields() {
        idField.setText("");
        quantityField.setText("");
        reorderField.setText("");
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage());
    }
}
