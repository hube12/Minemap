package kaptainwutax.minemap.ui.map.tool;

import kaptainwutax.mcutils.util.pos.BPos;

import java.awt.*;
import java.util.List;

public abstract class Tool {

    public abstract int getPointsTraced();

    public abstract boolean addPoint(BPos bpos);

    public abstract Shape getPartialShape();

    public abstract List<Shape> getPartialShapes();

    public abstract Shape getExactShape();

    public abstract List<Shape> getExactShapes();

    public abstract boolean isComplete();

    public abstract boolean isAcceptable();

    public abstract boolean isPartial();

    public abstract void reset();

    public abstract double getMetric();

    public abstract String[] getMetricString();

    public abstract boolean shouldFill();

    // allow to use technics to hide the fragment sides
    public abstract boolean shouldHideArtefact();

    public abstract Tool duplicate();

    public abstract Color getColor();

    public abstract void setColor(Color color);

    public abstract String getName();

    public abstract boolean isMultiplePolygon();
}
