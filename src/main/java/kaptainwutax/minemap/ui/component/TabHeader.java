package kaptainwutax.minemap.ui.component;

import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.util.ui.buttons.CloseButton;

import javax.swing.*;
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

    public boolean isPinned() {
        return !this.closeButton.isVisible();
    }

    public void setPinned(boolean state) {
        this.closeButton.setVisible(!state);
    }

    public String getName() {
        return this.tabTitle.getText();
    }

    public void setName(String name) {
        this.tabTitle.setText(name);
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
        this.closeButton = new CloseButton(12, 5, 1.5F);
        this.closeButton.addMouseListener(Events.Mouse.onPressed(onClose));
        this.add(this.closeButton);
    }

}
