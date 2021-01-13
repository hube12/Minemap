package kaptainwutax.minemap.util.ui.buttons;

import java.awt.*;

public class CopyButton extends ButtonIcon {
    public CopyButton(int size, int inset, float factor, boolean background, Color backgroundColor) {
        super(size, inset, factor, background, backgroundColor);
    }

    public CopyButton(int size, int inset, float factor) {
        super(size, inset, factor);
    }

    public CopyButton(int size, int inset) {
        super(size, inset);
    }

    public CopyButton(int size) {
        super(size);
    }

    public CopyButton() {
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

    }

    @Override
    public void changeBColor(Color color) {
        super.changeBColor(color);
    }
}
