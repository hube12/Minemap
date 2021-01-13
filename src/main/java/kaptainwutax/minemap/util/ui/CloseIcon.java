package kaptainwutax.minemap.util.ui;

import java.awt.*;

public class CloseIcon extends ButtonIcon {
    public CloseIcon(int size, int inset,float factor) {
        super(size,inset,factor,CloseIcon.class);
    }

    public CloseIcon() {
        super(CloseIcon.class);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
