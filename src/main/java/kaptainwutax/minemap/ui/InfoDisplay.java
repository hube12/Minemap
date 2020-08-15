package kaptainwutax.minemap.ui;

import java.awt.*;
import javax.swing.*;


public class InfoDisplay {
    public InfoDisplay() {
        initComponents();
    }

    private void initComponents() {
        //GEN-BEGIN:initComponents
        DisplayContent = new JLabel();

        //---- DisplayContent ----
        DisplayContent.setFocusable(false);
        DisplayContent.setOpaque(true);
        DisplayContent.setVerticalAlignment(SwingConstants.TOP);
        DisplayContent.setHorizontalAlignment(SwingConstants.LEFT);
        DisplayContent.setBackground(new Color(0, 0, 0, 127));
        DisplayContent.setForeground(Color.white);
        DisplayContent.setHorizontalTextPosition(SwingConstants.LEFT);
        DisplayContent.setFont(new Font(".SF NS Text", Font.BOLD, 14));
        //GEN-END:initComponents
    }

    //GEN-BEGIN:variables
    public JLabel DisplayContent;
    //GEN-END:variables
}
