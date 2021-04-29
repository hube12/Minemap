package kaptainwutax.minemap.ui.menubar;

import kaptainwutax.minemap.listener.Events;

import javax.swing.*;
import java.awt.event.KeyEvent;

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

    public void addMouseAndKeyListener(JMenuItem item,Runnable runnableMouse, Runnable runnableKeyboard, boolean immediate){
        item.addMouseListener(Events.Mouse.onPressed(e -> {
            if (immediate){
                runnableMouse.run();
            }else{
                SwingUtilities.invokeLater(runnableMouse);
            }
        }));
        item.addMenuKeyListener(Events.MenuKey.onPressed(e -> {
            if (item.isArmed() && e.getKeyCode()== KeyEvent.VK_ENTER){
                if (immediate){
                    runnableKeyboard.run();
                }else{
                    SwingUtilities.invokeLater(runnableKeyboard);
                }
            }
        }));
    }
}
