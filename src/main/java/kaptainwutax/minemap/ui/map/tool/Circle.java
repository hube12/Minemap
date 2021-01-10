package kaptainwutax.minemap.ui.map.tool;

import kaptainwutax.minemap.util.DisplayMaths;
import kaptainwutax.seedutils.mc.pos.BPos;

import java.awt.*;
import java.util.Random;


public class Circle extends Tool {
    private BPos pos1 = null;
    private BPos pos2 = null;
    private int pointsTraced = 0;
    private Color color;

    public Circle() {
        // you could crack that seed ;)
        Random rand = new Random();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        color = new Color(r, g, b);
    }

    public boolean addPoint(BPos bpos) {
        switch (pointsTraced) {
            case 0:
                pos1 = bpos;
                break;
            case 1:
                pos2 = bpos;
                break;
            default:
                return false;
        }
        pointsTraced++;
        return true;
    }

    @Override
    public Shape getPartialShape() {
        int offset = 5;
        switch (this.getPointsTraced()) {
            case 1:
                return DisplayMaths.getPolygon(pos1, offset);
            case 2:
                return DisplayMaths.getCircle(pos1, pos2);
        }
        return null;
    }

    public int getPointsTraced() {
        return pointsTraced;
    }

    public boolean isComplete() {
        return this.getPointsTraced() == 2 && pos1 != null && pos2 != null;
    }

    @Override
    public boolean isPartial() {
        switch (this.getPointsTraced()) {
            case 0:
                return false;
            case 1:
                return pos1 != null;
            case 2:
                return pos1 != null && pos2 != null;
        }
        return false;
    }

    public void reset() {
        pointsTraced = 0;
        pos1 = null;
        pos2 = null;
    }

    public double getMetric() {
        if (this.isComplete()) {
            double metric=DisplayMaths.circleArea(pos1, pos2);
            return DisplayMaths.round(metric, 2);
        }
        return 0;
    }

    public String getMetricString() {
        return this.getMetric() + " blocks sq";
    }

    @Override
    public boolean shouldFill() {
        return false;
    }

    @Override
    public boolean shouldHideArtefact() {
        return true;
    }

    @Override
    public Tool duplicate() {
        return new Circle();
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Circle{" +
                "pos1=" + pos1 +
                ", pos2=" + pos2 +
                ", pointsTraced=" + pointsTraced +
                ", color=" + color +
                '}';
    }
}
