package kaptainwutax.minemap.ui;

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
