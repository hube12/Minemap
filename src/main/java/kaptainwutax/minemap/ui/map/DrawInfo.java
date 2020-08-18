package kaptainwutax.minemap.ui.map;

import java.awt.*;

public class DrawInfo {

    public final Graphics g;
    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public DrawInfo(Graphics g, int x, int y, int width, int height) {
        this.g = g;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

}
