package kaptainwutax.minemap.ui.map.tool;

import kaptainwutax.seedutils.mc.pos.BPos;

import java.awt.*;

public abstract class Tool implements Cloneable {

    public abstract int getPointsTraced();

    public abstract boolean addPoint(BPos bpos);

    public abstract Shape getPartialShape();

    public abstract boolean isComplete();

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

}
