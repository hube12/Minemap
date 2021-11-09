package com.seedfinding.minemap.util.ui.buttons;

import java.awt.*;

public class UnknownButton extends ButtonIcon {
    public UnknownButton(int size, int inset, float factor, boolean background, Color backgroundColor, boolean border) {
        super(size, inset, factor, background, backgroundColor, border);
    }

    public UnknownButton(int size, int inset, float factor, boolean background, Color backgroundColor) {
        super(size, inset, factor, background, backgroundColor);
    }

    public UnknownButton(int size, int inset, float factor) {
        super(size, inset, factor);
    }

    public UnknownButton(int size, int inset) {
        super(size, inset);
    }

    public UnknownButton(int size) {
        super(size);
    }

    public UnknownButton() {
        super();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
