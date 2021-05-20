package kaptainwutax.minemap.util.ui.buttons;

import java.awt.*;

public class LockButton extends ButtonIcon {

    public LockButton(int size, int inset, float factor, boolean background, Color backgroundColor, boolean border) {
        super(size, inset, factor, background, backgroundColor, border);
    }

    public LockButton(int size, int inset, float factor, boolean background, Color backgroundColor) {
        super(size, inset, factor, background, backgroundColor);
    }

    public LockButton(int size, int inset, float factor) {
        super(size, inset, factor);
    }

    public LockButton(int size, int inset) {
        super(size, inset);
    }

    public LockButton(int size) {
        super(size);
    }

    public LockButton() {
        super();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
