package kaptainwutax.minemap.util.ui.buttons;

import java.awt.*;

public class InfoButton extends ButtonIcon {
    public InfoButton(int size, int inset, float factor, boolean background, Color backgroundColor) {
        super(size, inset, factor, background, backgroundColor);
    }

    public InfoButton(int size, int inset, float factor) {
        super(size, inset, factor);
    }

    public InfoButton(int size, int inset) {
        super(size, inset);
    }

    public InfoButton(int size) {
        super(size);
    }

    public InfoButton() {
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
