package com.seedfinding.minemap.util.tsp;

import com.seedfinding.latticg.util.Pair;
import com.seedfinding.mccore.util.pos.BPos;

import java.util.List;

public class Program {

    /** How much work the ant-colony solver may do. */
    public record Effort(int ants, int iterations) {
        /** Best quality, for one-shot planning (e.g. the route planner). */
        public static final Effort FULL = new Effort(Parameters.antPopSize, Parameters.iterationsMax);

        /** Cheap and cancellable, for interactive use: a handful of ants and a few hundred iterations at most. */
        public static Effort quick(int nodes) {
            int ants = Math.max(3, Math.min(10, nodes));
            int iterations = Math.max(40, Math.min(200, 4000 / Math.max(1, nodes)));
            return new Effort(ants, iterations);
        }
    }

    public Pair<Double, List<Integer>> startApplication(List<BPos> bPosList) {
        return this.startApplication(bPosList, Effort.FULL);
    }

    /**
     * @return the best tour found, or null if the calling thread was interrupted before it finished
     */
    public Pair<Double, List<Integer>> startApplication(List<BPos> bPosList, Effort effort) {
        // The solver reads its settings and the distance matrix from static fields, so only one run at a time.
        synchronized (Program.class) {
            int oldAnts = Parameters.antPopSize;
            try {
                Parameters.antPopSize = effort.ants();
                Parameters.NNSize = bPosList.size() < 10 ? bPosList.size() / 2 : 10;
                Environment environment = new Environment(TspReader.getDistances(bPosList));
                Statistics statistics = new Statistics(environment);
                environment.generateNearestNeighborList();
                environment.generateAntPopulation();
                environment.generateEnvironment();
                for (int n = 0; n < effort.iterations(); n++) {
                    if (Thread.currentThread().isInterrupted()) return null;
                    environment.constructSolutions();
                    environment.updatePheromone();
                }
                return statistics.calculateStatistics();
            } finally {
                Parameters.antPopSize = oldAnts;
            }
        }
    }

}
