package com.seedfinding.minemap.util.data;

import java.awt.*;

public record DrawInfo(int x, int y, int width, int height) {

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
