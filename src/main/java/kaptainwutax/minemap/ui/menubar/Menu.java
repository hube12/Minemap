package kaptainwutax.minemap.ui.menubar;

import javax.swing.*;

public abstract class Menu {
    protected JMenu menu;
    private boolean isActive = false;
    protected final Runnable deactivate = () -> isActive = false;
    protected final Runnable activate = () -> isActive = true;

    public boolean isActive() {
        return isActive;
    }

    public JMenu getMenu() {
        return menu;
    }

    public abstract void doDelayedLabels();
}
