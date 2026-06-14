package com.greenloop;

import com.greenloop.ui.LoginFrame;
import com.greenloop.ui.UITheme;

import javax.swing.SwingUtilities;

public class GreenLoopApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {//use for repainting issues
            UITheme.setupLookAndFeel();
            new LoginFrame().setVisible(true);
        });
    }
}
