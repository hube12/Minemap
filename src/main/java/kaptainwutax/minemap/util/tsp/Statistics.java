package kaptainwutax.minemap.util.tsp;

import com.seedfinding.latticg.util.Pair;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Calculate the statistics of the algorithm evolution
 */
public class Statistics {

    /**
     * Environment to be analysed
     */
    private final Environment environment;

    /**
     * The cost of the best so far tour
     */
    private double bestSoFar = Double.MAX_VALUE;


    /**
     * Needs an environment and the coordinates of the vertices to be drawn
     *
     * @param environment
     */
    public Statistics(Environment environment) {
        this.environment = environment;
    }

    /**
     * For each iteration get the best, the worst and the mean tour cost
     * of all tours constructed by the ants, if a improvement was detected
     * show show the values.
     */
    public Pair<Double, List<Integer>> calculateStatistics() {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double total = 0.0;
        Ant bestAnt = null;
        for (Ant ant : environment.getAnts()) {
            if (ant.getTourCost() < min) {
                min = ant.getTourCost();
                bestAnt = ant;
            }
            if (ant.getTourCost() > max) {
                max = ant.getTourCost();
            }
            total += ant.getTourCost();
        }
        if (min < bestSoFar) {
            bestSoFar = min;
            assert bestAnt != null;
            int[] bestTourSoFar = bestAnt.getTour().clone();
            List<Integer> list = IntStream.of(bestTourSoFar).boxed().collect(Collectors.toList());
            list.remove(list.size() - 1);
            return new Pair<>(min, list);
        }

        return null;
    }

}
