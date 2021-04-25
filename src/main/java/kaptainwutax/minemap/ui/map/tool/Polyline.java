package kaptainwutax.minemap.ui.map.tool;

import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.minemap.util.math.DisplayMaths;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class Polyline extends Tool {
    private final List<BPos> bPosList = new ArrayList<>();
    private int pointsTraced = 0;
    private Color color;

    public Polyline() {
        color = DisplayMaths.getRandomColor();
    }

    @Override
    public boolean addPoint(BPos bpos) {
        bPosList.add(bpos);
        pointsTraced++;
        return true;
    }

    @Override
    public Polygon getPartialShape() {
        return null;
    }

    @Override
    public List<Shape> getPartialShapes() {
        int offset = 5;
        if (bPosList.size() > 1) {
            return DisplayMaths.getPolylinePolygon(bPosList, offset);
        }
        return null;
    }

    @Override
    public Polygon getExactShape() {
        return null;
    }

    @Override
    public List<Shape> getExactShapes() {
        int offset = 2;
        if (bPosList.size() > 1) {
            return DisplayMaths.getPolylinePolygon(bPosList, offset);
        }
        return null;
    }

    @Override
    public int getPointsTraced() {
        return pointsTraced;
    }

    @Override
    public boolean isComplete() {
        return bPosList.size() > 1;
    }

    @Override
    public boolean isAcceptable() {
        return bPosList.size() > 1;
    }

    @Override
    public boolean isPartial() {
        return bPosList.size() > 1;
    }

    @Override
    public void reset() {
        pointsTraced = 0;
        bPosList.clear();
    }

    @Override
    public double getMetric() {
        double metric = 0.0D;
        for (int i = 0; i < bPosList.size() - 1; i++) {
            BPos pos1 = bPosList.get(i);
            BPos pos2 = bPosList.get(i + 1);
            metric += DisplayMaths.getDistance2D(pos1, pos2);
        }
        return DisplayMaths.round(metric, 2);
    }

    @Override
    public String[] getMetricString() {
        return new String[] {
            String.format("Polyline: %.2f blocks", this.getMetric())
        };
    }

    @Override
    public boolean shouldFill() {
        return true;
    }

    @Override
    public boolean shouldHideArtefact() {
        return false;
    }

    @Override
    public Tool duplicate() {
        return new Polyline();
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
        return "Polyline";
    }

    @Override
    public boolean isMultiplePolygon() {
        return true;
    }

    @Override
    public String toString() {
        return "Polyline{" +
            "bPosList=" + bPosList +
            ", pointsTraced=" + pointsTraced +
            ", color=" + color +
            '}';
    }
}
