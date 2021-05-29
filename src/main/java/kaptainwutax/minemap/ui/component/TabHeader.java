package kaptainwutax.minemap.ui.component;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.util.ui.buttons.CloseButton;
import kaptainwutax.minemap.util.ui.buttons.LockButton;
import kaptainwutax.minemap.util.ui.interactive.ExtendedTabbedPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class TabHeader extends ExtendedTabbedPane.TabbedPaneHeader {

    protected JLabel tabTitle;
    protected JButton closeButton;
    protected JButton lockButton;
    protected boolean isPinned;

    public TabHeader(String title, Consumer<MouseEvent> onClose) {
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));
        this.createTabTitle(title);
        this.createCloseButton(onClose);
        this.isPinned=false;
        this.setOpaque(false);
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean state) {
        this.setPinned(state,true);
    }
    public void setPinned(boolean state,boolean shouldSave) {
        MapContext context=MineMap.INSTANCE.worldTabs.getSelectedMapPanel().context;
        if (state){
            if (shouldSave){
                Configs.USER_PROFILE.addPinnedSeed(context.getWorldSeed(),context.getVersion(),context.getDimension());
            }
            this.remove(closeButton);
            this.add(lockButton);
        }else{
            if (shouldSave){
                Configs.USER_PROFILE.removePinnedSeed(context.getWorldSeed(),context.getVersion(),context.getDimension());
            }
            this.remove(lockButton);
            this.add(closeButton);
        }
        this.isPinned=state;
        this.repaint();
    }



    public void togglePinned() {
        this.setPinned(!this.isPinned);
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
        this.lockButton = new LockButton(12, 7, 1.2F,true,new Color(143, 219, 209, 117),false);
        this.closeButton.addMouseListener(Events.Mouse.onPressed(onClose));
        this.add(this.closeButton);
    }

}
