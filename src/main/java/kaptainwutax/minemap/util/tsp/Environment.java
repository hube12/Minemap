package kaptainwutax.minemap.util.tsp;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Represent the solution space. Contains the vertex and edges
 * of the graph to be optimized and the pheromone let by the ants.
 */
public class Environment {

    /**
     * Value for the initial amount of pheromone
     */
    private double initialTrail;

    /**
     * The graph problem matrix to be solved of NxN
     */
    private final double[][] graph;

    /**
     * The matrix of NxNN nearest neighbor for each vertex of the graph
     */
    private int[][] NNList;

    /**
     * The matrix of NxN indicating the pheromone deposited by the ants
     */
    private double[][] pheromone;

    /**
     * The matrix of NxN storing the value of the best edges calculated using
     * the pheromone amount and the quality of the edges (smaller distances).
     */
    private double[][] choiceInfo;

    /**
     * The ants agent
     */
    private Ant[] ants;

    /**
     * Environment requires a graph to solve
     */
    public Environment(double[][] graph) {
        super();
        this.graph = graph;
    }

    /**
     * Create a list with the nn nearest neighbors of each vertex and
     * keep they in a separate structure of dimension n x nn where
     * n = population size, and nn = nearest neighbors size.
     */
    public void generateNearestNeighborList() {
        NNList = new int[getNodesSize()][getNNSize()];
        // For each node of the graph, sort the nearest neighbors by distance
        // and cut the list by the size nn.
        for (int i = 0; i < getNodesSize(); i++) {
            Integer[] nodeIndex = new Integer[getNodesSize()];
            Double[] nodeData = new Double[getNodesSize()];
            for (int j = 0; j < getNodesSize(); j++) {
                nodeIndex[j] = j;
                nodeData[j] = getCost(i, j);
            }
            // The edge of the current vertex with himself is let as last
            // option to be selected to nearest neighbors list
            nodeData[i] = Collections.max(Arrays.asList(nodeData));
            Arrays.sort(nodeIndex, Comparator.comparingDouble(o -> nodeData[o]));
            for (int r = 0; r < getNNSize(); r++) {
                NNList[i][r] = nodeIndex[r];
            }
        }
    }

    /**
     * Create a population of k ants to search solutions in the environment,
     * where k is the number of ants.
     */
    public void generateAntPopulation() {
        ants = new Ant[getAntPopSize()];
        for (int k = 0; k < getAntPopSize(); k++) {
            ants[k] = new Ant(getNodesSize(), this);
        }
    }

    /**
     * Create pheromone and choice info structure:
     * -> Pheromone is used to represent the quality of the edges used to build solutions.
     * -> ChoiceInfo is calculated with the pheromone and the quality of routes, to be
     * used by the ants as decision rule and index to speed up the algorithm.
     * <p>
     * To generate the environment the pheromone is initialized taken in account the cost
     * of the nearest neighbor tour.
     */
    public void generateEnvironment() {
        pheromone = new double[getNodesSize()][getNodesSize()];
        choiceInfo = new double[getNodesSize()][getNodesSize()];
        initialTrail = 1.0 / (Parameters.rho * ants[0].calculateNearestNeighborTour());
        for (int i = 0; i < getNodesSize(); i++) {
            for (int j = i; j < getNodesSize(); j++) {
                pheromone[i][j] = initialTrail;
                pheromone[j][i] = initialTrail;
                choiceInfo[i][j] = initialTrail;
                choiceInfo[j][i] = initialTrail;
            }
        }
        calculateChoiceInformation();
    }

    /**
     * Calculate the proportional probability of an ant at vertex i select a neighbor
     * j based on the (i->j) edge cost (taken the inverse cost of the edge) and
     * (i->j) edge pheromone amount. The parameters alpha and beta control the
     * balance between heuristic and pheromone.
     */
    public void calculateChoiceInformation() {
        for (int i = 0; i < getNodesSize(); i++) {
            for (int j = 0; j < i; j++) {
                double heuristic = (1.0 / (getCost(i, j) + 0.1));
                choiceInfo[i][j] = Math.pow(pheromone[i][j], Parameters.alpha) * Math.pow(heuristic, Parameters.beta);
                choiceInfo[j][i] = choiceInfo[i][j];
            }
        }
    }

    /**
     * Put each ant to construct a solution in the environment.
     */
    public void constructSolutions() {
        // At the first step reset all ants (clearVisited) and put each one
        // in a random vertex of the graph.
        int phase = 0;
        for (int k = 0; k < getAntPopSize(); k++) {
            ants[k].clearVisited();
            ants[k].startAtRandomPosition(phase);
        }
        // Make all ants choose the next non visited vertex based in the
        // pheromone trails and heuristic of the edge cost.
        while (phase < getNodesSize() - 1) {
            phase++;
            for (int k = 0; k < getAntPopSize(); k++) {
                ants[k].goToNNListAsDecisionRule(phase);
            }
        }
        // Close the circuit and calculate the total cost
        for (int k = 0; k < getAntPopSize(); k++) {
            ants[k].finishTourCircuit();
        }
    }

    /**
     * Update the pheromone taking into account the quality of the solutions build
     * by the ants and the evaporation rate
     */
    public void updatePheromone() {
        evaporatePheromone();
        for (int k = 0; k < getAntPopSize(); k++) {
            depositPheromone(ants[k]);
        }
        calculateChoiceInformation();
    }

    /**
     * Evaporate the amount of pheromone by a exponential factor (1 - rho)
     * for all edges
     */
    public void evaporatePheromone() {
        for (int i = 0; i < getNodesSize(); i++) {
            for (int j = i; j < getNodesSize(); j++) {
                pheromone[i][j] = (1 - Parameters.rho) * pheromone[i][j];
                pheromone[j][i] = pheromone[i][j];
            }
        }
    }

    /**
     * For the ant, deposit the amount of pheromone in all edges used in the ant where
     * the amount of pheromone deposited is proportional to the solution quality
     *
     * @param ant
     */
    public void depositPheromone(Ant ant) {
        double dTau = 1.0 / ant.getTourCost();
        for (int i = 0; i < getNodesSize(); i++) {
            int j = ant.getRoutePhase(i);
            int l = ant.getRoutePhase(i + 1);
            pheromone[j][l] = pheromone[j][l] + dTau;
            pheromone[l][j] = pheromone[j][l];
        }
    }

    /**
     * Return the number of nodes
     *
     * @return graphLength
     */
    public int getNodesSize() {
        return graph.length;
    }

    /**
     * Return the size of nearest neighbor list
     *
     * @return nnSize
     */
    public int getNNSize() { return Parameters.NNSize; }

    /**
     * Return the distance between to vertices
     *
     * @param from
     * @param to
     * @return cost
     */
    public double getCost(int from, int to) {
        return graph[from][to];
    }

    /**
     * Return the ant population size
     *
     * @return antPopSize
     */
    public int getAntPopSize() {
        return Parameters.antPopSize;
    }

    /**
     * Return the nearest neighbor of the index rank position
     *
     * @param from
     * @param index
     * @return targetVertex
     */
    public int getNNNode(int from, int index) {
        return this.NNList[from][index];
    }

    /**
     * Return the heuristic-pheromone value of the edge
     *
     * @param from
     * @param to
     * @return costInfo
     */
    public double getCostInfo(int from, int to) {
        return choiceInfo[from][to];
    }

    /**
     * Return the ant array
     *
     * @return ants
     */
    public Ant[] getAnts() {
        return ants;
    }
}
