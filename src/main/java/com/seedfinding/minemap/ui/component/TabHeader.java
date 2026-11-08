package com.seedfinding.minemap.ui.component;

import com.seedfinding.minemap.MineMap;
import com.seedfinding.minemap.init.Configs;
import com.seedfinding.minemap.listener.Events;
import com.seedfinding.minemap.ui.map.MapContext;
import com.seedfinding.minemap.util.ui.buttons.ButtonIcon;
import com.seedfinding.minemap.util.ui.buttons.CloseButton;
import com.seedfinding.minemap.util.ui.buttons.LockButton;
import com.seedfinding.minemap.util.ui.interactive.ExtendedTabbedPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class TabHeader extends ExtendedTabbedPane.TabbedPaneHeader {
    private static final int TAB_BUTTON_BOX = 26;
    protected JLabel tabTitle;
    protected JButton closeButton;
    protected JButton lockButton;
    protected boolean isSaved;

    public TabHeader(String title, Consumer<MouseEvent> onClose) {
        this.setLayout(new GridBagLayout());
        this.createTabTitle(title);
        this.createCloseButton(onClose);
        this.isSaved = false;
        this.setOpaque(false);
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean state) {
        this.setSaved(state, true);
    }

    public void setSaved(boolean state, boolean shouldSave) {
        MapContext context = MineMap.INSTANCE.worldTabs.getSelectedMapPanel().context;
        if (state) {
            if (shouldSave) {
                Configs.USER_PROFILE.addSavedSeeds(context.getWorldSeed(), context.getVersion(), context.getDimension());
            }
            this.remove(closeButton);
            this.add(lockButton);
        } else {
            if (shouldSave) {
                Configs.USER_PROFILE.removeSavedSeeds(context.getWorldSeed(), context.getVersion(), context.getDimension());
            }
            this.remove(lockButton);
            this.add(closeButton);
        }
        this.isSaved = state;
        this.repaint();
    }


    public void togglePinned() {
        this.setSaved(!this.isSaved);
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
        this.tabTitle = new JLabel(title);
        this.tabTitle.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
        this.add(this.tabTitle);
    }

    protected void createCloseButton(Consumer<MouseEvent> onClose) {
        this.closeButton = new CloseButton(11, 5, 1.5F);
        this.lockButton = new LockButton(11, 7, 1.2F, true, new Color(143, 219, 209, 117), false);
        ((ButtonIcon) this.closeButton).setBoxSize(TAB_BUTTON_BOX);
        ((ButtonIcon) this.lockButton).setBoxSize(TAB_BUTTON_BOX);
        this.closeButton.addMouseListener(Events.Mouse.onPressed(onClose));
        if (this.isSaved()) {
            this.add(this.lockButton);
        } else {
            this.add(this.closeButton);
        }
    }

}
