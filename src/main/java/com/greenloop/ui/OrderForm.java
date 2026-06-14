package com.greenloop.ui;

import com.greenloop.db.DBConnection;
import com.greenloop.model.Order;
import com.greenloop.model.OrderItem;

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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

public class OrderForm extends JFrame {
    private final JComboBox<String> clientBox = new JComboBox<>();
    private final JComboBox<String> productBox = new JComboBox<>();
    private final JTextField quantityField = new JTextField();
    private final JLabel totalLabel = new JLabel("Total: 0.00");
    private final DefaultTableModel itemModel = new DefaultTableModel(new String[]{"Product ID", "Product", "Quantity", "Price", "Line Total"}, 0);
    private final Order currentOrder = new Order();

    public OrderForm() {
        setTitle("Order Management");
        setLayout(new BorderLayout());
        UITheme.styleFrame(this, 920, 580);

        JPanel formPanel = UITheme.createCardPanel();
        formPanel.setLayout(new GridLayout(3, 2, 10, 10));
        formPanel.add(new JLabel("Client"));
        formPanel.add(clientBox);
        formPanel.add(new JLabel("Product"));
        formPanel.add(productBox);
        formPanel.add(new JLabel("Quantity"));
        formPanel.add(quantityField);
        UITheme.styleLabelsAndFields(formPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(UITheme.BACKGROUND);
        JButton addItemButton = UITheme.createPrimaryButton("Add Product");
        JButton calculateButton = UITheme.createButton("Calculate Total");
        JButton saveButton = UITheme.createPrimaryButton("Save Order");
        JButton clearButton = UITheme.createButton("Clear");
        totalLabel.setForeground(UITheme.PRIMARY_DARK);
        totalLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        buttonPanel.add(addItemButton);
        buttonPanel.add(calculateButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(totalLabel);

        addItemButton.addActionListener(e -> addOrderItem());
        calculateButton.addActionListener(e -> calculateTotal());
        saveButton.addActionListener(e -> saveOrder());
        clearButton.addActionListener(e -> clearOrder());

        JTable table = new JTable(itemModel);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.BACKGROUND);
        topPanel.add(UITheme.createHeader("Order Management", "Create orders, select products, and calculate totals"), BorderLayout.NORTH);
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setBackground(UITheme.BACKGROUND);
        formWrapper.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 24, 14, 24));
        formWrapper.add(formPanel, BorderLayout.CENTER);
        topPanel.add(formWrapper, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(UITheme.createScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadClients();
        loadProducts();
        currentOrder.setOrderDate(LocalDate.now());
        currentOrder.setDeliveryStatus("Pending");
    }

    private void loadClients() {
        clientBox.removeAllItems();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT client_id, name FROM clients");
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                clientBox.addItem(rs.getInt("client_id") + " - " + rs.getString("name"));
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadProducts() {
        productBox.removeAllItems();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT product_id, product_name, price FROM products");
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                productBox.addItem(rs.getInt("product_id") + " - " + rs.getString("product_name") + " - " + rs.getDouble("price"));
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void addOrderItem() {
        try {
            String[] productData = productBox.getSelectedItem().toString().split(" - ");
            int productId = Integer.parseInt(productData[0]);
            String productName = productData[1];
            double price = Double.parseDouble(productData[2]);
            int quantity = Integer.parseInt(quantityField.getText());

            OrderItem item = new OrderItem(productId, productName, quantity, price);
            currentOrder.getItems().add(item);//take a arraylist
            itemModel.addRow(new Object[]{productId, productName, quantity, price, item.getLineTotal()});
            quantityField.setText("");
            calculateTotal();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void calculateTotal() {
        currentOrder.calculateTotal();
        totalLabel.setText(String.format("Total: %.2f", currentOrder.getTotalAmount()));
    }

    private void saveOrder() {
        if (currentOrder.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one product.");
            return;
        }

        String orderSql = "INSERT INTO orders (client_id, order_date, total_amount, delivery_status) VALUES (?, ?, ?, ?)";
        String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        String stockSql = "UPDATE inventory SET quantity = quantity - ? WHERE product_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement orderPst = con.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
            // The order and its items should be saved together.
            con.setAutoCommit(false);
            currentOrder.setClientId(getSelectedClientId());
            currentOrder.calculateTotal();

            orderPst.setInt(1, currentOrder.getClientId());
            orderPst.setDate(2, Date.valueOf(currentOrder.getOrderDate()));
            orderPst.setDouble(3, currentOrder.getTotalAmount());
            orderPst.setString(4, currentOrder.getDeliveryStatus());
            orderPst.executeUpdate();

            ResultSet keys = orderPst.getGeneratedKeys();
            if (keys.next()) {
                currentOrder.setOrderId(keys.getInt(1));
            }

            try (PreparedStatement itemPst = con.prepareStatement(itemSql);
                 PreparedStatement stockPst = con.prepareStatement(stockSql)) {
                for (OrderItem item : currentOrder.getItems()) {
                    itemPst.setInt(1, currentOrder.getOrderId());
                    itemPst.setInt(2, item.getProductId());
                    itemPst.setInt(3, item.getQuantity());
                    itemPst.setDouble(4, item.getPrice());
                    itemPst.executeUpdate();

                    // Reduce available stock after each ordered product is saved.
                    stockPst.setInt(1, item.getQuantity());
                    stockPst.setInt(2, item.getProductId());
                    stockPst.executeUpdate();
                }
            }

            con.commit();
            JOptionPane.showMessageDialog(this, "Order saved with ID: " + currentOrder.getOrderId());
            clearOrder();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private int getSelectedClientId() {
        String selected = clientBox.getSelectedItem().toString();
        return Integer.parseInt(selected.split(" - ")[0]);
    }

    private void clearOrder() {
        currentOrder.getItems().clear();
        currentOrder.setOrderId(0);
        currentOrder.setOrderDate(LocalDate.now());
        itemModel.setRowCount(0);
        quantityField.setText("");
        totalLabel.setText("Total: 0.00");
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage());
    }
}
