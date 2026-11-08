package com.seedfinding.minemap.util.ui.interactive;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Makes a JLabel behave like a menu item: highlighted while hovered, and never highlighted while disabled
 * (a disabled label is the currently selected entry and must keep its "disabled" look).
 */
public class LabelController implements MouseListener {

    private final JLabel lbl;
    private boolean hovered = false;

    public LabelController(JLabel lbl) {
        this.lbl = lbl;
        // re-sync the colors whenever the label is enabled/disabled, even if the mouse does not move
        this.lbl.addPropertyChangeListener("enabled", e -> this.refresh());
        this.refresh();
    }

    private void refresh() {
        if (this.hovered && this.lbl.isEnabled()) {
            this.lbl.setBackground(UIManager.getColor("MenuItem.selectionBackground"));
            this.lbl.setForeground(UIManager.getColor("MenuItem.selectionForeground"));
        } else {
            this.lbl.setBackground(UIManager.getColor("MenuItem.background"));
            this.lbl.setForeground(UIManager.getColor("MenuItem.foreground"));
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        MenuSelectionManager.defaultManager().clearSelectedPath();
        this.hovered = false;
        this.refresh();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        this.hovered = true;
        this.refresh();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.hovered = false;
        this.refresh();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }
}
