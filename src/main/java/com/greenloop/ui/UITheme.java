package com.greenloop.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;

public class UITheme {
    public static final Color BACKGROUND = new Color(244, 247, 245);
    public static final Color PANEL = Color.WHITE;
    public static final Color PRIMARY = new Color(20, 126, 87);
    public static final Color PRIMARY_DARK = new Color(13, 86, 60);
    public static final Color TEXT = new Color(31, 41, 55);
    public static final Color MUTED_TEXT = new Color(107, 114, 128);
    public static final Color BORDER = new Color(220, 229, 224);

    private UITheme() {
    }

    public static void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ex) {
            System.out.println("Default look and feel is used.");
        }
    }

    public static void styleFrame(JFrame frame, int width, int height) {
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(BACKGROUND);
    }

    public static JLabel createHeader(String title, String subtitle) {
        JLabel label = new JLabel("<html><div style='font-size:22px;font-weight:bold;color:#1f2937;'>"
                + title + "</div><div style='font-size:11px;color:#6b7280;padding-top:4px;'>"
                + subtitle + "</div></html>");
        label.setBorder(new EmptyBorder(18, 24, 16, 24));
        return label;
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(18, 20, 18, 20)
        ));
        return panel;
    }

    public static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setBackground(new Color(232, 241, 236));
        button.setForeground(TEXT);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(8, 14, 8, 14)
        ));
        return button;
    }

    public static JButton createPrimaryButton(String text) {
        JButton button = createButton(text);
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_DARK),
                new EmptyBorder(9, 16, 9, 16)
        ));
        return button;
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT);
        table.setSelectionBackground(new Color(208, 240, 224));//after selecting a row
        table.setSelectionForeground(TEXT);
        table.setGridColor(new Color(232, 238, 235));
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(PRIMARY_DARK);
        header.setForeground(Color.WHITE);
    }

    public static JScrollPane createScrollPane(JTable table) {
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    public static void styleLabelsAndFields(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JLabel label) {
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                label.setForeground(MUTED_TEXT);
            } else if (component instanceof JTextField field) {
                styleTextField(field);
            } else if (component instanceof JComboBox<?> comboBox) {
                comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                comboBox.setBackground(Color.WHITE);
                comboBox.setForeground(TEXT);
            } else if (component instanceof Container child) {
                styleLabelsAndFields(child);
            }
        }
    }

    public static void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(7, 9, 7, 9)
        ));
    }

    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 28));
        label.setForeground(PRIMARY_DARK);
        return label;
    }

    public static void addOuterPadding(JComponent component)
    {
        component.setBorder(new EmptyBorder(0, 24, 18, 24));
    }
}
