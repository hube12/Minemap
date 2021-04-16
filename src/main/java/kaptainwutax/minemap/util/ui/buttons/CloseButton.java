package kaptainwutax.minemap.util.ui.buttons;

import java.awt.*;

public class CloseButton extends ButtonIcon {

    public CloseButton(int size, int inset, float factor, boolean background, Color backgroundColor, boolean border) {
        super(size, inset, factor, background, backgroundColor, border);
    }

    public CloseButton(int size, int inset, float factor, boolean background, Color backgroundColor) {
        super(size, inset, factor, background, backgroundColor);
    }

    public CloseButton(int size, int inset, float factor) {
        super(size, inset, factor);
    }

    public CloseButton(int size, int inset) {
        super(size, inset);
    }

    public CloseButton(int size) {
        super(size);
    }

    public CloseButton() {
        super();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
