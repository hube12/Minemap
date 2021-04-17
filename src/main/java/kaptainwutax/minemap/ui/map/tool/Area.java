package kaptainwutax.minemap.ui.map.tool;

import kaptainwutax.minemap.util.math.DisplayMaths;
import kaptainwutax.mcutils.util.pos.BPos;

import java.awt.*;
import java.util.Arrays;


public class Area extends Tool {
    private BPos pos1 = null;
    private BPos pos2 = null;
    private BPos pos3 = null;
    private BPos pos4 = null;
    private int pointsTraced = 0;
    private Color color;

    public Area() {
        color = DisplayMaths.getRandomColor();
    }

    @Override
    public boolean addPoint(BPos bpos) {
        switch (pointsTraced) {
            case 0:
                pos1 = bpos;
                break;
            case 1:
                pos2 = bpos;
                break;
            case 2:
                pos3 = bpos;
                break;
            case 3:
                pos4 = bpos;
                break;
            default:
                return false;
        }
        pointsTraced++;
        return true;
    }


    @Override
    public Polygon getPartialShape() {
        int offset = 5;
        switch (this.getPointsTraced()) {
            case 1:
                return DisplayMaths.getPolygon(pos1, offset);
            case 2:
                return DisplayMaths.getPolygon(pos1, pos2, offset);
            case 3:
                return DisplayMaths.getPolygon(pos1, pos2, pos3);
            case 4:
                return DisplayMaths.getPolygon(pos1, pos2, pos3, pos4);
        }
        return null;
    }

    @Override
    public int getPointsTraced() {
        return pointsTraced;
    }

    @Override
    public boolean isComplete() {
        return this.getPointsTraced() == 4 && pos1 != null && pos2 != null && pos3 != null && pos4 != null;
    }

    @Override
    public boolean isAcceptable() {
        return this.getPointsTraced() >= 3 && pos1 != null && pos2 != null && pos3 != null;
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
            case 3:
                return pos1 != null && pos2 != null && pos3 != null;
            case 4:
                return pos1 != null && pos2 != null && pos3 != null && pos4 != null;
        }
        return false;
    }

    @Override
    public void reset() {
        pointsTraced = 0;
        pos1 = null;
        pos2 = null;
    }

    @Override
    public double getMetric() {
        double metric = 0;
        if (this.isComplete()) {
            metric = DisplayMaths.polygonArea(Arrays.asList(pos1, pos2, pos3, pos4));
        }
        if (this.getPointsTraced() >= 3) {
            metric = DisplayMaths.polygonArea(Arrays.asList(pos1, pos2, pos3));
        }
        return DisplayMaths.round(metric, 2);
    }

    @Override
    public String[] getMetricString() {
        return new String[] {
                String.format("Area: %.2f blocks sq", this.getMetric())
        };
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
        return new Area();
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
    public String getName() {
        return "Area";
    }

    @Override
    public String toString() {
        return "Square{" +
                "pos1=" + pos1 +
                ", pos2=" + pos2 +
                ", pos3=" + pos3 +
                ", pos4=" + pos4 +
                ", pointsTraced=" + pointsTraced +
                ", color=" + color +
                '}';
    }
}
