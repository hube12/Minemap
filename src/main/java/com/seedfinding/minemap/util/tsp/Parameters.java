package com.seedfinding.minemap.util.tsp;

/**
 * Global parameters used to adjust the ACO
 */
public class Parameters {

    /**
     * Pheromone evaporation rate
     */
    public static double rho = 0.5;

    /**
     * Pheromone importance
     */
    public static double alpha = 1.0;

    /**
     * Heuristic importance
     */
    public static double beta = 1.5;

    /**
     * Size of ant population
     */
    public static int antPopSize = 20;

    /**
     * Size of nearest neighbor list for each vertex
     */
    public static int NNSize = 4;

    /**
     * Number of iterations to find a good solution
     */
    public static int iterationsMax = 1500;

}
