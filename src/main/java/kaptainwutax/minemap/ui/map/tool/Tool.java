package kaptainwutax.minemap.ui.map.tool;

import kaptainwutax.seedutils.mc.pos.BPos;

import java.awt.*;

public abstract class Tool implements Cloneable {

    public abstract int getPointsTraced();

    public abstract boolean addPoint(BPos bpos);

    public abstract Polygon getShape();

    public abstract boolean isComplete();

    public abstract void reset();

    public abstract double getMetric();

    public abstract String getMetricString();

    public abstract boolean shouldFill();

    public abstract Tool duplicate();

    public abstract Color getColor();

    public abstract void setColor(Color color);

}
