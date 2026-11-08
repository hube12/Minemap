package com.seedfinding.minemap.util.tsp;

import com.seedfinding.latticg.util.Pair;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.minemap.util.data.ListUtil;
import com.seedfinding.minemap.util.math.DisplayMaths;

import java.util.ArrayList;
import java.util.List;

/**
 * Orders a set of stops into a short route that begins at a given start position.
 * Uses the ant-colony TSP solver for the closed tour, then opens it at the most expensive edge touching the start.
 */
public final class RoutePlanner {
    /** Below this many nodes every closed tour has the same length, so the solver is skipped. */
    private static final int MIN_NODES_FOR_SOLVER = 4;

    private RoutePlanner() {}

    /**
     * @param order  start first, then the stops in visiting order (start repeated at the end if the route returns)
     * @param length total route length in blocks (2D)
     */
    public record Result(List<BPos> order, double length) {}

    public static Result plan(BPos start, List<BPos> stops, boolean returnToStart) {
        List<BPos> nodes = new ArrayList<>(stops.size() + 1);
        nodes.add(start);
        nodes.addAll(stops);

        if (nodes.size() >= MIN_NODES_FOR_SOLVER) {
            // the solver keeps its parameters and distance matrix in static fields
            synchronized (RoutePlanner.class) {
                Pair<Double, List<Integer>> tour = new Program().startApplication(nodes);
                if (tour != null) ListUtil.reindexInPlace(nodes, tour.getSecond().toArray(new Integer[0]));
            }
            ListUtil.cycleInPlace(nodes, start);
            if (!returnToStart && nodes.size() > 2) {
                // The tour is a loop: walk it in the direction that drops the longer of the two edges at the start.
                double toLast = DisplayMaths.getDistance2D(nodes.getLast(), start);
                double toSecond = DisplayMaths.getDistance2D(nodes.get(1), start);
                if (toSecond > toLast) {
                    List<BPos> rest = new ArrayList<>(nodes.subList(1, nodes.size()));
                    java.util.Collections.reverse(rest);
                    nodes = new ArrayList<>();
                    nodes.add(start);
                    nodes.addAll(rest);
                }
            }
        } else {
            nodes = nearestNeighbour(nodes);
        }

        if (returnToStart && nodes.size() > 1) nodes.add(start);
        return new Result(nodes, length(nodes));
    }

    public static double length(List<BPos> route) {
        double total = 0.0D;
        for (int i = 0; i < route.size() - 1; i++) {
            total += DisplayMaths.getDistance2D(route.get(i), route.get(i + 1));
        }
        return total;
    }

    private static List<BPos> nearestNeighbour(List<BPos> nodes) {
        List<BPos> left = new ArrayList<>(nodes.subList(1, nodes.size()));
        List<BPos> out = new ArrayList<>();
        BPos current = nodes.getFirst();
        out.add(current);
        while (!left.isEmpty()) {
            BPos best = left.getFirst();
            for (BPos candidate : left) {
                if (DisplayMaths.getDistance2D(current, candidate) < DisplayMaths.getDistance2D(current, best)) best = candidate;
            }
            left.remove(best);
            out.add(best);
            current = best;
        }
        return out;
    }
}
