package com.seedfinding.minemap.ui.map.tool;

import com.seedfinding.latticg.util.Pair;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.minemap.MineMap;
import com.seedfinding.minemap.ui.map.MapManager;
import com.seedfinding.minemap.util.data.ListUtil;
import com.seedfinding.minemap.util.math.DisplayMaths;
import com.seedfinding.minemap.util.tsp.Program;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class Polyline extends Tool {
    private BPos firstPoint = null;
    private final LinkedList<BPos> bPosList = new LinkedList<>();
    private int pointsTraced = 0;
    private Shape strokePath;
    private final Program program = new Program();
    private SwingWorker<Pair<Double, List<Integer>>, Void> worker;
    /** Below this many points every ordering of a closed loop is equally long, so skip the solver. */
    private static final int MIN_POINTS_TO_OPTIMIZE = 4;
    private Color color;
    public static final double LINE_WIDTH_BLOCKS = 6.0D;

    public Polyline() {
        color = DisplayMaths.getRandomColor();
    }

    @Override
    public boolean addPoint(BPos bpos) {
        if (firstPoint == null) firstPoint = bpos;
        bPosList.add(bpos);
        strokePath = null;
        pointsTraced++;
        MapManager manager = MineMap.INSTANCE.worldTabs.getSelectedMapPanel().getManager();
        if (bPosList.size() < MIN_POINTS_TO_OPTIMIZE) {
            SwingUtilities.invokeLater(() -> manager.getPanel().rightBar.tooltip.updateToolsMetrics());
            return true;
        }
        // Re-optimise in the background with a small effort; a newer click cancels the previous run.
        if (worker != null) worker.cancel(true);
        final List<BPos> snapshot = new ArrayList<>(bPosList);
        worker = new SwingWorker<Pair<Double, List<Integer>>, Void>() {
            @Override
            protected Pair<Double, List<Integer>> doInBackground() {
                return program.startApplication(snapshot, Program.Effort.quick(snapshot.size()));
            }

            @Override
            protected void done() {
                if (isCancelled()) return;
                try {
                    Pair<Double, List<Integer>> res = get();
                    if (res == null) return;
                    // points clicked while the solver ran are not in the result: reindex keeps them at the end
                    ListUtil.reindexInPlace(bPosList, res.getSecond().toArray(new Integer[0]));
                    ListUtil.cycleInPlace(bPosList, firstPoint);
                    strokePath = null;
                } catch (Exception ignored) {
                    // solver failed or was interrupted: keep the current order
                }
                manager.getPanel().rightBar.tooltip.updateToolsMetrics();
            }
        };
        worker.execute();
        return true;
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
        if (bPosList.size() < 2) return null;
        // rebuilt only when the points change, not on every repaint of every fragment
        if (strokePath == null) strokePath = DisplayMaths.getPolylinePath(bPosList);
        return strokePath;
    }

    @Override
    public double getStrokeWidthBlocks() {
        return LINE_WIDTH_BLOCKS;
    }

    @Override
    public Polygon getExactShape() {
        return null;
    }

    @Override
    public List<Shape> getExactShapes() {
        int offset = 0;
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
        strokePath = null;
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
