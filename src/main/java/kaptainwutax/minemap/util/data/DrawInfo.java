package kaptainwutax.minemap.util.data;

import java.awt.*;

public class DrawInfo {

    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public DrawInfo(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle getRectangle() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public String toString() {
        return "DrawInfo{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
