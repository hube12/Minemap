package com.seedfinding.minemap.ui.map.tool;

import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.minemap.util.math.DisplayMaths;
import com.seedfinding.minemap.util.tsp.RoutePlanner;

import java.awt.*;
import java.util.List;

/** A precomputed route drawn on the map. Unlike {@link Polyline} it is not edited by clicking and keeps its order. */
public class Route extends Tool {
    private final List<BPos> stops;
    private final String label;
    private Color color = DisplayMaths.getRandomColor();
    private Shape strokePath;
    private final List<Runnable> removalListeners = new java.util.concurrent.CopyOnWriteArrayList<>();

    public Route(List<BPos> stops, String label) {
        this.stops = List.copyOf(stops);
        this.label = label;
    }

    /** Called when the route is removed from the map (sidebar close button or a toggle). */
    public void addRemovalListener(Runnable listener) {
        this.removalListeners.add(listener);
    }

    public void notifyRemoved() {
        this.removalListeners.forEach(Runnable::run);
    }

    @Override
    public int getPointsTraced() {
        return this.stops.size();
    }

    @Override
    public boolean addPoint(BPos bpos) {
        return false;
    }

    @Override
    public Polygon getPartialShape() {
        return null;
    }

    @Override
    public List<Shape> getPartialShapes() {
        return null; // drawn through getStrokePath()
    }

    @Override
    public Shape getStrokePath() {
        if (this.stops.size() < 2) return null;
        if (this.strokePath == null) this.strokePath = DisplayMaths.getPolylinePath(this.stops);
        return this.strokePath;
    }

    @Override
    public double getStrokeWidthBlocks() {
        return Polyline.LINE_WIDTH_BLOCKS;
    }

    @Override
    public Polygon getExactShape() {
        return null;
    }

    @Override
    public List<Shape> getExactShapes() {
        return this.stops.size() > 1 ? DisplayMaths.getPolylinePolygon(this.stops, 0) : null;
    }

    @Override
    public boolean isComplete() {
        return this.stops.size() > 1;
    }

    @Override
    public boolean isAcceptable() {
        return this.isComplete();
    }

    @Override
    public boolean isPartial() {
        return this.stops.size() > 1;
    }

    @Override
    public void reset() {}

    @Override
    public double getMetric() {
        return DisplayMaths.round(RoutePlanner.length(this.stops), 2);
    }

    @Override
    public String[] getMetricString() {
        return new String[] {String.format("%s: %d stops, %.0f blocks", this.label, this.stops.size() - 1, this.getMetric())};
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
        Route copy = new Route(this.stops, this.label);
        copy.color = this.color;
        return copy;
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String getName() {
        return "Route";
    }

    @Override
    public boolean isMultiplePolygon() {
        return true;
    }
}
