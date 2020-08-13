package kaptainwutax.minemap.ui.component;

import kaptainwutax.minemap.listener.Events;

import javax.swing.*;
import javax.swing.plaf.metal.MetalIconFactory;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class TabHeader extends JPanel {

    public TabHeader(String title, Consumer<MouseEvent> onClose) {
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));

        JLabel tabTitle = new JLabel(title);
        JButton closeButton = new JButton(MetalIconFactory.getInternalFrameCloseIcon(16));
        closeButton.addMouseListener(Events.Mouse.onClick(onClose));

        this.add(tabTitle);
        this.add(closeButton);
        this.setOpaque(false);
    }

}
