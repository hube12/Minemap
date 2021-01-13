package kaptainwutax.minemap.util.ui.buttons;

import java.awt.*;

public class JumpButton extends ButtonIcon {
    public JumpButton(int size, int inset, float factor, boolean background, Color backgroundColor) {
        super(size, inset, factor, background, backgroundColor);
    }

    public JumpButton(int size, int inset, float factor) {
        super(size, inset, factor);
    }

    public JumpButton(int size, int inset) {
        super(size, inset);
    }

    public JumpButton(int size) {
        super(size);
    }

    public JumpButton() {
    }

    @Override
    public void changeBColor(Color color) {
        super.changeBColor(color);
    }
}
