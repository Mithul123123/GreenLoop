package com.greenloop.ui;

import com.greenloop.db.DBConnection;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReportForm extends JFrame {
    private final DefaultTableModel tableModel = new DefaultTableModel();

    public ReportForm() {
        setTitle("Reports");
        setLayout(new BorderLayout());
        UITheme.styleFrame(this, 900, 540);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(UITheme.BACKGROUND);
        buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 24, 14, 24));
        JButton monthlySalesButton = UITheme.createPrimaryButton("Monthly Sales Summary");
        JButton lowStockButton = UITheme.createButton("Low Stock Report");
        JButton revenueButton = UITheme.createButton("Revenue Report");
        buttonPanel.add(monthlySalesButton);
        buttonPanel.add(lowStockButton);
        buttonPanel.add(revenueButton);

        monthlySalesButton.addActionListener(e -> loadMonthlySales());
        lowStockButton.addActionListener(e -> loadLowStock());
        revenueButton.addActionListener(e -> loadRevenue());

        JTable table = new JTable(tableModel);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.BACKGROUND);
        topPanel.add(UITheme.createHeader("Reports", "Review sales, revenue, and low stock items"), BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(UITheme.createScrollPane(table), BorderLayout.CENTER);
        loadMonthlySales();
    }

    private void loadMonthlySales() {
        tableModel.setColumnIdentifiers(new String[]{"Month", "Orders", "Sales Total"});
        tableModel.setRowCount(0);
        String sql = "SELECT FORMAT(order_date, 'yyyy-MM') AS sale_month, COUNT(*) AS order_count, SUM(total_amount) AS sales_total FROM orders GROUP BY FORMAT(order_date, 'yyyy-MM') ORDER BY sale_month DESC;";
        loadReport(sql, "sale_month", "order_count", "sales_total");
    }

    private void loadLowStock() {
        tableModel.setColumnIdentifiers(new String[]{"Product", "Quantity", "Reorder Level"});
        tableModel.setRowCount(0);
        String sql = "SELECT p.product_name, i.quantity, i.reorder_level FROM inventory i " +
                "JOIN products p ON i.product_id = p.product_id WHERE i.quantity <= i.reorder_level";
        loadReport(sql, "product_name", "quantity", "reorder_level");
    }

    private void loadRevenue() {
        tableModel.setColumnIdentifiers(new String[]{"Total Orders", "Total Revenue", "Average Order Value"});
        tableModel.setRowCount(0);
        String sql = "SELECT COUNT(*) AS total_orders, SUM(total_amount) AS total_revenue, " +
                "AVG(total_amount) AS average_order_value FROM orders";
        loadReport(sql, "total_orders", "total_revenue", "average_order_value");
    }

    private void loadReport(String sql, String columnOne, String columnTwo, String columnThree) {
        // One helper method is reused by all three report buttons.
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getObject(columnOne),
                        rs.getObject(columnTwo),
                        rs.getObject(columnThree)
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
