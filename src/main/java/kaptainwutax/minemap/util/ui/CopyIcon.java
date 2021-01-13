package kaptainwutax.minemap.util.ui;

import java.awt.*;

public class CopyIcon extends ButtonIcon {
    public CopyIcon(int size, int inset, float factor) {
        super(size,inset,factor,true,new Color(151, 167, 152,200), CopyIcon.class);
    }

    public CopyIcon() {
        super(CopyIcon.class);
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
