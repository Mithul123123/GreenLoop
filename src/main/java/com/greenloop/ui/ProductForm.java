package com.greenloop.ui;

import com.greenloop.db.DBConnection;

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

public class ProductForm extends JFrame {
    private final JTextField idField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField priceField = new JTextField();
    private final JTextField ecoRatingField = new JTextField();
    private final DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Price", "Eco Rating"}, 0);
//0 means initia number of raws
    public ProductForm() {
        setTitle("Product Management");
        setLayout(new BorderLayout());
        UITheme.styleFrame(this, 860, 540);

        JPanel formPanel = UITheme.createCardPanel();
        formPanel.setLayout(new GridLayout(4, 2, 10, 10));
        formPanel.add(new JLabel("Product ID"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Product Name"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Price"));
        formPanel.add(priceField);
        formPanel.add(new JLabel("Eco Rating"));
        formPanel.add(ecoRatingField);
        UITheme.styleLabelsAndFields(formPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(UITheme.BACKGROUND);
        JButton addButton = UITheme.createPrimaryButton("Add Product");
        JButton updateButton = UITheme.createButton("Update");
        JButton deleteButton = UITheme.createButton("Delete");
        JButton clearButton = UITheme.createButton("Clear");
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        JTable table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> fillFieldsFromTable(table));

        addButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());
        clearButton.addActionListener(e -> clearFields());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.BACKGROUND);
        topPanel.add(UITheme.createHeader("Product Catalogue", "Add, update, delete, and view eco-packaging products"), BorderLayout.NORTH);
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setBackground(UITheme.BACKGROUND);
        formWrapper.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 24, 14, 24));
        formWrapper.add(formPanel, BorderLayout.CENTER);
        topPanel.add(formWrapper, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(UITheme.createScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        loadProducts();
    }

    private void addProduct() {
        String sql = "INSERT INTO products (product_name, price, eco_rating) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, nameField.getText());
            pst.setDouble(2, Double.parseDouble(priceField.getText()));
            pst.setString(3, ecoRatingField.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Product added.");
            loadProducts();
            clearFields();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void updateProduct() {
        String sql = "UPDATE products SET product_name=?, price=?, eco_rating=? WHERE product_id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, nameField.getText());
            pst.setDouble(2, Double.parseDouble(priceField.getText()));
            pst.setString(3, ecoRatingField.getText());
            pst.setInt(4, Integer.parseInt(idField.getText()));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Product updated.");
            loadProducts();
            clearFields();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void deleteProduct() {
        String sql = "DELETE FROM products WHERE product_id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, Integer.parseInt(idField.getText()));
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Product deleted.");
            loadProducts();
            clearFields();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadProducts() {
        tableModel.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT * FROM products");
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getDouble("price"),
                        rs.getString("eco_rating")
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
            priceField.setText(tableModel.getValueAt(row, 2).toString());
            ecoRatingField.setText(tableModel.getValueAt(row, 3).toString());
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        priceField.setText("");
        ecoRatingField.setText("");
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage());
    }
}
