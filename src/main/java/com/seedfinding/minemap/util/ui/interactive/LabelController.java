package com.seedfinding.minemap.util.ui.interactive;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class LabelController implements MouseListener {

    JLabel lbl;

    public LabelController(JLabel lbl) {
        this.lbl = lbl;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        String selectedText = lbl.getText().replaceAll("<[^>]*>", "").replace("&ensp;", "").trim();
        // you can get the original text here if you want
        javax.swing.MenuSelectionManager.defaultManager().clearSelectedPath();
        lbl.setBackground(UIManager.getColor("MenuItem.background"));
        lbl.setForeground(UIManager.getColor("MenuItem.foreground"));
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        lbl.setBackground(UIManager.getColor("MenuItem.selectionBackground"));
        lbl.setForeground(UIManager.getColor("MenuItem.selectionForeground"));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        lbl.setBackground(UIManager.getColor("MenuItem.background"));
        lbl.setForeground(UIManager.getColor("MenuItem.foreground"));
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }
}
