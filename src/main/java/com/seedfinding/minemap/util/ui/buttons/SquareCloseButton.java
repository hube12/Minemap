package com.seedfinding.minemap.util.ui.buttons;

import java.awt.*;

public class SquareCloseButton extends ButtonIcon {

    public SquareCloseButton(int size, int inset, float factor, boolean background, Color backgroundColor, boolean border) {
        super(size, inset, factor, background, backgroundColor, border);
    }

    public SquareCloseButton(int size, int inset, float factor, boolean background, Color backgroundColor) {
        super(size, inset, factor, background, backgroundColor);
    }

    public SquareCloseButton(int size, int inset, float factor) {
        super(size, inset, factor);
    }

    public SquareCloseButton(int size, int inset) {
        super(size, inset);
    }

    public SquareCloseButton(int size) {
        super(size);
    }

    public SquareCloseButton() {
        super();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
