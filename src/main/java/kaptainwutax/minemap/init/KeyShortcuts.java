package kaptainwutax.minemap.init;

import java.awt.*;
import java.awt.event.KeyEvent;

public class KeyShortcuts {
    public static void registerShortcuts() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(new KeyEventDispatcher() {
                    @Override
                    public boolean dispatchKeyEvent(KeyEvent e) {
                        System.out.println(e.paramString());
                        return false;
                    }

                });
    }
}
