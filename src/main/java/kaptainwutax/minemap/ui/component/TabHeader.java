package kaptainwutax.minemap.ui.component;

import kaptainwutax.minemap.listener.Events;

import javax.swing.*;
import javax.swing.plaf.metal.MetalIconFactory;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class TabHeader extends JPanel {

    protected JLabel tabTitle;
    protected JButton closeButton;

    public TabHeader(String title, Consumer<MouseEvent> onClose) {
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));
        this.createTabTitle(title);
        this.createCloseButton(onClose);
        this.setOpaque(false);
    }

    public JLabel getTabTitle() {
        return this.tabTitle;
    }

    public JButton getCloseButton() {
        return this.closeButton;
    }

    protected void createTabTitle(String title) {
        this.add(this.tabTitle = new JLabel(title));
    }

    protected void createCloseButton(Consumer<MouseEvent> onClose) {
        this.closeButton = new JButton(MetalIconFactory.getInternalFrameCloseIcon(14));
        this.closeButton.addMouseListener(Events.Mouse.onClick(onClose));
        this.add(this.closeButton);
    }

}
